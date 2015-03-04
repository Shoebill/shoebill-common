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
public class TimedListDialog extends ListDialog {

    public static abstract class AbstractTimedListDialogBuilder
            <DialogType extends TimedListDialog, DialogBuilderType extends AbstractTimedListDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractListDialogBuilder<DialogType, DialogBuilderType> {
        protected AbstractTimedListDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType blockedTime(int seconds) {
            dialog.setBlockedTime(seconds);
            return (DialogBuilderType) this;
        }
    }

    public static class TimedListDialogBuilder extends AbstractTimedListDialogBuilder<TimedListDialog, TimedListDialogBuilder> {
        private TimedListDialogBuilder(Player player, EventManager rootEventManager) {
            super(new TimedListDialog(player, rootEventManager));
        }
    }

    public static AbstractTimedListDialogBuilder<?, ?> create(Player player, EventManager rootEventManager) {
        return new TimedListDialogBuilder(player, rootEventManager);
    }

    protected TimedListDialog(Player player, EventManager eventManager) {
        super(player, eventManager);
    }

    private int blockedTime;
    private Timer timer;
    private int currentTime;
    private String originalAccept = "";

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

        player.showDialog(getDialogId(), getStyle(), getCaption(), getItemString(), getButtonOk(), getButtonCancel());
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
