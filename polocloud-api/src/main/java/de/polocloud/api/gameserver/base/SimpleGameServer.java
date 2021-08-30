package de.polocloud.api.gameserver.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.gameserver.GameServerUpdatePacket;
import de.polocloud.api.network.packets.master.MasterRequestsServerTerminatePacket;
import de.polocloud.api.network.protocol.packet.base.other.ForwardingPacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.base.IWrapper;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This is just an info class
 * No Netty instances like {@link ChannelHandlerContext} can be returned
 */
public class SimpleGameServer implements IGameServer {

    /**
     * The name of the server
     */
    private String name;

    /**
     * The motd of the server
     */
    private String motd;

    /**
     * The visibility state
     */
    private boolean serviceVisibility;

    /**
     * The status
     */
    private GameServerStatus gameServerStatus;

    /**
     * The snowflake id
     */
    private long snowflake;

    /**
     * The ping in ms
     */
    private long ping;

    /**
     * The startedTime in ms
     */
    private long startedTime;

    /**
     * The startedTime as long
     */
    private long memory;

    /**
     * The port
     */
    private int port;

    /**
     * The maximum players
     */
    private int maxPlayers;

    /**
     * The template (group)
     */
    private ITemplate template;

    /**
     * If authenticated
     */
    private boolean registered;

    /**
     * The netty context
     */
    private ChannelHandlerContext channelHandlerContext;

    /**
     * The host
     */
    private String host;

    public SimpleGameServer() {
    }

    public SimpleGameServer(String name, String motd, boolean serviceVisibility, GameServerStatus gameServerStatus, long snowflake, long ping, long startedTime, long memory, int port, int maxplayers, ITemplate template) {
        this.name = name;
        this.motd = motd;
        this.serviceVisibility = serviceVisibility;
        this.gameServerStatus = gameServerStatus;
        this.snowflake = snowflake;
        this.ping = ping;
        this.startedTime = startedTime;
        this.memory = memory;
        this.port = port;
        this.maxPlayers = maxplayers;
        this.template = template;
        this.registered = false;
        this.host = "127.0.0.1";
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getHost() {
        return host;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    @Override
    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    @Override
    public boolean isRegistered() {
        return registered;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public GameServerStatus getStatus() {
        return gameServerStatus;
    }

    @Override
    public void setStatus(GameServerStatus status) {
        gameServerStatus = status;
    }

    @Override
    public long getSnowflake() {
        return snowflake;
    }

    @Override
    public IWrapper getWrapper() {
        for (String wrapperName : getTemplate().getWrapperNames()) {
            IWrapper get = PoloCloudAPI.getInstance().getWrapperManager().getWrapper(wrapperName);
            if (get != null) {
                return get;
            }
        }
        return null;
    }


    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setSnowflake(long snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public void setStartedTime(long ms) {
        this.startedTime = ms;
    }

    @Override
    public void clone(Consumer<IGameServer> consumer) {
        IGameServer gameServer = this;
        consumer.accept(gameServer);
    }

    public void setTemplate(ITemplate template) {
        this.template = template;
    }

    @Override
    public void setMemory(long memory) {
        this.memory = memory;
    }

    @Override
    public void newSnowflake() {
        this.setSnowflake(Snowflake.getInstance().nextId());
    }

    @Override
    public void setTemplate(String template) {
        this.setTemplate(PoloHelper.sneakyThrows(() -> PoloCloudAPI.getInstance().getTemplateManager().getTemplate(template)));
    }

    @Override
    public ITemplate getTemplate() {
        return template;
    }

    @Override
    public List<ICloudPlayer> getCloudPlayers() {
        return PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached().stream().filter(cloudPlayer -> cloudPlayer.getMinecraftServer() != null && cloudPlayer.getMinecraftServer().getName().equalsIgnoreCase(this.name) || cloudPlayer.getProxyServer() != null && cloudPlayer.getProxyServer().getName().equalsIgnoreCase(this.name)).collect(Collectors.toList());
    }

    @Override
    public long getTotalMemory() {
        return memory;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getOnlinePlayers() {
        return this.getCloudPlayers().size();
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public long getPing() {
        return ping;
    }

    @Override
    public long getStartTime() {
        return startedTime;
    }

    @Override
    public void stop() {
        if (getWrapper() == null) {
            return;
        }
        PoloCloudAPI.getInstance().getGameServerManager().unregisterGameServer(this);
        getWrapper().sendPacket(new MasterRequestsServerTerminatePacket(this));
    }

    @Override
    public void terminate() {
        stop();
    }

    @Override
    public void receivePacket(Packet packet) {
        sendPacket(new ForwardingPacket(PoloType.GENERAL_GAMESERVER, this.name, packet));
    }

    @Override
    public void sendPacket(Packet packet) {
        if (this.channelHandlerContext != null) {
            this.channelHandlerContext.writeAndFlush(packet).addListener(PoloHelper.getChannelFutureListener(SimpleGameServer.class));;
            return;
        }
        PoloCloudAPI.getInstance().getConnection().sendPacket(new ForwardingPacket(PoloType.GENERAL_GAMESERVER, this.name, packet));
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public void setMotd(String motd) {
        this.motd = motd;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public ChannelHandlerContext ctx() {
        return null;
    }

    @Override
    public void setMaxPlayers(int players) {
        this.maxPlayers = players;
    }

    @Override
    public void setVisible(boolean serviceVisibility) {
        this.serviceVisibility = serviceVisibility;
    }

    @Override
    public boolean getServiceVisibility() {
        return serviceVisibility;
    }

    @Override
    public void update() {
        this.updateInternally();
        PoloCloudAPI.getInstance().sendPacket(new GameServerUpdatePacket(this));
    }

    @Override
    public void updateInternally() {
        PoloCloudAPI.getInstance().getGameServerManager().updateObject(this);
    }
}