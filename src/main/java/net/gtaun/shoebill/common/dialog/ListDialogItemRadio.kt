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
import java.util.function.IntSupplier

open class ListDialogItemRadio : ListDialogItem {

    open class RadioItemBuilder : AbstractItemBuilder<ListDialogItemRadio, RadioItemBuilder>() {

        open fun item(item: RadioItem) = item { item }
        open fun colorChecked(color: Color) = colorChecked { color }
        open fun colorUnchecked(color: Color) = colorUnchecked { color }
        open fun selectedIndex(index: Int) = selectedIndex { index }
        open fun selectedIndexSupplier(supplier: IntSupplier) = selectedIndexSupplier { supplier.asInt }
        open fun onRadioItemSelect(handler: ItemSelectHandler) = onRadioItemSelect { handler }

        open fun item(init: RadioItemBuilder.() -> RadioItem): RadioItemBuilder {
            item.addItem(init(this))
            return this
        }

        open fun colorChecked(init: RadioItemBuilder.() -> Color): RadioItemBuilder {
            val offColor = item.radioItemColorSupplier?.get(false) ?: Color.RED
            item.setRadioColor(init(this), offColor)
            return this
        }

        open fun colorUnchecked(init: RadioItemBuilder.() -> Color): RadioItemBuilder {
            val onColor = item.radioItemColorSupplier?.get(true) ?: Color.GREEN
            item.setRadioColor(onColor, init(this))
            return this
        }

        open fun selectedIndex(init: RadioItemBuilder.() -> Int): RadioItemBuilder {
            item.selectedIndexSupplier = IntSupplier { init(this) }
            return this
        }

        open fun selectedIndexSupplier(init: RadioItemBuilder.() -> Int): RadioItemBuilder {
            item.selectedIndexSupplier = IntSupplier { init(this) }
            return this
        }

        open fun onRadioItemSelect(init: RadioItemBuilder.() -> ItemSelectHandler): RadioItemBuilder {
            item.radioItemSelectHandler = init(this)
            return this
        }

        init {
            item = ListDialogItemRadio()
        }

    }

    open class RadioItem {
        var itemText: String? = null
            private set

        var checkedColor: Color? = null
        var selectHandler: RadioItemSelectHandler? = null

        @JvmOverloads
        constructor(itemString: String? = null, checkedColor: Color? = null,
                    selectHandler: RadioItemSelectHandler? = null) {
            this.itemText = itemString
            this.checkedColor = checkedColor
            this.selectHandler = selectHandler
        }

        constructor(itemString: String, selectHandler: RadioItemSelectHandler) : this(itemString, null, selectHandler)

        open fun onSelect(dialogItem: ListDialogItemRadio) = selectHandler?.onSelect(dialogItem)
    }


    @FunctionalInterface
    interface ItemSelectHandler {
        fun onSelect(dialogItem: ListDialogItemRadio, item: RadioItem, index: Int)
    }

    @FunctionalInterface
    interface RadioItemSelectHandler {
        fun onSelect(dialogItem: ListDialogItemRadio)
    }


    private val options: MutableList<RadioItem> = mutableListOf()
    open var radioItemColorSupplier: ConditionSupplier<Color>? = null
    open var selectedIndexSupplier: IntSupplier? = null
    open var radioItemSelectHandler: ItemSelectHandler? = null

    constructor() : super()

    @JvmOverloads
    constructor(itemText: String, checkedColor: Color = Color.GREEN,
                uncheckedColor: Color = Color.GRAY) : super(itemText) {
        setRadioColor(checkedColor, uncheckedColor)
    }

    fun addItem(item: RadioItem) = options.add(item)

    fun setRadioColor(checkedColor: Color, uncheckedColor: Color) {
        radioItemColorSupplier = ConditionSupplier { c -> if (c) checkedColor else uncheckedColor }
    }

    override var itemText: String
        get() {
            var text = super.itemText
            for (i in options.indices) {
                val selected = selected
                val item = options[i]
                if (i == selected) {
                    if (item.checkedColor != null)
                        text += item.checkedColor!!.embeddingString + " [" + item.itemText + "]"
                    else
                        text += radioItemColorSupplier!![true].embeddingString + " [" + item.itemText + "]"
                } else
                    text += radioItemColorSupplier!![false].embeddingString + " [" + item.itemText + "]"
            }
            return text
        }
        set(value) {
            super.itemText = value
        }

    override fun onItemSelect() {
        if (options.isEmpty()) return
        val index = (selected + 1) % options.size
        val item = options[index]
        item.onSelect(this)
        onItemSelect(item, index)
        super.onItemSelect()
    }

    open fun onItemSelect(item: RadioItem, index: Int) = radioItemSelectHandler?.onSelect(this, item, index)

    open val selected: Int
        get() = selectedIndexSupplier?.asInt ?: -1

    companion object {

        @JvmStatic
        fun create() = RadioItemBuilder()

        fun RadioItemSelectHandler(handler: (ListDialogItemRadio) -> Unit) = object : RadioItemSelectHandler {
            override fun onSelect(dialogItem: ListDialogItemRadio) {
                handler(dialogItem)
            }
        }

        fun ItemSelectHandler(handler: (ListDialogItemRadio, RadioItem, Int) -> Unit) = object : ItemSelectHandler {
            override fun onSelect(dialogItem: ListDialogItemRadio, item: RadioItem, index: Int) {
                handler(dialogItem, item, index)
            }

        }
    }
}