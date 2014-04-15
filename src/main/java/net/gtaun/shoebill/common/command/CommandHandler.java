package net.gtaun.shoebill.common.command;

import java.util.Queue;

import net.gtaun.shoebill.object.Player;

@FunctionalInterface
public interface CommandHandler
{
	boolean handle(Player p, Queue<Object> params);
}
