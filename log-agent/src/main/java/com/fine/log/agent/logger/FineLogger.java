package com.fine.log.agent.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.*;

/**
 * @Author: javafine
 * @Description:
 * @Date: Created in 11:25 2022/10/9
 */
public class FineLogger {
    public static final String LOG_TYPE = "lType";
    public static final String LOG_EXECUTE_TIME = "eTime";
    public static final String LOG_CLASS_NAME = "cName";
    public static final String LOG_METHOD_NAME = "mName";
    public static final String LOG_METHOD_ARGS = "mArgs";
    public static final String LOG_METHOD_RET = "mRet";
    public static final String LOG_TRACE_ID = "traceID";
    public static final String LOG_REQUEST_URL = "rURL";
    public static final String LOG_REQUEST_HEADER = "rHeader";
    public static final String LOG_THIRD_URL = "tURL";
    public static final String LOG_THIRD_HEADER = "tHeader";
    public static final Map<String, String> mapLogType=new HashMap<String, String>();

    public static final String TRACE_ID = "trace-id";
    public static final Set<String> HEADER_EXCLUDE = new HashSet<String>(Arrays.asList(TRACE_ID, "content-type", "content-length", "host", "user-agent", "accept", "accept-encoding", "connection"));

    public static String getMDC(String key) {
        return MDC.get(key);
    }

    public static void putMDC(String key, String value){
        MDC.put(key, value);
    }

    public static void putMDC(Map<String, String> map){
        if(map!=null && !map.isEmpty()){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                putMDC(entry.getKey(), entry.getValue());
            }
        }
    }

    public enum LogType {
        HTTP,ACCESS,SERVICE,THIRD,DEFAULT
    }

    public static void info(){
        info("-");
    }

    public static void info(String msg){
        String message = "-";
        if(msg!=null && !msg.isEmpty()){
            message = msg;
        }
        LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).info(message);
    }

    public static void info(Map<String, String> map){
        if(map==null || map.isEmpty()) {
            info();
            return;
        }
        Map<String, String> temp = MDC.getCopyOfContextMap();
        if (temp!=null && !temp.isEmpty()) {
            map.putAll(temp);
        }
        MDC.setContextMap(map);
        info();
        MDC.setContextMap(temp);
    }
}
