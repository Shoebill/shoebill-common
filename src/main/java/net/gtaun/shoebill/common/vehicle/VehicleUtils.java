/**
 * Copyright (C) 2013 MK124
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.shoebill.common.vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

public final class VehicleUtils
{
	private VehicleUtils()
	{
		
	}

	public static boolean isVehicleDriver(Vehicle vehicle, Player player)
	{
		return player.getVehicle() == vehicle && player.getState() == PlayerState.DRIVER;
	}

	public static Player getVehicleDriver(Vehicle vehicle)
	{
		for (Player player : Shoebill.get().getSampObjectManager().getPlayers())
		{
			if (player.getVehicle() == vehicle && player.getState() == PlayerState.DRIVER) return player;
		}
		
		return null;
	}
	
	public static List<Player> getVehiclePassengers(Vehicle vehicle)
	{
		List<Player> passengers = Shoebill.get().getSampObjectManager().getPlayers().stream().filter(player -> player.getVehicle() == vehicle && player.getState() == PlayerState.PASSENGER).collect(Collectors.toList());

        Collections.sort(passengers, (o1, o2) -> o1.getVehicleSeat() - o2.getVehicleSeat());
		
		return passengers;
	}
}
