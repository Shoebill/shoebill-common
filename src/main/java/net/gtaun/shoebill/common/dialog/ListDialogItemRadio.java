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
import java.util.function.IntSupplier;

import net.gtaun.shoebill.data.Color;

public class ListDialogItemRadio extends ListDialogItem
{
	public static class ItemRadioBuilder extends AbstractItemBuilder<ListDialogItemRadio, ItemRadioBuilder>
	{
		private ItemRadioBuilder()
		{
			super(new ListDialogItemRadio("Unnamed"));
		}

		public ItemRadioBuilder item(RadioItem radioItem)
		{
			item.addItem(radioItem);
			return (ItemRadioBuilder) this;
		}

		public ItemRadioBuilder item(String itemText, Color checkedColor, RadioItemSelectHandler selectHandler)
		{
			item.addItem(new RadioItem(itemText, checkedColor, selectHandler));
			return (ItemRadioBuilder) this;
		}

		public ItemRadioBuilder item(String itemText, Color checkedColor)
		{
			item.addItem(new RadioItem(itemText, checkedColor));
			return (ItemRadioBuilder) this;
		}

		public ItemRadioBuilder item(String itemText, RadioItemSelectHandler selectHandler)
		{
			item.addItem(new RadioItem(itemText, selectHandler));
			return (ItemRadioBuilder) this;
		}

		public ItemRadioBuilder item(String itemText)
		{
			item.addItem(new RadioItem(itemText));
			return (ItemRadioBuilder) this;
		}

		public ItemRadioBuilder radioColor(Color checkedColor, Color uncheckedColor)
		{
			item.setRadioColor(checkedColor, uncheckedColor);
			return (ItemRadioBuilder) this;
		}

		public ItemRadioBuilder selectedIndex(IntSupplier indexSupplier)
		{
			item.setSelectedIndexSupplier(indexSupplier);
			return (ItemRadioBuilder) this;
		}

		public ItemRadioBuilder onRadioItemSelect(ItemSelectHandler selectHandler)
		{
			item.setRadioItemSelectHandler(selectHandler);
			return (ItemRadioBuilder) this;
		}
	}

	public static ItemRadioBuilder create()
	{
		return new ItemRadioBuilder();
	}


	public static class RadioItem
	{
		private String itemText;
		private Color checkedColor;
		private RadioItemSelectHandler selectHandler;

		public RadioItem(String itemString, Color checkedColor, RadioItemSelectHandler selectHandler)
		{
			this.itemText = itemString;
			this.checkedColor = checkedColor;
			this.selectHandler = selectHandler;
		}

		public RadioItem(String itemString, Color checkedColor)
		{
			this.itemText = itemString;
			this.checkedColor = checkedColor;
		}

		public RadioItem(String itemString, RadioItemSelectHandler selectHandler)
		{
			this(itemString, null, selectHandler);
		}

		public RadioItem(String itemString)
		{
			this(itemString, null, null);
		}

		public String getItemText()
		{
			return itemText;
		}

		public void onSelect()
		{
			if (selectHandler != null) selectHandler.onSelect();
		}
	}


	@FunctionalInterface
	public interface ItemSelectHandler
	{
		void onSelect(RadioItem item, int index);
	}

	@FunctionalInterface
	public interface RadioItemSelectHandler
	{
		void onSelect();
	}


	private final List<RadioItem> options;

	private ConditionSupplier<Color> radioItemColorSupplier;

	private IntSupplier selectedIndexSupplier;
	private ItemSelectHandler radioItemSelectHandler;


	public ListDialogItemRadio(String itemText)
	{
		this(itemText, Color.GRAY);
	}

	public ListDialogItemRadio(String itemText, Color uncheckedColor)
	{
		super(itemText);
		this.options = new ArrayList<>();
		setRadioColor(Color.GREEN, uncheckedColor);
	}

	public ListDialogItemRadio(String itemText, Color checkedColor, Color uncheckedColor)
	{
		super(itemText);
		this.options = new ArrayList<>();
		setRadioColor(checkedColor, uncheckedColor);
	}

	public void addItem(RadioItem item)
	{
		options.add(item);
	}

	public void setRadioColor(Color checkedColor, Color uncheckedColor)
	{
		radioItemColorSupplier = (c) -> c ? checkedColor : uncheckedColor;
	}

	public void setSelectedIndexSupplier(IntSupplier selectedSupplier)
	{
		this.selectedIndexSupplier = selectedSupplier;
	}

	public void setRadioItemSelectHandler(ItemSelectHandler itemSelectHandler)
	{
		this.radioItemSelectHandler = itemSelectHandler;
	}

	@Override
	public String getItemText()
	{
		String text = super.getItemText();
		for(int i=0; i<options.size(); i++)
		{
			int selected = getSelected();
			RadioItem item = options.get(i);
			if (i == selected)
			{
				if (item.checkedColor != null) text += item.checkedColor.toEmbeddingString() + " [" + item.getItemText() + "]";
				else text += radioItemColorSupplier.get(true).toEmbeddingString() + " [" + item.getItemText() + "]";
			}
			else text += radioItemColorSupplier.get(false).toEmbeddingString() + " [" + item.getItemText() + "]";
		}
		return text;
	}

	@Override
	public final void onItemSelect()
	{
		if (options.isEmpty()) return;
		int index = (getSelected() + 1) % options.size();
		RadioItem item = options.get(index);
		item.onSelect();
		onItemSelect(item, index);
		super.onItemSelect();
	}

	public void onItemSelect(RadioItem item, int index)
	{
		if (radioItemSelectHandler != null) radioItemSelectHandler.onSelect(item, index);
	}

	public int getSelected()
	{
		if (selectedIndexSupplier == null) return -1;
		return selectedIndexSupplier.getAsInt();
	}
}