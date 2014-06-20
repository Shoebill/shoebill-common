package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.object.Player;

@FunctionalInterface
public interface CustomCommandHandler
{
	boolean handle(Player p, String cmd, String params);
}
