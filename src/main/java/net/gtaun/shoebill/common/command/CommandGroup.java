package net.gtaun.shoebill.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class CommandGroup
{
	private static Collection<CommandEntry> generateCommandEntries(Object object)
	{
		List<CommandEntry> entries = new ArrayList<>();
		
		Class<?> clz = object.getClass();
		Arrays.stream(clz.getMethods()).forEach((m) ->
		{
			String name = m.getName();
			Class<?>[] paramTypes = m.getParameterTypes();
			if (m.getReturnType() != boolean.class) return;
			if (paramTypes.length < 1) return;
			
			Command command = m.getAnnotation(Command.class);
			if (command == null) return;
			if (paramTypes[0] != Player.class) return;
			
			paramTypes = Arrays.copyOfRange(paramTypes, 1, paramTypes.length);
			
			short priority = command.priority();
			boolean strictMode = command.strictMode();
			
			entries.add(new CommandEntry(name, paramTypes, priority, strictMode, (player, params) ->
			{
				try
				{
					return (boolean) m.invoke(object, params);
				}
				catch (Exception e)
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
	
	
	public CommandGroup()
	{
		commands = new HashMap<>();
	}
	
	public void registerCommands(Object object)
	{
		generateCommandEntries(object).forEach((e) -> registerCommand(e));
	}
	
	public void registerCommand(String command, Class<?>[] paramTypes, CommandHandler handler)
	{
		registerCommand(command, paramTypes, (short) 0, false, handler);
	}
	
	public void registerCommand(String command, Class<?>[] paramTypes, short priority, boolean strictMode, CommandHandler handler)
	{
		registerCommand(new CommandEntry(command, paramTypes, priority, strictMode, (player, params) ->
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
			entries = new TreeSet<CommandEntry>((e1, e2) -> e1.getParamTypes().length - e2.getParamTypes().length);
			commands.put(entry.getCommand(), entries);
		}
		entries.add(entry);
	}
	
	public boolean processCommand(Player player, String commandText)
	{
		String[] splits = StringUtils.split(commandText, " ", 2);
		return processCommand(player, splits[0], splits.length == 2 ? splits[1] : "");
	}
	
	public boolean processCommand(Player player, String command, String paramText)
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
				continue;
			}
		}
		
		return false;
	}
}
