package com.fine.log.agent.handler;

import com.fine.log.agent.logger.FineLogger;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @Author: javafine
 * @Description:
 * @Date: Created in 11:09 2022/10/9
 */
public class ThirdHandler {
    private static final String THIRD_CLASS_NAME = "org.apache.http.impl.client.CloseableHttpClient";
    private static final String THIRD_METHOD_NAME = "doExecute";

    public static ElementMatcher thirdMethodMatcher(){
        return named(THIRD_METHOD_NAME);
    }

    public static ElementMatcher thirdClassMatcher(){
        return hasSuperType(named(THIRD_CLASS_NAME));
    }


    public static class ThirdInterceptor {
        @RuntimeType
        public static Object intercept(@SuperCall Callable<?> zuper, @Origin Method method, @Argument(0) HttpHost target, @Argument(1) HttpRequest request) throws Exception {
            request.addHeader(FineLogger.TRACE_ID, FineLogger.getMDC(FineLogger.LOG_TRACE_ID));

            FineLogger.putMDC(FineLogger.LOG_THIRD_URL, target.toURI());
            StringBuilder sb = new StringBuilder();
            for (Header h : request.getAllHeaders()){
                if (!FineLogger.HEADER_EXCLUDE.contains(h.getName())){
                    sb.append(h.getValue()).append(",");
                }
            }
            FineLogger.putMDC(FineLogger.LOG_THIRD_HEADER, sb.toString());
            //覆盖HTTP请求地址和header
            Map<String, String> map = new HashMap<>();
            map.put(FineLogger.LOG_REQUEST_URL, target.toURI());
            map.put(FineLogger.LOG_REQUEST_HEADER, sb.toString());

            return CommonHandler.CommonInterceptor.intercept(zuper, method, new Object[]{}, map);
        }
    }
}
