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

import java.util.function.Supplier;

import net.gtaun.shoebill.SampObjectManager;
import net.gtaun.shoebill.constant.DialogStyle;
import net.gtaun.shoebill.event.dialog.DialogCloseEvent;
import net.gtaun.shoebill.event.dialog.DialogCloseEvent.DialogCloseType;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.DialogId;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.Attentions;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

/**
 * 
 * @author MK124
 */
public abstract class AbstractDialog
{
	@SuppressWarnings("unchecked")
	public static abstract class AbstractDialogBuilder
	<DialogType extends AbstractDialog, DialogBuilderType extends AbstractDialogBuilder<DialogType, DialogBuilderType>>
	{
		protected final DialogType dialog;
		
		
		protected AbstractDialogBuilder(DialogType dialog)
		{
			this.dialog = dialog;
		}
		
		public DialogBuilderType parentDialog(AbstractDialog parent)
		{
			dialog.setParentDialog(parent);
			return (DialogBuilderType) this;
		}
		
		public DialogBuilderType caption(String caption)
		{
			dialog.setCaption(caption);
			return (DialogBuilderType) this;
		}
		
		public DialogBuilderType caption(DialogTextSupplier captionSupplier)
		{
			dialog.setCaption(captionSupplier);
			return (DialogBuilderType) this;
		}
		
		public DialogBuilderType buttonOk(String buttonOk)
		{
			dialog.setButtonOk(buttonOk);
			return (DialogBuilderType) this;
		}
		
		public DialogBuilderType buttonOk(DialogTextSupplier buttonOkSupplier)
		{
			dialog.setButtonOk(buttonOkSupplier);
			return (DialogBuilderType) this;
		}
		
		public DialogBuilderType buttonCancel(String buttonCancel)
		{
			dialog.setButtonCancel(buttonCancel);
			return (DialogBuilderType) this;
		}
		
		public DialogBuilderType buttonCancel(DialogTextSupplier buttonCancelSupplier)
		{
			dialog.setButtonCancel(buttonCancelSupplier);
			return (DialogBuilderType) this;
		}
		
		public DialogBuilderType onClickCancel(DialogHandler handler)
		{
			dialog.setClickCancelHandler(handler);
			return (DialogBuilderType) this;
		}
		
		public DialogBuilderType onShow(DialogHandler handler)
		{
			dialog.setShowHandler(handler);
			return (DialogBuilderType) this;
		}
		
		public DialogBuilderType onClose(DialogCloseHandler handler)
		{
			dialog.setCloseHandler(handler);
			return (DialogBuilderType) this;
		}
		
		public DialogType build()
		{
			return dialog;
		}
	}
	
	
	@FunctionalInterface
	public interface DialogCloseHandler
	{
		void onClose(AbstractDialog dialog, DialogCloseType type);
	}
	
	
	protected final Player player;
	protected final EventManagerNode eventManagerNode;
	private final EventManagerNode eventManagerInternal;
	
	private final DialogId dialogId;
	private final DialogStyle style;

	private AbstractDialog parentDialog;
	
	private DialogTextSupplier captionSupplier = (d) -> "None";
	private DialogTextSupplier buttonOkSupplier = (d) -> "OK";
	private DialogTextSupplier buttonCancelSupplier = (d) -> "Cancel";

	private DialogHandler showHandler = null;
	private DialogCloseHandler closeHandler = null;
	private DialogHandler clickCancelHandler = null;
	
	
	AbstractDialog(DialogStyle style, Player player, EventManager rootEventManager)
	{
		this.style = style;
		this.player = player;
		
		eventManagerInternal = rootEventManager.createChildNode();
		eventManagerNode = rootEventManager.createChildNode();
		
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
		eventManagerInternal.destroy();
		eventManagerNode.destroy();
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public DialogStyle getStyle()
	{
		return style;
	}

	public AbstractDialog getParentDialog()
	{
		return parentDialog;
	}
	
	public void setParentDialog(AbstractDialog parentDialog)
	{
		this.parentDialog = parentDialog;
	}

	public void showParentDialog()
	{
		eventManagerNode.cancelAll();
		if (parentDialog != null) parentDialog.show();
	}
	
	public void setCaption(String caption)
	{
		captionSupplier = (d) -> caption;
	}
	
	public void setCaption(Supplier<String> captionSupplier)
	{
		this.captionSupplier = (d) -> captionSupplier.get();
	}
	
	public void setCaption(DialogTextSupplier captionSupplier)
	{
		this.captionSupplier = captionSupplier;
	}
	
	public String getCaption()
	{
		return captionSupplier.get(this);
	}
	
	public void setButtonOk(String buttonOk)
	{
		buttonOkSupplier = (d) -> buttonOk;
	}

	public void setButtonOk(Supplier<String> buttonOkSupplier)
	{
		this.buttonOkSupplier = (d) -> buttonOkSupplier.get();
	}

	public void setButtonOk(DialogTextSupplier buttonOkSupplier)
	{
		this.buttonOkSupplier = buttonOkSupplier;
	}
	
	public void setButtonCancel(String buttonCancel)
	{
		buttonCancelSupplier = (d) -> buttonCancel;
	}
	
	public void setButtonCancel(Supplier<String> buttonCancelSupplier)
	{
		this.buttonCancelSupplier = (d) -> buttonCancelSupplier.get();
	}
	
	public void setButtonCancel(DialogTextSupplier buttonCancelSupplier)
	{
		this.buttonCancelSupplier = buttonCancelSupplier;
	}
	
	public void setClickCancelHandler(DialogHandler onClickCancelHandler)
	{
		this.clickCancelHandler = onClickCancelHandler;
	}
	
	public void setShowHandler(DialogHandler showHandler)
	{
		this.showHandler = showHandler;
	}
	
	public void setCloseHandler(DialogCloseHandler closeHandler)
	{
		this.closeHandler = closeHandler;
	}
	
	protected void show(String text)
	{
		onShow();
		
		eventManagerInternal.cancelAll();
		eventManagerInternal.registerHandler(DialogResponseEvent.class, HandlerPriority.NORMAL, Attentions.create().object(dialogId), (e) ->
		{
			if (e.getDialogResponse() == 1)
			{
				onClickOk(e);
			}
			else
			{
				onClickCancel();
			}
			
			onClose(DialogCloseType.RESPOND);
		});

		eventManagerInternal.registerHandler(DialogCloseEvent.class, HandlerPriority.NORMAL, Attentions.create().object(dialogId), (e) ->
		{
			eventManagerInternal.cancelAll();
			onClose(e.getType());
		});
		
		player.showDialog(dialogId, style, captionSupplier.get(this), text, buttonOkSupplier.get(this), buttonCancelSupplier.get(this));
	}
	
	public abstract void show();
	
	protected void onShow()
	{
		if (showHandler != null) showHandler.handle(this);
	}

	protected void onClose(DialogCloseType type)
	{
		if (closeHandler != null) closeHandler.onClose(this, type);
	}

	void onClickOk(DialogResponseEvent event)
	{
		
	}
	
	protected void onClickCancel()
	{
		if (clickCancelHandler != null) clickCancelHandler.handle(this);
	}
}
