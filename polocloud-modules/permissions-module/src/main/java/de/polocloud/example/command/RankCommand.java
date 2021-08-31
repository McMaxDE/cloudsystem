package de.polocloud.example.command;

import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.player.ICloudPlayer;

public class RankCommand implements CommandListener {

    @Command(
        name = "rank",
        aliases = {"permissions","perms"},
        usage = "",
        description = "None"
    )
    public void execute(CommandExecutor executor, String[] fullArgs, String... args) {
        executor.sendMessage("test2");
        if(!executor.hasPermission("cloud.permissions")){
            executor.sendMessage("test1");
            return;
        }

        ICloudPlayer cloudPlayer = (ICloudPlayer) executor;



    }
}
