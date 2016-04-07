package net.gtaun.shoebill.test;

import net.gtaun.util.event.*;

/**
 * Created by marvin on 07.04.16.
 * Copyright (c) Marvin Haschker 2016.
 */
public class NullEventManagerNode implements EventManagerNode {

    private EventManager parent;
    private boolean destroyed = false;

    public NullEventManagerNode(EventManager parent) {
        this.parent = parent;
    }

    @Override
    public void cancelAll() {

    }

    @Override
    public void destroy() {
        if (isDestroy()) return;

        destroyed = true;
    }

    @Override
    public boolean isDestroy() {
        return destroyed;
    }

    @Override
    public EventManager getParent() {
        return parent;
    }

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
