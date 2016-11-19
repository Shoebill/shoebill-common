package net.gtaun.shoebill.common.command

import net.gtaun.shoebill.entities.Player

@FunctionalInterface
interface CustomCommandHandler {
    fun handle(p: Player, cmd: String, params: String): Boolean
}

fun CustomCommandHandler(handler: (Player, String, String) -> Boolean) = object : CustomCommandHandler {
    override fun handle(p: Player, cmd: String, params: String): Boolean {
        return handler(p, cmd, params)
    }
}