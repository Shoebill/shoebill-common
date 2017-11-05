package net.gtaun.shoebill.common

import net.gtaun.util.event.EventManager

import java.util.*

@AllOpen
class ShoebillContextManager(parentEventManager: EventManager) : AbstractShoebillContext(parentEventManager) {

    private val contextContainers =
            mutableMapOf<Class<out AbstractShoebillContext>, MutableSet<AbstractShoebillContext>>()

    override fun onInit() {}

    override fun onDestroy() {}

    fun <ContextType : AbstractShoebillContext> manage(context: ContextType): ContextType {
        val clazz = context.javaClass

        val contextContainer = contextContainers.getOrPut(clazz, { mutableSetOf() })
        if (contextContainer.contains(context)) return context

        contextContainer.add(context)
        addDestroyable(context)
        context.init()
        return context
    }

    fun destroy(context: AbstractShoebillContext) {
        val clazz = context.javaClass
        val contextContainer = contextContainers[clazz] ?: return

        contextContainer.remove(context)
        context.destroy()
        removeDestroyable(context)
    }

    fun <ContextType : AbstractShoebillContext> getContext(clazz: Class<ContextType>): ContextType? {
        val contextContainer = contextContainers[clazz] ?: return null
        val context = contextContainer.firstOrNull() ?: return null

        return clazz.cast(context)
    }

    fun <ContextType : AbstractShoebillContext> getContexts(clazz: Class<ContextType>): Set<ContextType> {
        val contextContainer = contextContainers[clazz] ?: return emptySet()

        return Collections.unmodifiableSet(contextContainer) as Set<ContextType>
    }
}
