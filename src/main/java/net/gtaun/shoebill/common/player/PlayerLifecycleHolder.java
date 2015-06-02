package net.gtaun.shoebill.common.player;

import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

public class PlayerLifecycleHolder implements Destroyable {
    @FunctionalInterface
    public interface PlayerLifecycleObjectFactory<T extends PlayerLifecycleObject> {
        T create(EventManager eventManager, Player player);
    }

    @FunctionalInterface
    public interface EventHandlerRegisterer {
        void register(EventManager eventManager);
    }

    class PlayerLifecycleObjectStuff {
        PlayerLifecycleObjectFactory<? extends PlayerLifecycleObject> factory;
        EventManagerNode eventManagerNode;
    }


    private final EventManagerNode eventManagerNode;

    private final Map<Class<?>, PlayerLifecycleObjectStuff> objectStuffs;
    private final Map<Player, Map<Class<?>, PlayerLifecycleObject>> holder;


    public PlayerLifecycleHolder(EventManager eventManager) {
        eventManagerNode = eventManager.createChildNode();
        objectStuffs = new HashMap<>();
        holder = new HashMap<>();

        eventManagerNode.registerHandler(PlayerConnectEvent.class, HandlerPriority.MONITOR, (PlayerConnectEvent e) ->
        {
            Player player = e.getPlayer();
            Map<Class<?>, PlayerLifecycleObject> playerLifecycleObjects = new HashMap<>();
            holder.put(player, playerLifecycleObjects);

            for (Entry<Class<?>, PlayerLifecycleObjectStuff> entry : objectStuffs.entrySet()) {
                Class<?> clz = entry.getKey();
                PlayerLifecycleObjectStuff stuff = entry.getValue();
                
                PlayerLifecycleObject object = stuff.factory.create(eventManagerNode, player);
                playerLifecycleObjects.put(clz, object);
                object.init();
            }
        });

        eventManagerNode.registerHandler(PlayerDisconnectEvent.class, HandlerPriority.BOTTOM, (PlayerDisconnectEvent e) ->
        {
            Player player = e.getPlayer();
            Map<Class<?>, PlayerLifecycleObject> playerLifecycleObjects = holder.get(player);
            holder.remove(player);

            for (PlayerLifecycleObject object : playerLifecycleObjects.values()) object.destroy();
        });
    }

    @Override
    public void destroy() {
        if (isDestroyed()) return;

        eventManagerNode.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return eventManagerNode.isDestroy();
    }

    public <T extends PlayerLifecycleObject> void registerClass(final Class<T> clz) {
        registerClass(clz, null, null);
    }


    public <T extends PlayerLifecycleObject> void registerClass(Class<T> clz, PlayerLifecycleObjectFactory<T> factory) {
        registerClass(clz, factory, null);
    }

    public <T extends PlayerLifecycleObject> void registerClass(Class<T> clz, PlayerLifecycleObjectFactory<T> factory, EventHandlerRegisterer handlerRegisterer) {
        if (objectStuffs.containsKey(clz)) return;
        if (factory == null)
        {
            final Constructor<T> constructor;
            try {
                constructor = clz.getConstructor(EventManager.class, Player.class);
                constructor.setAccessible(true);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new UnsupportedOperationException(e);
            }

            factory = (eventManager, player) -> {
                try {
                    return constructor.newInstance(eventManager, player);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }
            };
        }

        PlayerLifecycleObjectStuff stuff = new PlayerLifecycleObjectStuff();
        stuff.factory = factory;
        objectStuffs.put(clz, stuff);

        if (handlerRegisterer != null) {
            stuff.eventManagerNode = eventManagerNode.createChildNode();
            handlerRegisterer.register(stuff.eventManagerNode);
        }

        Player.get().forEach((player) ->
        {
            Map<Class<?>, PlayerLifecycleObject> playerLifecycleObjects = holder.get(player);

            PlayerLifecycleObject object = stuff.factory.create(eventManagerNode, player);
            playerLifecycleObjects.put(clz, object);
            object.init();
        });
    }

    public <T extends PlayerLifecycleObject> void unregisterClass(Class<T> clz) {
        PlayerLifecycleObjectStuff stuff = objectStuffs.get(clz);
        if (stuff == null) return;

        if (stuff.eventManagerNode != null) stuff.eventManagerNode.destroy();

        Player.get().forEach((player) ->
        {
            Map<Class<?>, PlayerLifecycleObject> playerLifecycleObjects = holder.get(player);
            PlayerLifecycleObject object = playerLifecycleObjects.get(clz);
            playerLifecycleObjects.remove(clz);
            object.destroy();
        });

        objectStuffs.remove(clz);
    }

    public <T extends PlayerLifecycleObject> T getObject(Player player, Class<T> clz) {
        if (!objectStuffs.containsKey(clz)) return null;

        Map<Class<?>, PlayerLifecycleObject> playerLifecycleObjects = holder.get(player);
        if (playerLifecycleObjects == null) return null;

        return clz.cast(playerLifecycleObjects.get(clz));
    }

    public <T extends PlayerLifecycleObject> Collection<T> getObjects(Class<T> clz) {
        if (!objectStuffs.containsKey(clz)) return Collections.emptyList();

        Collection<T> objects = new LinkedList<>();
        holder.values().forEach((m) -> objects.add(clz.cast(m.get(clz))));

        return objects;
    }
}
