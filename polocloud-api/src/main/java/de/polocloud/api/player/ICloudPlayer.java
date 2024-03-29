package de.polocloud.api.player;

import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.extras.IPlayerConnection;
import de.polocloud.api.player.extras.IPlayerSettings;
import de.polocloud.api.pool.PoloObject;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.IPropertyHolder;
import de.polocloud.api.util.Snowflake;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface ICloudPlayer extends PoloObject<ICloudPlayer>, CommandExecutor, IPropertyHolder {

    @Override
    default long getSnowflake() {
        return Snowflake.getInstance().nextId();
    }

    /**
     * Gets the {@link UUID} of this player
     */
    UUID getUUID();

    /**
     * Gets the current proxy-Server as {@link IGameServer}
     * of this player
     */
    IGameServer getProxyServer();

    /**
     * Gets the current spigot-Server as {@link IGameServer}
     * of this player
     */
    IGameServer getMinecraftServer();

    /**
     * THe {@link IPlayerConnection} of this player
     *
     * @return connection
     */
    IPlayerConnection getConnection();

    /**
     * THe {@link IPlayerSettings} of this player
     *
     * @return settings
     */
    IPlayerSettings getSettings();

    /**
     * The ping of the player to the network
     */
    long getPing();

    /**
     * Connects this player to a {@link IGameServer}
     *
     * @param gameServer the server to connect to
     */
    void sendTo(IGameServer gameServer);

    /**
     * Sends the tabList header and footer to this player
     *
     * @param header the header
     * @param footer the footer
     */
    void sendTabList(String header, String footer);

    /**
     * Updates this player all over the network
     */
    void update();

    /**
     * Kicks this player from the network
     * with a given reason to display
     *
     * @param reason the reason
     */
    void kick(String reason);

    /**
     * Sends this player to a fallback
     */
    void sendToFallback();

    /**
     * Sends this player to a fallback
     * Except servers
     */
    void sendToFallbackExcept(String... except);

}
