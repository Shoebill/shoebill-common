package net.gtaun.shoebill.common.command;

import net.gtaun.shoebill.object.Player;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

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
    private short priority;
    private String helpMessage;
    private CommandHandlerInternal handler;
    private boolean caseSensitive;
    private String categorie;
    private List<CustomCommandHandler> beforeCheckers;
    private List<CustomCommandHandler> customHandlers;
    private CommandParameter[] parameterAnnotations;

    public CommandEntryInternal(String command, Class<?>[] paramTypes, short priority, String helpMessage, boolean caseSensitive,
                                CommandHandlerInternal handler, String categorie, List<CustomCommandHandler> beforeCheckers, List<CustomCommandHandler> customHandlers,
                                CommandParameter[] parameterAnnotations) {
        if(parameterAnnotations == null) parameterAnnotations = new CommandParameter[0];
        this.command = command;
        this.paramTypes = paramTypes;
        this.priority = priority;
        this.helpMessage = helpMessage;
        this.caseSensitive = caseSensitive;
        this.handler = handler;
        this.categorie = categorie;
        this.beforeCheckers = beforeCheckers;
        this.customHandlers = customHandlers;
        this.parameterAnnotations = parameterAnnotations;
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

    public List<CustomCommandHandler> getBeforeCheckers() {
        return beforeCheckers;
    }

    public List<CustomCommandHandler> getCustomHandlers() {
        return customHandlers;
    }

    public CommandParameter[] getParameterAnnotations() {
        return parameterAnnotations;
    }
}
