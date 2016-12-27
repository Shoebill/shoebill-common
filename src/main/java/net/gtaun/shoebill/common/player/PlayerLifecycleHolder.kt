package net.gtaun.shoebill.common.player

import net.gtaun.shoebill.Shoebill
import net.gtaun.shoebill.common.LifecycleHolder
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.event.player.PlayerConnectEvent
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent
import net.gtaun.util.event.EventManager
import net.gtaun.util.event.HandlerPriority
import kotlin.reflect.KClass

/**
 * Created by marvin on 19.11.16 in project shoebill-common.
 * Copyright (c) 2016 Marvin Haschker. All rights reserved.
 */

class PlayerLifecycleHolder @JvmOverloads constructor(eventManager: EventManager = Shoebill.get().eventManager) :
        LifecycleHolder<Player>(eventManager) {

    init {
        eventManagerNode.registerHandler(PlayerConnectEvent::class, {
            buildObjects(it.player)
        }, HandlerPriority.MONITOR)

        eventManagerNode.registerHandler(PlayerDisconnectEvent::class, {
            destroyObjects(it.player)
        }, HandlerPriority.BOTTOM)
    }

    fun <B : PlayerLifecycleObject> registerClass(lifecycleObject: Class<B>) =
            registerClass(lifecycleObject, makeFactory(lifecycleObject.kotlin))


    companion object {

        fun <B : PlayerLifecycleObject> makeFactory(clazz: KClass<B>) = object : LifecycleFactory<Player, B> {
            override fun create(input: Player): B {
                val constructor = clazz.constructors.filter {
                    it.parameters.size == 1 &&
                            it.parameters.first().type == Player::class
                }
                        .firstOrNull() ?: throw Exception("No valid constructor available for class $clazz.")
                return constructor.call(input)
            }

        }

    }

}