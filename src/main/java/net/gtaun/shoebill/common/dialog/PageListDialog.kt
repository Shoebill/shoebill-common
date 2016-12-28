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

import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.entities.Player
import net.gtaun.util.event.EventManager

/**
 * @author MK124
 * @author Marvin Haschker
 */
open class PageListDialog protected constructor(player: Player, eventManager: EventManager) : ListDialog(player, eventManager) {

    open class PageListDialogBuilder(player: Player, parentEventManager: EventManager) :
            AbstractListDialogBuilder<PageListDialog, PageListDialogBuilder>() {

        open fun itemsPerPage(count: Int) = itemsPerPage { count }
        open fun currentPage(page: Int) = currentPage { page }
        open fun previousPage(text: String) = previousPage { text }
        open fun nextPage(text: String) = nextPage { text }
        open fun onPageTurn(handler: PageTurnHandler) = onPageTurn { handler }

        open fun itemsPerPage(init: PageListDialogBuilder.() -> Int): PageListDialogBuilder {
            dialog.itemsPerPage = init(this)
            return this
        }

        open fun currentPage(init: PageListDialogBuilder.() -> Int): PageListDialogBuilder {
            dialog.currentPage = init(this)
            return this
        }

        open fun previousPage(init: PageListDialogBuilder.() -> String): PageListDialogBuilder {
            dialog.prevPageItemText = init(this)
            return this
        }

        open fun nextPage(init: PageListDialogBuilder.() -> String): PageListDialogBuilder {
            dialog.nextPageItemText = init(this)
            return this
        }

        open fun onPageTurn(init: PageListDialogBuilder.() -> PageTurnHandler): PageListDialogBuilder {
            dialog.pageTurnHandler = init(this)
            return this
        }

        init {
            dialog = PageListDialog(player, parentEventManager)
        }

    }

    open var itemsPerPage = 10
    open var currentPage: Int = 0

    open var prevPageItemText = "<< Prev Page <<"
    open var nextPageItemText = ">> Next Page >>"

    open var pageTurnHandler: PageTurnHandler? = null
    open val enabledItems: List<ListDialogItem>
        get() = items.filter { it.isEnabled }

    open val maxPage: Int
        get() = (items.size - 1) / itemsPerPage

    override fun show() = show(listString)

    open val listString: String
        get() {
            var listStr = ""
            displayedItems.clear()

            if (enabledItems.size >= itemsPerPage + 1) {
                displayedItems.add(object : ListDialogItem(Color.GRAY.embeddingString + prevPageItemText) {
                    override fun onItemSelect() {
                        var page = currentPage - 1
                        if (page < 0) page = maxPage
                        show(page)
                    }
                })
            }

            val offset = itemsPerPage * currentPage
            for (i in 0..itemsPerPage - 1) {
                val index = offset + i
                if (enabledItems.size <= index) break

                val item = enabledItems[offset + i]
                displayedItems.add(item)
            }

            if (displayedItems.size >= itemsPerPage + 1)
                displayedItems.add(object : ListDialogItem(Color.GRAY.embeddingString + nextPageItemText) {
                    override fun onItemSelect() {
                        var page = currentPage + 1
                        if (page > maxPage) page = 0
                        show(page)
                    }
                })

            for (item in displayedItems) {
                listStr += item.itemText + "\n"
            }
            return listStr
        }

    open fun show(page: Int) {
        if (currentPage != page) {
            currentPage = page
            if (currentPage > maxPage)
                currentPage = maxPage
            else if (currentPage < 0) currentPage = 0

            onPageTurn()
        }

        show()
    }

    open fun onPageTurn() = pageTurnHandler?.onPageTurn(this)

    @FunctionalInterface
    interface PageTurnHandler {
        fun onPageTurn(dialog: PageListDialog)
    }

    companion object {

        @JvmStatic
        fun create(player: Player, parentEventManager: EventManager) = PageListDialogBuilder(player, parentEventManager)

        fun PageTurnHandler(handler: (PageListDialog) -> Unit) = object : PageTurnHandler {
            override fun onPageTurn(dialog: PageListDialog) {
                handler(dialog)
            }
        }
    }
}
