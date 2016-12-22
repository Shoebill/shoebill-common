package net.gtaun.shoebill.common.command

import net.gtaun.shoebill.event.player.PlayerCommandEvent
import net.gtaun.shoebill.event.player.PlayerTextEvent
import net.gtaun.shoebill.entities.Destroyable
import net.gtaun.shoebill.entities.Player
import net.gtaun.util.event.EventManager
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerEntry
import net.gtaun.util.event.HandlerPriority

import java.util.ArrayList

class PlayerCommandManager(eventManager: EventManager) : CommandGroup(), Destroyable {

    var helpMessageSupplier = DEFAULT_HELP_MESSAGE_SUPPLIER

    private val eventManagerNode: EventManagerNode by lazy {
        eventManager.createChildNode()
    }

    override fun destroy() = eventManagerNode.destroy()

    override val isDestroyed: Boolean
        get() = eventManagerNode.isDestroyed

    fun installCommandHandler(priority: HandlerPriority): HandlerEntry? =
            eventManagerNode.registerHandler(PlayerCommandEvent::class, { e ->
                if (processCommand(e.player, e.command.substring(1)))
                    e.setProcessed()
            }, priority)

    fun installTextHandler(priority: HandlerPriority, prefix: String): HandlerEntry? =
            eventManagerNode.registerHandler(PlayerTextEvent::class, { e ->
                val text = e.text
                if (!text.startsWith(prefix)) return@registerHandler
                if (processCommand(e.player, text.substring(prefix.length), prefix)) {
                    e.disallow()
                    e.interrupt()
                }
            }, priority)


    fun uninstallAllHandlers() = eventManagerNode.cancelAll()

    val commandEntries: List<CommandEntry>
        get() {
            val entries = ArrayList<CommandEntry>()
            getCommandEntries(entries, "")
            return entries
        }

    fun getCommandEntries(path: String): List<CommandEntry> {
        val entries = ArrayList<CommandEntry>()
        getCommandEntries(entries, "", path)
        return entries
    }

    companion object {

        val DEFAULT_USAGE_MESSAGE_SUPPLIER = UsageMessageSupplier { player, prefix, command ->
            val stringBuilder = StringBuilder("Usage: " + prefix + command.command)
            if (command.parameters.isNotEmpty()) {
                for (i in 0..command.parameters.size - 1) {
                    stringBuilder.append(" [").append(command.parameters[i].name).append("]")
                }
            }
            if (command.helpMessage != null)
                stringBuilder.append(" - ").append(command.helpMessage)
            stringBuilder.toString()
        }

        val DEFAULT_HELP_MESSAGE_SUPPLIER = HelpMessageSupplier { p, c, m -> m }
    }
}
