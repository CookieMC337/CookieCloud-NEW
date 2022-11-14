package de.cookiemc.modules.proxy.listener;

import de.cookiemc.driver.event.EventListener;
import de.cookiemc.driver.event.defaults.server.ServiceRegisterEvent;
import de.cookiemc.driver.event.defaults.server.ServiceUnregisterEvent;
import de.cookiemc.driver.event.defaults.task.TaskUpdateEvent;
import de.cookiemc.modules.proxy.ProxyModule;

public class ModuleListener {

    @EventListener
    public void handle(TaskUpdateEvent event) {
        ProxyModule.getInstance().updateTabList();
        ProxyModule.getInstance().updateMotd();
    }

    @EventListener
    public void handle(ServiceRegisterEvent event) {
        ProxyModule.getInstance().updateTabList();
        ProxyModule.getInstance().updateMotd();
    }

    @EventListener
    public void handle(ServiceUnregisterEvent event) {
        ProxyModule.getInstance().updateTabList();
        ProxyModule.getInstance().updateMotd();
    }
}
