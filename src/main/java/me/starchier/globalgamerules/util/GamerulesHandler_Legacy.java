package me.starchier.globalgamerules.util;

import me.starchier.globalgamerules.Globalgamerules;
import org.bukkit.World;

import java.util.List;

public class GamerulesHandler_Legacy {
    private Globalgamerules plugin;
    private PluginUtil pluginUtil;
    private LanguageUtil languageUtil;
    public GamerulesHandler_Legacy(Globalgamerules plugin, PluginUtil pluginUtil, LanguageUtil languageUtil) {
        this.plugin = plugin;
        this.pluginUtil = pluginUtil;
        this.languageUtil = languageUtil;
    }
    public void syncGamerules(World world) {
        List<String> gamerules = pluginUtil.getGamerules();
        if(pluginUtil.isExemptWorld(world.getName())) {
            return;
        }
        for(String s : gamerules) {
            if(world.isGameRule(s)) {
                String value = pluginUtil.getGameruleValue(s);
                world.setGameRuleValue(s, value);
            }
        }
    }
}
