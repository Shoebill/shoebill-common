package net.gtaun.shoebill.common.command

import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.entities.Player
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.Pair
import java.lang.reflect.Method
import java.util.*
import java.util.function.Function
import java.util.regex.Pattern

@SuppressWarnings("RedundantCast")
open class CommandGroup {

    private val commands: MutableMap<String, MutableCollection<CommandEntryInternal>> = mutableMapOf()
    private val groups: MutableSet<CommandGroup> = mutableSetOf()
    private val childGroups: MutableMap<String, CommandGroup> = mutableMapOf()

    var notFoundHandler: CommandNotFoundHandler? = null
    var usageMessageSupplier: UsageMessageSupplier? = PlayerCommandManager.DEFAULT_USAGE_MESSAGE_SUPPLIER

    fun registerCommands(vararg objects: Any) {
        objects.forEach {
            generateCommandEntries(it).forEach { entry -> registerCommand(entry) }
        }
    }

    @JvmOverloads
    fun registerCommand(command: String, paramTypes: Array<Class<*>>, handler: CommandHandler, commandParameters: Array<CommandParameter>,
                        helpMessage: String? = null, category: String? = null, priority: Short = 0, caseSensitive: Boolean = false,
                        beforeCheckers: List<CustomCommandHandler> = listOf(), customHandlers: List<CustomCommandHandler> = listOf()) {
        registerCommand(CommandEntryInternal(command, paramTypes, priority, helpMessage, caseSensitive, CommandEntryInternal.CommandHandlerInternal { player, params ->
            val paramQueue = LinkedList<Any>()
            Collections.addAll(paramQueue, *params)
            paramQueue.poll()
            handler.handle(player, paramQueue)
        }, category, beforeCheckers, customHandlers, commandParameters))
    }

    private fun registerCommand(entry: CommandEntryInternal) {
        var entries: MutableCollection<CommandEntryInternal>? = commands[entry.command]
        if (entries == null) {
            entries = ArrayList<CommandEntryInternal>()
            commands.put(entry.command, entries)
        }
        entries.add(entry)
    }

    fun registerGroup(group: CommandGroup) = groups.add(group)
    fun unregisterGroup(group: CommandGroup) = groups.remove(group)
    fun containsGroup(group: CommandGroup): Boolean = groups.contains(group)
    fun registerChildGroup(group: CommandGroup, childName: String) = childGroups.put(childName, group)

    fun unregisterChildGroup(group: CommandGroup) {
        val it = childGroups.entries.iterator()
        while (it.hasNext()) {
            if (it.next().value === group) it.remove()
        }
    }

    fun containsChildGroup(group: CommandGroup): Boolean {
        for (g in childGroups.entries) {
            if (g === group) return true
        }

        return false
    }

    fun processCommand(player: Player, commandText: String): Boolean = processCommand("", player, commandText)
    fun processCommand(player: Player, command: String, paramText: String): Boolean =
            processCommand("", player, command.trim { it <= ' ' }, paramText)

    protected fun processCommand(path: String, player: Player, commandText: String): Boolean {
        val splits = StringUtils.split(commandText, " ", 2)
        if (splits.size < 1) return false
        return processCommand(path, player, splits[0], if (splits.size == 2) splits[1] else "")
    }

