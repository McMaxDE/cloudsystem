package de.polocloud.bootstrap.network;

import com.google.common.collect.Lists;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.protocol.packet.base.Packet;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimplePacketHandler<T extends Packet> implements IPacketHandler<Packet> {

    public static final List<SimplePacketHandler<?>> LISTENING = Lists.newArrayList();
    private Class<? extends Packet> packet;
    private BiConsumer<ChannelHandlerContext, T> actions;
    private Consumer<T> action;

    public SimplePacketHandler(Class<? extends Packet> packet, BiConsumer<ChannelHandlerContext, T> actions) {
        this.packet = packet;
        this.actions = actions;
        LISTENING.add(this);
    }

    public SimplePacketHandler(Class<T> packet, Consumer<T> action) {
        this.packet = packet;
        this.action = action;
        LISTENING.add(this);
    }

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet packet) {
        this.packet = packet.getClass();

        if (action != null) action.accept((T) packet);
        if (actions != null) actions.accept(ctx, (T) packet);
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return packet;
    }

    public BiConsumer<ChannelHandlerContext, T> getActions() {
        return actions;
    }

    public Class<? extends Packet> getPacket() {
        return packet;
    }

    public Consumer<T> getAction() {
        return action;
    }

}
