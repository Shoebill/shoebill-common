package net.gtaun.shoebill.common;

import java.util.LinkedList;
import java.util.List;

import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

public abstract class AbstractShoebillContext implements Destroyable
{
	protected final EventManager rootEventManager;
	protected final EventManagerNode eventManager;

	private List<Destroyable> destroyables;
	
	
	public AbstractShoebillContext( EventManager rootEventManager)
	{
		this.rootEventManager = rootEventManager;
		this.eventManager = rootEventManager.createChildNode();
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
		
		eventManager.destroy();
	}
	
	@Override
	public boolean isDestroyed()
	{
		return destroyables == null;
	}

	protected abstract void onInit();
	protected abstract void onDestroy();
}
