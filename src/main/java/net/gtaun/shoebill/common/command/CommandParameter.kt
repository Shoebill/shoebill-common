package net.gtaun.shoebill.common.command

/**
 * Created by marvin on 15.02.16.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
@Retention
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class CommandParameter(val name: String, val description: String = "")
