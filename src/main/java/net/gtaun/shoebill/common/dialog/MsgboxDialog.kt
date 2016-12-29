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
@AllOpen
class MsgboxDialog(parentEventManager: EventManager) : AbstractDialog(DialogStyle.MSGBOX, parentEventManager) {

    @Suppress("unused")
    @AllOpen
    class MsgboxDialogBuilder(parentEventManager: EventManager) :
            AbstractDialog.Builder<MsgboxDialog, MsgboxDialogBuilder>() {

        fun message(message: String) = message { message }
        fun messageSupplier(supplier: DialogTextSupplier) = messageSupplier { supplier }

        fun message(init: MsgboxDialogBuilder.() -> String): MsgboxDialogBuilder {
            dialog.message = init(this)
            return this
        }

        fun messageSupplier(init: MsgboxDialogBuilder.() -> DialogTextSupplier): MsgboxDialogBuilder {
            dialog.messageSupplier = init(this)
            return this
        }

        init {
            dialog = MsgboxDialog(parentEventManager)
        }
    }

    var messageSupplier = DialogTextSupplier { "None" }
    var clickOkHandler: ClickOkHandler? = null

    var message: String
        get() = this.messageSupplier[this]
        set(message) {
            this.messageSupplier = DialogTextSupplier { message }
        }

    fun setMessage(messageSupplier: DialogTextSupplier) {
        this.messageSupplier = messageSupplier
    }

    override fun show(player: Player) = show(player, messageSupplier[this])

    override fun onClickOk(event: DialogResponseEvent) = onClickOk(event.player)

    fun onClickOk(player: Player) {
        clickOkHandler?.onClickOk(this, player) ?: return Unit
    }

    @FunctionalInterface
    interface ClickOkHandler {
        fun onClickOk(dialog: MsgboxDialog, player: Player)
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun create(eventManager: EventManager = Shoebill.get().eventManager) = MsgboxDialogBuilder(eventManager)

        fun ClickOkHandler(handler: (MsgboxDialog, Player) -> Unit) = object : ClickOkHandler {
            override fun onClickOk(dialog: MsgboxDialog, player: Player) {
                handler(dialog, player)
            }
        }
    }
}
