package com.fine.log.agent.handler;

import com.fine.log.agent.logger.FineLogger;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatcher;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

/**
 * @Author: javafine
 * @Description:
 * @Date: Created in 11:15 2022/10/9
 */
public class HttpHandler {

    private static final String HTTP_CLASS_NAME = "javax.servlet.http.HttpServlet";
    private static final String HTTP_METHOD_NAME = "service";
    private static final String HTTP_ARG_CLASS_NAME_0 = "javax.servlet.http.HttpServletRequest";
    private static final String HTTP_ARG_CLASS_NAME_1 = "javax.servlet.http.HttpServletResponse";

    public static ElementMatcher httpMethodMatcher(){
        return named(HTTP_METHOD_NAME)
                .and(takesArgument(0, named(HTTP_ARG_CLASS_NAME_0)).and(takesArgument(1, named(HTTP_ARG_CLASS_NAME_1))));
    }

    public static ElementMatcher httpClassMatcher(){
        return named(HTTP_CLASS_NAME);
    }


    public static class HttpInterceptor {
        @RuntimeType
        public static Object intercept(@SuperCall Callable<?> zuper, @Origin Method method, @Argument(0) HttpServletRequest req) throws Exception {
//            System.out.println("--------------req agent "+req.getClass().getClassLoader().toString()+"|"+req.getClass().hashCode());
            String tID = req.getHeader(FineLogger.TRACE_ID);
            if (tID==null || tID.isEmpty()){
                tID = req.getParameter(FineLogger.TRACE_ID);
            }
            if (tID==null || tID.isEmpty()){
                tID = UUID.randomUUID().toString();
            }
            FineLogger.clear();
            FineLogger.putMDC(FineLogger.LOG_TRACE_ID, tID);
            FineLogger.putNDC(tID);
            Map<String, String> map = new HashMap<>();
            map.put(FineLogger.LOG_REQUEST_URL, req.getRequestURL().toString());
            StringBuilder sb = new StringBuilder();
            for (Enumeration<String> e = req.getHeaderNames(); e.hasMoreElements();){
                String name = e.nextElement();
                if(!FineLogger.HEADER_EXCLUDE.contains(name)){
                    sb.append(req.getHeader(name)).append(',');
                }
            }
            map.put(FineLogger.LOG_REQUEST_HEADER, sb.toString());
            return CommonHandler.CommonInterceptor.intercept(zuper, method, Arrays.asList(req.getParameterMap().values()).toArray(), map);
        }
    }
}
