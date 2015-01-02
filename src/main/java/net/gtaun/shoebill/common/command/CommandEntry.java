package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.common.command.CommandEntryInternal.CommandHandlerInternal;
import net.gtaun.shoebill.object.Player;

public class CommandEntry
{
	private CommandEntryInternal entry;
	private String path;
	
	
	public CommandEntry(CommandEntryInternal entry, String path)
	{
		this.entry = entry;
		this.path = path;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public String getCommand()
	{
		return entry.completeCommand(path);
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

	public String getHelpMessage() { return entry.getHelpMessage(); }

	public boolean isCaseSensitive() { return entry.isCaseSensitive(); }

	public CommandHandlerInternal getHandler()
	{
		return entry.getHandler();
	}
	
	public boolean handle(Player player, Object[] params)
	{
		return entry.handle(player, params);
	}

	public String getCategorie() { return entry.getCategorie(); }
}
