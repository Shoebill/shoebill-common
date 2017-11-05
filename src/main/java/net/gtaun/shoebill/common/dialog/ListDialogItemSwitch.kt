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

import java.util.function.BooleanSupplier
import java.util.function.IntSupplier
import java.util.function.Supplier

@AllOpen
class ListDialogItemSwitch : ListDialogItem {

    @Suppress("UNCHECKED_CAST")
    @AllOpen
    abstract class AbstractSwitchItemBuilder<T : ListDialogItemSwitch, B : AbstractSwitchItemBuilder<T, B>> : AbstractItemBuilder<T, B>() {

        fun onSwitch(onText: String) = onSwitch { onText }
        fun offSwitch(offText: String) = offSwitch { offText }
        fun switchText(onText: String, offText: String) = switchText { Pair(onText, offText) }
        fun statusSupplier(supplier: BooleanSupplier) = statusSupplier { supplier }
        fun onColor(on: Color) = onColor { on }
        fun offColor(off: Color) = offColor { off }
        fun switchColor(on: Color, off: Color) = switchColor { Pair(on, off) }

        fun onSwitch(init: B.() -> String): B {
            item.setSwitchText(init(this as B), item.switchTextSupplier(false))
            return this
        }

        fun offSwitch(init: B.() -> String): B{
            item.setSwitchText(item.switchTextSupplier(true), init(this as B))
            return this
        }

        fun switchText(init: B.() -> Pair<String, String>): B {
            val pair = init(this as B)
            item.setSwitchText(pair.first, pair.second)
            return this
        }

        fun onColor(init: B.() -> Color): B{
            item.setSwitchColor(init(this as B), item.switchColorSupplier(false))
            return this
        }

        fun offColor(init: B.() -> Color): B {
            item.setSwitchColor(item.switchColorSupplier(true), init(this as B))
            return this
        }

        fun statusSupplier(init: B.() -> BooleanSupplier): B {
            item.statusSupplier = init(this as B)
            return this
        }

        fun switchColor(init: B.() -> Pair<Color, Color>): B {
            val pair = init(this as B)
            item.setSwitchColor(pair.first, pair.second)
            return this
        }
    }

    @AllOpen
    class SwitchItemBuilder : AbstractSwitchItemBuilder<ListDialogItemSwitch, SwitchItemBuilder>() {

        init {
            item = ListDialogItemSwitch("Unnamed")
        }

    }

    var switchTextSupplier = DEFAULT_SWITCH_TEXT_SUPPLIER
    var switchColorSupplier = DEFAULT_SWITCH_COLOR_SUPPLIER
    var statusSupplier: BooleanSupplier? = null

    constructor(itemText: String) : super(itemText)
    constructor(textSupplier: DialogTextSupplier) : super(textSupplier)

    fun setSwitchText(on: String, off: String) {
        switchTextSupplier = { s -> if (s) on else off }
    }

    fun setSwitchColor(on: Color, off: Color) {
        switchColorSupplier = { s -> if (s) on else off }
    }

    override var itemText: String
        get() = super.itemText + switchColorSupplier(isSwitched).embeddingString + " [" + switchTextSupplier(isSwitched) + "]"
        set(value) {
            super.itemText = value
        }

    override val isEnabled: Boolean
        get() = true

    val isSwitched: Boolean
        get() = statusSupplier?.asBoolean ?: false

    companion object {

        @JvmStatic
        fun create() = SwitchItemBuilder()

        private val DEFAULT_SWITCH_TEXT_SUPPLIER = { s: Boolean -> if (s) "ON" else "OFF" }
        private val DEFAULT_SWITCH_COLOR_SUPPLIER = { s: Boolean -> if (s) Color.GREEN else Color.GRAY }
    }
}