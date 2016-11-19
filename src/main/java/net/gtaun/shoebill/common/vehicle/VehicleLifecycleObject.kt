package net.gtaun.shoebill.common.vehicle

import net.gtaun.shoebill.common.AbstractShoebillContext
import net.gtaun.shoebill.entities.Vehicle
import net.gtaun.util.event.EventManager

abstract class VehicleLifecycleObject(eventManager: EventManager, val vehicle: Vehicle) :
        AbstractShoebillContext(eventManager)
