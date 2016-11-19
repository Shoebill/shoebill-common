package net.gtaun.shoebill.common.player

import net.gtaun.shoebill.Shoebill
import net.gtaun.shoebill.common.LifecycleObject
import net.gtaun.shoebill.entities.Player

/**
 * Created by marvin on 19.11.16 in project shoebill-common.
 * Copyright (c) 2016 Marvin Haschker. All rights reserved.
 */

abstract class PlayerLifecycleObject(val player: Player) : LifecycleObject(Shoebill.get().eventManager)