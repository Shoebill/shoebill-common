package net.gtaun.shoebill.common.command

import net.gtaun.shoebill.entities.Player

/**
 * Created by marvin on 16.10.16 in project shoebill-common.
 * Copyright (c) 2016 Marvin Haschker. All rights reserved.
 */
@FunctionalInterface
interface UsageMessageSupplier {
    operator fun get(player: Player, prefix: String, command: CommandEntry): String
}

fun UsageMessageSupplier(handler: (Player, String, CommandEntry) -> String) = object : UsageMessageSupplier {
    override fun get(player: Player, prefix: String, command: CommandEntry): String {
        return handler(player, prefix, command)
    }
}

@FunctionalInterface
interface HelpMessageSupplier {
    operator fun get(player: Player, command: String, message: String): String
}

fun HelpMessageSupplier(handler: (Player, String, String) -> String) = object : HelpMessageSupplier {
    override fun get(player: Player, command: String, message: String): String {
        return handler(player, command, message)
    }
}