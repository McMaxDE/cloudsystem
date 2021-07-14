package de.polocloud.api.network.protocol.packet.handler;

import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<IPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket packet, ByteBuf byteBuf) throws Exception {

        int id = PacketRegistry.getPacketId(packet.getClass());
        if (id == -1) {
            throw new NullPointerException("Packet with " + packet.getClass().getSimpleName() + " was not registered");
        }
        System.out.println("write packet #" + packet.getClass().getSimpleName());
        byteBuf.writeInt(id);
        packet.write(byteBuf);

    }
}