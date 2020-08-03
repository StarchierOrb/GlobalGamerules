package me.starchier.globalgamerules;

import me.starchier.globalgamerules.bStats.MetricsLite;
import me.starchier.globalgamerules.commands.command;
import me.starchier.globalgamerules.events.Events;
import me.starchier.globalgamerules.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class Globalgamerules extends JavaPlugin {
    private String locale;
    @Override
    public void onEnable() {
        getLogger().info("Initializing plugin...");
        PluginUtil pluginUtil = new PluginUtil(this);
        LanguageUtil languageUtil = new LanguageUtil(this, pluginUtil);
        CommandUtil commandUtil = new CommandUtil(this, languageUtil, pluginUtil);
        saveDefaultConfig();
        locale = pluginUtil.getLocale();
        startupInit(languageUtil, commandUtil, pluginUtil);
        try {
            int pluginId = 8372;
            MetricsLite metrics = new MetricsLite(this, pluginId);
        } catch (Exception e) { }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public void startupInit(LanguageUtil languageUtil, CommandUtil commandUtil, PluginUtil pluginUtil) {
        if(locale.equalsIgnoreCase("zh_CN")) {
            getLogger().info("全局游戏规则插件 正在初始化...");
            getLogger().info("┏━━ 检测服务器版本: "+ChatColor.AQUA+pluginUtil.getVersion()+(pluginUtil.isLegacy()?ChatColor.GOLD+" (兼容模式开启)":""));
            getLogger().info("┣━━ 语言文件初始化...");
            languageUtil.initLang();
            getLogger().info("┣━━ 加载语言文件: " + ChatColor.DARK_AQUA + locale);
            getLogger().info("┣━━ 加载并检查配置文件中...");
            commandUtil.getValidSetting();
            //getConfig().set("gamerules", currentGamerules);
            getLogger().info("┣━━ 正在注册指令...");
            getCommand("ggr").setExecutor(new command(this, pluginUtil, languageUtil, commandUtil));
            getCommand("ggr").setTabCompleter(new command(this, pluginUtil, languageUtil, commandUtil));
            getLogger().info("┣━━ 正在注册监听器...");
            Bukkit.getPluginManager().registerEvents(new Events(this, pluginUtil, languageUtil), this);
            getLogger().info("┣━━ 正在同步全局游戏规则...");
            for(World world : Bukkit.getWorlds()) {
                if(!pluginUtil.isExemptWorld(world.getName())) {
                    if(pluginUtil.isLegacy()) {
                        GamerulesHandler_Legacy gamerulesHandler_legacy = new GamerulesHandler_Legacy(this, pluginUtil, languageUtil);
                        gamerulesHandler_legacy.syncGamerules(world);
                    } else {
                        GamerulesHandler gamerulesHandler = new GamerulesHandler(this,pluginUtil, languageUtil);
                        gamerulesHandler.syncGamerules(world);
                    }
                    String msg = languageUtil.getMsg("gamerule-synced").replace("%s", world.getName());
                    getLogger().info(msg);
                } else {
                    getLogger().info(languageUtil.getMsg("is-exempt-world").replace("%s", world.getName()));
                }
            }
            getLogger().info("┗━━ "+ ChatColor.GREEN +"插件已启用！");
            getLogger().info("来给发个电呗,支持一下啦：）  afdian.net/@Starc ");
        } else {
            getLogger().info("┏━━ Server version: "+ChatColor.AQUA+pluginUtil.getVersion()+(pluginUtil.isLegacy()?ChatColor.GOLD+" (Legacy Mode)":""));
            getLogger().info("┣━━ Initializing language files...");
            languageUtil.initLang();
            getLogger().info("┣━━ Selected locale: " + ChatColor.DARK_AQUA +locale);
            getLogger().info("┣━━ Loading and checking config file...");
            commandUtil.getValidSetting();
            getLogger().info("┣━━ Registering commands...");
            getCommand("ggr").setExecutor(new command(this, pluginUtil, languageUtil, commandUtil));
            getCommand("ggr").setTabCompleter(new command(this, pluginUtil, languageUtil, commandUtil));
            getLogger().info("┣━━ Initializing listeners...");
            Bukkit.getPluginManager().registerEvents(new Events(this, pluginUtil, languageUtil), this);
            getLogger().info("┣━━ Syncing global gamerules...");
            for(World world : Bukkit.getWorlds()) {
                if(!pluginUtil.isExemptWorld(world.getName())) {
                    if(pluginUtil.isLegacy()) {
                        GamerulesHandler_Legacy gamerulesHandler_legacy = new GamerulesHandler_Legacy(this, pluginUtil, languageUtil);
                        gamerulesHandler_legacy.syncGamerules(world);
                    } else {
                        GamerulesHandler gamerulesHandler = new GamerulesHandler(this,pluginUtil, languageUtil);
                        gamerulesHandler.syncGamerules(world);
                    }
                    String msg = languageUtil.getMsg("gamerule-synced").replace("%s", world.getName());
                    getLogger().info(msg);
                } else {
                    getLogger().info(languageUtil.getMsg("is-exempt-world").replace("%s", world.getName()));
                }
            }
            getLogger().info("┗━━ "+ ChatColor.GREEN +"Plugin loaded successfully!");
            getLogger().info("Welcome to donate us: paypal.me/starchier :P");
        }
    }
}
