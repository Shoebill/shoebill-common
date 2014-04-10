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

import net.gtaun.shoebill.SampObjectFactory;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.DialogStyle;
import net.gtaun.shoebill.event.dialog.DialogCancelEvent;
import net.gtaun.shoebill.event.dialog.DialogCancelEvent.DialogCancelType;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.DialogId;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

/**
 * 抽象对话框类。
 * 
 * @author MK124
 */
public abstract class AbstractDialog
{
	protected final Shoebill shoebill;
	protected final Player player;
	protected final AbstractDialog parentDialog;

	protected final EventManagerNode eventManager;
	
	private final DialogId dialog;
	private final DialogStyle style;
	
	protected String caption = "None";
	protected String buttonOk = "OK";
	protected String buttonCancel = "Cancel";
	
	
	AbstractDialog(DialogStyle style, Player player, Shoebill shoebill, EventManager rootEventManager)
	{
		this(style, player, shoebill, rootEventManager, null);
	}
	
	AbstractDialog(DialogStyle style, Player player, Shoebill shoebill, EventManager rootEventManager, AbstractDialog parentDialog)
	{
		this.style = style;
		this.shoebill = shoebill;
		this.player = player;
		this.eventManager = rootEventManager.createChildNode();
		this.parentDialog = parentDialog;
		
		SampObjectFactory factory = shoebill.getSampObjectFactory();
		dialog = factory.createDialogId();
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}
	
	protected void destroy()
	{
		eventManager.destroy();
	}
	
	public void showParentDialog()
	{
		destroy();
		if (parentDialog != null) parentDialog.show();
	}
	
	protected void show(String text)
	{
		// FIXME
		eventManager.registerHandler(DialogResponseEvent.class, HandlerPriority.NORMAL, (DialogResponseEvent e) ->
		{
			eventManager.destroy();
			if (e.getDialogResponse() == 1)
			{
				onClickOk(e);
			}
			else
			{
				onClickCancel();
			}
		});

		// FIXME
		eventManager.registerHandler(DialogCancelEvent.class, HandlerPriority.NORMAL, (DialogCancelEvent e) ->
		{
			eventManager.destroy();
			AbstractDialog.this.onCancel(e.getType());
		});
		
		player.showDialog(dialog, style, caption, text, buttonOk, buttonCancel);
	}
	
	public abstract void show();
	
	protected void onCancel(DialogCancelType type)
	{
		
	}
	
	protected void onClickCancel()
	{
		
	}
	
	void onClickOk(DialogResponseEvent event)
	{
		
	}
}
