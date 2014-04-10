package net.gtaun.shoebill.common.player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

public class PlayerLifecycleHolder implements Destroyable
{
	public interface PlayerLifecycleObjectFactory<T extends AbstractPlayerContext> 
	{
		T create(Shoebill shoebill, EventManager eventManager, Player player);
	}
	
	
	private final Shoebill shoebill;
	private final EventManagerNode eventManager;

	private final Map<Class<?>, PlayerLifecycleObjectFactory<? extends AbstractPlayerContext>> classFactories;
	private final Map<Player, Map<Class<?>, AbstractPlayerContext>> holder;

	private boolean destroyed;
	
	
	public PlayerLifecycleHolder(Shoebill shoebill, EventManager rootEventManager)
	{
		this.shoebill = shoebill;
		
		eventManager = rootEventManager.createChildNode();
		classFactories = new HashMap<>();
		holder = new HashMap<>();

		eventManager.registerHandler(PlayerConnectEvent.class, HandlerPriority.MONITOR, (PlayerConnectEvent e) ->
		{
			Player player = e.getPlayer();
			Map<Class<?>, AbstractPlayerContext> playerLifecycleObjects = new HashMap<>();
			holder.put(player, playerLifecycleObjects);
			
			for (Entry<Class<?>, PlayerLifecycleObjectFactory<? extends AbstractPlayerContext>> entry : classFactories.entrySet())
			{
				Class<?> clz = entry.getKey();
				PlayerLifecycleObjectFactory<? extends AbstractPlayerContext> factory = entry.getValue();
				
				AbstractPlayerContext object = factory.create(shoebill, eventManager, player);
				playerLifecycleObjects.put(clz, object);
				object.init();
			}
		});
		
		eventManager.registerHandler(PlayerDisconnectEvent.class, HandlerPriority.BOTTOM, (PlayerDisconnectEvent e) ->
		{
			Player player = e.getPlayer();
			Map<Class<?>, AbstractPlayerContext> playerLifecycleObjects = holder.get(player);
			holder.remove(player);
			
			for (AbstractPlayerContext object : playerLifecycleObjects.values()) object.destroy();
		});
	}

	@Override
	public void destroy()
	{
		if (destroyed) return;
		
		eventManager.destroy();
		destroyed = true;
	}

	@Override
	public boolean isDestroyed()
	{
		return destroyed;
	}
	
	public <T extends AbstractPlayerContext> void registerClass(final Class<T> clz)
	{
		final Constructor<T> constructor;
		try
		{
			constructor = clz.getConstructor(Shoebill.class, EventManager.class, Player.class);
			constructor.setAccessible(true);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			throw new UnsupportedOperationException(e);
		}
		
		registerClass(clz, new PlayerLifecycleObjectFactory<T>()
		{
			@Override
			public T create(Shoebill shoebill, EventManager eventManager, Player player)
			{
				try
				{
					return constructor.newInstance(shoebill, eventManager, player);
				}
				catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					e.printStackTrace();
					return null;
				}
			}
		});
	}
	
	public <T extends AbstractPlayerContext> void registerClass(Class<T> clz, PlayerLifecycleObjectFactory<T> factory)
	{
		if (classFactories.containsKey(clz)) return;
		
		Collection<Player> players = shoebill.getSampObjectManager().getPlayers();
		for (Player player : players)
		{
			Map<Class<?>, AbstractPlayerContext> playerLifecycleObjects = holder.get(player);

			AbstractPlayerContext object = factory.create(shoebill, eventManager, player);
			playerLifecycleObjects.put(clz, object);
			object.init();
		}
		
		classFactories.put(clz, factory);
	}
	
	public <T extends AbstractPlayerContext> void unregisterClass(Class<T> clz)
	{
		if (classFactories.containsKey(clz) == false) return;
		
		Collection<Player> players = shoebill.getSampObjectManager().getPlayers();
		for (Player player : players)
		{
			Map<Class<?>, AbstractPlayerContext> playerLifecycleObjects = holder.get(player);
			AbstractPlayerContext object = playerLifecycleObjects.get(clz);
			playerLifecycleObjects.remove(clz);
			object.destroy();
		}
		
		classFactories.remove(clz);
	}
	
	public <T extends AbstractPlayerContext> T getObject(Player player, Class<T> clz)
	{
		if (classFactories.containsKey(clz) == false) return null;
		
		Map<Class<?>, AbstractPlayerContext> playerLifecycleObjects = holder.get(player);
		return clz.cast(playerLifecycleObjects.get(clz));
	}
	
	public <T extends AbstractPlayerContext> Collection<T> getObjects(Class<T> clz)
	{
		if (classFactories.containsKey(clz) == false) return null;
		
		Collection<T> objects = new LinkedList<>();
		for (Entry<Player, Map<Class<?>, AbstractPlayerContext>> entry : holder.entrySet())
		{
			objects.add(clz.cast(entry.getValue().get(clz)));
		}
		
		return objects;
	}
}
