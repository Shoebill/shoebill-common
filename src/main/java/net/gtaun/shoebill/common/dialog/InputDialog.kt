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

import net.gtaun.shoebill.Shoebill
import net.gtaun.shoebill.common.AllOpen
import net.gtaun.shoebill.constant.DialogStyle
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.event.dialog.DialogResponseEvent
import net.gtaun.util.event.EventManager

/**
 * @author MK124
 * @author Marvin Haschker
 */
@Suppress("CanBeParameter")
@AllOpen
class InputDialog
@JvmOverloads constructor(parentEventManager: EventManager, val passwordMode: Boolean = false) :
        AbstractDialog(if (passwordMode) DialogStyle.PASSWORD else DialogStyle.INPUT, parentEventManager) {

    @Suppress("unused")
    @AllOpen
    class InputDialogBuilder(parentEventManager: EventManager) :
            AbstractDialog.Builder<InputDialog, InputDialogBuilder>() {

        fun message(message: String) = message { message }
        fun messageSupplier(supplier: DialogTextSupplier) = messageSupplier { supplier }
        fun onClickOk(handler: ClickOkHandler) = onClickOk { handler }

        fun message(init: InputDialogBuilder.() -> String): InputDialogBuilder {
            dialog.message = init(this)
            return this
        }

        fun messageSupplier(init: InputDialogBuilder.() -> DialogTextSupplier): InputDialogBuilder {
            dialog.messageSupplier = init(this)
            return this
        }

        fun onClickOk(init: InputDialogBuilder.() -> ClickOkHandler): InputDialogBuilder {
            dialog.clickOkHandler = init(this)
            return this
        }

        init {
            dialog = InputDialog(parentEventManager)
        }
    }

    var messageSupplier = DialogTextSupplier { "None" }
    var clickOkHandler: ClickOkHandler? = null

    var message: String
        get() = messageSupplier[this]
        set(message) {
            this.messageSupplier = DialogTextSupplier { message }
        }

    fun setMessage(messageSupplier: DialogTextSupplier) {
        this.messageSupplier = messageSupplier
    }

    override fun show(player: Player) {
        show(player, messageSupplier[this])
    }

    override fun onClickOk(event: DialogResponseEvent) = onClickOk(event.player, event.inputText)

    fun onClickOk(player: Player, inputText: String) {
        clickOkHandler?.onClickOk(this, player, inputText) ?: return Unit
    }

    @FunctionalInterface
    interface ClickOkHandler {
        fun onClickOk(dialog: InputDialog, player: Player, text: String)
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun create(eventManager: EventManager = Shoebill.get().eventManager) = InputDialogBuilder(eventManager)

        fun ClickOkHandler(handler: (InputDialog, Player, String) -> Unit) = object : ClickOkHandler {
            override fun onClickOk(dialog: InputDialog, player: Player, text: String) {
                handler(dialog, player, text)
            }
        }
    }
}
