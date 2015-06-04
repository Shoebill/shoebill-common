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

	public <ContextType extends AbstractShoebillContext>
	ContextType manage(ContextType context)
	{
		addDestroyable(context);
		context.init();
		return context;
	}

	public void destroy(AbstractShoebillContext context)
	{
		context.destroy();
		removeDestroyable(context);
	}
}
