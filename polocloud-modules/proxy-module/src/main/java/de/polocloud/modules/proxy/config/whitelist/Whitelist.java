package de.polocloud.modules.proxy.config.whitelist;

import com.google.common.collect.Lists;

import java.util.List;

public class Whitelist {

    private List<String> whitelistPlayers;

    public Whitelist() {
        this.whitelistPlayers = Lists.newArrayList();
    }

    public List<String> getWhitelistPlayers() {
        return whitelistPlayers;
    }
}