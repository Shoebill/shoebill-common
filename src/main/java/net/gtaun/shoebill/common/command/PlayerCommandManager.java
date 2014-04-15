package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

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
