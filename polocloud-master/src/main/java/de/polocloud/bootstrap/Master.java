package de.polocloud.bootstrap;

import com.esotericsoftware.kryonetty.network.ConnectEvent;
import com.esotericsoftware.kryonetty.network.ReceiveEvent;
import com.esotericsoftware.kryonetty.network.handler.NetworkHandler;
import com.esotericsoftware.kryonetty.network.handler.NetworkListener;
import com.google.common.collect.Lists;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.protocol.packet.TestPacket;
import de.polocloud.api.network.server.SimpleNettyServer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.template.SimpleTemplate;
import de.polocloud.bootstrap.template.SimpleTemplateService;
import de.polocloud.bootstrap.template.TemplateStorage;

import java.util.Arrays;
import java.util.Collection;

public class Master implements IStartable, ITerminatable {

    private final CloudAPI cloudAPI;

    private SimpleNettyServer nettyServer;

    private final ITemplateService templateService;

    public Master() {
        this.cloudAPI = new PoloCloudAPI(new PoloAPIGuiceModule());

        //load templateStorage from config
        this.templateService = new SimpleTemplateService(this.cloudAPI, TemplateStorage.FILE);

        this.templateService.getTemplateLoader().loadTemplates();

        this.templateService.getTemplateSaver().save(new SimpleTemplate("Lobby", 1, 8));

    }

    @Override
    public void start() {
        this.nettyServer = this.cloudAPI.getGuice().getInstance(SimpleNettyServer.class);

        nettyServer.start();

        this.nettyServer.registerListener(new NetworkListener() {
            @NetworkHandler
            public void handle(ConnectEvent event) {
                System.out.println("Client connected! " + event.getCtx().channel().toString());
            }

            @NetworkHandler
            public void handle(ReceiveEvent event) {
                Object object = event.getObject();

                TestPacket packet = (TestPacket) object;
                System.out.println(packet.getKey());

            }
        });
    }

    @Override
    public boolean terminate() {
        return this.nettyServer.terminate();
    }
}