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

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ListDialogItem
{
	@SuppressWarnings("unchecked")
	public static abstract class AbstractItemBuilder
	<ItemType extends ListDialogItem, ItemBuilderType extends AbstractItemBuilder<ItemType, ItemBuilderType>>
	{
		protected final ItemType item;
		
		
		protected AbstractItemBuilder(ItemType item)
		{
			this.item = item;
		}
		
		public ItemBuilderType data(Object data)
		{
			item.setData(data);
			return (ItemBuilderType) this;
		}
		
		public ItemBuilderType itemText(String itemText)
		{
			item.setItemText(itemText);
			return (ItemBuilderType) this;
		}

		public ItemBuilderType itemText(Supplier<String> textSupplier)
		{
			item.setItemText(textSupplier);
			return (ItemBuilderType) this;
		}
		
		public ItemBuilderType itemText(ItemTextSupplier<?> textSupplier)
		{
			item.setItemText(textSupplier);
			return (ItemBuilderType) this;
		}

		public ItemBuilderType enabled(BooleanSupplier enabledSupplier)
		{
			item.setItemEnabledSupplier(enabledSupplier);
			return (ItemBuilderType) this;
		}
		
		public ItemBuilderType enabled(ItemBooleanSupplier<?> enabledSupplier)
		{
			item.setItemEnabledSupplier(enabledSupplier);
			return (ItemBuilderType) this;
		}
		
		public ItemBuilderType onSelect(ItemSelectSimpleHandler selectHandler)
		{
			item.setSelectHandler(selectHandler);
			return (ItemBuilderType) this;
		}
		
		public ItemBuilderType onSelect(ItemSelectHandler<?> selectHandler)
		{
			item.setSelectHandler(selectHandler);
			return (ItemBuilderType) this;
		}
		
		public ItemType build()
		{
			return item;
		}
	}
	
	public static class ItemBuilder extends AbstractItemBuilder<ListDialogItem, ItemBuilder>
	{
		private ItemBuilder()
		{
			super(new ListDialogItem("-"));
		}
	}
	
	public static AbstractItemBuilder<?, ?> create()
	{
		return new ItemBuilder();
	}
	
	
	@FunctionalInterface
	public interface ItemTextSupplier<ItemData>
	{
		String getItemText(ItemData data);
	}
	
	@FunctionalInterface
	public interface ItemBooleanSupplier<ItemData>
	{
		boolean getBoolean(ItemData data);
	}

	@FunctionalInterface
	public interface ItemSelectSimpleHandler
	{
		void onItemSelect(ListDialogItem item);
	}
	
	@FunctionalInterface
	public interface ItemSelectHandler<ItemData>
	{
		void onItemSelect(ListDialogItem item, ItemData data);
	}
	
	
	protected ListDialog currentDialog;
	
	protected Object data;
	protected ItemTextSupplier<Object> itemTextSupplier;
	protected ItemBooleanSupplier<Object> itemEnabledSupplier;
	protected ItemSelectHandler<Object> selectHandler;
	

	protected <DataType> ListDialogItem(String itemText)
	{
		setItemText(itemText);
	}
	
	protected <DataType> ListDialogItem(Supplier<String> textSupplier)
	{
		setItemText(textSupplier);
	}
	
	public ListDialogItem(String itemText, ItemSelectSimpleHandler handler)
	{
		setItemText(itemText);
		setSelectHandler(handler);
	}
	
	public ListDialogItem(Supplier<String> textSupplier, ItemSelectSimpleHandler handler)
	{
		setItemText(textSupplier);
		setSelectHandler(handler);
	}

	public ListDialogItem(String itemText, BooleanSupplier enabledSupplier, ItemSelectSimpleHandler handler)
	{
		setItemText(itemText);
		setItemEnabledSupplier(enabledSupplier);
		setSelectHandler(handler);
	}
	
	public ListDialogItem(Supplier<String> textSupplier, BooleanSupplier enabledSupplier, ItemSelectSimpleHandler handler)
	{
		setItemText(textSupplier);
		setItemEnabledSupplier(enabledSupplier);
		setSelectHandler(handler);
	}
	
	public <DataType> ListDialogItem(DataType data, String itemText, ItemSelectHandler<DataType> handler)
	{
		setData(data);
		setItemText(itemText);
		setSelectHandler(handler);
	}
	
	public <DataType> ListDialogItem(DataType data, ItemTextSupplier<DataType> textSupplier, ItemSelectHandler<DataType> handler)
	{
		setData(data);
		setItemText(textSupplier);
		setSelectHandler(handler);
	}
	
	public <DataType> ListDialogItem(DataType data, String itemText, ItemBooleanSupplier<DataType> enabledSupplier, ItemSelectHandler<DataType> handler)
	{
		setData(data);
		setItemText(itemText);
		setItemEnabledSupplier(enabledSupplier);
		setSelectHandler(handler);
	}
	
	public <DataType> ListDialogItem(DataType data, ItemTextSupplier<DataType> textSupplier, ItemBooleanSupplier<DataType> enabledSupplier, ItemSelectHandler<DataType> handler)
	{
		setData(data);
		setItemText(textSupplier);
		setItemEnabledSupplier(enabledSupplier);
		setSelectHandler(handler);
	}
	
	public ListDialog getCurrentDialog()
	{
		return currentDialog;
	}

	public void setData(Object data)
	{
		this.data = data;
	}
	
	public void setItemText(String itemText)
	{
		itemTextSupplier = (d) -> itemText;
	}

	public void setItemText(Supplier<String> textSupplier)
	{
		itemTextSupplier = (d) -> textSupplier.get();
	}
	
	@SuppressWarnings("unchecked")
	public void setItemText(ItemTextSupplier<?> textSupplier)
	{
		itemTextSupplier = (ItemTextSupplier<Object>) textSupplier;
	}
	
	public void setItemEnabledSupplier(BooleanSupplier enabledSupplier)
	{
		itemEnabledSupplier = (d) -> enabledSupplier.getAsBoolean();
	}
	
	@SuppressWarnings("unchecked")
	public void setItemEnabledSupplier(ItemBooleanSupplier<?> enabledSupplier)
	{
		itemEnabledSupplier = (ItemBooleanSupplier<Object>) enabledSupplier;
	}

	public void setSelectHandler(ItemSelectSimpleHandler selectHandler)
	{
		this.selectHandler = (i, d) -> selectHandler.onItemSelect(i);
	}
	
	@SuppressWarnings("unchecked")
	public void setSelectHandler(ItemSelectHandler<?> selectHandler)
	{
		this.selectHandler = (ItemSelectHandler<Object>) selectHandler;
	}
	
	public String getItemText()
	{
		return itemTextSupplier != null ? itemTextSupplier.getItemText(null) : "-";
	}
	
	public boolean isEnabled()
	{
		if (itemEnabledSupplier == null) return true;
		return itemEnabledSupplier.getBoolean(data);
	}
	
	public void onItemSelect()
	{
		if (selectHandler != null) selectHandler.onItemSelect(this, data);
	}
}
