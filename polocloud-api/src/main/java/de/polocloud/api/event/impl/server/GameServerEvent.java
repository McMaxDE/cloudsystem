package de.polocloud.api.event.impl.server;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.IEvent;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;

import java.io.IOException;

@EventData(nettyFire = true)
public class GameServerEvent implements IEvent {

    private IGameServer gameServer;

    public GameServerEvent(IGameServer gameServer) {
        this.gameServer = gameServer;
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.gameServer = buf.readGameServer();
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeGameServer(gameServer);
    }

    public IGameServer getGameServer() {
        return gameServer;
    }
}