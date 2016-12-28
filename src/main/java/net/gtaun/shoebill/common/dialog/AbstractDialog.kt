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

        open fun caption(caption: String) = caption { caption }
        open fun captionSupplier(supplier: DialogTextSupplier) = captionSupplier { supplier }
        open fun buttonOk(buttonOk: String) = buttonOk { buttonOk }
        open fun buttonOkSupplier(supplier: DialogTextSupplier) = buttonOkSupplier { supplier }
        open fun buttonCancel(buttonCancel: String) = buttonCancel { buttonCancel }
        open fun buttonCancelSupplier(supplier: DialogTextSupplier) = buttonCancelSupplier { supplier }
        open fun onShow(handler: DialogHandler) = onShow { handler }
        open fun onClose(handler: DialogCloseHandler) = onClose { handler }
        open fun onCancel(handler: DialogHandler) = onCancel { handler }
        open fun parentDialog(parentDialog: AbstractDialog) = parentDialog { parentDialog }

        open fun caption(init: B.() -> String): B {
            dialog.caption = init(this as B)
            return this
        }

        open fun captionSupplier(init: B.() -> DialogTextSupplier): B {
            dialog.captionSupplier = init(this as B)
            return this
        }

        open fun buttonOk(init: B.() -> String): B {
            dialog.buttonOk = init(this as B)
            return this
        }

        open fun buttonOkSupplier(init: B.() -> DialogTextSupplier): B {
            dialog.buttonOkSupplier = init(this as B)
            return this
        }

        open fun buttonCancel(init: B.() -> String): B {
            dialog.buttonCancel = init(this as B)
            return this
        }

        open fun buttonCancelSupplier(init: B.() -> DialogTextSupplier): B {
            dialog.setButtonCancel(init(this as B))
            return this
        }

        open fun onShow(init: B.() -> DialogHandler): B {
            dialog.showHandler = init(this as B)
            return this
        }

        open fun onClose(init: B.() -> DialogCloseHandler): B {
            dialog.closeHandler = init(this as B)
            return this
        }

        open fun onCancel(init: B.() -> DialogHandler): B {
            dialog.clickCancelHandler = init(this as B)
            return this
        }

        open fun parentDialog(init: B.() -> AbstractDialog): B {
            dialog.parentDialog = init(this as B)
            return this
        }

        open fun build(): T = dialog
    }

    @FunctionalInterface
    interface DialogCloseHandler {
        fun onClose(dialog: AbstractDialog, type: DialogCloseType)
    }

    protected val eventManagerNode: EventManagerNode = parentEventManager.createChildNode()
    private val eventManagerInternal: EventManagerNode = parentEventManager.createChildNode()

    open val dialogId: DialogId = DialogId.create()

    open var parentDialog: AbstractDialog? = null

    open var captionSupplier = DialogTextSupplier { "None" }
    open var buttonOkSupplier = DialogTextSupplier { "OK" }
    open var buttonCancelSupplier = DialogTextSupplier { "Cancel" }

    open var showHandler: DialogHandler? = null
    open var closeHandler: DialogCloseHandler? = null
    open var clickCancelHandler: DialogHandler? = null

    @Throws(Throwable::class)
    protected fun finalize() {
        destroy()
    }

    protected fun destroy() {
        eventManagerInternal.destroy()
        eventManagerNode.destroy()
    }

    open fun showParentDialog() {
        eventManagerNode.cancelAll()
        parentDialog?.show()
    }

    open fun setCaption(captionSupplier: DialogTextSupplier) {
        this.captionSupplier = captionSupplier
    }

    open var caption: String
        get() = captionSupplier[this]
        set(caption) {
            captionSupplier = DialogTextSupplier { caption }
        }

    open fun setButtonOk(buttonOkSupplier: DialogTextSupplier) {
        this.buttonOkSupplier = buttonOkSupplier
    }

    open fun setButtonCancel(buttonCancelSupplier: DialogTextSupplier) {
        this.buttonCancelSupplier = buttonCancelSupplier
    }

    open var buttonOk: String
        get() = buttonOkSupplier[this]
        set(buttonOk) {
            buttonOkSupplier = DialogTextSupplier { buttonOk }
        }

    open var buttonCancel: String
        get() = buttonCancelSupplier[this]
        set(buttonCancel) {
            buttonCancelSupplier = DialogTextSupplier { buttonCancel }
        }

    open fun show(text: String) {
        onShow()

        eventManagerInternal.cancelAll()
        eventManagerInternal.registerHandler(DialogResponseEvent::class, { e ->
            eventManagerInternal.cancelAll()
            onClose(DialogCloseType.RESPOND)

            if (e.dialogResponse == 1) {
                onClickOk(e)
            } else {
                onClickCancel()
            }

        }, HandlerPriority.NORMAL, Attentions.create().`object`(dialogId))

        eventManagerInternal.registerHandler(DialogCloseEvent::class, { e ->
            if (e.type != DialogCloseType.RESPOND) {
                eventManagerInternal.cancelAll()
                onClose(e.type)
            }
        }, HandlerPriority.NORMAL, Attentions.create().`object`(dialogId))

        player.showDialog(dialogId, style, captionSupplier[this], text,
                buttonOkSupplier[this], buttonCancelSupplier[this])
    }

    abstract fun show()

    open fun onShow() = showHandler?.handle(this)

    open fun onClickOk(event: DialogResponseEvent) {}

    open fun onClose(type: DialogCloseType) = closeHandler?.onClose(this, type)
    open fun onClickCancel() = clickCancelHandler?.handle(this)

    companion object {
        fun DialogCloseHandler(handler: (AbstractDialog, DialogCloseType) -> Unit) = object : DialogCloseHandler {
            override fun onClose(dialog: AbstractDialog, type: DialogCloseType) {
                handler(dialog, type)
            }
        }
    }
}
