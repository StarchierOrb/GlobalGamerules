package me.starchier.globalgamerules.util;

import me.starchier.globalgamerules.Globalgamerules;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;

public class GamerulesHandler {
    private Globalgamerules plugin;
    private PluginUtil pluginUtil;
    private LanguageUtil languageUtil;
    public GamerulesHandler(Globalgamerules plugin, PluginUtil pluginUtil, LanguageUtil languageUtil) {
        this.plugin = plugin;
        this.pluginUtil = pluginUtil;
        this.languageUtil = languageUtil;
    }
    public void syncGamerules(World world) {
        GameruleWrapper gameruleWrapper = new GameruleWrapper();
        List<String> gamerules = pluginUtil.getGamerules();
        for(String s : gamerules) {
            if(world.isGameRule(s)) {
                if(pluginUtil.isBooleanGamerule(s)) {
                    boolean value = pluginUtil.getGameruleValue(s).equalsIgnoreCase("true");
                    world.setGameRule(gameruleWrapper.getGamerule(s), value);
                } else {
                    int value = Integer.parseInt(pluginUtil.getGameruleValue(s));
                    world.setGameRule(gameruleWrapper.getGamerule(s), value);
                }
            }
        }
    }
}
