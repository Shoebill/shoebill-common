package net.gtaun.shoebill.common.command;

import java.lang.annotation.Annotation;

/**
 * Created by marvin on 16.10.16 in project shoebill-common.
 * Copyright (c) 2016 Marvin Haschker. All rights reserved.
 */
class Utils {

    protected static CommandParameter makeCommandParameterAnnotation(String name) {
        return makeCommandParameterAnnotation(name, null);
    }

    private static CommandParameter makeCommandParameterAnnotation(String name, String description) {
        return new CommandParameter() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return CommandParameter.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String description() {
                return description;
            }
        };
    }
}
