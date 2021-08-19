package de.polocloud.wrapper.network.handler;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperRequestShutdownPacket;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;

public class WrapperRequestShutdownHandler implements IPacketHandler<Packet> {
    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Got shutdown request from master! stopping...");
        Wrapper.getInstance().terminate();
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return WrapperRequestShutdownPacket.class;
    }
}
