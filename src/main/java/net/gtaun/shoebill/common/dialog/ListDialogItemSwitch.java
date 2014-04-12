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

import net.gtaun.shoebill.data.Color;

public class ListDialogItemSwitch extends ListDialogItem
{
	public static class ItemSwitchBuilder extends AbstractItemBuilder<ListDialogItemSwitch, ItemSwitchBuilder>
	{
		private ItemSwitchBuilder()
		{
			super(new ListDialogItemSwitch("Unnamed"));
		}
		
		public ItemSwitchBuilder data(Object data)
		{
			item.setData(data);
			return (ItemSwitchBuilder) this;
		}
		
		public ItemSwitchBuilder switchText(String on, String off)
		{
			item.setSwitchText(on, off);
			return (ItemSwitchBuilder) this;
		}
		
		public ItemSwitchBuilder switchColor(Color on, Color off)
		{
			item.setSwitchColor(on, off);
			return (ItemSwitchBuilder) this;
		}
		
		public ItemSwitchBuilder statusSupplier(BooleanSupplier statusSupplier)
		{
			item.setStatusSupplier(statusSupplier);
			return (ItemSwitchBuilder) this;
		}
		
		public ListDialogItemSwitch build()
		{
			return item;
		}
	}
	
	public static ItemSwitchBuilder create()
	{
		return new ItemSwitchBuilder();
	}
	
	
	@FunctionalInterface
	public interface ConditionSupplier<T>
	{
		T get(boolean condition);
	}

	private static final ConditionSupplier<String> DEFAULT_SWITCH_TEXT_SUPPLIER = (s) -> s ? "ON" : "OFF";
	private static final ConditionSupplier<Color> DEFAULT_SWITCH_COLOR_SUPPLIER = (s) -> s ? Color.GREEN : Color.GRAY;
	
	
	private ConditionSupplier<String> switchTextSupplier = DEFAULT_SWITCH_TEXT_SUPPLIER;
	private ConditionSupplier<Color> switchColorSupplier = DEFAULT_SWITCH_COLOR_SUPPLIER;
	
	private BooleanSupplier statusSupplier;
	

	public ListDialogItemSwitch(String itemText)
	{
		super(itemText);
	}
	
	public ListDialogItemSwitch(Supplier<String> textSupplier)
	{
		super(textSupplier);
	}
	
	public void setSwitchText(String on, String off)
	{
		switchTextSupplier = (s) -> s ? on : off;
	}
	
	public void setSwitchColor(Color on, Color off)
	{
		switchColorSupplier = (s) -> s ? on : off;
	}
	
	public void setStatusSupplier(BooleanSupplier switchedSupplier)
	{
		this.statusSupplier = switchedSupplier;
	}
	
	public String getItemText()
	{
		return super.getItemText() + switchColorSupplier.get(isSwitched()).toEmbeddingString() + " [" + switchTextSupplier.get(isSwitched()) + "]";
	}
	
	public boolean isEnabled()
	{
		return true;
	}
	
	public boolean isSwitched()
	{
		if (statusSupplier == null) return false;
		return true;
	}
}