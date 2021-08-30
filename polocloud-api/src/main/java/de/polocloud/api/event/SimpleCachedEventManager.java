
package de.polocloud.api.event;


import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.ICancellable;
import de.polocloud.api.event.base.IEvent;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.handling.EventMethod;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.IEventHandler;
import de.polocloud.api.network.packets.api.EventPacket;
import de.polocloud.api.scheduler.Scheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class SimpleCachedEventManager implements IEventManager {

    /**
     * All cached registered classes
     */
    private final Map<IListener, List<EventMethod<EventHandler>>> registeredClasses;
    private final Map<Class<? extends IEvent>, List<IEventHandler<?>>> eventHandlers;

    public SimpleCachedEventManager() {
        this.registeredClasses = new HashMap<>();
        this.eventHandlers = new HashMap<>();
    }

    @Override
    public void registerListener(IListener listener) {
        List<EventMethod<EventHandler>> eventMethods = new ArrayList<>();

        for (Method m : listener.getClass().getDeclaredMethods()) {
            EventHandler annotation = m.getAnnotation(EventHandler.class);

            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];

                m.setAccessible(true);
                eventMethods.add(new EventMethod<>(listener, m, parameterType, annotation));
            }
        }

        eventMethods.sort(Comparator.comparingInt(em -> em.getAnnotation().value().getValue()));
        registeredClasses.put(listener, eventMethods);
    }


    @Override
    public void unregisterListener(Class<? extends IListener> listenerClass) {
        for (IListener iListener : registeredClasses.keySet()) {
            if (iListener.getClass().equals(listenerClass)) {
                registeredClasses.remove(iListener);
            }
        }
    }

    @Override
    public <E extends IEvent> void registerHandler(Class<E> eventClass, IEventHandler<E> handler) {
        List<IEventHandler<?>> iEventHandlers = eventHandlers.get(eventClass);
        if (iEventHandlers == null) {
            iEventHandlers = new LinkedList<>();
        }
        iEventHandlers.add(handler);
        this.eventHandlers.put(eventClass, iEventHandlers);
    }

    @Override
    public <E extends IEvent> void unregisterHandler(Class<E> eventClass, Class<? extends IEventHandler<E>> handlerClass) {


        for (List<IEventHandler<?>> value : this.eventHandlers.values()) {
            for (IEventHandler<?> iEventHandler : value) {
                if (iEventHandler.getClass().equals(handlerClass)) {
                    List<IEventHandler<?>> iEventHandlers = eventHandlers.get(eventClass);
                    if (iEventHandlers == null) {
                        iEventHandlers = new LinkedList<>();
                    }
                    iEventHandlers.remove(iEventHandler);
                    this.eventHandlers.put(eventClass, iEventHandlers);
                }
            }
        }

    }

    @Override
    public <E extends IEvent> void fireEvent(E event, Consumer<E> callback) {
        this.fireEvent(event);
        callback.accept(event);
    }


    @Override
    public boolean fireEvent(IEvent event) {

        EventData eventData = event.getClass().getAnnotation(EventData.class);

        boolean nettyFire = eventData == null || eventData.nettyFire();
        PoloType[] ignoredTypes = eventData != null ? eventData.ignoreTypes() : new PoloType[0];
        boolean async = eventData != null && eventData.async();

        if (nettyFire) {
            if (PoloCloudAPI.getInstance().getType().isPlugin()) {
                if (PoloCloudAPI.getInstance().getConnection() != null) {
                    PoloCloudAPI.getInstance().getConnection().sendPacket(new EventPacket(event, PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName(), ignoredTypes, async));
                }
            } else {
                if (PoloCloudAPI.getInstance().getConnection() != null) {
                    PoloCloudAPI.getInstance().getConnection().sendPacket(new EventPacket(event, "cloud", ignoredTypes, async));
                }
            }
        }

        for (Class<? extends IEvent> aClass : this.eventHandlers.keySet()) {
            if (event.getClass().equals(aClass)) {
                for (IEventHandler iEventHandler : this.eventHandlers.get(aClass)) {
                    if (async) {
                        Scheduler.runtimeScheduler().async().schedule(() -> iEventHandler.handleEvent(event));
                    } else {
                        iEventHandler.handleEvent(event);
                    }
                }
            }
        }
        try {
            registeredClasses.forEach((object, methodList) -> {
                for (EventMethod<EventHandler> em : methodList) {
                    if (em.getaClass().equals(event.getClass())) {
                        if (async) {
                            Scheduler.runtimeScheduler().async().schedule(() -> {
                                try {
                                    em.getMethod().invoke(em.getListener(), event);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            try {
                                em.getMethod().invoke(em.getListener(), event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            return event instanceof ICancellable && ((ICancellable) event).isCancelled();
        } catch (Exception e) {
            return false;
        }
    }

}