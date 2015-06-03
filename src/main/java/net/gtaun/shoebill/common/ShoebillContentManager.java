package net.gtaun.shoebill.common;

import net.gtaun.util.event.EventManager;

public class ShoebillContentManager extends AbstractShoebillContext
{
	public ShoebillContentManager(EventManager parentEventManager)
	{
		super(parentEventManager);
	}

	@Override
	protected void onInit()
	{

	}

	@Override
	protected void onDestroy()
	{

	}

	public void manage(AbstractShoebillContext context)
	{
		addDestroyable(context);
		context.init();
	}

	public void destroy(AbstractShoebillContext context)
	{
		context.destroy();
		removeDestroyable(context);
	}
}
