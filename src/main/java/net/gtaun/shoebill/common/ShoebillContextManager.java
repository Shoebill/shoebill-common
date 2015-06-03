package net.gtaun.shoebill.common;

import net.gtaun.util.event.EventManager;

public class ShoebillContextManager extends AbstractShoebillContext
{
	public ShoebillContextManager(EventManager parentEventManager)
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
