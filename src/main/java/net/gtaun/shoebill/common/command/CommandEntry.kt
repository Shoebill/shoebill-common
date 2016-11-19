package net.gtaun.shoebill.common.command

import net.gtaun.shoebill.common.command.CommandEntryInternal.CommandHandlerInternal
import net.gtaun.shoebill.entities.Player

class CommandEntry(val entry: CommandEntryInternal, val path: String) {

    val command: String
        get() = entry.completeCommand(path)

    val parameterTypes: Array<Class<*>>
        get() = entry.paramTypes

    val priority: Short
        get() = entry.priority

    val helpMessage: String?
        get() = entry.helpMessage

    val isCaseSensitive: Boolean
        get() = entry.isCaseSensitive

    val handler: CommandHandlerInternal
        get() = entry.handler

    fun handle(player: Player, params: Array<Any>): Boolean = entry.handle(player, params)

    val category: String?
        get() = entry.category

    val parameters: Array<CommandParameter>
        get() = entry.parameterAnnotations
}
