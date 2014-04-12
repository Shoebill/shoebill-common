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

import net.gtaun.shoebill.constant.DialogStyle;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * 抽象输入对话框类。
 * 
 * @author MK124
 */
public abstract class InputDialog extends AbstractDialog
{
	private final boolean passwordMode;
	
	
	protected String message = "None";
	
	
	public InputDialog(Player player, EventManager rootEventManager)
	{
		this(player, rootEventManager, null);
	}

	public InputDialog(Player player, EventManager rootEventManager, AbstractDialog parentDialog)
	{
		super(DialogStyle.INPUT, player, rootEventManager, parentDialog);
		this.passwordMode = false;
	}

	public InputDialog(Player player, EventManager rootEventManager, boolean passwordMode)
	{
		this(player, rootEventManager, null, passwordMode);
	}
	
	public InputDialog(Player player, EventManager rootEventManager, AbstractDialog parentDialog, boolean passwordMode)
	{
		super(passwordMode ? DialogStyle.PASSWORD : DialogStyle.INPUT, player, rootEventManager, parentDialog);
		this.passwordMode = passwordMode;
	}

	public InputDialog(Player player, EventManager rootEventManager, String caption, String message)
	{
		this(player, rootEventManager, null, caption, message);
	}

	public InputDialog(Player player, EventManager rootEventManager, AbstractDialog parentDialog, String caption, String message)
	{
		super(DialogStyle.INPUT, player, rootEventManager, parentDialog);
		this.passwordMode = false;
		this.caption = caption;
		this.message = message;
	}

	public InputDialog(Player player, EventManager rootEventManager, String caption, String message, boolean passwordMode)
	{
		this(player, rootEventManager, null, caption, message, passwordMode);
	}
	
	public InputDialog(Player player, EventManager rootEventManager, AbstractDialog parentDialog, String caption, String message, boolean passwordMode)
	{
		super(passwordMode ? DialogStyle.PASSWORD : DialogStyle.INPUT, player, rootEventManager, parentDialog);
		this.passwordMode = passwordMode;
		this.caption = caption;
		this.message = message;
	}
	
	public boolean isPasswordMode()
	{
		return passwordMode;
	}
	
	@Override
	public void show()
	{
		show(message);
	}
	
	@Override
	final void onClickOk(DialogResponseEvent event)
	{
		onClickOk(event.getInputText());
	}
	
	public void onClickOk(String inputText)
	{
		
	}
}
