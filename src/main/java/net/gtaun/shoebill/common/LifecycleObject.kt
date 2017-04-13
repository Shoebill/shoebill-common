package net.gtaun.shoebill.common

import net.gtaun.shoebill.Shoebill
import net.gtaun.util.event.EventManager

/**
 * Created by marvin on 14.11.16 in project shoebill-common.
 * Copyright (c) 2016 Marvin Haschker. All rights reserved.
 */

@AllOpen
abstract class LifecycleObject
@JvmOverloads constructor(eventManager: EventManager = Shoebill.get().eventManager) :
        AbstractShoebillContext(eventManager)