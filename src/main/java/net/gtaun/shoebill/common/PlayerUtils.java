/**
 * Copyright (C) 2012 MK124
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

package net.gtaun.shoebill.common;

import org.apache.commons.lang3.math.NumberUtils;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.object.Player;

/**
 * 玩家工具类。
 * 
 * @author MK124
 */
public final class PlayerUtils
{
	private PlayerUtils()
	{
		
	}
	
	public static Player getPlayer(int id)
	{
		return Shoebill.Instance.get().getSampObjectStore().getPlayer(id);
	}
	
	public static Player getPlayer(String name)
	{
		return Shoebill.Instance.get().getSampObjectStore().getPlayer(name);
	}
	
	public static Player getPlayerByNameOrId(String nameOrId)
	{
		Player player = getPlayer(nameOrId);
		if (player == null && NumberUtils.isDigits(nameOrId)) player = getPlayer(NumberUtils.toInt(nameOrId));
		
		return player;
	}
}
