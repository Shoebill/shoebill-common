package net.gtaun.shoebill.common.dialog;

import net.gtaun.shoebill.event.dialog.DialogCloseEvent;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.Attentions;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;

/**
 * Created by marvin on 07.02.15 in project shoebill-common.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
public class TimedInputDialog extends InputDialog {

    public static abstract class AbstractTimedInputDialogBuilder
            <DialogType extends TimedInputDialog, DialogBuilderType extends AbstractTimedInputDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractInputDialogBuilder<DialogType, DialogBuilderType> {
        protected AbstractTimedInputDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType blockedTime(int seconds) {
            dialog.setBlockedTime(seconds);
            return (DialogBuilderType) this;
        }
    }

    public static class TimedInputDialogBuilder extends AbstractTimedInputDialogBuilder<TimedInputDialog, TimedInputDialogBuilder> {
        private TimedInputDialogBuilder(Player player, EventManager parentEventManager) {
            super(new TimedInputDialog(player, parentEventManager));
        }

        private TimedInputDialogBuilder(Player player, EventManager parentEventManager, boolean passwordMode) {
            super(new TimedInputDialog(player, parentEventManager, passwordMode));
        }
    }

    public static AbstractTimedInputDialogBuilder<?, ?> create(Player player, EventManager parentEventManager) {
        return new TimedInputDialogBuilder(player, parentEventManager);
    }

    public static AbstractTimedInputDialogBuilder<?, ?> create(Player player, EventManager parentEventManager, boolean passwordMode) {
        return new TimedInputDialogBuilder(player, parentEventManager, passwordMode);
    }

    private int blockedTime;
    private Timer timer;
    private int currentTime;
    private String originalAccept = "";

    public TimedInputDialog(Player player, EventManager parentEventManager) {
        super(player, parentEventManager);
    }

    public TimedInputDialog(Player player, EventManager parentEventManager, boolean passwordMode) {
        super(player, parentEventManager, passwordMode);
    }

    public TimedInputDialog(Player player, EventManager parentEventManager, String caption, String message) {
        super(player, parentEventManager, caption, message);
    }

    public TimedInputDialog(Player player, EventManager parentEventManager, String caption, String message, boolean passwordMode) {
        super(player, parentEventManager, caption, message, passwordMode);
    }

    public TimedInputDialog(Player player, EventManager parentEventManager, DialogTextSupplier captionSupplier, DialogTextSupplier messageSupplier) {
        super(player, parentEventManager, captionSupplier, messageSupplier);
    }

    public TimedInputDialog(Player player, EventManager parentEventManager, DialogTextSupplier captionSupplier, DialogTextSupplier messageSupplier, boolean passwordMode) {
        super(player, parentEventManager, captionSupplier, messageSupplier, passwordMode);
    }

    public void setBlockedTime(int blockedTime) {
        this.blockedTime = blockedTime;
    }

    public int getBlockedTime() {
        return blockedTime;
    }

    @Override
    public void show() {
        currentTime = blockedTime * 1000;
        originalAccept = getButtonOk();
        timer = Timer.create(1000, (i) -> {
            currentTime -= 1000;
            player.cancelDialog();
            if (currentTime <= 0) {
                setButtonOk(originalAccept);
                originalAccept = "";
                showDialog();
                timer.stop();
                timer.destroy();
            } else lowerSecond();
        });
        timer.start();
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
        timer.destroy();
    }

    @Override
    protected void destroy() {
        super.destroy();
        timer.destroy();
    }
}
