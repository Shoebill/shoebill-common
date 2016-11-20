package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.object.Player;

import java.util.Queue;

@FunctionalInterface
public interface CommandHandler {
    boolean handle(Player p, Queue<Object> params);
}
