/*
 * Copyright 2014 - Present Rafael Winterhalter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fine.log.agent;

import com.fine.log.agent.handler.*;
import com.fine.log.agent.logger.FineLogger;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @Author: javafine
 * @Description:
 * @Date: Created in 17:46 2022/9/21
 */
public class LogAgent {
    /**
     * 1. 拦截HTTP请求：{@link HttpHandler#httpClassMatcher()}、{@link HttpHandler#httpMethodMatcher()}，
     * 打印请求的headers（）、返回值等：{@link HttpHandler.HttpInterceptor#intercept(Callable, Method, HttpServletRequest)}；
     *
     * 2. 拦截controller层的所有接口方法：{@link AccessHandler#accessTypeMatcher()} 、{@link AccessHandler#accessMethodMatcher()}，
     * 打印方法参数、返回值和方法信息等：{@link CommonHandler.CommonInterceptor#intercept(Callable, Method, Object[])}；
     *
     * 3. 拦截service层的所有接口方法：{@link ServiceHandler#serviceTypeMatcher()}，
     * 打印方法参数、返回值和方法信息等：{@link CommonHandler.CommonInterceptor#intercept(Callable, Method, Object[])}；
     *
     * 4. 拦截使用HttpClient请求三方接口的请求：{@link ThirdHandler#thirdClassMatcher()}、{@link ThirdHandler#thirdMethodMatcher()}，
     * 打印请求的地址、headers、返回值等：{@link ThirdHandler.ThirdInterceptor#intercept(Callable, Method, HttpHost, HttpRequest)}。
     */
    public static void premain(String arg, Instrumentation instrumentation) {
        new AgentBuilder.Default()
//                .with(AgentBuilder.Listener.StreamWriting.toSystemError())
                .type(CommonHandler.domainClassMatcher().or(ThirdHandler.thirdClassMatcher()).or(HttpHandler.httpClassMatcher()))
                .transform(new AgentBuilder.Transformer() {
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                                            TypeDescription typeDescription,
                                                            ClassLoader classLoader,
                                                            JavaModule module,
                                                            ProtectionDomain protectionDomain) {
//                        System.out.println("instrument---"+classLoader.toString()+" | "+typeDescription.getName());
                        for (AnnotationDescription annotation : typeDescription.getDeclaredAnnotations()){
                            if(AccessHandler.accessTypeMatcher().matches(annotation.getAnnotationType())){
                                FineLogger.mapLogType.put(typeDescription.getName(), FineLogger.LogType.ACCESS.name());
                                return builder.method(AccessHandler.accessMethodMatcher())
                                        .intercept(MethodDelegation.to(CommonHandler.CommonInterceptor.class));
                            } else if(ServiceHandler.serviceTypeMatcher().matches(annotation.getAnnotationType())){
                                FineLogger.mapLogType.put(typeDescription.getName(), FineLogger.LogType.SERVICE.name());
                                continue;
                            } else {

                            }
                        }
                        if(ThirdHandler.thirdClassMatcher().matches(typeDescription)){
                            FineLogger.mapLogType.put(typeDescription.getName(), FineLogger.LogType.THIRD.name());
                            return builder.method(ThirdHandler.thirdMethodMatcher())
                                    .intercept(MethodDelegation.to(ThirdHandler.ThirdInterceptor.class));
                        }
                        if(HttpHandler.httpClassMatcher().matches(typeDescription)){
                            FineLogger.mapLogType.put(typeDescription.getName(), FineLogger.LogType.HTTP.name());
                            return builder.method(HttpHandler.httpMethodMatcher())
                                    .intercept(MethodDelegation.to(HttpHandler.HttpInterceptor.class));
                        }
                        return builder.method(any())
                                .intercept(MethodDelegation.to(CommonHandler.CommonInterceptor.class));
                    }
                })
                .installOn(instrumentation);
    }

    //未实现
    public static void agentmain(String arg, Instrumentation instrumentation){

    }







}
