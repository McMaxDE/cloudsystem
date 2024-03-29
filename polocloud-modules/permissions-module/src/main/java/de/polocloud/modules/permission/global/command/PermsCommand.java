package de.polocloud.modules.permission.global.command;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.annotation.CommandPermission;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.PermissionPool;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionGroup;
import de.polocloud.modules.permission.global.setup.PermissionGroupSetup;

import java.util.List;
import java.util.stream.Collectors;

public class PermsCommand implements CommandListener {

    @Command(
        name = "permissions",
        aliases = {"perms", "cloudperms", "poloperms"},
        description = "Manages the Permissions module"
    )
    @CommandExecutors(ExecutorType.ALL)
    @CommandPermission("cloud.use")
    public void execute(CommandExecutor executor, String[] fullArgs, String... args) {
        String prefix = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix(executor);

        PermissionPool permissionPool = PermissionPool.getInstance();

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("group")) {
                if (args[1].equalsIgnoreCase("list")) {
                    if (permissionPool.getAllCachedPermissionGroups().isEmpty()) {
                        executor.sendMessage(prefix + "§cUnfortunately there are §eno PermissionGroups §cyet!");
                        return;
                    }
                    executor.sendMessage(prefix + "----[PermissionGroups]----");
                    for (IPermissionGroup group : permissionPool.getAllCachedPermissionGroups()) {
                        executor.sendMessage(prefix + group.getName() + " §8(§7Id: §3" + group.getId() + "§8)");
                    }
                    executor.sendMessage(prefix + "----[/PermissionGroups]----");
                } else if (args[1].equalsIgnoreCase("create")) {
                    if (executor instanceof ICloudPlayer) {
                        executor.sendMessage(prefix + "§cThis can only be executed in the §eMaster-Console§c!");
                        return;
                    }
                    new PermissionGroupSetup().sendSetup();
                } else {
                    sendHelp(executor, prefix);
                }
            } else {
                sendHelp(executor, prefix);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("group")) {
                String name = args[1];
                IPermissionGroup permissionGroup = permissionPool.getCachedPermissionGroup(name);
                if (permissionGroup == null) {
                    executor.sendMessage(prefix + "§cThere is no existing PermissionGroup with the name §e" + name + "§c!");
                    return;
                }
                if (args[2].equalsIgnoreCase("delete")) {
                    permissionPool.deletePermissionGroup(permissionGroup);
                    executor.sendMessage(prefix + "§7You §asuccessfully §7deleted group §c" + permissionGroup.getName() + "§8!");

                } else if (args[2].equalsIgnoreCase("info")) {

                    List<String> inheritances = ((SimplePermissionGroup)permissionGroup).getInheritancesStringBased();

                    executor.sendMessage(prefix + "----[PermissionGroup]----");
                    executor.sendMessage(prefix + "§8» §7Name§8: §b" + permissionGroup.getDisplay().getColor().getColor() + permissionGroup.getName());
                    executor.sendMessage(prefix + "§8» §7Priority §8: §b" + permissionGroup.getId());
                    executor.sendMessage(prefix + "§8» §7Color §8: §b" + permissionGroup.getDisplay().getColor().getColor() + permissionGroup.getDisplay().getColor().name());
                    executor.sendMessage(prefix + "§8» §7Prefix §8: §b" + permissionGroup.getDisplay().getPrefix() + "Name");
                    executor.sendMessage(prefix + "§8» §7Suffix §8: §b" + permissionGroup.getDisplay().getSuffix());
                    executor.sendMessage(prefix + "§8» §7ChatFormat §8: §b" + permissionGroup.getDisplay().getChatFormat());
                    executor.sendMessage(prefix + "§8» §7Inheritances §8: §b" + inheritances.toString().replace("[", "").replace("]", ""));
                    executor.sendMessage(prefix + "----[/PermissionGroup]----");
                } else {
                    sendHelp(executor, prefix);
                }
            } else {
                sendHelp(executor, prefix);
            }
        } else {
            sendHelp(executor, prefix);
        }
    }


    private void sendHelp(CommandExecutor executor, String prefix) {

        String slash = executor instanceof ICloudPlayer ? "/" : "";

        executor.sendMessage(prefix + "----[Permissions]----");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms group list §7Lists all groups");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms group create §7Creates a new group");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms group <Group> delete §7Deletes a group");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms group <Group> info §7Displays info about a group");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms group <Group> permission add <Permission> §7Adds a permission to a group");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms group <Group> permission remove <Permission> §7Removes a permission from a group");

        executor.sendMessage(prefix + "§8» §b" + slash + "perms user <User> info §7Displays info about a user");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms user <User> group add <Group> <TimeSpan> §7Adds a player to a group for a given timespan");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms user <User> group remove <Group> §7Removes a player from a group");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms user <User> permission add <Permission> <TimeSpan> §7Adds a permission to a player for a given timespan");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms user <User> permission remove <Permission> §7Removes a permission from a player");
        executor.sendMessage(prefix + "----[/Permissions]----");
    }
}
