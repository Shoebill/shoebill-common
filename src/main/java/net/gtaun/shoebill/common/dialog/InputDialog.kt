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
import net.gtaun.shoebill.event.dialog.DialogResponseEvent
import net.gtaun.shoebill.entities.Player
import net.gtaun.util.event.EventManager
import net.gtaun.util.event.EventManagerNode

import java.util.ArrayList

@Suppress("CanBeParameter")
/**
 * @author MK124
 * @author Marvin Haschker
 */
open class InputDialog
@JvmOverloads constructor(player: Player, parentEventManager: EventManager, val passwordMode: Boolean = false) :
        AbstractDialog(if (passwordMode) DialogStyle.PASSWORD else DialogStyle.INPUT, player, parentEventManager) {

    @Suppress("unused")
    open class InputDialogBuilder(player: Player, parentEventManager: EventManager) :
            AbstractDialog.Builder<InputDialog, InputDialogBuilder>() {

        open fun message(message: String) = message { message }
        open fun messageSupplier(supplier: DialogTextSupplier) = messageSupplier { supplier }
        open fun onClickOk(handler: ClickOkHandler) = onClickOk { handler }

        open fun message(init: InputDialogBuilder.() -> String): InputDialogBuilder {
            dialog.message = init(this)
            return this
        }

        open fun messageSupplier(init: InputDialogBuilder.() -> DialogTextSupplier): InputDialogBuilder {
            dialog.messageSupplier = init(this)
            return this
        }

        open fun onClickOk(init: InputDialogBuilder.() -> ClickOkHandler): InputDialogBuilder {
            dialog.clickOkHandler = init(this)
            return this
        }

        init {
            dialog = InputDialog(player, parentEventManager)
        }
    }

    open var messageSupplier = DialogTextSupplier { "None" }
    open var clickOkHandler: ClickOkHandler? = null

    open var message: String
        get() = messageSupplier[this]
        set(message) {
            this.messageSupplier = DialogTextSupplier { message }
        }

    open fun setMessage(messageSupplier: DialogTextSupplier) {
        this.messageSupplier = messageSupplier
    }

    override fun show() {
        show(messageSupplier[this])
    }

    override fun onClickOk(event: DialogResponseEvent) = onClickOk(event.inputText)

    open fun onClickOk(inputText: String) {
        clickOkHandler?.onClickOk(this, inputText) ?: return Unit
    }

    @FunctionalInterface
    interface ClickOkHandler {
        fun onClickOk(dialog: InputDialog, text: String)
    }

    companion object {

        @JvmStatic
        fun create(player: Player, eventManager: EventManager) = InputDialogBuilder(player, eventManager)

        fun ClickOkHandler(handler: (InputDialog, String) -> Unit) = object : ClickOkHandler {
            override fun onClickOk(dialog: InputDialog, text: String) {
                handler(dialog, text)
            }
        }
    }
}
