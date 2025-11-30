package com.hexagonal.framework.crosscutting.tracing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Trace {
    // İsteğe bağlı: Span ismini özelleştirmek istersen kullanabilirsin
    String name() default "";
}