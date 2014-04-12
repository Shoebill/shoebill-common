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

public abstract class ListDialogItemCheck extends ListDialogItem
{
	public abstract class CheckItem
	{
		protected String itemString;
		protected Color checkedColor;

		public CheckItem(String itemString, Color checkedColor)
		{
			this.itemString = itemString;
			this.checkedColor = checkedColor;
		}
		
		public CheckItem(String itemString)
		{
			this(itemString, Color.GREEN);
		}
		
		public String getItemString()
		{
			return itemString;
		}
		
		public abstract boolean isChecked();
	}
	
	
	private final List<CheckItem> options;
	private Color uncheckedColor;

	
	public ListDialogItemCheck(String itemText, Color uncheckedColor)
	{
		super(itemText);
		this.uncheckedColor = uncheckedColor;
		this.options = new ArrayList<>();
	}
	
	public ListDialogItemCheck(String itemText)
	{
		this(itemText, Color.GRAY);
	}
	
	public void addItem(CheckItem item)
	{
		options.add(item);
	}
	
	public String getItemText()
	{
		String text = super.getItemText();
		for(CheckItem item : options)
		{
			if (item.isChecked()) text += item.checkedColor.toEmbeddingString() + " [" + item.getItemString() + "]";
			else text += uncheckedColor.toEmbeddingString() + " [" + item.getItemString() + "]";
		}
		return text;
	}
	
	public boolean isEnabled()
	{
		return true;
	}

	public abstract void onItemSelect();
}