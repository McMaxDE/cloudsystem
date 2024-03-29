package de.polocloud.bootstrap.creator;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;

import java.util.List;

public class SimpleServerCreator extends ServerCreator {

    @Inject
    private IGameServerManager gameServerManager;

    @Override
    public boolean check(ITemplate template) {
        List<IGameServer> serversByTemplate = gameServerManager.getCached(template);

        if (serversByTemplate == null) {
            return false;
        }

        if (template.getMaxServerCount() <= serversByTemplate.size()) {
            return false;
        }

        if (serversByTemplate.size() < template.getMinServerCount()) {
            return true;
        }


        int onlinePlayers = 0;
        int totalMaxPlayers = serversByTemplate.size() * template.getMaxPlayers();

        float percentage;

        for (IGameServer gameServer : serversByTemplate) {
            onlinePlayers += gameServer.getOnlinePlayers();
        }

        percentage = (onlinePlayers * 100.0F) / (totalMaxPlayers);

        if (percentage >= template.getServerCreateThreshold()) {
            PoloLogger.print(LogLevel.INFO, "Group " + template.getName() + " is " + percentage + "% full! " + "(" + template.getServerCreateThreshold() + "% required)");
            return true;
        }

        return false;

    }

}
