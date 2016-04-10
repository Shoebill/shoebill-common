package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.gtaun.shoebill.common.command.PlayerCommandManager.DEFAULT_USAGE_MESSAGE_SUPPLIER;

@SuppressWarnings("RedundantCast")
public class CommandGroup {
    private static final Map<Class<?>, Function<String, Object>> TYPE_PARSER = new HashMap<>();

    static {

        TYPE_PARSER.put(int.class, (s) -> Integer.parseInt(s));
        TYPE_PARSER.put(Integer.class, (s) -> Integer.parseInt(s));

        TYPE_PARSER.put(String.class, (s) -> s);

        TYPE_PARSER.put(short.class, (s) -> Short.parseShort(s));
        TYPE_PARSER.put(Short.class, (s) -> Short.parseShort(s));

        TYPE_PARSER.put(byte.class, (s) -> Byte.parseByte(s));
        TYPE_PARSER.put(Byte.class, (s) -> Byte.parseByte(s));

        TYPE_PARSER.put(char.class, (s) -> s.length() > 0 ? s.charAt(0) : 0);
        TYPE_PARSER.put(Character.class, (s) -> s.length() > 0 ? s.charAt(0) : 0);

        TYPE_PARSER.put(float.class, (s) -> Float.parseFloat(s));
        TYPE_PARSER.put(Float.class, (s) -> Float.parseFloat(s));

        TYPE_PARSER.put(double.class, (s) -> Double.parseDouble(s));
        TYPE_PARSER.put(Double.class, (s) -> Double.parseDouble(s));

        TYPE_PARSER.put(Boolean.class, (s) -> Boolean.parseBoolean(s));

        TYPE_PARSER.put(Player.class, (s) -> Player.getByNameOrId(s));
        TYPE_PARSER.put(Color.class, (s) -> new Color(Integer.parseUnsignedInt(s, 16)));
    }

    private Map<String, Collection<CommandEntryInternal>> commands;
    private Set<CommandGroup> groups;
    private Map<String, CommandGroup> childGroups;

    private CommandNotFoundHandler notFoundHandler;
    private PlayerCommandManager.UsageMessageSupplier usageMessageSupplier;

    public CommandGroup() {
        commands = new HashMap<>();
        usageMessageSupplier = DEFAULT_USAGE_MESSAGE_SUPPLIER;

        groups = new HashSet<>();
        childGroups = new HashMap<>();
    }

    private static Collection<CommandEntryInternal> generateCommandEntries(Object object) {
        List<CommandEntryInternal> entries = new ArrayList<>();

        List<CustomCommandHandler> beforeCheckers = new ArrayList<>();
        List<CustomCommandHandler> customHandlers = new ArrayList<>();

        beforeCheckers.addAll(generateBeforeCheckers(object));
        customHandlers.addAll(generateCustomHandlers(object));

        Class<?> clz = object.getClass();
        Arrays.stream(clz.getMethods()).forEach((m) ->
        {
            Command command = m.getAnnotation(Command.class);
            if (command == null) return;
            if (m.getReturnType() != boolean.class) return;

            Parameter[] methodParams = m.getParameters();
            if (methodParams.length < 1) return;
            if (methodParams[0].getType() != Player.class) return;

            String name = m.getName();

            Class<?>[] paramTypes = new Class<?>[methodParams.length - 1];
            CommandParameter[] commandParameters = new CommandParameter[methodParams.length - 1];

            for (int i = 1; i < methodParams.length; i++) {
                paramTypes[i - 1] = methodParams[i].getType();
                if (methodParams[i].getAnnotations() != null) {
                    Annotation[] annotations = methodParams[i].getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType() == CommandParameter.class) {
                            commandParameters[i - 1] = (CommandParameter) annotation;
                            break;
                        }
                    }
                }
                if (commandParameters[i - 1] == null)
                    commandParameters[i - 1] = makeCommandParameterAnnotation(methodParams[i].getName());
            }

            if (!StringUtils.isBlank(command.name())) name = command.name();
            short priority = command.priority();

            String helpMessage = null, category = null;
            CommandHelp help = m.getAnnotation(CommandHelp.class);
            if (help != null) {
                helpMessage = help.value();
                category = help.category();
            }

            entries.add(new CommandEntryInternal(name, paramTypes, priority, helpMessage, command.caseSensitive(), (player, params) ->
            {
                try {
                    return (boolean) m.invoke(object, params);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                return false;
            }, category, beforeCheckers, customHandlers, commandParameters, clz));
        });

