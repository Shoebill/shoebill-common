package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.object.Player;
import org.apache.commons.lang3.StringUtils;

class CommandEntryInternal {
    public static String completePath(String path, String child) {
        return StringUtils.isBlank(path) ? child : path + " " + child;
    }

    @FunctionalInterface
    public interface CommandHandlerInternal {
        boolean handle(Player player, Object[] params);
    }


    private String command;
    private Class<?>[] paramTypes;
    private String[] paramNames;
    private short priority;
    private String helpMessage;
    private CommandHandlerInternal handler;
    private boolean caseSensitive;
    private String categorie;


    public CommandEntryInternal(String command, Class<?>[] paramTypes, String[] paramNames, short priority, String helpMessage, boolean caseSensitive,
                                CommandHandlerInternal handler, String categorie) {
        this.command = command;
        this.paramTypes = paramTypes;
        this.paramNames = paramNames;
        this.priority = priority;
        this.helpMessage = helpMessage;
        this.caseSensitive = caseSensitive;
        this.handler = handler;
        this.categorie = categorie;
    }

    public String getCommand() {
        return command;
    }

    public String completeCommand(String path) {
        return completePath(path, command);
    }

    public Class<?>[] getParamTypes() {
        return paramTypes.clone();
    }

    public String[] getParamNames() {
        return paramNames.clone();
    }

    public short getPriority() {
        return priority;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public CommandHandlerInternal getHandler() {
        return handler;
    }

    public boolean handle(Player player, Object[] params) {
        return handler.handle(player, params);
    }

    public String getCategorie() {
        return categorie;
    }
}
