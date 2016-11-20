package net.gtaun.shoebill.common.dialog;

/**
 * Created by marvin on 29.11.15.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
public class TabListDialogItem extends ListDialogItem {

    @SuppressWarnings("unchecked")
    public static class AbstractTabItemBuilder
            <ItemType extends TabListDialogItem, ItemBuilderType extends AbstractTabItemBuilder<ItemType, ItemBuilderType>> extends AbstractItemBuilder<ItemType, ItemBuilderType> {

        protected AbstractTabItemBuilder(ItemType item) {
            super(item);
        }

        public ItemBuilderType column(int index, ListDialogItem item) {
            this.item.addColumn(index, item);
            return (ItemBuilderType) this;
        }

        public ItemType build() {
            return item;
        }
    }

    public static class TabItemBuilder extends AbstractTabItemBuilder<TabListDialogItem, TabItemBuilder> {
        private TabItemBuilder() {
            super(new TabListDialogItem());
        }
    }

    public static AbstractTabItemBuilder<?, ?> create() {
        return new TabItemBuilder();
    }

    private ListDialogItem[] columns = new ListDialogItem[4];

    public void addColumn(int index, ListDialogItem item) {
        if(index < 0 || index > 3) throw new IllegalArgumentException("Index must be between 0 and 4");
        columns[index] = item;
    }

    public String getItemText() {
        StringBuilder stringBuilder = new StringBuilder();
        int columnCount = getColumnCount();
        for(int i = 0; i < columnCount; i++) {
            ListDialogItem item = columns[i];
            if(item == null || !item.isEnabled()) continue;
            stringBuilder.append(item.getItemText());
            if(i + 1 < columnCount) {
                stringBuilder.append("\t");
            }
        }
        stringBuilder.append("\n");
        if(columnCount < TabListDialog.MAX_HEADERS)
            stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    private int getColumnCount() {
        int count = 0;
        for(ListDialogItem item : columns) {
            if(item == null || !item.isEnabled()) continue;
            count += 1;
        }
        return count;
    }
}
