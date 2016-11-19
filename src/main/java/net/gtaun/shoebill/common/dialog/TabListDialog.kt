package net.gtaun.shoebill.common.dialog

import net.gtaun.shoebill.constant.DialogStyle
import net.gtaun.shoebill.entities.Player
import net.gtaun.util.event.EventManager

/**
 * Created by marvin on 29.11.15.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
open class TabListDialog protected constructor(player: Player, eventManager: EventManager) : ListDialog(player, eventManager) {

    class TabListDialogBuilder : AbstractListDialogBuilder<TabListDialog, TabListDialogBuilder> {

        constructor(player: Player, parentEventManager: EventManager) : super() {
            dialog = TabListDialog(player, parentEventManager)
        }

        fun header(index: Int, header: String): TabListDialogBuilder {
            dialog.setHeader(index, header)
            return this
        }

    }

    private val headers = arrayOf<String?>(null, null, null, null)

    init {
        style = DialogStyle.TABLIST
    }

    fun setHeader(index: Int, title: String) {
        if (index < 0 || index > 3) throw IllegalArgumentException("Index must be between 0 and 4")
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
        fun create(player: Player, parentEventManager: EventManager) = TabListDialogBuilder(player, parentEventManager)

        internal val MAX_HEADERS = 4
    }
}
