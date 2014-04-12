/**
 * Copyright (C) 2012-2014 MK124
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

import net.gtaun.shoebill.data.Color;

public abstract class ListDialogItemRadio extends ListDialogItem
{
	public class RadioItem
	{
		protected String itemString;
		protected Color checkedColor;

		public RadioItem(String itemString, Color checkedColor)
		{
			this.itemString = itemString;
			this.checkedColor = checkedColor;
		}
		
		public RadioItem(String itemString)
		{
			this(itemString, Color.GREEN);
		}
		
		public String getItemString()
		{
			return itemString;
		}
		
		public void onSelected()
		{
			
		}
	}
	
	
	private final List<RadioItem> options;
	private Color uncheckedColor;


	public ListDialogItemRadio(String itemText)
	{
		this(itemText, Color.GRAY);
	}	
	
	public ListDialogItemRadio(String itemText, Color uncheckedColor)
	{
		super(itemText);
		this.uncheckedColor = uncheckedColor;
		this.options = new ArrayList<>();
	}
	
	public void addItem(RadioItem item)
	{
		options.add(item);
	}
	
	public String getItemText()
	{
		String text = super.getItemText();
		for(int i=0; i<options.size(); i++)
		{
			int selected = getSelected();
			RadioItem item = options.get(i);
			if (i == selected) text += item.checkedColor.toEmbeddingString() + " [" + item.getItemString() + "]";
			else text += uncheckedColor.toEmbeddingString() + " [" + item.getItemString() + "]";
		}
		return text;
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
		onItemSelect(item, index);
	}

	public void onItemSelect(RadioItem item, int itemIndex)
	{
		
	}
	
	public abstract int getSelected();
}