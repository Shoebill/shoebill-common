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

package net.gtaun.shoebill.common.dialog;

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
		
		public boolean isEnabled()
		{
			return true;
		}
		
		public abstract void onItemSelect();
	}
	
	public static abstract class DialogListItemSwitch extends DialogListItem
	{
		private final String string;
		private final String onMessage;
		private final String offMessage;
		
		private boolean switched;

		public DialogListItemSwitch(String string, boolean switched, String onMessage, String offMessage)
		{
			this.string = string;
			this.switched = switched;
			this.onMessage = onMessage;
			this.offMessage = offMessage;
		}
		
		public DialogListItemSwitch(String string, boolean switched)
		{
			this.string = string;
			this.switched = switched;
			this.onMessage = "ON";
			this.offMessage = "OFF";
		}
		
		public DialogListItemSwitch(String string)
		{
			this.string = string;
			this.onMessage = "ON";
			this.offMessage = "OFF";
		}
		
		public String toItemString()
		{
			return string + " [" + (switched ? onMessage : offMessage) + "]";
		}
		
		public boolean isEnabled()
		{
			return true;
		}
		
		public boolean isSwitched()
		{
			return switched;
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
