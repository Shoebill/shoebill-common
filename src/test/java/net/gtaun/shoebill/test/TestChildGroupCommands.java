package net.gtaun.shoebill.test;

import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.object.Player;

/**
 * Created by marvin on 07.04.16.
 * Copyright (c) Marvin Haschker 2016.
 */
public class TestChildGroupCommands {

    @Command
    public boolean exit(Player player) {
        return true;
    }

}
