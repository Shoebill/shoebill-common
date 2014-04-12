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

import net.gtaun.shoebill.SampObjectManager;
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
	protected final Player player;
	protected final AbstractDialog parentDialog;

	protected final EventManagerNode eventManagerNode;
	
	private final DialogId dialogId;
	private final DialogStyle style;
	
	protected String caption = "None";
	protected String buttonOk = "OK";
	protected String buttonCancel = "Cancel";
	
	
	AbstractDialog(DialogStyle style, Player player, EventManager rootEventManager)
	{
		this(style, player, rootEventManager, null);
	}
	
	AbstractDialog(DialogStyle style, Player player, EventManager rootEventManager, AbstractDialog parentDialog)
	{
		this.style = style;
		this.player = player;
		this.eventManagerNode = rootEventManager.createChildNode();
		this.parentDialog = parentDialog;
		
		dialogId = SampObjectManager.get().createDialogId();
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}
	
	protected void destroy()
	{
		eventManagerNode.destroy();
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public AbstractDialog getParentDialog()
	{
		return parentDialog;
	}
	
	public DialogStyle getStyle()
	{
		return style;
	}
	
	public void setCaption(String caption)
	{
		this.caption = caption;
	}
	
	public String getCaption()
	{
		return caption;
	}
	
	public void setButtonOk(String buttonOk)
	{
		this.buttonOk = buttonOk;
	}
	
	public String getButtonOk()
	{
		return buttonOk;
	}
	
	public void setButtonCancel(String buttonCancel)
	{
		this.buttonCancel = buttonCancel;
	}
	
	public String getButtonCancel()
	{
		return buttonCancel;
	}
	
	public void showParentDialog()
	{
		destroy();
		if (parentDialog != null) parentDialog.show();
	}
	
	protected void show(String text)
	{
		// FIXME
		eventManagerNode.registerHandler(DialogResponseEvent.class, HandlerPriority.NORMAL, (DialogResponseEvent e) ->
		{
			eventManagerNode.destroy();
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
		eventManagerNode.registerHandler(DialogCancelEvent.class, HandlerPriority.NORMAL, (DialogCancelEvent e) ->
		{
			eventManagerNode.destroy();
			AbstractDialog.this.onCancel(e.getType());
		});
		
		player.showDialog(dialogId, style, caption, text, buttonOk, buttonCancel);
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
