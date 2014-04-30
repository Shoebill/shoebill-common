package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
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
	
	
	public PlayerCommandManager(EventManager eventManager, HandlerPriority priority)
	{
		eventManagerNode = eventManager.createChildNode();
		eventManagerNode.registerHandler(PlayerCommandEvent.class, priority, (e) ->
		{
			if (processCommand(e.getPlayer(), e.getCommand().substring(1))) e.setProcessed();
		});
	}
	
	public boolean processCommand(Player player, String commandText, boolean sendUsages)
	{
		List<Pair<String, CommandEntry>> matchedCommands = new ArrayList<>();
		if (processCommand("", matchedCommands, player, commandText)) return true;
		if (!sendUsages) return false;
		
		if (matchedCommands.isEmpty()) return false;
		sendUsageMessages(player, matchedCommands);
		return true;
	}
	
	public void sendUsageMessage(Player player, String path)
	{
		sendUsageMessages(player, getMatchedCommands(path));
	}

	private void sendUsageMessage(Player player, String path, CommandEntry entry)
	{
		String command = (path + " " + entry.getCommand()).trim();
		String message = "Usage: /" + command;
		for (String paramName : entry.getParamNames()) message += " [" + paramName + "]";

		player.sendMessage(Color.RED, message);
	}
	
	private void sendUsageMessages(Player player, List<Pair<String, CommandEntry>> commands)
	{
		for (Pair<String, CommandEntry> e : commands) sendUsageMessage(player, e.getLeft(), e.getRight());
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
}
