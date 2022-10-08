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

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.*;
import net.bytebuddy.utility.JavaModule;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @Author: FanBo
 * @Description:
 * @Date: Created in 17:46 2022/9/21
 */
public class LogAgent {
    private static final String[] CLASS_NAME_START = new String[]{};
    private static final String[] ACCESS_TYPE_ANNOTATION = new String[]{};
    private static final String[] SERVICE_TYPE_ANNOTATION = new String[]{};
    private static final String[] ACCESS_METHOD_ANNOTATION = new String[]{"org.springframework.web.bind.annotation.GetMapping","org.springframework.web.bind.annotation.PostMapping"};

    public static ElementMatcher.Junction matcherClass(){
        ElementMatcher.Junction<TypeDescription> matcherClass = nameStartsWith("com.fine.example.");
        for (String nameStart : CLASS_NAME_START) {
            if(!nameStart.isEmpty()){
                matcherClass = matcherClass.or(nameStartsWith(nameStart));
            }
        }
        return matcherClass;
    }

    public static ElementMatcher.Junction matcherAccessType() {
        ElementMatcher.Junction<TypeDescription> matcherAccessType = named("org.springframework.web.bind.annotation.RestController");
        for (String annotation : ACCESS_TYPE_ANNOTATION) {
            if (!annotation.isEmpty()) {
                matcherAccessType = matcherAccessType.or(named(annotation));
            }
        }
        return matcherAccessType;
    }

    public static ElementMatcher.Junction matcherServiceType() {
        ElementMatcher.Junction<TypeDescription> matcherServiceType = named("org.springframework.stereotype.Service");
        for (String annotation : SERVICE_TYPE_ANNOTATION) {
            if (!annotation.isEmpty()) {
                matcherServiceType = matcherServiceType.or(named(annotation));
            }
        }
        return matcherServiceType;
    }

    public static ElementMatcher.Junction matcherAccessMethod() {
        return isAnnotatedWith(nameStartsWith("org.springframework.web.bind.annotation"));
    }

    public static ElementMatcher.Junction matcherAccessMethod2() {
        ElementMatcher.Junction<MethodDescription> matcherAccessMethod = isAnnotatedWith(named("org.springframework.web.bind.annotation.RequestMapping"));
        for (String annotation : ACCESS_METHOD_ANNOTATION) {
            if (!annotation.isEmpty()) {
                matcherAccessMethod = matcherAccessMethod.or(isAnnotatedWith(named(annotation)));
            }
        }
        return matcherAccessMethod;
    }

    public static ElementMatcher forThirdMethod(){
        return named("doExecute");
    }

    public static ElementMatcher forThirdClass(){
        return hasSuperType(named("org.apache.http.impl.client.CloseableHttpClient"));
    }

    public static ElementMatcher forHttpMethod(){
        return named("service")
                .and(takesArgument(0, named("javax.servlet.http.HttpServletRequest")).and(takesArgument(1, named("javax.servlet.http.HttpServletResponse"))));
    }

    public static ElementMatcher forHttpClass(){
        return named("javax.servlet.http.HttpServlet");
    }

