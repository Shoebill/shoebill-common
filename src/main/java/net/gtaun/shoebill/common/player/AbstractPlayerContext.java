package net.gtaun.shoebill.common.player;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

public abstract class AbstractPlayerContext extends AbstractShoebillContext
{
	protected final Player player;
	
	
	public AbstractPlayerContext(Shoebill shoebill, EventManager rootEventManager, Player player)
	{
		super(shoebill, rootEventManager);
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}