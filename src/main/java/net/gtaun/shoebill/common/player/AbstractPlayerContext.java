package net.gtaun.shoebill.common.player;

import java.util.LinkedList;
import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.ManagedEventManager;

public abstract class AbstractPlayerContext implements Destroyable
{
	protected final Shoebill shoebill;
	protected final EventManager rootEventManager;
	protected final ManagedEventManager eventManager;
	protected final Player player;

	private List<Destroyable> destroyables;
	
	
	public AbstractPlayerContext(Shoebill shoebill, EventManager rootEventManager, Player player)
	{
		this.shoebill = shoebill;
		this.rootEventManager = rootEventManager;
		this.eventManager = new ManagedEventManager(rootEventManager);
		this.player = player;
		this.destroyables = new LinkedList<>();
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	protected void addDestroyable(Destroyable destroyable)
	{
		destroyables.add(destroyable);
	}

	public final void init()
	{
		onInit();
	}
	
	@Override
	public final void destroy()
	{
		if (isDestroyed()) return;
		
		onDestroy();
		for (Destroyable destroyable : destroyables) destroyable.destroy();
		eventManager.cancelAll();
		
		destroyables = null;
	}
	
	@Override
	public boolean isDestroyed()
	{
		return destroyables != null;
	}

	protected abstract void onInit();
	protected abstract void onDestroy();
}