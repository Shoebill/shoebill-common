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
import java.util.function.BooleanSupplier;

import net.gtaun.shoebill.data.Color;

public class ListDialogItemCheck extends ListDialogItem
{
	public static class ItemCheckBuilder extends AbstractItemBuilder<ListDialogItemCheck, ItemCheckBuilder>
	{
		private ItemCheckBuilder()
		{
			super(new ListDialogItemCheck("Unnamed"));
		}
		
		public ItemCheckBuilder item(CheckItem checkItem)
		{
			item.addItem(checkItem);
			return (ItemCheckBuilder) this;
		}
		
		public ItemCheckBuilder item(String itemText, Color checkedColor, BooleanSupplier statusSupplier)
		{
			item.addItem(new CheckItem(itemText, checkedColor, statusSupplier));
			return (ItemCheckBuilder) this;
		}

		public ItemCheckBuilder item(String itemText, BooleanSupplier statusSupplier)
		{
			item.addItem(new CheckItem(itemText, statusSupplier));
			return (ItemCheckBuilder) this;
		}
	}
	
	public static ItemCheckBuilder create()
	{
		return new ItemCheckBuilder();
	}
	
	
	public static class CheckItem
	{
		protected String itemText;
		protected Color checkedColor;
		
		private BooleanSupplier statusSupplier;

		public CheckItem(String itemText, Color checkedColor, BooleanSupplier statusSupplier)
		{
			this.itemText = itemText;
			this.checkedColor = checkedColor;
			this.statusSupplier = statusSupplier;
		}
		
		public CheckItem(String itemText, BooleanSupplier statusSupplier)
		{
			this(itemText, null, statusSupplier);
		}

		protected CheckItem(String itemText, Color checkedColor)
		{
			this(itemText, checkedColor, null);
		}
		
		protected CheckItem(String itemText)
		{
			this(itemText, null, null);
		}
		
		public void setStatusSupplier(BooleanSupplier statusSupplier)
		{
			this.statusSupplier = statusSupplier;
		}
		
		public String getItemText()
		{
			return itemText;
		}
		
		public boolean isChecked()
		{
			if (statusSupplier == null) return false;
			return statusSupplier.getAsBoolean();
		}
	}
	
	
	private final List<CheckItem> options;

	private ConditionSupplier<Color> checkItemColorSupplier;
	
	
	public ListDialogItemCheck(String itemText)
	{
		this(itemText, Color.GRAY);
	}
	
	public ListDialogItemCheck(String itemText, Color uncheckedColor)
	{
		this(itemText, Color.GREEN, uncheckedColor);
	}
	
	public ListDialogItemCheck(String itemText, Color checkedColor, Color uncheckedColor)
	{
		super(itemText);
		this.options = new ArrayList<>();
		setCheckItemColorSupplier(checkedColor, uncheckedColor);
	}
	
	public void addItem(CheckItem item)
	{
		options.add(item);
	}
	
	public void setCheckItemColorSupplier(Color checkedColor, Color uncheckedColor)
	{
		checkItemColorSupplier = (c) -> c ? checkedColor : uncheckedColor;
	}
	
	@Override
	public String getItemText()
	{
		String text = super.getItemText();
		for(CheckItem item : options)
		{
			if (item.isChecked())
			{
				if (item.checkedColor != null) text += item.checkedColor.toEmbeddingString() + " [" + item.getItemText() + "]";
				else text += checkItemColorSupplier.get(true).toEmbeddingString() + " [" + item.getItemText() + "]";
			}
			else text += checkItemColorSupplier.get(false).toEmbeddingString() + " [" + item.getItemText() + "]";
		}
		return text;
	}
}