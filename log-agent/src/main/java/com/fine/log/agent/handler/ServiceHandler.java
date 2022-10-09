package com.fine.log.agent.handler;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @Author: FanBo
 * @Description:
 * @Date: Created in 11:07 2022/10/9
 */
public class ServiceHandler {
    private static final String SERVICE_TYPE_ANNOTATION_DEAFAULT = "org.springframework.stereotype.Service";
    private static final String[] SERVICE_TYPE_ANNOTATION = new String[]{};

    public static ElementMatcher.Junction serviceTypeMatcher() {
        ElementMatcher.Junction<TypeDescription> matcherServiceType = named(SERVICE_TYPE_ANNOTATION_DEAFAULT);
        for (String annotation : SERVICE_TYPE_ANNOTATION) {
            if (!annotation.isEmpty()) {
                matcherServiceType = matcherServiceType.or(named(annotation));
            }
        }
        return matcherServiceType;
    }
}
