package net.gtaun.shoebill.common.vehicle;

import net.gtaun.shoebill.event.destroyable.DestroyEvent;
import net.gtaun.shoebill.event.vehicle.VehicleCreateEvent;
import net.gtaun.shoebill.entities.Destroyable;
import net.gtaun.shoebill.entities.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class VehicleLifecycleHolder implements Destroyable {
    public interface VehicleLifecycleObjectFactory<T extends VehicleLifecycleObject> {
        T create(EventManager eventManager, Vehicle vehicle);
    }
    
    private final EventManagerNode eventManagerNode;

    private final Map<Class<?>, VehicleLifecycleObjectFactory<? extends VehicleLifecycleObject>> objectFactories;
    private final Map<Vehicle, Map<Class<?>, VehicleLifecycleObject>> holder;


    public VehicleLifecycleHolder(EventManager eventManager) {
        eventManagerNode = eventManager.createChildNode();
        objectFactories = new HashMap<>();
        holder = new HashMap<>();

        eventManagerNode.registerHandler(VehicleCreateEvent.class, HandlerPriority.MONITOR, (e) ->
        {
            Vehicle vehicle = e.getVehicle();
            Map<Class<?>, VehicleLifecycleObject> vehicleLifecycleObjects = new HashMap<>();
            holder.put(vehicle, vehicleLifecycleObjects);

            for (Map.Entry<Class<?>, VehicleLifecycleObjectFactory<? extends VehicleLifecycleObject>> entry : objectFactories.entrySet()) {
                Class<?> clz = entry.getKey();
                VehicleLifecycleObjectFactory<? extends VehicleLifecycleObject> factory = entry.getValue();

                VehicleLifecycleObject object = factory.create(eventManagerNode, vehicle);
                vehicleLifecycleObjects.put(clz, object);
                object.init();
            }
        });

        eventManagerNode.registerHandler(DestroyEvent.class, HandlerPriority.BOTTOM, (e) ->
        {
            if (e.getDestroyable() instanceof Vehicle) {
                Vehicle vehicle = (Vehicle) e.getDestroyable();
                Map<Class<?>, VehicleLifecycleObject> vehicleLifecycleObjects = holder.get(vehicle);
                holder.remove(vehicle);

                vehicleLifecycleObjects.values().forEach(net.gtaun.shoebill.common.vehicle.VehicleLifecycleObject::destroy);
            }
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

    public <T extends VehicleLifecycleObject> void registerClass(final Class<T> clz) {
        final Constructor<T> constructor;
        try {
            constructor = clz.getConstructor(EventManager.class, Vehicle.class);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new UnsupportedOperationException(e);
        }

        registerClass(clz, (eventManager, vehicle) -> {
            try {
                return constructor.newInstance(eventManager, vehicle);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public <T extends VehicleLifecycleObject> void registerClass(Class<T> clz, VehicleLifecycleObjectFactory<T> factory) {
        if (objectFactories.containsKey(clz)) return;

        Vehicle.get().forEach((vehicle) ->
        {
            Map<Class<?>, VehicleLifecycleObject> vehicleLifecycleObjects = holder.get(vehicle);

            VehicleLifecycleObject object = factory.create(eventManagerNode, vehicle);
            if (vehicleLifecycleObjects == null) return;
            vehicleLifecycleObjects.put(clz, object);
            object.init();
        });

        objectFactories.put(clz, factory);
    }

    public <T extends VehicleLifecycleObject> void unregisterClass(Class<T> clz) {
        if (!objectFactories.containsKey(clz)) return;

        Vehicle.get().forEach((vehicle) ->
        {
            Map<Class<?>, VehicleLifecycleObject> vehicleLifecycleObjects = holder.get(vehicle);
            VehicleLifecycleObject object = vehicleLifecycleObjects.get(clz);
            vehicleLifecycleObjects.remove(clz);
            object.destroy();
        });

        objectFactories.remove(clz);
    }

    public <T extends VehicleLifecycleObject> T getObject(Vehicle vehicle, Class<T> clz) {
        if (!objectFactories.containsKey(clz)) return null;

        Map<Class<?>, VehicleLifecycleObject> vehicleLifecycleObjects = holder.get(vehicle);
        if (vehicleLifecycleObjects == null) return null;

        return clz.cast(vehicleLifecycleObjects.get(clz));
    }

    public <T extends VehicleLifecycleObject> Collection<T> getObjects(Class<T> clz) {
        if (!objectFactories.containsKey(clz)) return Collections.emptyList();

        Collection<T> objects = new LinkedList<>();
        holder.values().forEach((m) -> objects.add(clz.cast(m.get(clz))));

        return objects;
    }
}
