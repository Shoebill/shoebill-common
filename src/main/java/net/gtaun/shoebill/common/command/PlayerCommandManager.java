package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerCommandManager extends CommandGroup implements Destroyable {
    @FunctionalInterface
    public interface UsageMessageSupplier {
        String get(Player player, String prefix, CommandEntry command);
    }

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

    @FunctionalInterface
    public interface HelpMessageSupplier {
        String get(Player player, String command, String message);
    }

    public static final HelpMessageSupplier DEFAULT_HELP_MESSAGE_SUPPLIER = (p, c, m) -> m;


    private EventManagerNode eventManagerNode;

    private UsageMessageSupplier usageMessageSupplier = DEFAULT_USAGE_MESSAGE_SUPPLIER;
    private HelpMessageSupplier helpMessageSupplier = DEFAULT_HELP_MESSAGE_SUPPLIER;


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
            if (processCommand(e.getPlayer(), e.getCommand().substring(1), "/", true)) e.setProcessed();
        });
    }

    public void installTextHandler(HandlerPriority priority, String prefix) {
        eventManagerNode.registerHandler(PlayerTextEvent.class, priority, (e) ->
        {
            String text = e.getText();
            if (!text.startsWith(prefix)) return;
            if (processCommand(e.getPlayer(), text.substring(prefix.length()), prefix, true)) {
                e.disallow();
                e.interrupt();
            }
        });
    }

    public void uninstallAllHandlers() {
        eventManagerNode.cancelAll();
    }

    @Override
    public boolean processCommand(Player player, String commandText) {
        return processCommand(player, commandText, "", false);
    }

    public boolean processCommand(Player player, String commandText, String prefix, boolean sendUsages) {
        List<Pair<String, CommandEntryInternal>> matchedCommands = new ArrayList<>();
        if (processCommand("", matchedCommands, player, commandText)) return true;
        if (!sendUsages) return false;

        if (matchedCommands.isEmpty()) return false;
        sendUsageMessages(player, prefix, matchedCommands);
        return true;
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

    public String getUsageMessage(Player player, String commandText) {
        return getUsageMessage(player, commandText, "/");
    }

    public String getUsageMessage(Player player, String commandText, String prefix) {
        String message = "";
        for (Iterator<Pair<String, CommandEntryInternal>> it = getMatchedCommands(commandText).iterator(); it.hasNext(); ) {
            Pair<String, CommandEntryInternal> e = it.next();
            message += getUsageMessage(player, e.getLeft(), prefix, e.getRight());
            if (it.hasNext()) message += "\n";
        }
        return message;
    }

    private String getUsageMessage(Player player, String path, String prefix, CommandEntryInternal entry) {
        return usageMessageSupplier.get(player, prefix, new CommandEntry(entry, path));
    }

    public void sendUsageMessage(Player player, String commandText) {
        sendUsageMessage(player, commandText, "/");
    }

    public void sendUsageMessage(Player player, String commandText, String prefix) {
        sendUsageMessages(player, prefix, getMatchedCommands(commandText));
    }

    private void sendUsageMessages(Player player, String prefix, List<Pair<String, CommandEntryInternal>> commands) {
        if (this.usageMessageSupplier == null) return;
        for (Pair<String, CommandEntryInternal> e : commands) {
            String usageMessage = getUsageMessage(player, e.getLeft(), prefix, e.getRight());
            if (usageMessage != null) player.sendMessage(Color.RED, usageMessage);
        }
    }

    public void setUsageMessageSupplier(UsageMessageSupplier usageMessageSupplier) {
        this.usageMessageSupplier = usageMessageSupplier;
    }

    public void setHelpMessageSupplier(HelpMessageSupplier helpMessageSupplier) {
        this.helpMessageSupplier = helpMessageSupplier;
    }
}
