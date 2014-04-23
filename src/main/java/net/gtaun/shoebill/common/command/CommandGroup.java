package net.gtaun.shoebill.common.command;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CommandGroup
{
	private static Collection<CommandEntry> generateCommandEntries(Object object)
	{
		List<CommandEntry> entries = new ArrayList<>();
		
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

			Class<?>[] paramTypes = new Class<?>[methodParams.length-1];
			String[] paramNames = new String[paramTypes.length];

			for (int i=1; i<methodParams.length; i++)
			{
				paramTypes[i-1] = methodParams[i].getType();
				paramNames[i-1] = methodParams[i].getName();
			}

			short priority = command.priority();
			boolean strictMode = command.strictMode();
			
			entries.add(new CommandEntry(name, paramTypes, paramNames, priority, strictMode, (player, params) ->
			{
				try
				{
					return (boolean) m.invoke(object, params);
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
				
				return false;
			}));
		});
		
		return entries;
	}
	
	private static Object[] parseParams(Class<?>[] types, String[] paramStrs) throws NumberFormatException
	{
		Object[] params = new Object[types.length];
		for (int i=0; i<types.length; i++) params[i] = parseParam(types[i], paramStrs[i]);
		return params;
	}
	
	private static Object parseParam(Class<?> type, String param) throws NumberFormatException
	{
		Function<String, Object> func = TYPE_PARSER.get(type);
		if (func == null) return null;
		return func.apply(param);
	}
	
	private static final Map<Class<?>, Function<String, Object>> TYPE_PARSER = new HashMap<>();
	static
	{
		TYPE_PARSER.put(String.class,		(s) -> s);
		
		TYPE_PARSER.put(int.class,			(s) -> Integer.parseInt(s));
		TYPE_PARSER.put(Integer.class,		(s) -> Integer.parseInt(s));
		
		TYPE_PARSER.put(short.class,		(s) -> Short.parseShort(s));
		TYPE_PARSER.put(Short.class,		(s) -> Short.parseShort(s));
		
		TYPE_PARSER.put(byte.class,			(s) -> Byte.parseByte(s));
		TYPE_PARSER.put(Byte.class,			(s) -> Byte.parseByte(s));
		
		TYPE_PARSER.put(char.class,			(s) -> s.length() > 0 ? s.charAt(0) : 0);
		TYPE_PARSER.put(Character.class,	(s) -> s.length() > 0 ? s.charAt(0) : 0);

		TYPE_PARSER.put(float.class,		(s) -> Float.parseFloat(s));
		TYPE_PARSER.put(Float.class,		(s) -> Float.parseFloat(s));
		
		TYPE_PARSER.put(double.class,		(s) -> Double.parseDouble(s));
		TYPE_PARSER.put(Double.class,		(s) -> Double.parseDouble(s));
		
		TYPE_PARSER.put(Player.class,		(s) -> Player.getByNameOrId(s));
		TYPE_PARSER.put(Color.class,		(s) -> new Color(Integer.parseUnsignedInt(s, 16)));
	}
	
	
	private Map<String, SortedSet<CommandEntry>> commands;
	private Set<CommandGroup> groups;
	private Map<String, CommandGroup> childGroups;
	
	
	public CommandGroup()
	{
		commands = new HashMap<>();
		groups = new HashSet<>();
		childGroups = new HashMap<>();
	}
	
	public void registerCommands(Object object)
	{
		generateCommandEntries(object).forEach((e) -> registerCommand(e));
	}
	
	public void registerCommand(String command, Class<?>[] paramTypes, String[] paramNames, CommandHandler handler)
	{
		registerCommand(command, paramTypes, paramNames, (short) 0, false, handler);
	}
	
	public void registerCommand(String command, Class<?>[] paramTypes, String[] paramNames, short priority, boolean strictMode, CommandHandler handler)
	{
		registerCommand(new CommandEntry(command, paramTypes, paramNames, priority, strictMode, (player, params) ->
		{
			Queue<Object> paramQueue = new LinkedList<>();
			Collections.addAll(paramQueue, params);
			paramQueue.poll();
			return handler.handle(player, paramQueue);
		}));
	}
	
	private void registerCommand(CommandEntry entry)
	{
		SortedSet<CommandEntry> entries = commands.get(entry.getCommand());
		if (entries == null)
		{
			entries = new TreeSet<CommandEntry>((e1, e2) -> e2.getParamTypes().length - e1.getParamTypes().length);
			commands.put(entry.getCommand(), entries);
		}
		entries.add(entry);
	}

	public void registerGroup(CommandGroup group)
	{
		groups.add(group);
	}

	public void unregisterGroup(CommandGroup group)
	{
		groups.remove(group);
	}

	public boolean containsGroup(CommandGroup group)
	{
		return groups.contains(group);
	}

	public void registerChildGroup(CommandGroup group, String childName)
	{
		childGroups.put(childName, group);
	}

	public void unregisterChildGroup(CommandGroup group)
	{
		for (Iterator<Map.Entry<String, CommandGroup>> it = childGroups.entrySet().iterator(); it.hasNext(); )
		{
			if (it.next().getValue() == group) it.remove();
		}
	}

	public boolean containsChildGroup(CommandGroup group)
	{
		for (Iterator<Map.Entry<String, CommandGroup>> it = childGroups.entrySet().iterator(); it.hasNext(); )
		{
			if (it.next().getValue() == group) return true;
		}

		return false;
	}
	
	public boolean processCommand(Player player, String commandText)
	{
		return processCommand("", null, player, commandText);
	}
	
	public boolean processCommand(Player player, String command, String paramText)
	{
		return processCommand("", null, player, command.trim(), paramText);
	}

	protected boolean processCommand(String path, List<Pair<String, CommandEntry>> matchedCmds, Player player, String commandText)
	{
		String[] splits = StringUtils.split(commandText, " ", 2);
		if (splits.length < 1) return false;
		return processCommand(path, matchedCmds, player, splits[0], splits.length == 2 ? splits[1] : "");
	}

	protected boolean processCommand(String path, List<Pair<String, CommandEntry>> matchedCmds, Player player, String command, String paramText)
	{
		SortedSet<CommandEntry> entries = commands.get(command);
		if (entries == null) return false;

		for (CommandEntry e : entries)
		{
			Class<?>[] types = e.getParamTypes();
			String[] paramStrs = StringUtils.split(paramText, " ", types.length);
			if (types.length != paramStrs.length && types.length != 0) continue;

			try
			{
				Object[] params = parseParams(types, paramStrs);
				params = ArrayUtils.add(params, 0, player);
				if (e.handle(player, params)) return true;
			}
			catch (NumberFormatException ex)
			{

			}

			if (matchedCmds != null) matchedCmds.add(new ImmutablePair<>(path, e));
		}

		for (CommandGroup group : groups)
		{
			if (group.processCommand(path, matchedCmds, player, command, paramText)) return true;
		}

		CommandGroup child = childGroups.get(command);
		if (child == null) return false;

		if (child.processCommand(path + " " + command, matchedCmds, player, paramText)) return true;

		return false;
	}
}
