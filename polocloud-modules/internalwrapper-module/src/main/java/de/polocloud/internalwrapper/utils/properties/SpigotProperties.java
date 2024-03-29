package de.polocloud.internalwrapper.utils.properties;

import java.io.File;

public class SpigotProperties extends ServiceProperties {

    public SpigotProperties(File file, int port, String motd, int maxPlayers, String name) {
        super(file, "server.properties", port);

        setProperties(new String[]{
            "generator-settings=\n" +
            "op-permission-level=4\n" +
            "allow-nether=true\n" +
            "resource-pack-hash=\n" +
            "level-name=world\n" +
            "allow-flight=false\n" +
            "announce-player-achievements=true\n" +
            "server-port=" + port + "\n" +
            "max-world-size=29999984\n" +
            "level-type=DEFAULT\n" +
            "level-seed=\n" +
            "force-gamemode=false\n" +
            "server-ip=0\n" +
            "network-compression-threshold=256\n" +
            "max-build-height=256\n" +
            "spawn-npcs=true\n" +
            "white-list=false\n" +
            "spawn-animals=true\n" +
            "hardcore=false\n" +
            "snooper-enabled=true\n" +
            "online-mode=false\n" +
            "resource-pack=\n" +
            "pvp=true\n" +
            "difficulty=1\n" +
            "enable-command-block=false\n" +
            "gamemode=0\n" +
            "player-idle-timeout=0\n" +
            "max-players=" + maxPlayers + "\n" +
            "spawn-monsters=true\n" +
            "generate-structures=true\n" +
            "view-distance=10\n" +
            "motd=" + motd + "\n"
        });
        writeFile();
    }
}
