package de.polocloud.api.event.impl.net;

import de.polocloud.api.event.base.EventData;
import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

@EventData(nettyFire = false)
public class ChannelActiveEvent extends CloudEvent {

    private final ChannelHandlerContext chx;

    public ChannelActiveEvent(ChannelHandlerContext chx) {
        this.chx = chx;
        setNettyFired(true);
    }

    public ChannelHandlerContext getChx() {
        return chx;
    }

}
