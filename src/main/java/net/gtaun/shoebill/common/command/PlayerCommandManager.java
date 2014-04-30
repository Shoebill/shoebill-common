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
	private EventManagerNode eventManagerNode;
	
	
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
		String message = "Usage: " + prefix + command;
		for (String paramName : entry.getParamNames()) message += " [" + paramName + "]";

		player.sendMessage(Color.RED, message);
	}
	
	private void sendUsageMessages(Player player, String prefix, List<Pair<String, CommandEntry>> commands)
	{
		for (Pair<String, CommandEntry> e : commands) sendUsageMessage(player, prefix, e.getLeft(), e.getRight());
	}
}
