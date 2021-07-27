package de.polocloud.tablist.collective;

import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.Master;
import de.polocloud.tablist.TablistModule;
import de.polocloud.tablist.cache.CloudPlayerTabCache;

import java.util.concurrent.ExecutionException;

public class CloudPlayerDisconnectListener implements EventHandler<CloudPlayerDisconnectEvent> {


    private boolean canUpdate;

    public CloudPlayerDisconnectListener() {
        this.canUpdate = TablistModule.getInstance().getTablistConfig().isUpdateOnPlayerConnection();
    }

    @Override
    public void handleEvent(CloudPlayerDisconnectEvent event) {
        ICloudPlayer player = event.getPlayer();

        CloudPlayerTabCache cloudPlayerTabCache = TablistModule.getInstance().getTabCache();
        cloudPlayerTabCache.remove(player.getUUID());
        if (!canUpdate) return;
        cloudPlayerTabCache.keySet().forEach(key -> {
            try {
                Master.getInstance().getCloudPlayerManager().getOnlinePlayer(key).get().sendTablist(cloudPlayerTabCache.get(key).getTabs()[0].getHeader().replace("%ONLINE_COUNT%", String.valueOf(getOnlineCount()))
                    , cloudPlayerTabCache.get(key).getTabs()[0].getFooter().replace("%ONLINE_COUNT%", String.valueOf(getOnlineCount())));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

    }

    public int getOnlineCount() {
        try {
            return Master.getInstance().getCloudPlayerManager().getAllOnlinePlayers().get().size();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
