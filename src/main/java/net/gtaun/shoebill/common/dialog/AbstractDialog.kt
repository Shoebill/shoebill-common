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
abstract class AbstractDialog(protected var style: DialogStyle, val player: Player, parentEventManager: EventManager) {

    @Suppress("UNCHECKED_CAST")
    open class Builder<T : AbstractDialog, B : Builder<T, B>> {

        lateinit protected var dialog: T

        open fun init(init: B.(B) -> Unit): B {
            init(this as B, this)
            return this
        }

        fun caption(caption: String) = caption { caption }
        fun captionSupplier(supplier: DialogTextSupplier) = captionSupplier { supplier }
        fun buttonOk(buttonOk: String) = buttonOk { buttonOk }
        fun buttonOkSupplier(supplier: DialogTextSupplier) = buttonOkSupplier { supplier }
        fun buttonCancel(buttonCancel: String) = buttonCancel { buttonCancel }
        fun buttonCancelSupplier(supplier: DialogTextSupplier) = buttonCancelSupplier { supplier }
        fun onShow(handler: DialogHandler) = onShow { handler }
        fun onClose(handler: DialogCloseHandler) = onClose { handler }
        fun onCancel(handler: DialogHandler) = onCancel { handler }
        fun parentDialog(parentDialog: T) = parentDialog { parentDialog }

        fun caption(init: B.() -> String): B {
            dialog.caption = init(this as B)
            return this
        }

        fun captionSupplier(init: B.() -> DialogTextSupplier): B {
            dialog.captionSupplier = init(this as B)
            return this
        }

        fun buttonOk(init: B.() -> String): B {
            dialog.buttonOk = init(this as B)
            return this
        }

        fun buttonOkSupplier(init: B.() -> DialogTextSupplier): B {
            dialog.buttonOkSupplier = init(this as B)
            return this
        }

        fun buttonCancel(init: B.() -> String): B {
            dialog.buttonCancel = init(this as B)
            return this
        }

        fun buttonCancelSupplier(init: B.() -> DialogTextSupplier): B {
            dialog.setButtonCancel(init(this as B))
            return this
        }

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

        fun parentDialog(init: B.() -> T): B {
            dialog.parentDialog = init(this as B)
            return this
        }

        fun build(): T = dialog
    }

    @FunctionalInterface
    interface DialogCloseHandler {
        fun onClose(dialog: AbstractDialog, type: DialogCloseType)
    }

    protected val eventManagerNode: EventManagerNode = parentEventManager.createChildNode()
    private val eventManagerInternal: EventManagerNode = parentEventManager.createChildNode()

    val dialogId: DialogId = DialogId.create()

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
        eventManagerNode.destroy()
    }

    fun showParentDialog() {
        eventManagerNode.cancelAll()
        parentDialog?.show()
    }

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

    fun show(text: String) {
        onShow()

        eventManagerInternal.cancelAll()
        eventManagerInternal.registerHandler(DialogResponseEvent::class.java, HandlerPriority.NORMAL, Attentions.create().`object`(dialogId)) { e ->
            eventManagerInternal.cancelAll()
            onClose(DialogCloseType.RESPOND)

            if (e.dialogResponse == 1) {
                onClickOk(e)
            } else {
                onClickCancel()
            }

        }

        eventManagerInternal.registerHandler(DialogCloseEvent::class.java, HandlerPriority.NORMAL, Attentions.create().`object`(dialogId)) { e ->
            if (e.type != DialogCloseType.RESPOND) {
                eventManagerInternal.cancelAll()
                onClose(e.type)
            }
        }

        player.showDialog(dialogId, style, captionSupplier[this], text,
                buttonOkSupplier[this], buttonCancelSupplier[this])
    }

    abstract fun show()

    protected fun onShow() = showHandler?.handle(this)

    internal open fun onClickOk(event: DialogResponseEvent) {
    }

    protected fun onClose(type: DialogCloseType) = closeHandler?.onClose(this, type)
    protected fun onClickCancel() = clickCancelHandler?.handle(this)

    companion object {
        fun DialogCloseHandler(handler: (AbstractDialog, DialogCloseType) -> Unit) = object : DialogCloseHandler {
            override fun onClose(dialog: AbstractDialog, type: DialogCloseType) {
                handler(dialog, type)
            }
        }
    }
}
