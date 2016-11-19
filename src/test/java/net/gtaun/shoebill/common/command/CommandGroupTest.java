package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.test.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by marvin on 07.04.16.
 * Copyright (c) Marvin Haschker 2016.
 */
public class CommandGroupTest {

    private PlayerCommandManager playerCommandManager;
    private CommandGroup firstGroup, secondGroup;
    private CommandGroup firstChildGroup;

    @Before
    public void setUp() throws Exception {
        playerCommandManager = new PlayerCommandManager(new NullEventManager());
        firstGroup = new CommandGroup();
        firstGroup.registerCommands(new TestCommands());
        firstGroup.setUsageMessageSupplier((player, prefix, command) -> "Usage Message Supplier for FirstGroup!");

        secondGroup = new CommandGroup();
        secondGroup.registerCommands(new TestCommandGroupCommands());
        secondGroup.setUsageMessageSupplier((player, prefix, command) -> "Usage Message Supplier for SecondGroup!");

        firstChildGroup = new CommandGroup();
        firstChildGroup.registerCommands(new TestChildGroupCommands());

        playerCommandManager.registerGroup(firstGroup);
        playerCommandManager.registerGroup(secondGroup);
        playerCommandManager.registerChildGroup(firstChildGroup, "house");
    }

    @Test
    public void processCommand() throws Exception {
        Player player = new NullPlayer();
        assertEquals(true, playerCommandManager.processCommand(player, "say"));
        assertEquals(false, playerCommandManager.processCommand(player, "hello"));
        assertEquals(true, playerCommandManager.processCommand(player, "house exit"));
        assertEquals(false, playerCommandManager.processCommand(player, "house enter"));
    }

    @Test
    public void getUsageMessage() throws Exception {
        Player player = new NullPlayer();
        assertEquals("Usage Message Supplier for FirstGroup!", firstGroup.getUsageMessage(player, "say"));
        assertEquals("Usage Message Supplier for SecondGroup!", secondGroup.getUsageMessage(player, "kick"));
        assertEquals("Usage: /exit", firstChildGroup.getUsageMessage(player, "exit"));
    }

    @Test
    public void getCommands() throws Exception {
        assertEquals(2, firstGroup.getCommands().size());
        assertEquals(2, secondGroup.getCommands().size());
        assertEquals(1, firstChildGroup.getCommands().size());
        assertEquals(5, playerCommandManager.getCommands().size());
    }

}