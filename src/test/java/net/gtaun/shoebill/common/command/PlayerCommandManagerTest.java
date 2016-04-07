package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.test.NullEventManager;
import net.gtaun.shoebill.test.TestCommands;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by marvin on 07.04.16.
 * Copyright (c) Marvin Haschker 2016.
 */
public class PlayerCommandManagerTest {

    private PlayerCommandManager playerCommandManager;

    @org.junit.Before
    public void setUp() throws Exception {
        playerCommandManager = new PlayerCommandManager(new NullEventManager());
        playerCommandManager.registerCommands(new TestCommands());
        playerCommandManager.setUsageMessageSupplier((player, prefix, command) -> {
            StringBuilder stringBuilder = new StringBuilder("Usage: " + prefix + command.getCommand());
            if (command.getParameters().length > 0) {
                for (int i = 0; i < command.getParameters().length; i++) {
                    stringBuilder.append(" [").append(command.getParameters()[i].name()).append("]");
                }
            }
            if (command.getHelpMessage() != null)
                stringBuilder.append(" - ").append(command.getHelpMessage());
            return stringBuilder.toString();
        });
    }

    @org.junit.Test
    public void getCommandEntries() throws Exception {
        List<CommandEntry> commands = playerCommandManager.getCommandEntries();
        assertEquals(2, commands.size());
        assertEquals("", commands.get(0).getPath());
        assertEquals("", commands.get(1).getPath());

        assertEquals(2, playerCommandManager.getCommandEntries("").size());
        assertEquals(0, playerCommandManager.getCommandEntries("test").size());
    }

}