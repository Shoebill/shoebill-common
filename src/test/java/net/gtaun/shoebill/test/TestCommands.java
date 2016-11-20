package net.gtaun.shoebill.test;

import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;

/**
 * Created by marvin on 07.04.16.
 * Copyright (c) Marvin Haschker 2016.
 */
public class TestCommands {

    @Command
    public boolean kill(Player player) {
        return true;
    }

    @Command
    public boolean say(Player player, String message) {
        return true;
    }

}