    public static void premain(String arg, Instrumentation instrumentation) {
        new AgentBuilder.Default()
//                .with(AgentBuilder.Listener.StreamWriting.toSystemError())
                .type(matcherClass().or(forThirdClass()).or(forHttpClass()))
                .transform(new AgentBuilder.Transformer() {
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                                            TypeDescription typeDescription,
                                                            ClassLoader classLoader,
                                                            JavaModule module,
                                                            ProtectionDomain protectionDomain) {
//                        System.out.println("instrument---"+classLoader.toString()+" | "+typeDescription.getName());
                        for (AnnotationDescription annotation : typeDescription.getDeclaredAnnotations()){
                            if(matcherAccessType().matches(annotation.getAnnotationType())){
                                mapLogType.put(typeDescription.getName(), LogType.ACCESS.name());
                                return builder.method(matcherAccessMethod())
                                        .intercept(MethodDelegation.to(ProcessInterceptor.class));
                            } else if(matcherServiceType().matches(annotation.getAnnotationType())){
                                mapLogType.put(typeDescription.getName(), LogType.SERVICE.name());
                                continue;
                            } else {

                            }
                        }
                        if(forThirdClass().matches(typeDescription)){
                            mapLogType.put(typeDescription.getName(), LogType.THIRD.name());
                            return builder.method(forThirdMethod())
                                    .intercept(MethodDelegation.to(ThirdInterceptor.class));
                        }
                        if(forHttpClass().matches(typeDescription)){
                            mapLogType.put(typeDescription.getName(), LogType.HTTP.name());
                            return builder.method(forHttpMethod())
                                    .intercept(MethodDelegation.to(HttpInterceptor.class));
                        }
                        return builder.method(any())
                                .intercept(MethodDelegation.to(ProcessInterceptor.class));
                    }
                })
                .installOn(instrumentation);
    }

    public static void agentmain(String arg, Instrumentation instrumentation){

    }
    public static class ProcessInterceptor {
        @RuntimeType
        public static Object intercept(@SuperCall Callable<?> zuper, @Origin Method method, @AllArguments Object[] args) throws Exception {
            Object ret = null;
            ret = zuper.call();
            putMDC(LOG_TYPE, mapLogType.get(method.getDeclaringClass().getName()));
            putMDC(LOG_CLASS_NAME, method.getDeclaringClass().getName());
            putMDC(LOG_METHOD_NAME, method.getName());
            putMDC(LOG_METHOD_ARGS, Arrays.asList(args).toString());
            putMDC(LOG_METHOD_RET, String.valueOf(ret));
            LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).info("-");
            return ret;
        }
    }

    public static class HttpInterceptor {
        @RuntimeType
        public static Object intercept(@SuperCall Callable<?> zuper, @Origin Method method, @Argument(0) HttpServletRequest req) throws Exception {
//            System.out.println("--------------req agent "+req.getClass().getClassLoader().toString()+"|"+req.getClass().hashCode());
            putMDC(LOG_REQUEST_ID, req.getHeader("accessID"));
            putMDC(LOG_REQUEST_URL, req.getRequestURL().toString());
            StringBuilder sb = new StringBuilder();
            for (Enumeration<String> e = req.getHeaderNames(); e.hasMoreElements();){
                String name = e.nextElement();
                if(!"accessID".equals(name)){
                    sb.append(req.getHeader(name));
                }
            }
//            putMDC(LOG_REQUEST_HEADER, sb.toString());
            return ProcessInterceptor.intercept(zuper, method, req.getParameterMap().values().toArray());
        }
    }

    public static class ThirdInterceptor {
        @RuntimeType
        public static Object intercept(@SuperCall Callable<?> zuper, @Origin Method method, @Argument(0) HttpHost target, @Argument(1) HttpRequest request) throws Exception {
            putMDC(LOG_THIRD_URL, target.toURI());
            return ProcessInterceptor.intercept(zuper, method, new Object[]{});
        }
    }

    public static void putMDC(String key, String value){
        MDC.put(key, value);
    }

    public static final String LOG_TYPE = "lType";
    public static final String LOG_CLASS_NAME = "cName";
    public static final String LOG_METHOD_NAME = "mName";
    public static final String LOG_METHOD_ARGS = "mArgs";
    public static final String LOG_METHOD_RET = "mRet";
    public static final String LOG_REQUEST_ID = "rID";
    public static final String LOG_REQUEST_URL = "rURL";
    public static final String LOG_REQUEST_HEADER = "rHeader";
    public static final String LOG_THIRD_URL = "tURL";
    public static final String LOG_THIRD_HEADER = "tURL";

    public static final Map<String, String> mapLogType=new HashMap<String, String>();

    public enum LogType {
        HTTP,ACCESS,SERVICE,THIRD,DEFAULT;
    }

}
