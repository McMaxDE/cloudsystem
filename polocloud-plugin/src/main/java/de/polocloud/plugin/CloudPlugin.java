package de.polocloud.plugin;

import de.polocloud.plugin.function.BootstrapFunction;
import de.polocloud.plugin.function.NetworkRegisterFunction;
import de.polocloud.plugin.protocol.NetworkClient;
import de.polocloud.plugin.protocol.maintenance.MaintenanceState;
import de.polocloud.plugin.protocol.motd.MotdUpdateCache;
import de.polocloud.plugin.protocol.players.MaxPlayerProperty;

public class CloudPlugin {

    private static CloudPlugin instance;

    private MaintenanceState state;
    private MaxPlayerProperty maxPlayerProperty;

    private MotdUpdateCache motdUpdateCache;

    private BootstrapFunction bootstrapFunction;
    private NetworkClient networkClient;

    public CloudPlugin(BootstrapFunction bootstrapFunction, NetworkRegisterFunction networkRegisterFunction) {

        instance = this;

        this.bootstrapFunction = bootstrapFunction;
        this.maxPlayerProperty = new MaxPlayerProperty(this.bootstrapFunction);

        this.motdUpdateCache = new MotdUpdateCache();

        this.networkClient = new NetworkClient();
        this.networkClient.connect(bootstrapFunction.getNetworkPort());

        bootstrapFunction.initStatisticChannel(networkClient);
        bootstrapFunction.registerEvents(networkClient);
        networkRegisterFunction.callNetwork(networkClient);
    }

    public static CloudPlugin getInstance() {
        return instance;
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    public void setState(MaintenanceState state) {
        this.state = state;
    }

    public MaintenanceState getState() {
        return state;
    }

    public BootstrapFunction getBootstrapFunction() {
        return bootstrapFunction;
    }

    public MaxPlayerProperty getMaxPlayerProperty() {
        return maxPlayerProperty;
    }

    public MotdUpdateCache getMotdUpdateCache() {
        return motdUpdateCache;
    }
}
