package net.gtaun.shoebill.common.vehicle;

import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;

/**
 * Created by marvin on 01.11.14.
 */
public abstract class VehicleLifecycleObject extends AbstractShoebillContext
{
    protected final Vehicle vehicle;

    public VehicleLifecycleObject(EventManager eventManager, Vehicle vehicle)
    {
        super(eventManager);
        this.vehicle = vehicle;
    }

    public Vehicle getVehicle()
    {
        return vehicle;
    }
}
