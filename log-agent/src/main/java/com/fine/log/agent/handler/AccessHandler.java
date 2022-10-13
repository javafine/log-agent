package com.fine.log.agent.handler;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @Author: javafine
 * @Description:
 * @Date: Created in 10:53 2022/10/9
 */
public class AccessHandler {
    private static final String ACCESS_TYPE_ANNOTATION_DEAFAULT = "org.springframework.web.bind.annotation.RestController";
    private static final String[] ACCESS_TYPE_ANNOTATION = new String[]{};
    private static final String ACCESS_METHOD_ANNOTATION_START_DEAFAULT = "org.springframework.web.bind.annotation";

    public static ElementMatcher.Junction accessTypeMatcher() {
        ElementMatcher.Junction<TypeDescription> matcherAccessType = named(ACCESS_TYPE_ANNOTATION_DEAFAULT);
        for (String annotation : ACCESS_TYPE_ANNOTATION) {
            if (!annotation.isEmpty()) {
                matcherAccessType = matcherAccessType.or(named(annotation));
            }
        }
        return matcherAccessType;
    }

    public static ElementMatcher.Junction accessMethodMatcher() {
        return isAnnotatedWith(nameStartsWith(ACCESS_METHOD_ANNOTATION_START_DEAFAULT));
    }

}
