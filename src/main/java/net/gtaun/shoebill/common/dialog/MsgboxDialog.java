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

import net.gtaun.shoebill.constant.DialogStyle;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * @author MK124
 */
public class MsgboxDialog extends AbstractDialog {
    @SuppressWarnings("unchecked")
    public static abstract class AbstractMsgboxDialogBuilder
            <DialogType extends MsgboxDialog, DialogBuilderType extends AbstractMsgboxDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractDialogBuilder<DialogType, DialogBuilderType> {
        protected AbstractMsgboxDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType message(DialogTextSupplier messageSupplier) {
            dialog.setMessage(messageSupplier);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType message(String message) {
            dialog.setMessage(message);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType onClickOk(ClickOkHandler handler) {
            dialog.setClickOkHandler(handler);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType line(String line) {
            dialog.addLine(line);
            return (DialogBuilderType) this;
        }
    }

    public static class MsgboxDialogBuilder extends AbstractMsgboxDialogBuilder<MsgboxDialog, MsgboxDialogBuilder> {
        private MsgboxDialogBuilder(Player player, EventManager parentEventManager) {
            super(new MsgboxDialog(player, parentEventManager));
        }
    }

    public static AbstractMsgboxDialogBuilder<?, ?> create(Player player, EventManager parentEventManager) {
        return new MsgboxDialogBuilder(player, parentEventManager);
    }

    @FunctionalInterface
    public interface ClickOkHandler {
        void onClickOk(MsgboxDialog dialog);
    }


    private DialogTextSupplier messageSupplier = (d) -> "-";
    private ClickOkHandler clickOkHandler = null;
    private Collection<String> lines;

    protected MsgboxDialog(Player player, EventManager parentEventManager) {
        super(DialogStyle.MSGBOX, player, parentEventManager);
        lines = new ArrayList<>();
    }

    public MsgboxDialog(Player player, EventManager parentEventManager, String caption, String message) {
        super(DialogStyle.MSGBOX, player, parentEventManager);
        setCaption(caption);
        setMessage(message);
    }

    public MsgboxDialog(Player player, EventManager parentEventManager, Supplier<String> captionSupplier, Supplier<String> messageSupplier) {
        super(DialogStyle.MSGBOX, player, parentEventManager);
        setCaption(captionSupplier);
        setMessage(messageSupplier);
    }

    public MsgboxDialog(Player player, EventManager parentEventManager, DialogTextSupplier captionSupplier, DialogTextSupplier messageSupplier) {
        super(DialogStyle.MSGBOX, player, parentEventManager);
        setCaption(captionSupplier);
        setMessage(messageSupplier);
    }

    public void addLine(String message) {
        lines.add(message);
        StringBuilder stringBuilder = new StringBuilder("");
        Iterator<String> iterator = lines.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            stringBuilder.append(line);
            if (iterator.hasNext()) stringBuilder.append("\n");
        }
        setMessage(stringBuilder.toString());
    }

    public void setMessage(String message) {
        this.messageSupplier = (d) -> message;
    }

    public void setMessage(Supplier<String> messageSupplier) {
        this.messageSupplier = (d) -> messageSupplier.get();
    }

    public void setMessage(DialogTextSupplier messageSupplier) {
        this.messageSupplier = messageSupplier;
    }

    public String getMessage() {
        return this.messageSupplier.get(this);
    }

    public void setClickOkHandler(ClickOkHandler onClickOkHandler) {
        this.clickOkHandler = onClickOkHandler;
    }

    @Override
    public void show() {
        show(messageSupplier.get(this));
    }

    @Override
    final void onClickOk(DialogResponseEvent event) {
        onClickOk();
    }

    protected void onClickOk() {
        if (clickOkHandler != null) clickOkHandler.onClickOk(this);
    }
}
