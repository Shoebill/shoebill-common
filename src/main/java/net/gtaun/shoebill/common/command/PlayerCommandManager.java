package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class PlayerCommandManager extends CommandGroup implements Destroyable
{
	public interface UsageMessageSupplier		{ String get(Player player, String command, String prefix, String[] params); }
	
	public static final UsageMessageSupplier DEFAULT_USAGE_MESSAGE_SUPPLIER = (player, cmd, prefix, params) ->
	{
		String message = "Usage: " + prefix + cmd + " ";
		for (int i=0; i<params.length; i++)
		{
			message += "[" + params[i] + "]";
			if (i != params.length-1) message += " ";
		}
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
	
	public boolean processCommand(Player player, String commandText)
	{
		return processCommand(player, commandText, "", false);
	}
	
	public boolean processCommand(Player player, String commandText, String prefix, boolean sendUsages)
	{
		List<Pair<String, CommandEntry>> matchedCommands = new ArrayList<>();
		if (processCommand("", matchedCommands, player, commandText)) return true;
		if (!sendUsages) return false;
		
		if (matchedCommands.isEmpty()) return false;
		sendUsageMessages(player, prefix, matchedCommands);
		return true;
	}
	
	public void sendUsageMessage(Player player, String path)
	{
		sendUsageMessages(player, "/", getMatchedCommands(path));
	}
	
	public void sendUsageMessage(Player player, String path, String prefix)
	{
		sendUsageMessages(player, prefix, getMatchedCommands(path));
	}

	private void sendUsageMessage(Player player, String path, String prefix, CommandEntry entry)
	{
		String command = (path + " " + entry.getCommand()).trim();
		player.sendMessage(Color.RED, usageMessageSupplier.get(player, command, prefix, entry.getParamNames()));
	}
	
	private void sendUsageMessages(Player player, String prefix, List<Pair<String, CommandEntry>> commands)
	{
		for (Pair<String, CommandEntry> e : commands) sendUsageMessage(player, prefix, e.getLeft(), e.getRight());
	}
	
	public void setUsageMessageSupplier(UsageMessageSupplier usageMessageSupplier)
	{
		this.usageMessageSupplier = usageMessageSupplier;
	}
}
