package net.gtaun.shoebill.common

import net.gtaun.shoebill.entities.Destroyable
import net.gtaun.util.event.EventManager
import net.gtaun.util.event.EventManagerNode

import java.util.HashSet

abstract class AbstractShoebillContext(parentEventManager: EventManager) : Destroyable {

    private var isInitialized = false
    private var eventManagerNode: EventManagerNode = parentEventManager.createChildNode()
    private val destroyables = mutableSetOf<Destroyable>()

    fun addDestroyable(destroyable: Destroyable) = destroyables.add(destroyable)

    fun removeDestroyable(destroyable: Destroyable) = destroyables.remove(destroyable)

    fun init() {
        if (isInitialized) return
        try {
            onInit()
            isInitialized = true
        } catch (e: Throwable) {
            e.printStackTrace()
            destroy()
        }

    }

    override fun destroy() {
        if (isDestroyed) return

        onDestroy()
        destroyables.forEach { it.destroy() }
        destroyables.clear()

        eventManagerNode.destroy()

        isInitialized = false
    }

    override val isDestroyed: Boolean
        get() = !isInitialized

    protected abstract fun onInit()

    protected abstract fun onDestroy()
}
