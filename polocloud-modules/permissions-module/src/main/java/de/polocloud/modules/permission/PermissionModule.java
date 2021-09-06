package de.polocloud.modules.permission;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.database.DocumentObjectDatabase;
import de.polocloud.api.database.IDatabase;
import de.polocloud.api.guice.own.Guice;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.logger.helper.MinecraftColor;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.PermissionPool;
import de.polocloud.modules.permission.global.api.impl.*;
import de.polocloud.modules.permission.bootstrap.ModuleBootstrap;
import de.polocloud.modules.permission.global.command.PermsCommand;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Getter
public class PermissionModule {

    @Getter
    private static PermissionModule instance;

    private final IDatabase<SimplePermissionUser> userDatabase;
    private final IDatabase<SimplePermissionGroup> groupDatabase;

    public PermissionModule(ModuleBootstrap bootstrap) {
        instance = this;
        this.userDatabase = new DocumentObjectDatabase<>("permission-users", new File(bootstrap.getDataDirectory(), "permission-users"), SimplePermissionUser.class);
        this.groupDatabase = new DocumentObjectDatabase<>("permission-groups", new File(bootstrap.getDataDirectory(), "permission-groups"), SimplePermissionGroup.class);

        if (this.groupDatabase.getEntries().isEmpty()) {

            IPermissionGroup playerGroup = new SimplePermissionGroup("Player", 0, Collections.singletonList("cloud.defaultpermission"), new SimplePermissionDisplay(MinecraftColor.GRAY, "§aPlayer §8▏§7", "§7", ""), new ArrayList<>());
            IPermissionGroup adminGroup = new SimplePermissionGroup("Admin", 9999, Arrays.asList("cloud.use", "cloud.maintenance", "cloud.server.full.connect", "cloud.stop", "cloud.notify", "cloud.fulljoin"), new SimplePermissionDisplay(MinecraftColor.DARK_RED, "§4Admin §8▏§7", "§7", ""), Collections.singletonList("Player"));

            this.groupDatabase.insert(playerGroup.getName(), (SimplePermissionGroup) playerGroup);
            this.groupDatabase.insert(adminGroup.getName(), (SimplePermissionGroup) adminGroup);
        }

        PoloLogger.print(LogLevel.INFO, "§7PermissionModule loaded §b" + this.userDatabase.getEntries().size() + " §3PermissionUsers §7and §3" + this.groupDatabase.getEntries().size() + " §3PermissionGroups§7!");

        Guice.bind(PermissionPool.class).toInstance(new SimplePermissionPool());
    }

    public void load() {

        PoloCloudAPI.getInstance().getCommandManager().registerCommand(new PermsCommand());
    }

    public void enable() {

    }

    public void reload() {

    }

    public void shutdown() {

    }
}
