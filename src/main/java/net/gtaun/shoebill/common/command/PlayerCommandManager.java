package net.gtaun.shoebill.common.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

import org.apache.commons.lang3.tuple.Pair;

public class PlayerCommandManager extends CommandGroup implements Destroyable
{
	public interface UsageMessageSupplier		{ String get(Player player, String command, String prefix, String[] params); }
	
	public static final UsageMessageSupplier DEFAULT_USAGE_MESSAGE_SUPPLIER = (player, cmd, prefix, params) ->
	{
		String message = "Usage: " + prefix + cmd;
		for (String param : params) message += " [" + param + "]";
		return message;
	};
	
	
	private EventManagerNode eventManagerNode;

	private UsageMessageSupplier usageMessageSupplier = DEFAULT_USAGE_MESSAGE_SUPPLIER;
	
	
	public PlayerCommandManager(EventManager eventManager)
	{
		eventManagerNode = eventManager.createChildNode();
	}
	
	@Override
	public void destroy()
	{
		eventManagerNode.destroy();
	}
	
	@Override
	public boolean isDestroyed()
	{
		return eventManagerNode.isDestroy();
	}
	
	public void installCommandHandler(HandlerPriority priority)
	{
		eventManagerNode.registerHandler(PlayerCommandEvent.class, priority, (e) ->
		{
			if (processCommand(e.getPlayer(), e.getCommand().substring(1), "/", true)) e.setProcessed();
		});
	}

	public void installTextHandler(HandlerPriority priority, String prefix)
	{
		eventManagerNode.registerHandler(PlayerTextEvent.class, priority, (e) ->
		{
			String text = e.getText();
			if (!text.startsWith(prefix)) return;
			if (processCommand(e.getPlayer(), text.substring(prefix.length()), prefix, true))
			{
				e.disallow();
				e.interrupt();
			}
		});
	}
	
	public void uninstallAllHandlers()
	{
		eventManagerNode.cancelAll();
	}
	
	@Override
	public boolean processCommand(Player player, String commandText)
	{
		return processCommand(player, commandText, "", false);
	}
	
	public boolean processCommand(Player player, String commandText, String prefix, boolean sendUsages)
	{
		List<Pair<String, CommandEntryInternal>> matchedCommands = new ArrayList<>();
		if (processCommand("", matchedCommands, player, commandText)) return true;
		if (!sendUsages) return false;
		
		if (matchedCommands.isEmpty()) return false;
		sendUsageMessages(player, prefix, matchedCommands);
		return true;
	}

	public List<CommandEntry> getCommandEntries()
	{
		List<CommandEntry> entries = new ArrayList<>();
		getCommandEntries(entries, "");
		return entries;
	}
	
	public List<CommandEntry> getCommandEntries(String path)
	{
		List<CommandEntry> entries = new ArrayList<>();
		getCommandEntries(entries, "", path);
		return entries;
	}
	
	public String getUsageMessage(Player player, String commandText)
	{
		return getUsageMessage(player, commandText, "/");
	}
	
	public String getUsageMessage(Player player, String commandText, String prefix)
	{
		String message = "";
		for (Iterator<Pair<String, CommandEntryInternal>> it = getMatchedCommands(commandText).iterator(); it.hasNext(); )
		{
			Pair<String, CommandEntryInternal> e = it.next();
 			message += getUsageMessage(player, e.getLeft(), prefix, e.getRight());
 			if (it.hasNext()) message += "\n"; 
 		}
		return message;
	}

	private String getUsageMessage(Player player, String path, String prefix, CommandEntryInternal entry)
	{
		return usageMessageSupplier.get(player, entry.completeCommand(path), prefix, entry.getParamNames());
	}
	
	public void sendUsageMessage(Player player, String commandText)
	{
		sendUsageMessage(player, commandText, "/");
	}
	
	public void sendUsageMessage(Player player, String commandText, String prefix)
	{
		sendUsageMessages(player, prefix, getMatchedCommands(commandText));
	}
	
	private void sendUsageMessages(Player player, String prefix, List<Pair<String, CommandEntryInternal>> commands)
	{
		for (Pair<String, CommandEntryInternal> e : commands)
		{
			player.sendMessage(Color.RED, getUsageMessage(player, e.getLeft(), prefix, e.getRight()));
		}
	}
	
	public void setUsageMessageSupplier(UsageMessageSupplier usageMessageSupplier)
	{
		this.usageMessageSupplier = usageMessageSupplier;
	}
}