    protected fun processCommand(path: String, player: Player, command: String, paramText: String): Boolean {
        if (paramText.trim { it <= ' ' }.length == 0) {
            val childGroup = getChildGroup(this, command)
            if (childGroup != null && childGroup.notFoundHandler != null) {
                if (childGroup.notFoundHandler!!.handle(player, this, command))
                    return true
            }
        }

        val commands = ArrayList<Pair<String, CommandEntryInternal>>()
        val matchedCmds = ArrayList<Pair<String, CommandEntryInternal>>()
        getCommandEntries(path, command, commands)
        Collections.sort(commands) { p1, p2 ->
            val weights = 1000
            val e1 = p1.right
            val e2 = p2.right
            e2.priority * weights + e2.paramTypes.size - (e1.priority * weights + e1.paramTypes.size)
        }

        val pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*")
        for (e in commands) {
            val entry = e.right
            for (checker in entry.beforeCheckers)
                if (!checker.handle(player, command, paramText)) return true

            for (handler in entry.customHandlers)
                if (handler.handle(player, command, paramText)) return true

            val types = entry.paramTypes
            val matches = ArrayList<String>()
            val m = pattern.matcher(paramText) // strings with spaces can be made like this: "my string"
            while (m.find()) matches.add(m.group(1).replace("\"", ""))
            if (types.size == matches.size || types.size > 0 && types[types.size - 1] == String::class.java) {
                if (types.size > 0 && types[types.size - 1] == String::class.java) {
                    val stringBuilder = StringBuilder()
                    val stringParser = TYPE_PARSER[String::class.java]
                    for (i in matches.indices.reversed()) {
                        if (i < types.size - 1)
                            break
                        else {
                            val result = stringParser?.apply(matches[i])
                            if (result != null) {
                                matches.removeAt(i)
                                stringBuilder.insert(0, " " + result as String)
                            } else
                                break
                        }
                    }
                    val finalString = stringBuilder.toString().trim { it <= ' ' }
                    if (finalString.length > 0)
                        matches.add(finalString)
                }
                try {
                    var params = parseParams(types, matches.toTypedArray())
                    params = ArrayUtils.add(params, 0, player)
                    if (entry.handle(player, params))
                        return true
                } catch (ignored: Throwable) {
                }

            }
        }

        matchedCmds.addAll(commands)

        val child = childGroups[command]
        if (child != null) {
            val result = child.processCommand(CommandEntryInternal.completePath(path, command), player, paramText)
            if (result) return true
        }

        for (childGroup in groups) {
            val result = childGroup.processCommand(path, player, command, paramText)
            if (result) return true
        }

        if (!matchedCmds.isEmpty()) {
            sendUsageMessages(player, "/", commands)
            return true
        }
        return false
    }

    protected fun getCommandEntries(entries: MutableList<CommandEntry>, curPath: String) {
        commands.entries.map { it.value }.forEach { entries.addAll(it.map { e -> CommandEntry(e, curPath) }) }
        childGroups.forEach { it.value.getCommandEntries(entries, (curPath + " " + it.key).trim { it <= ' ' }) }
    }

    protected fun getCommandEntries(entries: MutableList<CommandEntry>, curPath: String, path: String) {
        if (curPath.startsWith(path)) {
            commands.entries.map { it.value }.forEach { entries.addAll(it.map { e -> CommandEntry(e, curPath) }) }
        }

        childGroups.forEach { it.value.getCommandEntries(entries, (curPath + " " + it.key).trim { it <= ' ' }, path) }
    }

    private fun getCommandEntries(path: String, command: String, commandEntries: MutableList<Pair<String, CommandEntryInternal>>) {
        commands.entries.filter { it.key.equals(command, true) }
                .forEach {
                    it.value.filter { cmd ->
                        if (cmd.isCaseSensitive) cmd.command.contentEquals(command)
                        else cmd.command.equals(command, true)
                    }.forEach { cmd ->
                        commandEntries.add(ImmutablePair<String, CommandEntryInternal>(path, cmd))
                    }
                }
    }

    fun getCommands(): Collection<CommandEntry> {
        val commands = ArrayList<CommandEntry>()
        getAllCommands(this, commands, "")
        return commands
    }

    protected fun getMatchedCommands(commandText: String): List<Pair<String, CommandEntryInternal>> {
        val entries = ArrayList<Pair<String, CommandEntryInternal>>()
        getMatchedCommands("", entries, commandText)
        return entries
    }

    private fun getMatchedCommands(path: String, matchedCmds: MutableList<Pair<String, CommandEntryInternal>>, commandText: String?) {
        var modifiedCommandText = commandText
        val splits = StringUtils.split(modifiedCommandText, " ", 2)

        if (splits.size == 0) return

        val command = splits[0]
        modifiedCommandText = if (splits.size > 1) splits[1] else null

        val commands = ArrayList<Pair<String, CommandEntryInternal>>()
        getCommandEntries(path, command, commands)
        Collections.sort(commands) { p1, p2 ->
            val weights = 1000
            val e1 = p1.right
            val e2 = p2.right
            e2.priority * weights + e2.paramTypes.size - (e1.priority * weights + e1.paramTypes.size)
        }

        matchedCmds.addAll(commands)

        if (modifiedCommandText != null) {
            val child = childGroups[command] ?: return
            child.getMatchedCommands(CommandEntryInternal.completePath(path, command), matchedCmds, modifiedCommandText)
        }
    }

