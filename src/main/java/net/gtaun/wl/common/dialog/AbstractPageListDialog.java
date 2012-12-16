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
