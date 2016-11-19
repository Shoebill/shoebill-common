package net.gtaun.shoebill.common.command


@Retention
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Command(val name: String = "", val priority: Short = 0, val caseSensitive: Boolean = false)
