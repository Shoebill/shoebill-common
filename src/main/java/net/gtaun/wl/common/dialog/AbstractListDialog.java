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

package net.gtaun.wl.common.dialog;

import java.util.ArrayList;
import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.DialogStyle;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * 抽象列表对话框类。
 * 
 * @author MK124
 */
public abstract class AbstractListDialog extends AbstractDialog
{
	public static abstract class DialogListItem
	{
		private final String string;
		
		public DialogListItem()
		{
			this("-");
		}
		
		public DialogListItem(String string)
		{
			this.string = string;
		}
		
		public String toItemString()
		{
			return string;
		}
		
		public abstract void onItemSelect();
	}
	

	protected final List<DialogListItem> dialogListItems;
	protected final List<DialogListItem> displayedItems;
	
	
	protected AbstractListDialog(Player player, Shoebill shoebill, EventManager eventManager)
	{
		super(DialogStyle.LIST, player, shoebill, eventManager);
		dialogListItems = new ArrayList<>();
		displayedItems = new ArrayList<>();
	}
	
	@Override
	public void show()
	{
		String listStr = "";
		displayedItems.clear();
		
		for (DialogListItem item : dialogListItems)
		{
			listStr += item.toItemString() + "\n";
			displayedItems.add(item);
		}
		
		show(listStr);
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 1)
		{
			int itemId = event.getListitem();
			DialogListItem item = displayedItems.get(itemId);
			
			item.onItemSelect();
		}
		else
		{
			destroy();
		}
	}
}
