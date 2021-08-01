package de.polocloud.proxy.bootstrap;

import de.polocloud.api.module.Module;
import de.polocloud.proxy.ProxyModule;

public class ProxyBootstrap extends Module {

    @Override
    public void onLoad() {
        new ProxyModule(this);
    }

    @Override
    public boolean onReload() {
        return true;
    }

    @Override
    public void onShutdown() {

    }
}
