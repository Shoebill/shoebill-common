package net.gtaun.shoebill.common.command

import net.gtaun.shoebill.entities.Player

/**
 * Created by marvin on 19.03.16.
 * Copyright (c) 2016 Marvin Haschker. All rights reserved.
 */
interface CommandNotFoundHandler {
    fun handle(player: Player, commandGroup: CommandGroup, command: String): Boolean
}

fun CommandNotFoundHandler(handler: (Player, CommandGroup, String) -> Boolean) = object : CommandNotFoundHandler {
    override fun handle(player: Player, commandGroup: CommandGroup, command: String): Boolean {
        return handler(player, commandGroup, command)
    }
}