package net.gtaun.shoebill.test;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.reflect.KClass;
import net.gtaun.util.event.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by marvin on 07.04.16.
 * Copyright (c) Marvin Haschker 2016.
 */
public class NullEventManager implements EventManager {

    @NotNull
    @Override
    public <E extends Event> HandlerEntry registerHandler(KClass<E> kClass, Function1<? super E, Unit> function1) {
        return null;
    }

    @NotNull
    @Override
    public <E extends Event> HandlerEntry registerHandler(KClass<E> kClass, Function1<? super E, Unit> function1, HandlerPriority handlerPriority) {
        return null;
    }

    @NotNull
    @Override
    public <E extends Event> HandlerEntry registerHandler(KClass<E> kClass, Function1<? super E, Unit> function1, HandlerPriority handlerPriority, Attentions attentions) {
        return null;
    }

    @NotNull
    @Override
    public <E extends Event> HandlerEntry registerHandler(Class<E> aClass, EventHandler<? super E> eventHandler) {
        return null;
    }

    @NotNull
    @Override
    public <E extends Event> HandlerEntry registerHandler(Class<E> aClass, EventHandler<? super E> eventHandler, HandlerPriority handlerPriority) {
        return null;
    }

    @NotNull
    @Override
    public <E extends Event> HandlerEntry registerHandler(Class<E> aClass, EventHandler<? super E> eventHandler, HandlerPriority handlerPriority, Attentions attentions) {
        return null;
    }

    @Override
    public <E extends Event> void dispatchEvent(E e, Object... objects) {

    }

    @Override
    public <E extends Event> void dispatchEvent(ThrowableHandler throwableHandler, E e, Object... objects) {

    }

    @NotNull
    @Override
    public EventManagerNode createChildNode() {
        return null;
    }

    @NotNull
    @Override
    public ThrowableHandler getThrowableHandler() {
        return null;
    }

    @Override
    public void setThrowableHandler(ThrowableHandler throwableHandler) {

    }
}
