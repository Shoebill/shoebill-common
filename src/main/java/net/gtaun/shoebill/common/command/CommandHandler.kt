package net.gtaun.shoebill.common.command

import net.gtaun.shoebill.entities.Player

import java.util.Queue

@FunctionalInterface
interface CommandHandler {
    fun handle(p: Player, params: Queue<Any>): Boolean
}

fun CommandHandler(handler: (Player, Queue<Any>) -> Boolean) = object : CommandHandler {
    override fun handle(p: Player, params: Queue<Any>): Boolean {
        return handler(p, params)
    }
}