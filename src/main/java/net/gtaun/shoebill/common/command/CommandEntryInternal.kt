package net.gtaun.shoebill.common.command

import net.gtaun.shoebill.entities.Player
import org.apache.commons.lang3.StringUtils

class CommandEntryInternal internal constructor(val command: String, val paramTypes: Array<Class<*>>, val priority: Short,
                                                val helpMessage: String?, val isCaseSensitive: Boolean,
                                                val handler: CommandEntryInternal.CommandHandlerInternal,
                                                val category: String?, val beforeCheckers: List<CustomCommandHandler>,
                                                val customHandlers: List<CustomCommandHandler>,
                                                val parameterAnnotations: Array<CommandParameter> = arrayOf()) {

    @FunctionalInterface
    interface CommandHandlerInternal {
        fun handle(player: Player, params: Array<Any>): Boolean
    }

    fun completeCommand(path: String): String = completePath(path, command)

    fun handle(player: Player, params: Array<Any>): Boolean = handler.handle(player, params)

    companion object {
        @JvmStatic
        fun completePath(path: String, child: String): String =
                if (StringUtils.isBlank(path)) child else path + " " + child

        fun CommandHandlerInternal(handler: (Player, Array<Any>) -> Boolean) = object : CommandHandlerInternal {
            override fun handle(player: Player, params: Array<Any>): Boolean = handler(player, params)
        }
    }
}
