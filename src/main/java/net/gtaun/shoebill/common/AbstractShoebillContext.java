package net.gtaun.shoebill.common;

import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractShoebillContext implements Destroyable {
    protected final EventManager parentEventManager;

    private boolean isInited = false;
    protected EventManagerNode eventManagerNode;
    private Set<Destroyable> destroyables = new HashSet<>();


    public AbstractShoebillContext(EventManager parentEventManager) {
        this.parentEventManager = parentEventManager;
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

            eventManagerNode = parentEventManager.createChildNode();
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
        destroyables.forEach(net.gtaun.shoebill.object.Destroyable::destroy);
        destroyables.clear();

        eventManagerNode.destroy();
        eventManagerNode = null;

        isInited = false;
    }

    @Override
    public boolean isDestroyed() {
        return !isInited();
    }

    public boolean isInited()
    {
        return isInited;
    }

    protected abstract void onInit();

    protected abstract void onDestroy();
}
