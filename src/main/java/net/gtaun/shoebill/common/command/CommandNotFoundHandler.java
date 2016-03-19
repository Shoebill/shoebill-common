package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.object.Player;

/**
 * Created by marvin on 19.03.16.
 * Copyright (c) 2016 Marvin Haschker. All rights reserved.
 */
public interface CommandNotFoundHandler {

    boolean handle(Player player, CommandGroup commandGroup, Command command);

}
