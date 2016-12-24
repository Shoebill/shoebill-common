package net.gtaun.shoebill.common.vehicle

import net.gtaun.shoebill.Shoebill
import net.gtaun.shoebill.common.LifecycleHolder
import net.gtaun.shoebill.entities.Vehicle
import net.gtaun.shoebill.event.destroyable.DestroyEvent
import net.gtaun.shoebill.event.vehicle.VehicleCreateEvent
import net.gtaun.util.event.EventManager
import net.gtaun.util.event.HandlerPriority
import kotlin.reflect.KClass

class VehicleLifecycleHolder @JvmOverloads constructor(eventManager: EventManager = Shoebill.get().eventManager) :
        LifecycleHolder<Vehicle>(eventManager) {

    init {
        eventManagerNode.registerHandler(VehicleCreateEvent::class, {
            buildObjects(it.vehicle)
        }, HandlerPriority.MONITOR)

        eventManagerNode.registerHandler(DestroyEvent::class, {
            if (it.destroyable is Vehicle) {
                destroyObjects(it.destroyable as Vehicle)
            }
        }, HandlerPriority.BOTTOM)
    }

    inline fun <reified B : VehicleLifecycleObject> registerClass(lifecycleObject: Class<B>) =
            registerClass(lifecycleObject, makeFactory(B::class))


    companion object {

        fun <B : VehicleLifecycleObject> makeFactory(clazz: KClass<B>) = object : LifecycleFactory<Vehicle, B> {
            override fun create(input: Vehicle): B {
                val constructor = clazz.constructors.filter {
                    it.parameters.size == 1 &&
                            it.parameters.first().type == Vehicle::class
                }
                        .firstOrNull() ?: throw Exception("No valid constructor available for class $clazz.")
                return constructor.call(input)
            }

        }

    }

}
