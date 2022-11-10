package com.fine.log.agent.handler;

import com.fine.log.agent.logger.FineLogger;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

/**
 * @Author: javafine
 * @Description:
 * @Date: Created in 14:04 2022/10/9
 */
public class CommonHandler {
    //被instrument的工程的包名前缀
    private static final String DOMAIN_PACKAGE_NAME_START_DEFAULT ="com.fine.example.";
    private static final String[] DOMAIN_PACKAGE_NAME_START = new String[]{};

    //过滤出被instrument的Class
    public static ElementMatcher.Junction domainClassMatcher(){
        ElementMatcher.Junction<TypeDescription> matcherClass = nameStartsWith(DOMAIN_PACKAGE_NAME_START_DEFAULT);
        for (String nameStart : DOMAIN_PACKAGE_NAME_START) {
            if(!nameStart.isEmpty()){
                matcherClass = matcherClass.or(nameStartsWith(nameStart));
            }
        }
        return matcherClass;
    }


    public static class CommonInterceptor {
        @RuntimeType
        public static Object intercept(@SuperCall Callable<?> zuper, @Origin Method method, @AllArguments Object[] args) throws Exception {
            return intercept(zuper, method, args, null);
        }

        @IgnoreForBinding
        public static Object intercept(@SuperCall Callable<?> zuper, @Origin Method method, @AllArguments Object[] args, Map<String,String> map) throws Exception {
            boolean startHere = false;
            String type = FineLogger.mapLogType.get(method.getDeclaringClass().getName());

            String mtID = FineLogger.getMDC(FineLogger.LOG_TRACE_ID);
            if (mtID==null || mtID.isEmpty()){
//                FineLogger.clear();
                startHere = true;
                mtID = UUID.randomUUID().toString();
                FineLogger.putMDC(FineLogger.LOG_TRACE_ID, mtID);
            }
            String ntID = FineLogger.getNDC();
            if (ntID==null || ntID.isEmpty()){
//                FineLogger.clear();
                ntID = UUID.randomUUID().toString();
                FineLogger.putNDC(ntID);
            }
            long start = System.currentTimeMillis();
            Object ret = null;
            ret = zuper.call();
            FineLogger.putMDC(FineLogger.LOG_TYPE, type==null?"":type);
            FineLogger.putMDC(FineLogger.LOG_EXECUTE_TIME, Long.toString(System.currentTimeMillis()-start));
            FineLogger.putMDC(FineLogger.LOG_CLASS_NAME, method.getDeclaringClass().getSimpleName());
            FineLogger.putMDC(FineLogger.LOG_METHOD_NAME, method.getName());
            FineLogger.putMDC(FineLogger.LOG_METHOD_ARGS, Arrays.asList(args).toString());
            FineLogger.putMDC(FineLogger.LOG_METHOD_RET, String.valueOf(ret));
            FineLogger.info(map);
            if(startHere){
                FineLogger.clear();
            }
            return ret;
        }
    }


}
