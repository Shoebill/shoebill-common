package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("RedundantCast")
public class CommandGroup {
    private static Collection<CommandEntryInternal> generateCommandEntries(Object object) {
        List<CommandEntryInternal> entries = new ArrayList<>();

        Class<?> clz = object.getClass();
        Arrays.stream(clz.getMethods()).forEach((m) ->
        {
            String name = m.getName();
            Parameter[] methodParams = m.getParameters();
            if (m.getReturnType() != boolean.class) return;
            if (methodParams.length < 1) return;

            Command command = m.getAnnotation(Command.class);
            if (command == null) return;
            if (methodParams[0].getType() != Player.class) return;


            Class<?>[] paramTypes = new Class<?>[methodParams.length - 1];
            String[] paramNames = new String[paramTypes.length];

            for (int i = 1; i < methodParams.length; i++) {
                paramTypes[i - 1] = methodParams[i].getType();
                paramNames[i - 1] = methodParams[i].getName();
            }

            if (!StringUtils.isBlank(command.name())) name = command.name();
            short priority = command.priority();

            String helpMessage = null, categorie = null;
            CommandHelp help = m.getAnnotation(CommandHelp.class);
            if (help != null) {
                helpMessage = help.value();
                categorie = help.categorie();
            }

            entries.add(new CommandEntryInternal(name, paramTypes, paramNames, priority, helpMessage, command.caseSensitive(), (player, params) ->
            {
                try {
                    return (boolean) m.invoke(object, params);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                return false;
            }, categorie));
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

            checkers.add((p, cmd, params) ->
            {
                try {
                    return (boolean) m.invoke(object, p, cmd, params);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            });
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

            checkers.add((p, cmd, params) ->
            {
                try {
                    return (boolean) m.invoke(object, p, cmd, params);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            });
        });

        return checkers;
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

    private static final Map<Class<?>, Function<String, Object>> TYPE_PARSER = new HashMap<>();

    static {
        TYPE_PARSER.put(String.class, (s) -> s);

        TYPE_PARSER.put(int.class, (s) -> Integer.parseInt(s));
        TYPE_PARSER.put(Integer.class, (s) -> Integer.parseInt(s));

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

        TYPE_PARSER.put(Player.class, (s) -> Player.getByNameOrId(s));
        TYPE_PARSER.put(Color.class, (s) -> new Color(Integer.parseUnsignedInt(s, 16)));
    }


    private Map<String, Collection<CommandEntryInternal>> commands;
    private List<CustomCommandHandler> beforeCheckers;
    private List<CustomCommandHandler> customHandlers;

    private Set<CommandGroup> groups;
    private Map<String, CommandGroup> childGroups;


    public CommandGroup() {
        commands = new HashMap<>();
        beforeCheckers = new ArrayList<>();
        customHandlers = new ArrayList<>();

        groups = new HashSet<>();
        childGroups = new HashMap<>();
    }

    public void registerCommands(Object object) {
        generateCommandEntries(object).forEach((e) -> registerCommand(e));
        beforeCheckers.addAll(generateBeforeCheckers(object));
        customHandlers.addAll(generateCustomHandlers(object));
    }

    public void registerCommand(String command, Class<?>[] paramTypes, String[] paramNames, CommandHandler handler) {
        registerCommand(command, paramTypes, paramNames, null, null, (short) 0, true, handler);
    }

    public void registerCommand(String command, Class<?>[] paramTypes, String[] paramNames, String helpMessage, String categorie, CommandHandler handler) {
        registerCommand(command, paramTypes, paramNames, helpMessage, categorie, (short) 0, true, handler);
    }

    public void registerCommand(String command, Class<?>[] paramTypes, String[] paramNames, String helpMessage, String categorie,
                                short priority, boolean caseSensitive, CommandHandler handler) {
        registerCommand(new CommandEntryInternal(command, paramTypes, paramNames, priority, helpMessage, caseSensitive, (player, params) ->
        {
            Queue<Object> paramQueue = new LinkedList<>();
            Collections.addAll(paramQueue, params);
            paramQueue.poll();
            return handler.handle(player, paramQueue);
        }, categorie));
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
        return processCommand("", null, player, commandText);
    }

    public boolean processCommand(Player player, String command, String paramText) {
        return processCommand("", null, player, command.trim(), paramText);
    }

    protected boolean processCommand(String path, List<Pair<String, CommandEntryInternal>> matchedCmds, Player player, String commandText) {
        String[] splits = StringUtils.split(commandText, " ", 2);
        if (splits.length < 1) return false;
        return processCommand(path, matchedCmds, player, splits[0], splits.length == 2 ? splits[1] : "");
    }

    protected boolean processCommand(String path, List<Pair<String, CommandEntryInternal>> matchedCmds, Player player, String command, String paramText) {
        List<Pair<String, CommandEntryInternal>> commands = new ArrayList<>();
        getCommandEntries(path, command, commands);
        Collections.sort(commands, (p1, p2) ->
        {
            final int weights = 1000;
            CommandEntryInternal e1 = p1.getRight(), e2 = p2.getRight();
            return (e2.getPriority() * weights + e2.getParamTypes().length) - (e1.getPriority() * weights + e1.getParamTypes().length);
        });

        for (CustomCommandHandler checker : beforeCheckers)
            if (!checker.handle(player, command, paramText)) return false;
        for (CustomCommandHandler handler : customHandlers) if (handler.handle(player, command, paramText)) return true;

        for (Pair<String, CommandEntryInternal> e : commands) {
            CommandEntryInternal entry = e.getRight();
            Class<?>[] types = entry.getParamTypes();
            List<String> matches = new ArrayList<>();
            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(paramText); // strings with spaces can be made like this: "my string"
            while (m.find()) matches.add(m.group(1).replace("\"", ""));
            if (types.length == matches.size()) {
                try {
                    Object[] params = parseParams(types, matches.toArray(new String[matches.size()]));
                    params = ArrayUtils.add(params, 0, player);
                    if (entry.handle(player, params)) return true;
                } catch (Throwable ex) {

                }
            }
        }

        matchedCmds.addAll(commands);

        CommandGroup child = childGroups.get(command);
        if (child == null) return false;

        return child.processCommand(CommandEntryInternal.completePath(path, command), matchedCmds, player, paramText);
    }

    protected void getCommandEntries(List<CommandEntry> entries, String curPath) {
        commands.entrySet().stream().map((e) -> e.getValue()).forEach((commands) ->
        {
            entries.addAll(commands.stream().map((e) -> new CommandEntry(e, curPath)).collect(Collectors.toList()));
        });

        for (CommandGroup group : groups) group.getCommandEntries(entries, curPath);
        for (Entry<String, CommandGroup> e : childGroups.entrySet())
            e.getValue().getCommandEntries(entries, (curPath + " " + e.getKey()).trim());
    }

    protected void getCommandEntries(List<CommandEntry> entries, String curPath, String path) {
        if (curPath.startsWith(path)) {
            commands.entrySet().stream().map((e) -> e.getValue()).forEach((commands) ->
            {
                entries.addAll(commands.stream().map((e) -> new CommandEntry(e, curPath)).collect(Collectors.toList()));
            });

            for (CommandGroup group : groups) group.getCommandEntries(entries, curPath);
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
        for (CommandGroup group : groups) group.getCommandEntries(path, command, commandEntries);
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

    public void replaceTypeParser(Class<?> type, Function<String, Object> function) {
        TYPE_PARSER.put(type, function);
    }
}
