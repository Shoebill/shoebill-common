package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.object.Player;

public class CommandEntry
{
	@FunctionalInterface
	public interface CommandHandlerInternal
	{
		boolean handle(Player player, Object[] params);
	}
	
	
	private String command;
	private Class<?>[] paramTypes;
	private short priority;
	private boolean strictMode;
	private CommandHandlerInternal handler;
	
	
	public CommandEntry(String command, Class<?>[] paramTypes, short priority, boolean strictMode, CommandHandlerInternal handler)
	{
		this.command = command;
		this.paramTypes = paramTypes;
		this.priority = priority;
		this.strictMode = strictMode;
		this.handler = handler;
	}
	
	public String getCommand()
	{
		return command;
	}
	
	public Class<?>[] getParamTypes()
	{
		return paramTypes.clone();
	}
	
	public short getPriority()
	{
		return priority;
	}
	
	public boolean isStrictMode()
	{
		return strictMode;
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
