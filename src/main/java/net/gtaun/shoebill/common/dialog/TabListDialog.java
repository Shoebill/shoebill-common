package net.gtaun.shoebill.common.dialog;

import net.gtaun.shoebill.constant.DialogStyle;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * Created by marvin on 29.11.15.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
public class TabListDialog extends ListDialog {

    @SuppressWarnings("unchecked")
    public static abstract class AbstractTabListDialogBuilder
            <DialogType extends TabListDialog, DialogBuilderType extends AbstractTabListDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractListDialogBuilder<DialogType, DialogBuilderType> {

        protected AbstractTabListDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType header(int index, String title) {
            dialog.setHeader(index, title);
            return (DialogBuilderType) this;
        }
    }

    public static class TabListDialogBuilder extends AbstractTabListDialogBuilder<TabListDialog, TabListDialogBuilder> {
        protected TabListDialogBuilder(Player player, EventManager parentEventManager) {
            super(new TabListDialog(player, parentEventManager));
        }
    }

    public static AbstractTabListDialogBuilder<?, ?> create(Player player, EventManager parentEventManager) {
        return new TabListDialogBuilder(player, parentEventManager);
    }

    public static final int MAX_HEADERS = 4;
    private String[] headers = {"", "", "", ""};

    protected TabListDialog(Player player, EventManager eventManager) {
        super(player, eventManager);
        style = DialogStyle.TABLIST;
    }

    public void setHeader(int index, String title) {
        if(index < 0 || index > 3) throw new IllegalArgumentException("Index must be between 0 and 4");
        headers[index] = title;
    }

    @Override
    public String getItemString() {
        String listStr = "";
        int headerCount = usedHeaderCount();
        if(headerCount > 0) {
            style = DialogStyle.TABLIST_HEADERS;
            for(int i = 0; i < headerCount; i++) {
                if(headers[i].length() < 1) continue;
                listStr += headers[i];
                if(i + 1 < headerCount) listStr += "\t";
                else {
                    listStr += "\n";
                    if(headerCount < MAX_HEADERS)
                        listStr += "\n";
                }
            }
        } else style = DialogStyle.TABLIST;
        displayedItems.clear();
        for(ListDialogItem item : items) {
            listStr += item.getItemText();
            displayedItems.add(item);
        }
        return listStr;
    }

    private int usedHeaderCount() {
        int count = 0;
        for(String header : headers) {
            if(header.length() > 0)
                count += 1;
        }
        return count;
    }
}
