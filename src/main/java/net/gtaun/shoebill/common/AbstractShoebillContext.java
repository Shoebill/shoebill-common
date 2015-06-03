package net.gtaun.shoebill.common;

import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class AbstractShoebillContext implements Destroyable {
    protected final EventManager rootEventManager;

    private boolean isInited = false;
    protected EventManagerNode eventManagerNode;
    private Set<Destroyable> destroyables = new HashSet<>();


    public AbstractShoebillContext(EventManager rootEventManager) {
        this.rootEventManager = rootEventManager;
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    protected void addDestroyable(Destroyable destroyable) {
        destroyables.add(destroyable);
    }

    protected void removeDestroyable(Destroyable destroyable)
    {
        destroyables.remove(destroyable);
    }

    public final void init() {
        try {
            if (isInited) return;

            eventManagerNode = rootEventManager.createChildNode();
            onInit();
            isInited = true;
        } catch (Throwable e) {
            e.printStackTrace();
            destroy();
        }
    }

    @Override
    public final void destroy() {
        if (isDestroyed()) return;

        onDestroy();
        for (Destroyable destroyable : destroyables) destroyable.destroy();
        destroyables.clear();

        eventManagerNode.destroy();
        eventManagerNode = null;

        isInited = false;
    }

    @Override
    public boolean isDestroyed() {
        return isInited();
    }

    public boolean isInited()
    {
        return isInited;
    }

    protected abstract void onInit();

    protected abstract void onDestroy();
}
