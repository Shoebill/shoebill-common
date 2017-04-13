/**
 * Copyright (C) 2012-2016 MK124

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
import net.gtaun.shoebill.constant.DialogStyle
import net.gtaun.shoebill.entities.DialogId
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.event.dialog.DialogCloseEvent
import net.gtaun.shoebill.event.dialog.DialogCloseEvent.DialogCloseType
import net.gtaun.shoebill.event.dialog.DialogResponseEvent
import net.gtaun.util.event.Attentions
import net.gtaun.util.event.EventManager
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority


/**
 * @author MK124
 * @author Marvin Haschker
 */
@AllOpen
abstract class AbstractDialog(protected var style: DialogStyle, parentEventManager: EventManager) {

    @Suppress("UNCHECKED_CAST")
    @AllOpen
    class Builder<T : AbstractDialog, B : Builder<T, B>> {

        @Suppress("ProtectedInFinal")
        lateinit protected var dialog: T

        fun init(init: B.(B) -> Unit): B {
            init(this as B, this)
            return this
        }

        fun caption(caption: String) = caption { caption }
        fun buttonOk(buttonOk: String) = buttonOk { buttonOk }
        fun buttonCancel(buttonCancel: String) = buttonCancel { buttonCancel }

        fun captionSupplier(supplier: DialogTextSupplier): B {
            dialog.captionSupplier = supplier
            return this as B
        }

        fun buttonOkSupplier(supplier: DialogTextSupplier): B {
            dialog.buttonOkSupplier = supplier
            return this as B
        }

        fun buttonCancelSupplier(supplier: DialogTextSupplier): B {
            dialog.buttonCancelSupplier = supplier
            return this as B
        }

        fun onShow(handler: DialogHandler) = onShow { handler }
        fun onClose(handler: DialogCloseHandler) = onClose { handler }
        fun onCancel(handler: DialogHandler) = onCancel { handler }
        fun parentDialog(parentDialog: AbstractDialog) = parentDialog { parentDialog }

        fun caption(init: B.() -> String): B {
            dialog.caption = init(this as B)
            return this
        }

        fun captionSupplier(init: B.(AbstractDialog) -> String): B =
                captionSupplier(DialogTextSupplier { init(this as B, it) })

        fun buttonOk(init: B.() -> String): B {
            dialog.buttonOk = init(this as B)
            return this
        }

        fun buttonOkSupplier(init: B.(AbstractDialog) -> String): B =
                buttonOkSupplier(DialogTextSupplier { init(this as B, it) })

        fun buttonCancel(init: B.() -> String): B {
            dialog.buttonCancel = init(this as B)
            return this
        }

        fun buttonCancelSupplier(init: B.(AbstractDialog) -> String): B =
                buttonCancelSupplier(DialogTextSupplier { init(this as B, it) })

        fun onShow(init: B.() -> DialogHandler): B {
            dialog.showHandler = init(this as B)
            return this
        }

        fun onClose(init: B.() -> DialogCloseHandler): B {
            dialog.closeHandler = init(this as B)
            return this
        }

        fun onCancel(init: B.() -> DialogHandler): B {
            dialog.clickCancelHandler = init(this as B)
            return this
        }

        fun parentDialog(init: B.() -> AbstractDialog): B {
            dialog.parentDialog = init(this as B)
            return this
        }

        fun build(): T = dialog
    }

    @FunctionalInterface
    interface DialogCloseHandler {
        fun onClose(dialog: AbstractDialog, player: Player, type: DialogCloseType)
    }

    private val eventManagerInternal: EventManagerNode = parentEventManager.createChildNode()
    val dialogId: DialogId = DialogId.create()

    init {
        eventManagerInternal.registerHandler(DialogResponseEvent::class, { e ->
            onClose(DialogCloseType.RESPOND, e.player)

            if (e.dialogResponse == 1) {
                onClickOk(e)
            } else {
                onClickCancel(e.player)
            }

        }, HandlerPriority.NORMAL, Attentions.create().obj(dialogId))

        eventManagerInternal.registerHandler(DialogCloseEvent::class, { e ->
            if (e.type != DialogCloseType.RESPOND) {
                onClose(e.type, e.player)
            }
        }, HandlerPriority.NORMAL, Attentions.create().obj(dialogId))
    }

    var parentDialog: AbstractDialog? = null

    var captionSupplier = DialogTextSupplier { "None" }
    var buttonOkSupplier = DialogTextSupplier { "OK" }
    var buttonCancelSupplier = DialogTextSupplier { "Cancel" }

    var showHandler: DialogHandler? = null
    var closeHandler: DialogCloseHandler? = null
    var clickCancelHandler: DialogHandler? = null

    @Throws(Throwable::class)
    protected fun finalize() {
        destroy()
    }

    protected fun destroy() {
        eventManagerInternal.destroy()
        dialogId.destroy()
    }

    fun showParentDialog(player: Player) = parentDialog?.show(player)

    fun setCaption(captionSupplier: DialogTextSupplier) {
        this.captionSupplier = captionSupplier
    }

    var caption: String
        get() = captionSupplier[this]
        set(caption) {
            captionSupplier = DialogTextSupplier { caption }
        }

    fun setButtonOk(buttonOkSupplier: DialogTextSupplier) {
        this.buttonOkSupplier = buttonOkSupplier
    }

    fun setButtonCancel(buttonCancelSupplier: DialogTextSupplier) {
        this.buttonCancelSupplier = buttonCancelSupplier
    }

    var buttonOk: String
        get() = buttonOkSupplier[this]
        set(buttonOk) {
            buttonOkSupplier = DialogTextSupplier { buttonOk }
        }

    var buttonCancel: String
        get() = buttonCancelSupplier[this]
        set(buttonCancel) {
            buttonCancelSupplier = DialogTextSupplier { buttonCancel }
        }

    fun show(player: Player, text: String) {
        onShow(player)
        player.showDialog(dialogId, style, captionSupplier[this], text,
                buttonOkSupplier[this], buttonCancelSupplier[this])
    }

    abstract fun show(player: Player)

    fun onShow(player: Player) = showHandler?.handle(this, player)

    fun onClickOk(event: DialogResponseEvent) {}

    fun onClose(type: DialogCloseType, player: Player) = closeHandler?.onClose(this, player, type)
    fun onClickCancel(player: Player) = clickCancelHandler?.handle(this, player)

    companion object {
        fun DialogCloseHandler(handler: (AbstractDialog, Player, DialogCloseType) -> Unit) = object : DialogCloseHandler {
            override fun onClose(dialog: AbstractDialog, player: Player, type: DialogCloseType) {
                handler(dialog, player, type)
            }
        }
    }
}
