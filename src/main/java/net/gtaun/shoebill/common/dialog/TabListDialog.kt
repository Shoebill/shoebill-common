package net.gtaun.shoebill.common.dialog

import net.gtaun.shoebill.Shoebill
import net.gtaun.shoebill.common.AllOpen
import net.gtaun.shoebill.constant.DialogStyle
import net.gtaun.util.event.EventManager

/**
 * Created by marvin on 29.11.15.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
@AllOpen
class TabListDialog protected constructor(eventManager: EventManager) : ListDialog(eventManager) {

    @AllOpen
    class TabListDialogBuilder(parentEventManager: EventManager) :
            AbstractListDialogBuilder<TabListDialog, TabListDialogBuilder>() {

        fun header(index: Int, header: String): TabListDialogBuilder {
            dialog.setHeader(index, header)
            return this
        }

        init {
            dialog = TabListDialog(parentEventManager)
        }

    }

    val headers = arrayOfNulls<String?>(4)

    init {
        style = DialogStyle.TABLIST
    }

    fun setHeader(index: Int, title: String) {
        if (index < 0 || index > 3) throw IllegalArgumentException("Index must be [0..3]")
        headers[index] = title
    }

    override val itemString: String
        get() {
            var listStr = ""
            val headerCount = usedHeaderCount
            if (headerCount > 0) {
                style = DialogStyle.TABLIST_HEADERS
                for (i in 0..headerCount - 1) {
                    if (headers[i]?.length ?: 0 < 1) continue
                    listStr += headers[i]
                    if (i + 1 < headerCount)
                        listStr += "\t"
                    else {
                        listStr += "\n"
                        if (headerCount < MAX_HEADERS)
                            listStr += "\n"
                    }
                }
            } else
                style = DialogStyle.TABLIST
            displayedItems.clear()
            for (item in items) {
                listStr += item.itemText
                displayedItems.add(item)
            }
            return listStr
        }

    val usedHeaderCount: Int
        get() = headers.filterNotNull().size

    companion object {

        @JvmStatic
        @JvmOverloads
        fun create(eventManager: EventManager = Shoebill.get().eventManager) = TabListDialogBuilder(eventManager)

        internal val MAX_HEADERS = 4
    }
}
