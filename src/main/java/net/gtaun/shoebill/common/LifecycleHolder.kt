package net.gtaun.shoebill.common

import net.gtaun.shoebill.Shoebill
import net.gtaun.shoebill.entities.Destroyable
import net.gtaun.util.event.EventManager
import net.gtaun.util.event.EventManagerNode

/**
 * Created by marvin on 14.11.16 in project shoebill-common.
 * Copyright (c) 2016 Marvin Haschker. All rights reserved.
 */

open class LifecycleHolder<T> @JvmOverloads
constructor(eventManager: EventManager = Shoebill.get().eventManager) : Destroyable {

    protected val eventManagerNode: EventManagerNode = eventManager.createChildNode()
    private val lifecycleObjects = mutableMapOf<T, MutableList<LifecycleObject>>()
    private val lifecycleFactories = mutableMapOf<Class<out LifecycleObject>,
            LifecycleFactory<T, LifecycleObject>>()

    open fun <B : LifecycleObject> registerClass(lifecycleObject: Class<B>, factory: LifecycleFactory<T, B>) {
        lifecycleFactories.put(lifecycleObject, factory)
        lifecycleObjects.forEach { buildObject(it.key, lifecycleObject) }
    }

    open fun <B : LifecycleObject> unregisterClass(lifecycleObject: Class<B>) {
        lifecycleObjects.forEach { destroyObject(it.key, lifecycleObject) }
        lifecycleFactories.remove(lifecycleObject)
    }

    @Suppress("UNCHECKED_CAST")
    open fun <B : LifecycleObject> getObject(input: T, clazz: Class<B>): B? {
        val objects = lifecycleObjects[input] ?: return null
        val lifecycleObject = objects.filter { it.javaClass == clazz }.firstOrNull() ?: return null
        return lifecycleObject as B
    }

    open fun <B : LifecycleObject> getObjects(clazz: Class<B>): List<B> {
        return lifecycleObjects.filter { it.value.filter { it.javaClass == clazz }.count() > 0 }
                .map { it.value }
                .first()
                .map { it as B }
    }

    open fun buildObjects(input: T) {
        val list = lifecycleFactories.map {
            val obj = it.value.create(input)
            obj.init()
            return@map obj
        }.toMutableList()
        lifecycleObjects.put(input, list)
    }

    open fun <B : LifecycleObject> buildObject(input: T, clazz: Class<B>) {
        val factory = lifecycleFactories.filter { it.key == clazz }.map { it.value }.firstOrNull() ?: return
        val playerList = lifecycleObjects[input] ?: return
        val obj = factory.create(input)
        obj.init()
        playerList.add(obj)
    }

    open fun destroyObjects(input: T) {
        val list = lifecycleObjects[input] ?: return
        list.forEach { it.destroy() }
        lifecycleObjects.remove(input)
    }

    open fun <B : LifecycleObject> destroyObject(input: T, clazz: Class<B>) {
        val playerList = lifecycleObjects[input] ?: return
        val obj = playerList.filter { it.javaClass == clazz }.firstOrNull() ?: return
        obj.destroy()
        playerList.remove(obj)
    }

    override val isDestroyed: Boolean
        get() = eventManagerNode.isDestroyed

    override fun destroy() = eventManagerNode.destroy()

    @FunctionalInterface
    interface LifecycleFactory<in B, out T : LifecycleObject> {
        fun create(input: B): T
    }

}