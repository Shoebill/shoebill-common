package net.gtaun.shoebill.common;

import java.util.LinkedList;
import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.ManagedEventManager;

public abstract class AbstractShoebillContext implements Destroyable
{
	protected final Shoebill shoebill;
	protected final EventManager rootEventManager;
	protected final ManagedEventManager eventManager;

	private List<Destroyable> destroyables;
	
	
	public AbstractShoebillContext(Shoebill shoebill, EventManager rootEventManager)
	{
		this.shoebill = shoebill;
		this.rootEventManager = rootEventManager;
		this.eventManager = new ManagedEventManager(rootEventManager);
		this.destroyables = new LinkedList<>();
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}
	
	protected void addDestroyable(Destroyable destroyable)
	{
		destroyables.add(destroyable);
	}

	public final void init()
	{
		try
		{
			onInit();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			destroy();
		}
	}
	
	@Override
	public final void destroy()
	{
		if (isDestroyed()) return;
		
		onDestroy();
		for (Destroyable destroyable : destroyables) destroyable.destroy();
		destroyables = null;
		
		eventManager.cancelAll();
	}
	
	@Override
	public boolean isDestroyed()
	{
		return destroyables == null;
	}

	protected abstract void onInit();
	protected abstract void onDestroy();
}
