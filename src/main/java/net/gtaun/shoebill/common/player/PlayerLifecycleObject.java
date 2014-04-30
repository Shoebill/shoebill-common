package net.gtaun.shoebill.common.player;

import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

public abstract class PlayerLifecycleObject extends AbstractShoebillContext
{
	protected final Player player;
	
	
	public PlayerLifecycleObject(EventManager eventManager, Player player)
	{
		super(eventManager);
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}