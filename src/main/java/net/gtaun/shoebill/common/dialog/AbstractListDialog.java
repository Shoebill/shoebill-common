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
import java.util.Collections;
import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.DialogStyle;
import net.gtaun.shoebill.data.Color;
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
		private String string;
		
		public DialogListItem()
		{
			this("-");
		}
		
		public DialogListItem(String string)
		{
			this.string = string;
		}
		
		public void setItemString(String str)
		{
			string = str;
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
		
		public DialogListItemSwitch(String string, String onMessage, String offMessage)
		{
			this.string = string;
			this.onMessage = onMessage;
			this.offMessage = offMessage;
		}
		
		public DialogListItemSwitch(String string)
		{
			this.string = string;
			this.onMessage = "ON";
			this.offMessage = "OFF";
		}
		
		public String toItemString()
		{
			return string + " [" + (isSwitched() ? onMessage : offMessage) + "]";
		}
		
		public boolean isEnabled()
		{
			return true;
		}
		
		public abstract boolean isSwitched();
		public abstract void onItemSelect();
	}
	
	public static abstract class DialogListItemRadio extends DialogListItem
	{
		public class RadioItem
		{
			protected String itemString;

			public RadioItem()
			{
				this.itemString = " ";
			}
			
			public RadioItem(String itemString)
			{
				this.itemString = itemString;
			}
			
			public String getItemString()
			{
				return itemString;
			}
			
			public Color getCheckedColor()
			{
				return Color.GREEN;
			}
			
			public void onSelected()
			{
				
			}
		}
		
		private final String string;
		private final List<RadioItem> options;
		
		public DialogListItemRadio(String string)
		{
			this.string = string;
			this.options = new ArrayList<>();
		}
		
		public void addItem(RadioItem item)
		{
			options.add(item);
		}
		
		public String toItemString()
		{
			String str = string;
			for(int i=0; i<options.size(); i++)
			{
				int selected = getSelected();
				RadioItem item = options.get(i);
				if (i == selected) str += item.getCheckedColor().toEmbeddingString() + " [" + item.getItemString() + "]";
				else str += Color.GRAY.toEmbeddingString() + " [" + item.getItemString() + "]";
			}
			return str;
		}
		
		public boolean isEnabled()
		{
			return true;
		}
		
		@Override
		public final void onItemSelect()
		{
			if (options.isEmpty()) return;
			int index = (getSelected() + 1) % options.size();
			RadioItem item = options.get(index);
			item.onSelected();
			onItemSelect(item);
		}

		public void onItemSelect(RadioItem item)
		{
			
		}
		
		public abstract int getSelected();
	}
	
	public static abstract class DialogListItemCheck extends DialogListItem
	{
		public abstract class CheckItem
		{
			protected String itemString;

			public CheckItem()
			{
				this.itemString = " ";
			}
			
			public CheckItem(String itemString)
			{
				this.itemString = itemString;
			}
			
			public String getItemString()
			{
				return itemString;
			}
			
			public Color getCheckedColor()
			{
				return Color.GREEN;
			}
			
			public abstract boolean isChecked();
		}
		
		private final String string;
		private final List<CheckItem> options;
		
		public DialogListItemCheck(String string)
		{
			this.string = string;
			this.options = new ArrayList<>();
		}
		
		public void addItem(CheckItem item)
		{
			options.add(item);
		}
		
		public String toItemString()
		{
			String str = string;
			for(CheckItem item : options)
			{
				if (item.isChecked()) str += item.getCheckedColor().toEmbeddingString() + " [" + item.toString() + "]";
				else str += Color.GRAY.toEmbeddingString() + " [" + item.toString() + "]";
			}
			return str;
		}
		
		public boolean isEnabled()
		{
			return true;
		}

		public abstract void onItemSelect();
	}
	

	protected final List<DialogListItem> dialogListItems;
	final List<DialogListItem> displayedItems;
	

	protected AbstractListDialog(Player player, Shoebill shoebill, EventManager eventManager)
	{
		this(player, shoebill, eventManager, null);
	}
	
	protected AbstractListDialog(Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog)
	{
		super(DialogStyle.LIST, player, shoebill, eventManager, parentDialog);
		dialogListItems = new ArrayList<>();
		displayedItems = new ArrayList<>();
	}
	
	public List<DialogListItem> getDialogListItems()
	{
		return Collections.unmodifiableList(displayedItems);
	}
	
	@Override
	public void show()
	{
		String listStr = "";
		displayedItems.clear();
		
		for (DialogListItem item : dialogListItems)
		{
			if (item.isEnabled() == false) continue;
			
			listStr += item.toItemString() + "\n";
			displayedItems.add(item);
		}
		
		show(listStr);
	}
	
	@Override
	final void onClickOk(DialogResponseEvent event)
	{
		int itemId = event.getListitem();
		DialogListItem item = displayedItems.get(itemId);

		onClickOk(item);
		item.onItemSelect();
	}
	
	protected void onClickOk(DialogListItem item)
	{
		
	}
}
