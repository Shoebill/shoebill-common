package net.gtaun.shoebill.common.player;

import java.util.LinkedList;
import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.ManagedEventManager;

public abstract class AbstractPlayerContext
{
	protected final Shoebill shoebill;
	protected final ManagedEventManager eventManager;
	protected final Player player;

	private final List<Destroyable> destroyables;
	
	
	public AbstractPlayerContext(Shoebill shoebill, EventManager eventManager, Player player)
	{
		this.shoebill = shoebill;
		this.eventManager = new ManagedEventManager(eventManager);
		this.player = player;
		this.destroyables = new LinkedList<>();
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	protected void addDestroyable(Destroyable destroyable)
	{
		destroyables.add(destroyable);
	}

	public final void initialize()
	{
		onInitialize();
	}
	
	public final void uninitialize()
	{
		onUninitialize();
		for (Destroyable destroyable : destroyables) destroyable.destroy();
		eventManager.cancelAll();
	}

	protected abstract void onInitialize();
	protected abstract void onUninitialize();
}