package de.polocloud.addons.signs;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CloudSign {

    private ITemplate template;

    private IGameServer gameServer;
    private Location location;

    private Sign sign;

    public CloudSign(ITemplate template, Location location) {
        this.template = template;
        this.location = location;

        if(!location.getChunk().isLoaded()) location.getChunk().load();

        this.sign = (Sign) location.getBlock().getState();
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(){
        System.out.println(gameServer.getName());
        this.sign.setLine(0, gameServer.getName());
        this.sign.setLine(1, gameServer.getStatus().toString());
        this.sign.setLine(2, gameServer.getOnlinePlayers() + "/" + gameServer.getTemplate().getMaxPlayers());
        this.sign.setLine(3, Arrays.stream(gameServer.getTemplate().getWrapperNames()).collect(Collectors.joining(", ")));
        this.sign.update(true);
    }

    public boolean hasServer(){
        return gameServer != null;
    }

    public ITemplate getTemplate() {
        return template;
    }

    public IGameServer getGameServer() {
        return gameServer;
    }

    public Location getLocation() {
        return location;
    }

    public void setGameServer(IGameServer gameServer) {
        this.gameServer = gameServer;
    }
}
