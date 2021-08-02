package de.polocloud.tablist.executes;

import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.template.fallback.FallbackProperty;
import de.polocloud.tablist.TablistModule;
import de.polocloud.tablist.attribute.AttributeConverter;
import de.polocloud.tablist.cache.CloudPlayerTabCache;
import de.polocloud.tablist.config.Tab;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TablistSetExecute implements TablistExecute {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void execute(ICloudPlayer iCloudPlayer, MasterConfig masterConfig, boolean playerUpdate) {
        CloudPlayerTabCache cloudPlayerTabCache = TablistModule.getInstance().getTabCache();

        List<String> fallbackGroups = masterConfig.getProperties().getFallbackProperties().stream().map(FallbackProperty::getTemplateName).collect(Collectors.toList());
        Tab tab = TablistModule.getInstance().getTablistConfig().getTabs().stream()
            .filter(key -> key.getUse() && (key.getGroups().length <= 0 || Arrays.stream(key.getGroups())
                .anyMatch(it -> fallbackGroups.contains(it)))).findAny().orElse(null);
        
        if (tab != null) {
            scheduler.scheduleWithFixedDelay(() -> {
                cloudPlayerTabCache.put(iCloudPlayer.getUUID(), tab);
                String[] args = AttributeConverter.convertTab(tab.getTabs()[0].getHeader(), tab.getTabs()[0].getFooter(), iCloudPlayer);
                iCloudPlayer.sendTablist(args[0], args[1]);
            }, 0, 1, TimeUnit.SECONDS);
        }
    }
}
