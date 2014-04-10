package net.gtaun.shoebill.common.player;

import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

public abstract class AbstractPlayerContext extends AbstractShoebillContext
{
	protected final Player player;
	
	
	public AbstractPlayerContext(EventManager rootEventManager, Player player)
	{
		super(rootEventManager);
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}