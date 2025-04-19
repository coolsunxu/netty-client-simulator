package com.example.nettyclientsimulator.annotation;

import java.lang.annotation.*;

/**
 * @author sunxu
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodMetrics {
    String name() default "";
    String des() default "";
    String[] tags() default {};
}
