package net.gtaun.shoebill.common.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by marvin on 15.02.16.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface CommandParameter {
    String name() default "";
    String description() default "";
}
