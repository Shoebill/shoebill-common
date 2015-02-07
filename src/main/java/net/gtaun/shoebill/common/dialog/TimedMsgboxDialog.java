package net.gtaun.shoebill.common.dialog;

import net.gtaun.shoebill.event.dialog.DialogCloseEvent;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.Attentions;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;

import java.util.function.Supplier;

/**
 * Created by marvin on 07.02.15 in project shoebill-common.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
public class TimedMsgboxDialog extends MsgboxDialog {

    public static abstract class AbstractTimedMsgboxDialogBuilder
            <DialogType extends TimedMsgboxDialog, DialogBuilderType extends AbstractTimedMsgboxDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractMsgboxDialogBuilder<DialogType, DialogBuilderType> {
        protected AbstractTimedMsgboxDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType blockedTime(int seconds) {
            dialog.setBlockedTime(seconds);
            return (DialogBuilderType) this;
        }
    }

    public static class TimedMsgboxDialogBuilder extends AbstractTimedMsgboxDialogBuilder<TimedMsgboxDialog, TimedMsgboxDialogBuilder> {
        private TimedMsgboxDialogBuilder(Player player, EventManager rootEventManager) {
            super(new TimedMsgboxDialog(player, rootEventManager));
        }
    }

    public static AbstractTimedMsgboxDialogBuilder<?, ?> create(Player player, EventManager rootEventManager) {
        return new TimedMsgboxDialogBuilder(player, rootEventManager);
    }

    private int blockedTime;
    private Timer blockTimer;
    private int currentTime;
    private String originalAccept = "";

    protected TimedMsgboxDialog(Player player, EventManager rootEventManager) {
        super(player, rootEventManager);
    }

    public TimedMsgboxDialog(Player player, EventManager rootEventManager, String caption, String message) {
        super(player, rootEventManager, caption, message);
    }

    public TimedMsgboxDialog(Player player, EventManager rootEventManager, Supplier<String> captionSupplier, Supplier<String> messageSupplier) {
        super(player, rootEventManager, captionSupplier, messageSupplier);
    }

    public TimedMsgboxDialog(Player player, EventManager rootEventManager, DialogTextSupplier captionSupplier, DialogTextSupplier messageSupplier) {
        super(player, rootEventManager, captionSupplier, messageSupplier);
    }

    public int getBlockedTime() {
        return blockedTime;
    }

    public void setBlockedTime(int blockedTime) {
        this.blockedTime = blockedTime;
    }

    @Override
    public void show() {
        currentTime = blockedTime * 1000;
        originalAccept = getButtonOk();
        blockTimer = Timer.create(1000, (i) -> {
            currentTime -= 1000;
            player.cancelDialog();
            if (currentTime <= 0) {
                setButtonOk(originalAccept);
                originalAccept = "";
                showDialog();
                blockTimer.stop();
                blockTimer.destroy();
            } else lowerSecond();
        });
        blockTimer.start();
        lowerSecond();
    }

    private void lowerSecond() {
        setButtonOk(String.valueOf(currentTime / 1000));
        showDialog();
    }

    private void showDialog() {
        onShow();

        eventManagerNode.cancelAll();
        eventManagerNode.registerHandler(DialogResponseEvent.class, HandlerPriority.NORMAL, Attentions.create().object(getDialogId()), (e) ->
        {

            if (currentTime <= 0) {
                eventManagerNode.cancelAll();
                if (e.getDialogResponse() == 1) {
                    onClickOk(e);
                } else {
                    onClickCancel();
                }
                onClose(DialogCloseEvent.DialogCloseType.RESPOND);
            } else showDialog();
        });

        eventManagerNode.registerHandler(DialogCloseEvent.class, HandlerPriority.NORMAL, Attentions.create().object(getDialogId()), (e) ->
        {
            if (e.getType() != DialogCloseEvent.DialogCloseType.RESPOND) eventManagerNode.cancelAll();
            onClose(e.getType());
        });

        player.showDialog(getDialogId(), getStyle(), getCaption(), getMessage(), getButtonOk(), getButtonCancel());
    }

    @Override
    protected void onClickCancel() {
        super.onClickCancel();
        blockTimer.destroy();
    }

    @Override
    protected void destroy() {
        super.destroy();
        blockTimer.destroy();
    }
}
