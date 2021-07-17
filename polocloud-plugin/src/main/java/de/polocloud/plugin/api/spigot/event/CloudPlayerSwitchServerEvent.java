package de.polocloud.plugin.api.spigot.event;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.plugin.api.CloudExecutor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.concurrent.CompletableFuture;

public class CloudPlayerSwitchServerEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private String playerName;
    private String fromName;
    private String toName;

    public CloudPlayerSwitchServerEvent(String playerName, String toName, String fromName) {
        this.playerName = playerName;
        this.toName = toName;
        this.fromName = fromName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public CompletableFuture<IGameServer> getFrom() {
        return CloudExecutor.getInstance().getGameServerManager().getGameServerByName(fromName);
    }

    public CompletableFuture<IGameServer> getTo() {
        return CloudExecutor.getInstance().getGameServerManager().getGameServerByName(toName);
    }

    public CompletableFuture<ICloudPlayer> getPlayer() {
        return CloudExecutor.getInstance().getCloudPlayerManager().getOnlinePlayer(playerName);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
