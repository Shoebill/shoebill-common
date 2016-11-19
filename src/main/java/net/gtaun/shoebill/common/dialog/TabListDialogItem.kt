package net.gtaun.shoebill.common.dialog

/**
 * Created by marvin on 29.11.15.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
class TabListDialogItem : ListDialogItem() {

    class TabListDialogItemBuilder : AbstractItemBuilder<TabListDialogItem, TabListDialogItemBuilder> {

        constructor() : super() {
            item = TabListDialogItem()
        }

        fun column(index: Int, item: ListDialogItem): TabListDialogItemBuilder {
            this.item.addColumn(index, item)
            return this
        }

    }

    private val columns = arrayOfNulls<ListDialogItem>(4)

    fun addColumn(index: Int, item: ListDialogItem) {
        if (index < 0 || index > 3) throw IllegalArgumentException("Index must be between 0 and 4")
        columns[index] = item
    }

    override var itemText: String
        get() {
            val stringBuilder = StringBuilder()
            val columnCount = columnCount
            for (i in 0..columnCount - 1) {
                val item = columns[i]
                if (item == null || !item.isEnabled) continue
                stringBuilder.append(item.itemText)
                if (i + 1 < columnCount) {
                    stringBuilder.append("\t")
                }
            }
            stringBuilder.append("\n")
            if (columnCount < TabListDialog.MAX_HEADERS)
                stringBuilder.append("\n")
            return stringBuilder.toString()
        }
        set(value) {
            super.itemText = value
        }

    private val columnCount: Int
        get() = columns.filterNotNull().filter { it.isEnabled }.count()

    companion object {

        @JvmStatic
        fun create() = TabListDialogItemBuilder()

    }
}
