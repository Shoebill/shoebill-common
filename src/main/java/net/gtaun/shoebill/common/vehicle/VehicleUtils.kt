/**
 * Copyright (C) 2013 MK124

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.shoebill.common.vehicle

import net.gtaun.shoebill.constant.PlayerState
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.entities.Vehicle

object VehicleUtils {

    @JvmStatic
    fun isVehicleDriver(vehicle: Vehicle, player: Player): Boolean =
            player.vehicle === vehicle && player.state == PlayerState.DRIVER

    @JvmStatic
    fun getVehicleDriver(vehicle: Vehicle): Player? =
            Player.get().filter { it.state == PlayerState.DRIVER && it.vehicle == vehicle }.firstOrNull()

    @JvmStatic
    fun getVehiclePassengers(vehicle: Vehicle): List<Player> {
        return Player.get().filter({ it.state == PlayerState.PASSENGER && it.vehicle === vehicle })
                .sortedBy { it.vehicleSeat }
    }

    @JvmStatic
    var Vehicle.driver: Player?
        get() = getVehicleDriver(this)
        set(player) {
            player?.setVehicle(this, 0)
        }

    @JvmStatic
    var Vehicle.passengers: List<Player>
        get() = getVehiclePassengers(this)
        set(value) {
            value.forEachIndexed { i, player ->
                player.setVehicle(this, i + 1)
            }
        }
}