        return entries;
    }

    private static List<CustomCommandHandler> generateBeforeCheckers(Object object) {
        List<CustomCommandHandler> checkers = new ArrayList<>();

        Class<?> clz = object.getClass();
        Arrays.stream(clz.getMethods()).forEach((m) ->
        {
            Parameter[] methodParams = m.getParameters();
            if (m.getReturnType() != boolean.class) return;
            if (methodParams.length != 3) return;
            if (methodParams[0].getType() != Player.class) return;
            if (methodParams[1].getType() != String.class) return;
            if (methodParams[2].getType() != String.class) return;

            BeforeCheck annotation = m.getAnnotation(BeforeCheck.class);
            if (annotation == null) return;

            checkers.add(makeCustomCommandHandler(object, m));
        });

        return checkers;
    }

    private static List<CustomCommandHandler> generateCustomHandlers(Object object) {
        List<CustomCommandHandler> checkers = new ArrayList<>();

        Class<?> clz = object.getClass();
        Arrays.stream(clz.getMethods()).forEach((m) ->
        {
            Parameter[] methodParams = m.getParameters();
            if (m.getReturnType() != boolean.class) return;
            if (methodParams.length != 3) return;
            if (methodParams[0].getType() != Player.class) return;
            if (methodParams[1].getType() != String.class) return;
            if (methodParams[2].getType() != String.class) return;

            CustomCommand annotation = m.getAnnotation(CustomCommand.class);
            if (annotation == null) return;

            checkers.add(makeCustomCommandHandler(object, m));
        });

        return checkers;
    }

    private static CustomCommandHandler makeCustomCommandHandler(Object object, Method m) {
        return (p, cmd, params) ->
        {
            try {
                return (boolean) m.invoke(object, p, cmd, params);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        };
    }

    private static Object[] parseParams(Class<?>[] types, String[] paramStrs) throws NumberFormatException {
        Object[] params = new Object[types.length];
        for (int i = 0; i < types.length; i++) params[i] = parseParam(types[i], paramStrs[i]);
        return params;
    }

    private static Object parseParam(Class<?> type, String param) throws NumberFormatException {
        Function<String, Object> func = TYPE_PARSER.get(type);
        if (func == null) return null;
        return func.apply(param);
    }

    public static void replaceTypeParser(Class<?> type, Function<String, Object> function) {
        TYPE_PARSER.put(type, function);
    }

    public static Function<String, Object> getTypeParser(Class<?> type) {
        return TYPE_PARSER.getOrDefault(type, null);
    }

    public static Map<Class<?>, Function<String, Object>> getTypeParsers() {
        return new HashMap<>(TYPE_PARSER);
    }

    private static CommandParameter makeCommandParameterAnnotation(String name) {
        return makeCommandParameterAnnotation(name, null);
    }

    private static CommandParameter makeCommandParameterAnnotation(String name, String description) {
        return new CommandParameter() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return CommandParameter.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String description() {
                return description;
            }
        };
    }

    private static CommandGroup getChildGroup(CommandGroup commandGroup, String name) {
        for (Map.Entry<String, CommandGroup> children : commandGroup.childGroups.entrySet()) {
            if (children.getKey().equalsIgnoreCase(name)) {
                return children.getValue();
            } else {
                CommandGroup other = getChildGroup(children.getValue(), name);
                if (other != null)
                    return other;
            }
        }
        return null;
    }

    public void registerCommands(Object... objects) {
        for(Object object : objects) {
            generateCommandEntries(object).forEach(this::registerCommand);
        }
    }

    public void registerCommand(String command, Class<?>[] paramTypes, CommandHandler handler,
                                List<CustomCommandHandler> beforeCheckers, List<CustomCommandHandler> customHandlers,
                                CommandParameter[] commandParameters, Class<?> origin) {
        registerCommand(command, paramTypes, null, null, (short) 0, true, handler, beforeCheckers, customHandlers, commandParameters, origin);
    }

    public void registerCommand(String command, Class<?>[] paramTypes, String helpMessage, String categorie, CommandHandler handler,
                                List<CustomCommandHandler> beforeCheckers, List<CustomCommandHandler> customHandlers,
                                CommandParameter[] commandParameters, Class<?> origin) {
        registerCommand(command, paramTypes, helpMessage, categorie, (short) 0, true, handler, beforeCheckers, customHandlers, commandParameters, origin);
    }

    public void registerCommand(String command, Class<?>[] paramTypes, String helpMessage, String categorie,
                                short priority, boolean caseSensitive, CommandHandler handler,
                                List<CustomCommandHandler> beforeCheckers, List<CustomCommandHandler> customHandlers,
                                CommandParameter[] commandParameters, Class<?> origin) {
        registerCommand(new CommandEntryInternal(command, paramTypes, priority, helpMessage, caseSensitive, (player, params) ->
        {
            Queue<Object> paramQueue = new LinkedList<>();
            Collections.addAll(paramQueue, params);
            paramQueue.poll();
            return handler.handle(player, paramQueue);
        }, categorie, beforeCheckers, customHandlers, commandParameters, origin));
    }

    private void registerCommand(CommandEntryInternal entry) {
        Collection<CommandEntryInternal> entries = commands.get(entry.getCommand());
        if (entries == null) {
            entries = new ArrayList<>();
            commands.put(entry.getCommand(), entries);
        }
        entries.add(entry);
    }

    public void registerGroup(CommandGroup group) {
        groups.add(group);
    }

    public void unregisterGroup(CommandGroup group) {
        groups.remove(group);
    }

    public boolean containsGroup(CommandGroup group) {
        return groups.contains(group);
    }

    public void registerChildGroup(CommandGroup group, String childName) {
        childGroups.put(childName, group);
    }

    public void unregisterChildGroup(CommandGroup group) {
        for (Iterator<Map.Entry<String, CommandGroup>> it = childGroups.entrySet().iterator(); it.hasNext(); ) {
            if (it.next().getValue() == group) it.remove();
        }
    }

    public boolean containsChildGroup(CommandGroup group) {
        for (Entry<String, CommandGroup> g : childGroups.entrySet()) {
            if (g == group) return true;
        }

        return false;
    }

    public boolean processCommand(Player player, String commandText) {
        return processCommand("", player, commandText);
    }

    public boolean processCommand(Player player, String command, String paramText) {
        return processCommand("", player, command.trim(), paramText);
    }

    protected boolean processCommand(String path, Player player, String commandText) {
        String[] splits = StringUtils.split(commandText, " ", 2);
        if (splits.length < 1) return false;
        return processCommand(path, player, splits[0], splits.length == 2 ? splits[1] : "");
    }

    protected boolean processCommand(String path, Player player, String command, String paramText) {
        if (paramText.trim().length() == 0) {
            CommandGroup childGroup = getChildGroup(this, command);
            if (childGroup != null && childGroup.getNotFoundHandler() != null) {
                if (childGroup.getNotFoundHandler().handle(player, this, createEmptyCommand()))
                    return true;
            }
        }

        List<Pair<String, CommandEntryInternal>> commands = new ArrayList<>();
        List<Pair<String, CommandEntryInternal>> matchedCmds = new ArrayList<>();
        getCommandEntries(path, command, commands);
        Collections.sort(commands, (p1, p2) ->
        {
            final int weights = 1000;
            CommandEntryInternal e1 = p1.getRight(), e2 = p2.getRight();
            return (e2.getPriority() * weights + e2.getParamTypes().length) - (e1.getPriority() * weights + e1.getParamTypes().length);
        });

        Pattern pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
        for (Pair<String, CommandEntryInternal> e : commands) {
            CommandEntryInternal entry = e.getRight();

            if (entry.getBeforeCheckers() != null) {
                for (CustomCommandHandler checker : entry.getBeforeCheckers())
                    if (!checker.handle(player, command, paramText)) return true;
            }
            if (entry.getCustomHandlers() != null) {
                for (CustomCommandHandler handler : entry.getCustomHandlers())
                    if (handler.handle(player, command, paramText)) return true;
            }

            Class<?>[] types = entry.getParamTypes();
            List<String> matches = new ArrayList<>();
            Matcher m = pattern.matcher(paramText); // strings with spaces can be made like this: "my string"
            while (m.find()) matches.add(m.group(1).replace("\"", ""));
            if (types.length == matches.size() || ((types.length > 0) && types[types.length - 1] == String.class)) {
                if (types.length > 0 && types[types.length - 1] == String.class) {
                    StringBuilder stringBuilder = new StringBuilder();
                    Function<String, Object> stringParser = TYPE_PARSER.get(String.class);
                    for (int i = matches.size() - 1; i >= 0; i--) {
                        if (i < types.length - 1) break;
                        else {
                            Object result = stringParser.apply(matches.get(i));
                            if (result != null) {
                                matches.remove(i);
                                stringBuilder.insert(0, " " + (String) result);
                            } else break;
                        }
                    }
                    final String finalString = stringBuilder.toString().trim();
                    if (finalString.length() > 0) //Don't allow empty strings
                        matches.add(finalString);
                }
                try {
                    Object[] params = parseParams(types, matches.toArray(new String[matches.size()]));
                    params = ArrayUtils.add(params, 0, player);
                    if (entry.handle(player, params))
                        return true;
                } catch (Throwable ignored) {}
            }
        }

        matchedCmds.addAll(commands);

        CommandGroup child = childGroups.get(command);
        if (child != null) {
            boolean result = child.processCommand(CommandEntryInternal.completePath(path, command), player, paramText);
            if (result) return true;
        }

        for (CommandGroup childGroup : groups) {
            boolean result = childGroup.processCommand(path, player, command, paramText);
            if (result) return true;
        }

        if (!matchedCmds.isEmpty()) {
            sendUsageMessages(player, "/", commands);
            return true;
        }
        return false;
    }

    private Command createEmptyCommand() {
        return new Command() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Command.class;
            }

            @Override
            public String name() {
                return null;
            }

            @Override
            public short priority() {
                return 0;
            }

            @Override
            public boolean caseSensitive() {
                return false;
            }
        };
    }

    protected void getCommandEntries(List<CommandEntry> entries, String curPath) {
        commands.entrySet().stream().map(Entry::getValue).forEach((commands) ->
        {
            entries.addAll(commands.stream().map((e) -> new CommandEntry(e, curPath)).collect(Collectors.toList()));
        });

        for (Entry<String, CommandGroup> e : childGroups.entrySet())
            e.getValue().getCommandEntries(entries, (curPath + " " + e.getKey()).trim());
    }

    protected void getCommandEntries(List<CommandEntry> entries, String curPath, String path) {
        if (curPath.startsWith(path)) {
            commands.entrySet().stream().map(Entry::getValue).forEach((commands) ->
            {
                entries.addAll(commands.stream().map((e) -> new CommandEntry(e, curPath)).collect(Collectors.toList()));
            });
        }

        for (Entry<String, CommandGroup> e : childGroups.entrySet())
            e.getValue().getCommandEntries(entries, (curPath + " " + e.getKey()).trim(), path);
    }

    private void getCommandEntries(String path, String command, List<Pair<String, CommandEntryInternal>> commandEntries) {
        commands.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(command))
                .forEach(entry -> entry.getValue().stream().filter(cmd -> (cmd.isCaseSensitive()) ? cmd.getCommand().contentEquals(command) : cmd.getCommand().equalsIgnoreCase(command))
                        .forEach(cmd -> commandEntries.add(new ImmutablePair<>(path, cmd))));
    }

    protected Collection<CommandEntry> getCommands() {
        Collection<CommandEntry> commands = new ArrayList<>();
        getAllCommands(this, commands, "");
        return commands;
    }

    private static void getAllCommands(CommandGroup commandGroup, Collection<CommandEntry> commands, String path) {
        commandGroup.commands.entrySet().stream().forEach(stringCollectionEntry -> {
            stringCollectionEntry.getValue().stream().forEach(commandEntryInternal -> {
                commands.add(new CommandEntry(commandEntryInternal, ((path.length() == 0) ? ("") : (path + " ")) + stringCollectionEntry.getKey()));
            });
        });
        commandGroup.groups.stream().forEach(commandGroup1 -> getAllCommands(commandGroup1, commands, ""));
        commandGroup.childGroups.entrySet().stream()
                .forEach(stringCommandGroupEntry -> getAllCommands(stringCommandGroupEntry.getValue(), commands, stringCommandGroupEntry.getKey()));
    }

    protected List<Pair<String, CommandEntryInternal>> getMatchedCommands(String commandText) {
        List<Pair<String, CommandEntryInternal>> entries = new ArrayList<>();
        getMatchedCommands("", entries, commandText);
        return entries;
    }

    private void getMatchedCommands(String path, List<Pair<String, CommandEntryInternal>> matchedCmds, String commandText) {
        String[] splits = StringUtils.split(commandText, " ", 2);

        if (splits.length == 0) return;

        String command = splits[0];
        commandText = splits.length > 1 ? splits[1] : null;

        List<Pair<String, CommandEntryInternal>> commands = new ArrayList<>();
        getCommandEntries(path, command, commands);
        Collections.sort(commands, (p1, p2) ->
        {
            final int weights = 1000;
            CommandEntryInternal e1 = p1.getRight(), e2 = p2.getRight();
            return (e2.getPriority() * weights + e2.getParamTypes().length) - (e1.getPriority() * weights + e1.getParamTypes().length);
        });

        matchedCmds.addAll(commands);

        if (commandText != null) {
            CommandGroup child = childGroups.get(command);
            if (child == null) return;
            child.getMatchedCommands(CommandEntryInternal.completePath(path, command), matchedCmds, commandText);
        }
    }

    public CommandNotFoundHandler getNotFoundHandler() {
        return notFoundHandler;
    }

    public void setNotFoundHandler(CommandNotFoundHandler notFoundHandler) {
        this.notFoundHandler = notFoundHandler;
    }

    public String getUsageMessage(Player player, String commandText) {
        return getUsageMessage(player, commandText, "/");
    }

    public String getUsageMessage(Player player, String commandText, String prefix) {
        String message = "";
        for (Iterator<Pair<String, CommandEntryInternal>> it = getMatchedCommands(commandText).iterator(); it.hasNext(); ) {
            Pair<String, CommandEntryInternal> e = it.next();
            message += getUsageMessage(player, e.getLeft(), prefix, e.getRight());
            if (it.hasNext()) message += "\n";
        }
        return message;
    }

    private String getUsageMessage(Player player, String path, String prefix, CommandEntryInternal entry) {
        return usageMessageSupplier.get(player, prefix, new CommandEntry(entry, path));
    }

    public void sendUsageMessage(Player player, String commandText) {
        sendUsageMessage(player, commandText, "/");
    }

    public void sendUsageMessage(Player player, String commandText, String prefix) {
        sendUsageMessages(player, prefix, getMatchedCommands(commandText));
    }

    private void sendUsageMessages(Player player, String prefix, List<Pair<String, CommandEntryInternal>> commands) {
        if (this.usageMessageSupplier == null) return;
        for (Pair<String, CommandEntryInternal> e : commands) {
            String usageMessage = getUsageMessage(player, e.getLeft(), prefix, e.getRight());
            if (usageMessage != null) player.sendMessage(Color.RED, usageMessage);
        }
    }

    public void setUsageMessageSupplier(PlayerCommandManager.UsageMessageSupplier usageMessageSupplier) {
        this.usageMessageSupplier = usageMessageSupplier;
    }

}
