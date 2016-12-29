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

import net.gtaun.shoebill.common.AllOpen
import net.gtaun.shoebill.entities.Player
import java.util.function.BiConsumer

@AllOpen
class ListDialogItem {

    @AllOpen
    abstract class AbstractItemBuilder<T : ListDialogItem, B : AbstractItemBuilder<T, B>> {

        protected lateinit var item: T

        fun init(init: B.(B) -> Unit): B {
            init(this as B, this)
            return this
        }

        fun text(text: String) = text { text }
        fun textSupplier(supplier: DialogTextSupplier) = textSupplier { supplier }
        fun enabled(enabled: Boolean) = enabled { enabled }
        fun enabledSupplier(supplier: ItemBooleanSupplier) = enabledSupplier { supplier }

        fun onSelect(handler: BiConsumer<ListDialogItem, Player>) = onSelect {
            item, player ->
            handler.accept(item, player)
        }

        fun onSelectHandler(handler: ItemSelectHandler) = onSelectHandler { handler }

        fun text(init: B.() -> String): B {
            item.itemText = init(this as B)
            return this
        }

        fun textSupplier(init: B.() -> DialogTextSupplier): B {
            item.itemTextSupplier = init(this as B)
            return this
        }

        fun enabled(init: B.() -> Boolean): B {
            item.itemEnabledSupplier = ItemBooleanSupplier { init(this as B) }
            return this as B
        }

        fun enabledSupplier(init: B.() -> ItemBooleanSupplier): B {
            item.itemEnabledSupplier = init(this as B)
            return this
        }

        fun onSelect(init: (ListDialogItem, Player) -> Unit): B {
            item.selectHandler = ItemSelectHandler { item, player -> init(item, player) }
            return this as B
        }

        fun onSelectHandler(init: B.() -> ItemSelectHandler): B {
            item.selectHandler = init(this as B)
            return this
        }

        fun build(): T = item
    }

    @AllOpen
    class ListDialogItemBuilder : AbstractItemBuilder<ListDialogItem, ListDialogItemBuilder>() {
        init {
            item = ListDialogItem()
        }
    }

    var currentDialog: ListDialog? = null
        set

    final var itemTextSupplier: DialogTextSupplier? = null
    final var itemEnabledSupplier: ItemBooleanSupplier? = null
    final var selectHandler: ItemSelectHandler? = null

    constructor()

    @JvmOverloads
    constructor(text: String, enabledSupplier: ItemBooleanSupplier? = null,
                handler: ItemSelectHandler? = null) {
        itemTextSupplier = DialogTextSupplier { text }
        itemEnabledSupplier = enabledSupplier
        selectHandler = handler
    }

    @JvmOverloads
    constructor(textSupplier: DialogTextSupplier, enabledSupplier: ItemBooleanSupplier? = null,
                handler: ItemSelectHandler? = null) {
        itemTextSupplier = textSupplier
        itemEnabledSupplier = enabledSupplier
        selectHandler = handler
    }

    var itemText: String
        get() = itemTextSupplier?.get(currentDialog) ?: "-"
        set(itemText) {
            itemTextSupplier = DialogTextSupplier { itemText }
        }

    val isEnabled: Boolean
        get() = itemEnabledSupplier == null || itemEnabledSupplier?.get() ?: true

    fun onItemSelect(player: Player) {
        selectHandler?.onItemSelect(this, player) ?: return Unit
    }

    @FunctionalInterface
    interface ItemBooleanSupplier {
        fun get(): Boolean
    }

    @FunctionalInterface
    interface ItemSelectHandler {
        fun onItemSelect(item: ListDialogItem, player: Player)
    }

    companion object {

        @JvmStatic
        fun create() = ListDialogItemBuilder()

        fun ItemBooleanSupplier(handler: () -> Boolean) = object : ItemBooleanSupplier {
            override fun get(): Boolean {
                return handler()
            }
        }

        fun ItemSelectHandler(handler: (ListDialogItem, Player) -> Unit) = object : ItemSelectHandler {
            override fun onItemSelect(item: ListDialogItem, player: Player) {
                return handler(item, player)
            }
        }
    }
}
