package net.gtaun.shoebill.common.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Command
{
	String name() default "";
	short priority() default 0;
	boolean caseSensitive() default false;
}
