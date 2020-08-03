package me.starchier.globalgamerules.events;

import me.starchier.globalgamerules.Globalgamerules;
import me.starchier.globalgamerules.util.GamerulesHandler;
import me.starchier.globalgamerules.util.GamerulesHandler_Legacy;
import me.starchier.globalgamerules.util.LanguageUtil;
import me.starchier.globalgamerules.util.PluginUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class Events implements Listener {
    private Globalgamerules plugin;
    private PluginUtil pluginUtil;
    private LanguageUtil languageUtil;
    public Events(Globalgamerules plugin, PluginUtil pluginUtil, LanguageUtil languageUtil) {
        this.plugin = plugin;
        this.pluginUtil = pluginUtil;
        this.languageUtil = languageUtil;
    }
    @EventHandler
    public void onWorldLoad(WorldLoadEvent evt) {
        if(!pluginUtil.isExemptWorld(evt.getWorld().getName())) {
            if(pluginUtil.isLegacy()) {
                GamerulesHandler_Legacy gamerulesHandler_legacy = new GamerulesHandler_Legacy(plugin, pluginUtil, languageUtil);
                gamerulesHandler_legacy.syncGamerules(evt.getWorld());
            } else {
                GamerulesHandler gamerulesHandler = new GamerulesHandler(plugin,pluginUtil, languageUtil);
                gamerulesHandler.syncGamerules(evt.getWorld());
            }
            String msg = languageUtil.getMsg("gamerule-synced").replace("%s", evt.getWorld().getName());
            plugin.getLogger().info(msg);
            return;
        }
        plugin.getLogger().info(languageUtil.getMsg("is-exempt-world").replace("%s", evt.getWorld().getName()));
    }
}
