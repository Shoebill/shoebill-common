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

package net.gtaun.mk.common.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * 抽象带分页功能的列表对话框类。
 * 
 * @author MK124
 */
public class AbstractPageListDialog extends AbstractListDialog
{
	private int itemsPerPage = 10;
	private int currentPage;
	
	
	protected AbstractPageListDialog(Player player, Shoebill shoebill, EventManager eventManager)
	{
		super(player, shoebill, eventManager);
	}
	
	public int getItemsPerPage()
	{
		return itemsPerPage;
	}
	
	public void setItemsPerPage(int itemsPerPage)
	{
		this.itemsPerPage = itemsPerPage;
	}
	
	public int getCurrentPage()
	{
		return currentPage;
	}
	
	public void setCurrentPage(int currentPage)
	{
		this.currentPage = currentPage;
	}
	
	public int getMaxPage()
	{
		return (dialogListItems.size()-1) / itemsPerPage;
	}
	
	@Override
	public void show()
	{
		String listStr = "";
		displayedItems.clear();
		
		displayedItems.add(new DialogListItem(Color.GRAY.toEmbeddingString() + "<< Prev Page <<")
		{
			@Override
			public void onItemSelect()
			{
				if (currentPage > getMaxPage()) currentPage = getMaxPage();
				if (currentPage > 0) currentPage--;
				show();
			}
		});
		
		int offset = itemsPerPage * currentPage;
		for (int i=0; i<itemsPerPage; i++)
		{
			int index = offset+i;
			if (dialogListItems.size() <= index) break;
			
			DialogListItem item = dialogListItems.get(offset+i);
			displayedItems.add(item);
		}
		
		if (displayedItems.size() >= itemsPerPage + 1) displayedItems.add(new DialogListItem(Color.GRAY.toEmbeddingString() + ">> Next Page >>")
		{
			@Override
			public void onItemSelect()
			{
				if (currentPage > getMaxPage()) currentPage = getMaxPage();
				if (currentPage < getMaxPage()) currentPage++;
				show();
			}
		});

		for (DialogListItem item : displayedItems)
		{
			listStr += item.toItemString() + "\n";
		}
		show(listStr);
	}
}
