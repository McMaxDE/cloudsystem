package de.polocloud.tablist.collective;

import com.google.inject.Inject;
import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.player.CloudPlayerJoinNetworkEvent;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.tablist.TablistModule;

public class CloudPlayerJoinListener implements EventHandler<CloudPlayerJoinNetworkEvent> {

    public boolean canUpdate;

    @Inject
    private MasterConfig config;

    public CloudPlayerJoinListener() {
        this.canUpdate = TablistModule.getInstance().getTablistConfig().isUpdateOnPlayerConnection();
    }

    @Override
    public void handleEvent(CloudPlayerJoinNetworkEvent event) {
        ICloudPlayer player = event.getPlayer();

        TablistModule.getInstance().getTablistSetExecute().execute(player, config, true);
        TablistModule.getInstance().getTablistUpdateExecute().execute(player, config, true);
    }
}
