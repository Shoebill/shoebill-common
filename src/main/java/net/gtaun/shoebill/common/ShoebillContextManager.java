package net.gtaun.shoebill.common;

import net.gtaun.util.event.EventManager;

import java.util.*;

public class ShoebillContextManager extends AbstractShoebillContext
{
	private Map<Class<? extends AbstractShoebillContext>, Set<AbstractShoebillContext>> contextContainers = new HashMap<>();


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
		Class<? extends AbstractShoebillContext> clazz = context.getClass();
		Set<AbstractShoebillContext> contextCotainer = contextContainers.get(clazz);
		if (contextCotainer == null)
		{
			contextCotainer = new HashSet<>();
			contextContainers.put(clazz, contextCotainer);
		}

		if (contextCotainer.contains(context)) return context;
		contextCotainer.add(context);

		addDestroyable(context);
		context.init();
		return context;
	}

	public void destroy(AbstractShoebillContext context)
	{
		Class<? extends AbstractShoebillContext> clazz = context.getClass();
		Set<AbstractShoebillContext> contextCotainer = contextContainers.get(clazz);
		if (contextCotainer == null) return;
		contextCotainer.remove(context);

		context.destroy();
		removeDestroyable(context);
	}

	public <ContextType extends AbstractShoebillContext>
	ContextType getContext(Class<ContextType> clazz)
	{
		Set<AbstractShoebillContext> contextCotainer = contextContainers.get(clazz);
		if (contextCotainer == null || contextCotainer.isEmpty()) return null;
		AbstractShoebillContext context = contextCotainer.iterator().next();
		return clazz.cast(context);
	}

	public <ContextType extends AbstractShoebillContext>
	Set<ContextType> getContexts(Class<ContextType> clazz)
	{
		Set<AbstractShoebillContext> contextCotainer = contextContainers.get(clazz);
		if (contextCotainer == null || contextCotainer.isEmpty()) return Collections.emptySet();
		return (Set<ContextType>) Collections.unmodifiableSet(contextCotainer);
	}
}
