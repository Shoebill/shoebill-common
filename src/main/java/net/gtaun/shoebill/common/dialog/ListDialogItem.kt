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

import java.util.function.Consumer

open class ListDialogItem {

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
        fun onSelect(handler: Consumer<ListDialogItem>) = onSelect { handler.accept(it) }
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

        fun onSelect(init: (ListDialogItem) -> Unit): B {
            item.selectHandler = ItemSelectHandler { init(it) }
            return this as B
        }

        fun onSelectHandler(init: B.() -> ItemSelectHandler): B {
            item.selectHandler = init(this as B)
            return this
        }

        fun build(): T = item
    }

    class ListDialogItemBuilder : AbstractItemBuilder<ListDialogItem, ListDialogItemBuilder> {
        constructor() : super() {
            item = ListDialogItem()
        }
    }

    var currentDialog: ListDialog? = null
        set

    protected var itemTextSupplier: DialogTextSupplier? = null
    protected var itemEnabledSupplier: ItemBooleanSupplier? = null
    protected var selectHandler: ItemSelectHandler? = null

    constructor() {
    }

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

    open var itemText: String
        get() = itemTextSupplier?.get(currentDialog) ?: "-"
        set(itemText) {
            itemTextSupplier = DialogTextSupplier { itemText }
        }

    open val isEnabled: Boolean
        get() = itemEnabledSupplier == null || itemEnabledSupplier?.get() ?: true

    open fun onItemSelect() {
        selectHandler?.onItemSelect(this) ?: return Unit
    }

    @FunctionalInterface
    interface ItemBooleanSupplier {
        fun get(): Boolean
    }

    @FunctionalInterface
    interface ItemSelectHandler {
        fun onItemSelect(item: ListDialogItem)
    }

    companion object {

        @JvmStatic
        fun create() = ListDialogItemBuilder()

        fun ItemBooleanSupplier(handler: () -> Boolean) = object : ItemBooleanSupplier {
            override fun get(): Boolean {
                return handler()
            }
        }

        fun ItemSelectHandler(handler: (ListDialogItem) -> Unit) = object : ItemSelectHandler {
            override fun onItemSelect(item: ListDialogItem) {
                return handler(item)
            }
        }
    }
}
