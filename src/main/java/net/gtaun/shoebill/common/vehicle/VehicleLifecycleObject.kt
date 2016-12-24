package net.gtaun.shoebill.common.vehicle

import net.gtaun.shoebill.Shoebill
import net.gtaun.shoebill.common.LifecycleObject
import net.gtaun.shoebill.entities.Vehicle

abstract class VehicleLifecycleObject(val vehicle: Vehicle) : LifecycleObject(Shoebill.get().eventManager)
