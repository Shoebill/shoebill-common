/**
 * Copyright (C) 2012 MK124
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.gtaun.wl.common;

import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;

/**
 * 未来插件组件抽象类。
 * 
 * @author MK124
 */
public abstract class WlPlugin extends Plugin
{
	protected WlPlugin()
	{
		
	}
	
	public boolean configure(Player player)
	{
		return false;
	}
}
