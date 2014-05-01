package net.gtaun.shoebill.common.command;

import org.apache.commons.lang3.StringUtils;

import net.gtaun.shoebill.common.command.CommandEntryInternal.CommandHandlerInternal;
import net.gtaun.shoebill.object.Player;

public class CommandEntry
{
	private CommandEntryInternal entry;
	private String path;
	
	
	public CommandEntry(CommandEntryInternal entry, String path)
	{
		this.entry = entry;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public String getCommand()
	{
		return StringUtils.isBlank(path) ? entry.getCommand() : path + " " + entry.getCommand();
	}
	
	public Class<?>[] getParamTypes()
	{
		return entry.getParamTypes();
	}

	public String[] getParamNames()
	{
		return entry.getParamNames();
	}

	public short getPriority()
	{
		return entry.getPriority();
	}
	
	public CommandHandlerInternal getHandler()
	{
		return entry.getHandler();
	}
	
	public boolean handle(Player player, Object[] params)
	{
		return entry.handle(player, params);
	}
}
