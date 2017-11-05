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
import java.util.function.BiConsumer
import java.util.function.BooleanSupplier

@AllOpen
class ListDialogItemCheck(itemText: String, checkedColor: Color, uncheckedColor: Color) :
        ListDialogItem(itemText) {

    @Suppress("UNCHECKED_CAST")
    @AllOpen
    abstract class AbstractCheckItemBuilder<T : ListDialogItemCheck, B : AbstractCheckItemBuilder<T, B>> : AbstractItemBuilder<T, B>() {

        fun item(init: B.() -> CheckItem): B {
            item.addItem(init(this as B))
            return this
        }

        fun item(item: CheckItem) = item { item }

        @JvmOverloads
        fun item(itemText: String, checkedColor: Color? = null, statusSupplier: BooleanSupplier? = null) = item {
            CheckItem(itemText, checkedColor, statusSupplier)
        }
    }

    @AllOpen
    class CheckItemBuilder : AbstractCheckItemBuilder<ListDialogItemCheck, CheckItemBuilder>() {

        init {
            item = ListDialogItemCheck("Unnamed")
        }

    }

    @AllOpen
    class CheckItem(itemText: String, var checkedColor: Color?, var statusSupplier: BooleanSupplier?) {

        var itemText: String = itemText
            protected set

        constructor(itemText: String, statusSupplier: BooleanSupplier) : this(itemText, null, statusSupplier)

        protected constructor(itemText: String, checkedColor: Color) : this(itemText, checkedColor, null)

        protected constructor(itemText: String) : this(itemText, null, null)

        val isChecked: Boolean
            get() = statusSupplier?.asBoolean ?: false
    }

    private val options: MutableList<CheckItem> = mutableListOf()
    private var checkItemColorSupplier: ConditionSupplier<Color>? = null

    @JvmOverloads
    constructor(itemText: String, uncheckedColor: Color = Color.GRAY) : this(itemText, Color.GREEN, uncheckedColor)

    init {
        setCheckItemColorSupplier(checkedColor, uncheckedColor)
    }

    fun addItem(item: CheckItem) = options.add(item)

    final fun setCheckItemColorSupplier(checkedColor: Color, uncheckedColor: Color) {
        checkItemColorSupplier = ConditionSupplier { c -> if (c) checkedColor else uncheckedColor }
    }

    override var itemText: String
        get() {
            var text = super.itemText
            for (item in options) {
                text += if (item.isChecked) {
                    if (item.checkedColor != null)
                        item.checkedColor!!.embeddingString + " [" + item.itemText + "]"
                    else
                        checkItemColorSupplier!![true].embeddingString + " [" + item.itemText + "]"
                } else
                    checkItemColorSupplier!![false].embeddingString + " [" + item.itemText + "]"
            }
            return text
        }
        set(value) {
            super.itemText = value
        }

    companion object {

        @JvmStatic
        fun create() = CheckItemBuilder()

    }

}