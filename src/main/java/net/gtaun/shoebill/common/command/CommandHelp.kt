package net.gtaun.shoebill.common.command

@Retention
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class CommandHelp(val value: String = "", val category: String = "")
