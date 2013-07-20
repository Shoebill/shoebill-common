package net.gtaun.shoebill.common.player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.util.event.ManagedEventManager;

public class PlayerLifecycleHolder implements Destroyable
{
	public static abstract class PlayerLifecycleObject
	{
		protected final Shoebill shoebill;
		protected final ManagedEventManager eventManager;
		protected final Player player;

		private final List<Destroyable> destroyables;
		
		
		public PlayerLifecycleObject(Shoebill shoebill, EventManager eventManager, Player player)
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

		private void initialize()
		{
			onInitialize();
		}
		
		private void uninitialize()
		{
			onUninitialize();
			for (Destroyable destroyable : destroyables) destroyable.destroy();
			eventManager.cancelAll();
		}

		protected abstract void onInitialize();
		protected abstract void onUninitialize();
	}
	
	public interface PlayerLifecycleObjectFactory<T extends PlayerLifecycleObject> 
	{
		T create(Shoebill shoebill, EventManager eventManager, Player player);
	}
	
	
	private final Shoebill shoebill;
	private final ManagedEventManager eventManager;

	private final Map<Class<?>, PlayerLifecycleObjectFactory<? extends PlayerLifecycleObject>> classFactories;
	private final Map<Player, Map<Class<?>, PlayerLifecycleObject>> holder;

	private boolean destroyed;
	
	
	public PlayerLifecycleHolder(Shoebill shoebill, EventManager rootEventManager)
	{
		this.shoebill = shoebill;
		
		eventManager = new ManagedEventManager(rootEventManager);
		classFactories = new HashMap<>();
		holder = new HashMap<>();

		eventManager.registerHandler(PlayerConnectEvent.class, playerEventHandler, HandlerPriority.MONITOR);
		eventManager.registerHandler(PlayerDisconnectEvent.class, playerEventHandler, HandlerPriority.BOTTOM);
	}

	@Override
	public void destroy()
	{
		if (destroyed) return;
		
		eventManager.cancelAll();
		destroyed = true;
	}

	@Override
	public boolean isDestroyed()
	{
		return destroyed;
	}
	
	public <T extends PlayerLifecycleObject> void registerClass(final Class<T> clz)
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
	
	public <T extends PlayerLifecycleObject> void registerClass(Class<T> clz, PlayerLifecycleObjectFactory<T> factory)
	{
		if (classFactories.containsKey(clz)) return;
		
		Collection<Player> players = shoebill.getSampObjectStore().getPlayers();
		for (Player player : players)
		{
			Map<Class<?>, PlayerLifecycleObject> playerLifecycleObjects = holder.get(player);

			PlayerLifecycleObject object = factory.create(shoebill, eventManager, player);
			playerLifecycleObjects.put(clz, object);
			object.initialize();
		}
		
		classFactories.put(clz, factory);
	}
	
	public <T extends PlayerLifecycleObject> void unregisterClass(Class<T> clz)
	{
		if (classFactories.containsKey(clz) == false) return;
		
		Collection<Player> players = shoebill.getSampObjectStore().getPlayers();
		for (Player player : players)
		{
			Map<Class<?>, PlayerLifecycleObject> playerLifecycleObjects = holder.get(player);
			PlayerLifecycleObject object = playerLifecycleObjects.get(clz);
			playerLifecycleObjects.remove(clz);
			object.uninitialize();
		}
		
		classFactories.remove(clz);
	}
	
	public <T extends PlayerLifecycleObject> T getObject(Player player, Class<T> clz)
	{
		if (classFactories.containsKey(clz) == false) return null;
		
		Map<Class<?>, PlayerLifecycleObject> playerLifecycleObjects = holder.get(player);
		return clz.cast(playerLifecycleObjects.get(clz));
	}
	
	public <T extends PlayerLifecycleObject> Collection<T> getObjects(Class<T> clz)
	{
		if (classFactories.containsKey(clz) == false) return null;
		
		Collection<T> objects = new LinkedList<>();
		for (Entry<Player, Map<Class<?>, PlayerLifecycleObject>> entry : holder.entrySet())
		{
			objects.add(clz.cast(entry.getValue().get(clz)));
		}
		
		return objects;
	}
	
	private final PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerConnect(PlayerConnectEvent event)
		{
			Player player = event.getPlayer();
			Map<Class<?>, PlayerLifecycleObject> playerLifecycleObjects = new HashMap<>();
			holder.put(player, playerLifecycleObjects);
			
			for (Entry<Class<?>, PlayerLifecycleObjectFactory<? extends PlayerLifecycleObject>> entry : classFactories.entrySet())
			{
				Class<?> clz = entry.getKey();
				PlayerLifecycleObjectFactory<? extends PlayerLifecycleObject> factory = entry.getValue();
				
				PlayerLifecycleObject object = factory.create(shoebill, eventManager, player);
				playerLifecycleObjects.put(clz, object);
				object.initialize();
			}
		}
		
		protected void onPlayerDisconnect(PlayerDisconnectEvent event)
		{
			Player player = event.getPlayer();
			Map<Class<?>, PlayerLifecycleObject> playerLifecycleObjects = holder.get(player);
			holder.remove(player);
			
			for (PlayerLifecycleObject object : playerLifecycleObjects.values())
			{
				object.uninitialize();
			}
		}
	};
}
