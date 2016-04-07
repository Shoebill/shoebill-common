package net.gtaun.shoebill.test;

import net.gtaun.util.event.*;

/**
 * Created by marvin on 07.04.16.
 * Copyright (c) Marvin Haschker 2016.
 */
public class NullEventManager implements EventManager {
    @Override
    public <E extends Event> HandlerEntry registerHandler(Class<E> type, short priority, Attentions concerns, EventHandler<E> handler) {
        return null;
    }

    @Override
    public <E extends Event> void dispatchEvent(ThrowableHandler handler, E event, Object... objects) {

    }

    @Override
    public EventManagerNode createChildNode() {
        return new NullEventManagerNode(this);
    }
}