    @JvmOverloads fun getUsageMessage(player: Player, commandText: String, prefix: String = "/"): String {
        var message = ""
        val it = getMatchedCommands(commandText).iterator()
        while (it.hasNext()) {
            val e = it.next()
            message += getUsageMessage(player, e.left, prefix, e.right)
            if (it.hasNext()) message += "\n"
        }
        return message
    }

    private fun getUsageMessage(player: Player, path: String, prefix: String, entry: CommandEntryInternal): String? {
        return usageMessageSupplier?.get(player, prefix, CommandEntry(entry, path))
    }

    @JvmOverloads
    fun sendUsageMessage(player: Player, commandText: String, prefix: String = "/") {
        sendUsageMessages(player, prefix, getMatchedCommands(commandText))
    }

    private fun sendUsageMessages(player: Player, prefix: String, commands: List<Pair<String, CommandEntryInternal>>) {
        if (this.usageMessageSupplier == null) return
        for (e in commands) {
            val usageMessage = getUsageMessage(player, e.left, prefix, e.right)
            if (usageMessage != null) player.sendMessage(Color.RED, usageMessage)
        }
    }

    companion object {
        private val TYPE_PARSER = HashMap<Class<*>, Function<String, Any>>()

        init {

            TYPE_PARSER.put(Integer.TYPE, Function<String, Any> { Integer.parseInt(it) })
            TYPE_PARSER.put(Int::class.java, Function<String, Any> { Integer.parseInt(it) })

            TYPE_PARSER.put(String::class.java, Function<String, Any> { it })

            TYPE_PARSER.put(java.lang.Short.TYPE, Function<String, Any> { java.lang.Short.parseShort(it) })
            TYPE_PARSER.put(Short::class.java, Function<String, Any> { java.lang.Short.parseShort(it) })

            TYPE_PARSER.put(java.lang.Byte.TYPE, Function<String, Any> { java.lang.Byte.parseByte(it) })
            TYPE_PARSER.put(Byte::class.java, Function<String, Any> { java.lang.Byte.parseByte(it) })

            TYPE_PARSER.put(Character.TYPE, Function<String, Any> { s -> if (s.length > 0) s[0] else 0 })
            TYPE_PARSER.put(Char::class.java, Function<String, Any> { s -> if (s.length > 0) s[0] else 0 })

            TYPE_PARSER.put(java.lang.Float.TYPE, Function<String, Any> { java.lang.Float.parseFloat(it) })
            TYPE_PARSER.put(Float::class.java, Function<String, Any> { java.lang.Float.parseFloat(it) })

            TYPE_PARSER.put(java.lang.Double.TYPE, Function<String, Any> { java.lang.Double.parseDouble(it) })
            TYPE_PARSER.put(Double::class.java, Function<String, Any> { java.lang.Double.parseDouble(it) })

            TYPE_PARSER.put(Boolean::class.java, Function<String, Any> { java.lang.Boolean.parseBoolean(it) })

            TYPE_PARSER.put(Player::class.java, Function<String, Any> { Player.getByNameOrId(it) })
            TYPE_PARSER.put(Color::class.java, Function<String, Any> { s -> Color(Integer.parseUnsignedInt(s, 16)) })
        }

        private fun generateCommandEntries(`object`: Any): Collection<CommandEntryInternal> {
            val entries = ArrayList<CommandEntryInternal>()

            val beforeCheckers = ArrayList<CustomCommandHandler>()
            val customHandlers = ArrayList<CustomCommandHandler>()

            beforeCheckers.addAll(generateBeforeCheckers(`object`))
            customHandlers.addAll(generateCustomHandlers(`object`))

            val clz = `object`.javaClass
            clz.methods.forEach {
                val command = it.getAnnotation(Command::class.java) ?: return@forEach
                if (it.returnType != Boolean::class.java) return@forEach

                val methodParams = it.parameters
                if (methodParams.size < 1) return@forEach
                if (methodParams[0].type != Player::class.java) return@forEach

                var name = it.name

                val paramTypes: MutableList<Class<*>> = mutableListOf()
                val commandParameters: MutableList<CommandParameter> = mutableListOf()

                (1..methodParams.size - 1).forEach { index ->
                    paramTypes.add(index - 1, methodParams[index].type)
                    methodParams[index].annotations?.forEach {
                        if (it.annotationClass == CommandParameter::class) {
                            commandParameters.add(index - 1, it as CommandParameter)
                        }
                    }
                    if (commandParameters.getOrNull(index - 1) == null)
                        commandParameters.add(index - 1, Utils.makeCommandParameterAnnotation(methodParams[index].name))
                }

                if (!StringUtils.isBlank(command.name)) name = command.name
                val priority = command.priority

                var helpMessage: String? = null
                var category: String? = null
                val help = it.getAnnotation(CommandHelp::class.java)
                if (help != null) {
                    helpMessage = help.value
                    category = help.category
                }

                entries.add(CommandEntryInternal(name, paramTypes.toTypedArray(), priority, helpMessage, command.caseSensitive,
                        CommandEntryInternal.CommandHandlerInternal { player, params ->
                            try {
                                return@CommandHandlerInternal it.invoke(`object`, *params) as Boolean
                            } catch (e: Throwable) {
                                e.printStackTrace()
                                return@CommandHandlerInternal false
                            }
                        }, category, beforeCheckers, customHandlers, commandParameters.toTypedArray()))
            }

            return entries
        }

        private fun generateBeforeCheckers(`object`: Any): List<CustomCommandHandler> {
            val checkers = ArrayList<CustomCommandHandler>()

            val clz = `object`.javaClass
            Arrays.stream(clz.methods).forEach { m ->
                val methodParams = m.parameters
                if (m.returnType != Boolean::class.java) return@forEach
                if (methodParams.size != 3) return@forEach
                if (methodParams[0].type != Player::class.java) return@forEach
                if (methodParams[1].type != String::class.java) return@forEach
                if (methodParams[2].type != String::class.java) return@forEach

                m.getAnnotation(BeforeCheck::class.java) ?: return@forEach

                checkers.add(makeCustomCommandHandler(`object`, m))
            }

            return checkers
        }

        private fun generateCustomHandlers(`object`: Any): List<CustomCommandHandler> {
            val checkers = ArrayList<CustomCommandHandler>()

            val clz = `object`.javaClass
            Arrays.stream(clz.methods).forEach { m ->
                val methodParams = m.parameters
                if (m.returnType != Boolean::class.java) return@forEach
                if (methodParams.size != 3) return@forEach
                if (methodParams[0].type != Player::class.java) return@forEach
                if (methodParams[1].type != String::class.java) return@forEach
                if (methodParams[2].type != String::class.java) return@forEach

                m.getAnnotation(CustomCommand::class.java) ?: return@forEach

                checkers.add(makeCustomCommandHandler(`object`, m))
            }

            return checkers
        }

        private fun makeCustomCommandHandler(`object`: Any, m: Method): CustomCommandHandler {
            return CustomCommandHandler { p, cmd, params ->
                try {
                    return@CustomCommandHandler m.invoke(`object`, p, cmd, params) as Boolean
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@CustomCommandHandler false
                }
            }
        }

        @Throws(NumberFormatException::class)
        private fun parseParams(types: Array<Class<*>>, params: Array<String>): Array<Any> =
                types.mapIndexed { index, clazz -> parseParam(clazz, params[index]) }.filterNotNull().toTypedArray()

        @Throws(NumberFormatException::class)
        private fun parseParam(type: Class<*>, param: String): Any? = TYPE_PARSER[type]?.apply(param)

        fun replaceTypeParser(type: Class<*>, function: Function<String, Any>) = TYPE_PARSER.put(type, function)
        fun getTypeParser(type: Class<*>): Function<String, Any>? = TYPE_PARSER[type]

        val typeParsers: Map<Class<*>, Function<String, Any>>
            get() = HashMap(TYPE_PARSER)

        private fun getChildGroup(commandGroup: CommandGroup, name: String): CommandGroup? {
            for ((key, value) in commandGroup.childGroups) {
                if (key.equals(name, ignoreCase = true)) {
                    return value
                } else {
                    val other = getChildGroup(value, name)
                    if (other != null)
                        return other
                }
            }
            return null
        }

        private fun getAllCommands(commandGroup: CommandGroup, commands: MutableCollection<CommandEntry>, path: String) {
            commandGroup.commands.entries.forEach { stringCollectionEntry -> stringCollectionEntry.value.forEach { commandEntryInternal -> commands.add(CommandEntry(commandEntryInternal, path)) } }
            commandGroup.groups.forEach { commandGroup1 -> getAllCommands(commandGroup1, commands, "") }
            commandGroup.childGroups.entries.forEach { stringCommandGroupEntry -> getAllCommands(stringCommandGroupEntry.value, commands, stringCommandGroupEntry.key) }
        }
    }

}