package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

import java.util.ArrayList;
import java.util.List;

public class PlayerCommandManager extends CommandGroup implements Destroyable {
    public static final UsageMessageSupplier DEFAULT_USAGE_MESSAGE_SUPPLIER = (player, prefix, command) ->
    {
        StringBuilder stringBuilder = new StringBuilder("Usage: " + prefix + command.getCommand());
        if(command.getParameters().length > 0) {
            for(int i = 0; i < command.getParameters().length; i++) {
                stringBuilder.append(" [").append(command.getParameters()[i].name()).append("]");
            }
        }
        if(command.getHelpMessage() != null)
            stringBuilder.append(" - ").append(command.getHelpMessage());
        return stringBuilder.toString();
    };
    public static final HelpMessageSupplier DEFAULT_HELP_MESSAGE_SUPPLIER = (p, c, m) -> m;
    private HelpMessageSupplier helpMessageSupplier = DEFAULT_HELP_MESSAGE_SUPPLIER;
    private EventManagerNode eventManagerNode;

    public PlayerCommandManager(EventManager eventManager) {
        eventManagerNode = eventManager.createChildNode();
    }

    @Override
    public void destroy() {
        eventManagerNode.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return eventManagerNode.isDestroy();
    }

    public void installCommandHandler(HandlerPriority priority) {
        eventManagerNode.registerHandler(PlayerCommandEvent.class, priority, (e) ->
        {
            if (processCommand(e.getPlayer(), e.getCommand().substring(1))) e.setProcessed();
        });
    }

    public void installTextHandler(HandlerPriority priority, String prefix) {
        eventManagerNode.registerHandler(PlayerTextEvent.class, priority, (e) ->
        {
            String text = e.getText();
            if (!text.startsWith(prefix)) return;
            if (processCommand(e.getPlayer(), text.substring(prefix.length()), prefix)) {
                e.disallow();
                e.interrupt();
            }
        });
    }

    public void uninstallAllHandlers() {
        eventManagerNode.cancelAll();
    }

    public List<CommandEntry> getCommandEntries() {
        List<CommandEntry> entries = new ArrayList<>();
        getCommandEntries(entries, "");
        return entries;
    }

    public List<CommandEntry> getCommandEntries(String path) {
        List<CommandEntry> entries = new ArrayList<>();
        getCommandEntries(entries, "", path);
        return entries;
    }

    public void setHelpMessageSupplier(HelpMessageSupplier helpMessageSupplier) {
        this.helpMessageSupplier = helpMessageSupplier;
    }

    @FunctionalInterface
    public interface UsageMessageSupplier {
        String get(Player player, String prefix, CommandEntry command);
    }

    @FunctionalInterface
    public interface HelpMessageSupplier {
        String get(Player player, String command, String message);
    }
}
