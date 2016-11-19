/**
 * Copyright (C) 2012-2014 MK124

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.shoebill.common.dialog

import net.gtaun.shoebill.constant.DialogStyle
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.event.dialog.DialogResponseEvent
import net.gtaun.util.event.EventManager
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * @author MK124
 * @author Marvin Haschker
 */
open class ListDialog constructor(player: Player, eventManager: EventManager) :
        AbstractDialog(DialogStyle.LIST, player, eventManager) {

    @Suppress("UNCHECKED_CAST")
    abstract class AbstractListDialogBuilder<T : ListDialog, B : AbstractListDialogBuilder<T, B>> :
            Builder<T, B>() {
        fun item(item: ListDialogItem) = item { item }
        fun item(itemText: String) = item { ListDialogItem(itemText) }

        fun item(init: B.() -> ListDialogItem): B {
            dialog.addItem(init(this as B))
            return this
        }
    }

    class ListDialogBuilder : AbstractListDialogBuilder<ListDialog, ListDialogBuilder> {
        constructor(player: Player, parentEventManager: EventManager) : super() {
            dialog = ListDialog(player, parentEventManager)
        }
    }

    val items: MutableList<ListDialogItem>
    val displayedItems: MutableList<ListDialogItem> = mutableListOf()
    var clickOkHandler: ClickOkHandler? = null

    init {
        items = object : ArrayList<ListDialogItem>() {
            override fun add(index: Int, element: ListDialogItem) {
                element.currentDialog = this@ListDialog
                super.add(index, element)
            }

            override fun add(element: ListDialogItem): Boolean {
                element.currentDialog = this@ListDialog
                return super.add(element)
            }

            override operator fun set(index: Int, element: ListDialogItem): ListDialogItem {
                element.currentDialog = this@ListDialog
                return super.set(index, element)
            }

            override fun addAll(elements: Collection<ListDialogItem>): Boolean {
                elements.forEach { e -> e.currentDialog = this@ListDialog }
                return super.addAll(elements)
            }

            override fun addAll(index: Int, elements: Collection<ListDialogItem>): Boolean {
                elements.forEach { e -> e.currentDialog = this@ListDialog }
                return super.addAll(index, elements)
            }
        }
    }

    override fun show() = show(itemString)

    open val itemString: String
        get() {
            var listStr = ""
            displayedItems.clear()

            for (item in items) {
                if (!item.isEnabled) continue

                var text = item.itemText
                if (StringUtils.isEmpty(text)) text = "-"

                listStr += text + "\n"
                displayedItems.add(item)
            }
            return listStr
        }

    override fun onClickOk(event: DialogResponseEvent) {
        val itemId = event.listItem
        val item = displayedItems[itemId]

        item.onItemSelect()
        onClickOk(item)
    }

    private fun onClickOk(item: ListDialogItem) {
        clickOkHandler?.onClickOk(this, item) ?: return Unit
    }

    fun addItem(item: ListDialogItem) = items.add(item)

    @JvmOverloads
    fun addItem(itemText: String, enabledSupplier: ListDialogItem.ItemBooleanSupplier? = null,
                handler: ListDialogItem.ItemSelectHandler? = null) =
            items.add(ListDialogItem(itemText, enabledSupplier, handler))

    @JvmOverloads
    fun addItem(supplier: DialogTextSupplier, enabledSupplier: ListDialogItem.ItemBooleanSupplier? = null,
                handler: ListDialogItem.ItemSelectHandler? = null) =
            items.add(ListDialogItem(supplier, enabledSupplier, handler))

    @FunctionalInterface
    interface ClickOkHandler {
        fun onClickOk(dialog: ListDialog, item: ListDialogItem)
    }

    companion object {

        @JvmStatic
        fun create(player: Player, eventManager: EventManager) = ListDialogBuilder(player, eventManager)

        fun ClickOkHandler(handler: (ListDialog, ListDialogItem) -> Unit) = object : ClickOkHandler {
            override fun onClickOk(dialog: ListDialog, item: ListDialogItem) {
                handler(dialog, item)
            }
        }
    }
}
