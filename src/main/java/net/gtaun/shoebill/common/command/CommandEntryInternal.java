package net.gtaun.shoebill.common.command;

import org.apache.commons.lang3.StringUtils;

import net.gtaun.shoebill.object.Player;

class CommandEntryInternal
{
	public static String completePath(String path, String child)
	{
		return StringUtils.isBlank(path) ? child : path + " " + child;
	}

	@FunctionalInterface
	public interface CommandHandlerInternal
	{
		boolean handle(Player player, Object[] params);
	}


	private String command;
	private Class<?>[] paramTypes;
	private String[] paramNames;
	private short priority;
	private String helpMessage;
	private CommandHandlerInternal handler;


	public CommandEntryInternal(String command, Class<?>[] paramTypes, String[] paramNames, short priority, String helpMessage, CommandHandlerInternal handler)
	{
		this.command = command;
		this.paramTypes = paramTypes;
		this.paramNames = paramNames;
		this.priority = priority;
		this.helpMessage = helpMessage;
		this.handler = handler;
	}

	public String getCommand()
	{
		return command;
	}

	public String completeCommand(String path)
	{
		return completePath(path, command);
	}

	public Class<?>[] getParamTypes()
	{
		return paramTypes.clone();
	}

	public String[] getParamNames()
	{
		return paramNames.clone();
	}

	public short getPriority()
	{
		return priority;
	}

	public String getHelpMessage()
	{
		return helpMessage;
	}

	public CommandHandlerInternal getHandler()
	{
		return handler;
	}

	public boolean handle(Player player, Object[] params)
	{
		return handler.handle(player, params);
	}
}
