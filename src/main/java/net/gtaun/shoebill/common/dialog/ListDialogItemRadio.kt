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
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.entities.Player
import java.util.function.BooleanSupplier
import java.util.function.IntSupplier

@AllOpen
class ListDialogItemRadio : ListDialogItem {

    @Suppress("UNCHECKED_CAST")
    @AllOpen
    abstract class AbstractRadioItemBuilder<T : ListDialogItemRadio, B : AbstractRadioItemBuilder<T, B>> : AbstractItemBuilder<T, B>() {

        fun item(item: RadioItem) = item { item }
        fun colorChecked(color: Color) = colorChecked { color }
        fun colorUnchecked(color: Color) = colorUnchecked { color }
        fun selectedIndex(index: Int) = selectedIndex { index }

        fun selectedIndexSupplier(supplier: IntSupplier): B {
            item.selectedIndexSupplier = supplier
            return this as B
        }

        fun onRadioItemSelect(handler: ItemSelectHandler) = onRadioItemSelect { handler }

        fun item(init: B.() -> RadioItem): B {
            item.addItem(init(this as B))
            return this
        }

        fun colorChecked(init: B.() -> Color): B {
            val offColor = item.radioItemColorSupplier?.get(false) ?: Color.RED
            item.setRadioColor(init(this as B), offColor)
            return this
        }

        fun colorUnchecked(init: B.() -> Color): B {
            val onColor = item.radioItemColorSupplier?.get(true) ?: Color.GREEN
            item.setRadioColor(onColor, init(this as B))
            return this
        }

        fun selectedIndex(init: B.() -> Int): B {
            item.selectedIndexSupplier = IntSupplier { init(this as B) }
            return this as B
        }

        fun selectedIndexSupplier(init: B.() -> Int) =
                selectedIndexSupplier(IntSupplier { init(this as B) })

        fun onRadioItemSelect(init: B.() -> ItemSelectHandler): B {
            item.radioItemSelectHandler = init(this as B)
            return this
        }
    }

    @AllOpen
    class RadioItemBuilder : AbstractRadioItemBuilder<ListDialogItemRadio, RadioItemBuilder>() {

        init {
            item = ListDialogItemRadio()
        }

    }

    @AllOpen
    class RadioItem {
        final var itemText: String? = null
        final var checkedColor: Color? = null
        final var selectHandler: RadioItemSelectHandler? = null

        @JvmOverloads
        constructor(itemString: String? = null, checkedColor: Color? = null,
                    selectHandler: RadioItemSelectHandler? = null) {
            this.itemText = itemString
            this.checkedColor = checkedColor
            this.selectHandler = selectHandler
        }

        constructor(itemString: String, selectHandler: RadioItemSelectHandler) : this(itemString, null, selectHandler)

        fun onSelect(dialogItem: ListDialogItemRadio, player: Player) = selectHandler?.onSelect(dialogItem, player)
    }


    @FunctionalInterface
    interface ItemSelectHandler {
        fun onSelect(dialogItem: ListDialogItemRadio, player: Player, item: RadioItem, index: Int)
    }

    @FunctionalInterface
    interface RadioItemSelectHandler {
        fun onSelect(dialogItem: ListDialogItemRadio, player: Player)
    }


    private val options: MutableList<RadioItem> = mutableListOf()
    var radioItemColorSupplier: ConditionSupplier<Color>? = null
    var selectedIndexSupplier: IntSupplier? = null
    var radioItemSelectHandler: ItemSelectHandler? = null

    constructor() : super()

    @JvmOverloads
    constructor(itemText: String, checkedColor: Color = Color.GREEN,
                uncheckedColor: Color = Color.GRAY) : super(itemText) {
        setRadioColor(checkedColor, uncheckedColor)
    }

    fun addItem(item: RadioItem) = options.add(item)

    final fun setRadioColor(checkedColor: Color, uncheckedColor: Color) {
        radioItemColorSupplier = ConditionSupplier { c -> if (c) checkedColor else uncheckedColor }
    }

    override var itemText: String
        get() {
            var text = super.itemText
            for (i in options.indices) {
                val selected = selected
                val item = options[i]
                text += if (i == selected) {
                    if (item.checkedColor != null)
                        item.checkedColor!!.embeddingString + " [" + item.itemText + "]"
                    else
                        radioItemColorSupplier!![true].embeddingString + " [" + item.itemText + "]"
                } else
                    radioItemColorSupplier!![false].embeddingString + " [" + item.itemText + "]"
            }
            return text
        }
        set(value) {
            super.itemText = value
        }

    override fun onItemSelect(player: Player) {
        if (options.isEmpty()) return
        val index = (selected + 1) % options.size
        val item = options[index]
        item.onSelect(this, player)
        onItemSelect(item, player, index)
        super.onItemSelect(player)
    }

    fun onItemSelect(item: RadioItem, player: Player, index: Int) =
            radioItemSelectHandler?.onSelect(this, player, item, index)

    val selected: Int
        get() = selectedIndexSupplier?.asInt ?: -1

    companion object {

        @JvmStatic
        fun create() = RadioItemBuilder()

        fun RadioItemSelectHandler(handler: (ListDialogItemRadio, Player) -> Unit) = object : RadioItemSelectHandler {
            override fun onSelect(dialogItem: ListDialogItemRadio, player: Player) {
                handler(dialogItem, player)
            }
        }

        fun ItemSelectHandler(handler: (ListDialogItemRadio, Player, RadioItem, Int) -> Unit) = object : ItemSelectHandler {
            override fun onSelect(dialogItem: ListDialogItemRadio, player: Player, item: RadioItem, index: Int) {
                handler(dialogItem, player, item, index)
            }

        }
    }
}