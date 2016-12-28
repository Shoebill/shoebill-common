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

import java.util.function.BooleanSupplier
import java.util.function.Supplier

open class ListDialogItemSwitch : ListDialogItem {

    open class SwitchItemBuilder : AbstractItemBuilder<ListDialogItemSwitch, SwitchItemBuilder>() {

        open fun onSwitch(onText: String) = onSwitch { onText }
        open fun offSwitch(offText: String) = offSwitch { offText }
        open fun switchText(onText: String, offText: String) = switchText { Pair(onText, offText) }
        open fun statusSupplier(supplier: BooleanSupplier) = statusSupplier { supplier }
        open fun onColor(on: Color) = onColor { on }
        open fun offColor(off: Color) = offColor { off }
        open fun switchColor(on: Color, off: Color) = switchColor { Pair(on, off) }

        open fun onSwitch(init: SwitchItemBuilder.() -> String): SwitchItemBuilder {
            item.setSwitchText(init(this), item.switchTextSupplier(false))
            return this
        }

        open fun offSwitch(init: SwitchItemBuilder.() -> String): SwitchItemBuilder {
            item.setSwitchText(item.switchTextSupplier(true), init(this))
            return this
        }

        open fun switchText(init: SwitchItemBuilder.() -> Pair<String, String>): SwitchItemBuilder {
            val pair = init(this)
            item.setSwitchText(pair.first, pair.second)
            return this
        }

        open fun onColor(init: SwitchItemBuilder.() -> Color): SwitchItemBuilder {
            item.setSwitchColor(init(this), item.switchColorSupplier(false))
            return this
        }

        open fun offColor(init: SwitchItemBuilder.() -> Color): SwitchItemBuilder {
            item.setSwitchColor(item.switchColorSupplier(true), init(this))
            return this
        }

        open fun statusSupplier(init: SwitchItemBuilder.() -> BooleanSupplier): SwitchItemBuilder {
            item.statusSupplier = init(this)
            return this
        }

        open fun switchColor(init: SwitchItemBuilder.() -> Pair<Color, Color>): SwitchItemBuilder {
            val pair = init(this)
            item.setSwitchColor(pair.first, pair.second)
            return this
        }

        init {
            item = ListDialogItemSwitch("Unnamed")
        }

    }

    open var switchTextSupplier = DEFAULT_SWITCH_TEXT_SUPPLIER
    open var switchColorSupplier = DEFAULT_SWITCH_COLOR_SUPPLIER
    open var statusSupplier: BooleanSupplier? = null

    constructor(itemText: String) : super(itemText)
    constructor(textSupplier: DialogTextSupplier) : super(textSupplier)

    open fun setSwitchText(on: String, off: String) {
        switchTextSupplier = { s -> if (s) on else off }
    }

    open fun setSwitchColor(on: Color, off: Color) {
        switchColorSupplier = { s -> if (s) on else off }
    }

    override var itemText: String
        get() = super.itemText + switchColorSupplier(isSwitched).embeddingString + " [" + switchTextSupplier(isSwitched) + "]"
        set(value) {
            super.itemText = value
        }

    override val isEnabled: Boolean
        get() = true

    open val isSwitched: Boolean
        get() = statusSupplier?.asBoolean ?: false

    companion object {

        @JvmStatic
        fun create() = SwitchItemBuilder()

        private val DEFAULT_SWITCH_TEXT_SUPPLIER = { s: Boolean -> if (s) "ON" else "OFF" }
        private val DEFAULT_SWITCH_COLOR_SUPPLIER = { s: Boolean -> if (s) Color.GREEN else Color.GRAY }
    }
}