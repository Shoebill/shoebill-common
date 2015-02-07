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

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author MK124
 */
public class PageListDialog extends ListDialog {
    @SuppressWarnings("unchecked")
    public static abstract class AbstractPageListDialogBuilder
            <DialogType extends PageListDialog, DialogBuilderType extends AbstractPageListDialogBuilder<DialogType, DialogBuilderType>>
            extends AbstractListDialogBuilder<DialogType, DialogBuilderType> {
        protected AbstractPageListDialogBuilder(DialogType dialog) {
            super(dialog);
        }

        public DialogBuilderType itemsPerPage(int itemsPerPage) {
            dialog.setItemsPerPage(itemsPerPage);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType page(int currentPage) {
            dialog.setCurrentPage(currentPage);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType prevPage(String itemText) {
            dialog.setPrevPageItemText(itemText);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType nextPage(String itemText) {
            dialog.setNextPageItemText(itemText);
            return (DialogBuilderType) this;
        }

        public DialogBuilderType onPageTurn(PageTurnHandler handler) {
            dialog.setPageTurnHandler(handler);
            return (DialogBuilderType) this;
        }
    }

    private static class PageListDialogBuilder extends AbstractPageListDialogBuilder<PageListDialog, PageListDialogBuilder> {
        protected PageListDialogBuilder(Player player, EventManager rootEventManager) {
            super(new PageListDialog(player, rootEventManager));
        }
    }

    public static AbstractPageListDialogBuilder<?, ?> create(Player player, EventManager rootEventManager) {
        return new PageListDialogBuilder(player, rootEventManager);
    }


    @FunctionalInterface
    public interface PageTurnHandler {
        void onPageTurn(PageListDialog dialog);
    }


    private int itemsPerPage = 10;
    private int currentPage;

    private String prevPageItemText = "<< Prev Page <<";
    private String nextPageItemText = ">> Next Page >>";

    private PageTurnHandler pageTurnHandler;


    protected PageListDialog(Player player, EventManager eventManager) {
        super(player, eventManager);
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getMaxPage() {
        return (items.size() - 1) / itemsPerPage;
    }

    public String getPrevPageItemText() {
        return prevPageItemText;
    }

    public void setPrevPageItemText(String prevPageItemText) {
        this.prevPageItemText = prevPageItemText;
    }

    public String getNextPageItemText() {
        return nextPageItemText;
    }

    public void setNextPageItemText(String nextPageItemText) {
        this.nextPageItemText = nextPageItemText;
    }

    public void setPageTurnHandler(PageTurnHandler pageTurnHandler) {
        this.pageTurnHandler = pageTurnHandler;
    }

    @Override
    public void show() {
        show(getListString());
    }

    protected String getListString() {
        String listStr = "";
        displayedItems.clear();

        displayedItems.add(new ListDialogItem(Color.GRAY.toEmbeddingString() + prevPageItemText) {
            @Override
            public void onItemSelect() {
                int page = currentPage - 1;
                if (page < 0) page = getMaxPage();
                show(page);
            }
        });

        int offset = itemsPerPage * currentPage;
        for (int i = 0; i < itemsPerPage; i++) {
            int index = offset + i;
            if (items.size() <= index) break;

            ListDialogItem item = items.get(offset + i);
            displayedItems.add(item);
        }

        if (displayedItems.size() >= itemsPerPage + 1)
            displayedItems.add(new ListDialogItem(Color.GRAY.toEmbeddingString() + nextPageItemText) {
                @Override
                public void onItemSelect() {
                    int page = currentPage + 1;
                    if (page > getMaxPage()) page = 0;
                    show(page);
                }
            });

        for (ListDialogItem item : displayedItems) {
            listStr += item.getItemText() + "\n";
        }
        return listStr;
    }

    protected void show(int page) {
        if (currentPage != page) {
            currentPage = page;
            if (currentPage > getMaxPage()) currentPage = getMaxPage();
            else if (currentPage < 0) currentPage = 0;

            onPageTurn();
        }

        show();
    }

    public void onPageTurn() {
        if (pageTurnHandler != null) pageTurnHandler.onPageTurn(this);
    }
}
