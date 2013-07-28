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

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.DialogStyle;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * 抽象消息对话框类。
 * 
 * @author MK124
 */
public abstract class AbstractMsgboxDialog extends AbstractDialog
{
	protected AbstractMsgboxDialog(Player player, Shoebill shoebill, EventManager rootEventManager)
	{
		this(player, shoebill, rootEventManager, null);
	}
	
	protected AbstractMsgboxDialog(Player player, Shoebill shoebill, EventManager rootEventManager, AbstractDialog parentDialog)
	{
		super(DialogStyle.MSGBOX, player, shoebill, rootEventManager, parentDialog);
	}
	
	@Override
	final void onClickOk(DialogResponseEvent event)
	{
		onClickOk();
	}
	
	protected void onClickOk()
	{
		
	}
}
