package me.starchier.globalgamerules.commands;

import me.starchier.globalgamerules.Globalgamerules;
import me.starchier.globalgamerules.util.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class command implements TabExecutor {
    private Globalgamerules plugin;
    private PluginUtil pluginUtil;
    private LanguageUtil languageUtil;
    private CommandUtil commandUtil;
    public command(Globalgamerules plugin, PluginUtil pluginUtil, LanguageUtil languageUtil, CommandUtil commandUtil) {
        this.plugin=plugin;
        this.pluginUtil = pluginUtil;
        this.languageUtil = languageUtil;
        this.commandUtil = commandUtil;
    }
    private final String[] subCommands = {"world", "set", "check", "reload", "sync", "remove"};
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("ggr")) {
            if(sender instanceof Player && !commandUtil.hasPermission(sender)) {
                sender.sendMessage(languageUtil.getMsg("no-permission"));
                return true;
            }
            if(args.length<1) {
                for(String s: languageUtil.getMsgList("help-msg")) {
                    sender.sendMessage(s);
                }
                return true;
            }
            switch(args[0]) {
                case "world": {
                    if(!commandUtil.sendNoPermMsg(sender, "globalgamerules.world")) {
                        return true;
                    }
                    if(args.length<2) {
                        for(String s: languageUtil.getMsgList("world-help")) {
                            sender.sendMessage(s);
                        }
                        return true;
                    }
                    switch (args[1]) {
                        case "add": {
                            if(args.length<3&&sender instanceof Player) {
                                commandUtil.processCurrentWorld(sender, true);
                                return true;
                            }
                            if(args.length==3) {
                                commandUtil.processWorld(sender, args[2], true);
                                return true;
                            }
                            for(String s: languageUtil.getMsgList("world-help")) {
                                sender.sendMessage(s);
                            }
                            return true;
                        }
                        case "remove": {
                            if(args.length<3&&sender instanceof Player) {
                                commandUtil.processCurrentWorld(sender, false);
                                return true;
                            }
                            if(args.length==3) {
                                commandUtil.processWorld(sender, args[2], false);
                                return true;
                            }
                            for(String s: languageUtil.getMsgList("world-help")) {
                                sender.sendMessage(s);
                            }
                            return true;
                        }
                        case "info": {
                            sender.sendMessage(languageUtil.getMsg("exempt-worlds"));
                            sender.sendMessage(pluginUtil.listExemptWorld((sender instanceof Player ? ((Player) sender).getWorld().getName():null)));
                            return true;
                        }
                        default: {
                            for(String s: languageUtil.getMsgList("world-help")) {
                                sender.sendMessage(s);
                            }
                            return true;
                        }
                    }
                }
                case "reload": {
                    if(!commandUtil.sendNoPermMsg(sender, "globalgamerules.reload")) {
                        return true;
                    }
                    plugin.getLogger().info("Reloading config...");
                    plugin.reloadConfig();
                    pluginUtil.resetConfigCache();
                    languageUtil.initLang();
                    commandUtil.getValidSetting();
                    //plugin.getConfig().set("gamerules", currentGamerules);
                    for(World world : Bukkit.getWorlds()) {
                        if(!pluginUtil.isExemptWorld(world.getName())) {
                            if(pluginUtil.isLegacy()) {
                                GamerulesHandler_Legacy gamerulesHandler_legacy = new GamerulesHandler_Legacy(plugin, pluginUtil, languageUtil);
                                gamerulesHandler_legacy.syncGamerules(world);
                            } else {
                                GamerulesHandler gamerulesHandler = new GamerulesHandler(plugin,pluginUtil, languageUtil);
                                gamerulesHandler.syncGamerules(world);
                            }
                            String msg = languageUtil.getMsg("gamerule-synced").replace("%s", world.getName());
                            plugin.getLogger().info(msg);
                        } else {
                            plugin.getLogger().info(languageUtil.getMsg("is-exempt-world").replace("%s", world.getName()));
                        }
                    }
                    sender.sendMessage(languageUtil.getMsg("reload-completed"));
                    return true;
                }
                case "check": {
                    if(!commandUtil.sendNoPermMsg(sender, "globalgamerules.check")) {
                        return true;
                    }
                    commandUtil.checkGamerule((args.length==1 ? null : args[1]), sender);
                    return true;
                }
                case "remove": {
                    if(!commandUtil.sendNoPermMsg(sender, "globalgamerules.remove")) {
                        return true;
                    }
                    if(args.length<2) {
                        for(String s: languageUtil.getMsgList("help-msg")) {
                            sender.sendMessage(s);
                        }
                        return true;
                    }
                    if(!pluginUtil.getGamerules().contains(args[1])) {
                        sender.sendMessage(languageUtil.getMsg("invalid-gamerule").replace("%s",args[1]));
                        return true;
                    }
                    Map<String,String> grMap = new HashMap<String, String>();
                    for(String s: pluginUtil.getGamerules()) {
                        if(s.equals(args[1])) {
                            continue;
                        }
                        grMap.put(s, pluginUtil.getGameruleValue(s));
                    }
                    plugin.getConfig().createSection("gamerules", grMap);
                    plugin.saveConfig();
                    pluginUtil.resetConfigCache();
                    sender.sendMessage(languageUtil.getMsg("gamerule-removed").replace("%s",args[1]));
                    return true;
                }
                case "sync": {
                    if(!commandUtil.sendNoPermMsg(sender, "globalgamerules.sync")) {
                        return true;
                    }
                    for(World world : Bukkit.getWorlds()) {
                        if(!pluginUtil.isExemptWorld(world.getName())) {
                            if(pluginUtil.isLegacy()) {
                                GamerulesHandler_Legacy gamerulesHandler_legacy = new GamerulesHandler_Legacy(plugin, pluginUtil, languageUtil);
                                gamerulesHandler_legacy.syncGamerules(world);
                            } else {
                                GamerulesHandler gamerulesHandler = new GamerulesHandler(plugin,pluginUtil, languageUtil);
                                gamerulesHandler.syncGamerules(world);
                            }
                            String msg = languageUtil.getMsg("gamerule-synced").replace("%s", world.getName());
                            plugin.getLogger().info(msg);
                        } else {
                            plugin.getLogger().info(languageUtil.getMsg("is-exempt-world").replace("%s", world.getName()));
                        }
                    }
                    sender.sendMessage(languageUtil.getMsg("sync-completed"));
                    return true;
                }
                case "set": {
                    if(!commandUtil.sendNoPermMsg(sender, "globalgamerules.set")) {
                        return true;
                    }
                    if(args.length<3) {
                        sender.sendMessage(languageUtil.getMsg("set-cmd-usage"));
                        return true;
                    }
                    for(World w : Bukkit.getWorlds()) {
                        if(!w.isGameRule(args[1])) {
                            sender.sendMessage(languageUtil.getMsg("invalid-gamerule").replace("%s",args[1]));
                            return true;
                        }
                        break;
                    }
                    commandUtil.setGamerule(args[1], args[2], sender);
                    return true;
                }
                default: {
                    for(String s: languageUtil.getMsgList("help-msg")) {
                        sender.sendMessage(s);
                    }
                    return true;
                }
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if(sender instanceof Player && !commandUtil.hasPermission(sender)) {
            return new ArrayList<>();
        }
        if(args.length<2) {
            return Arrays.stream(subCommands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        if(args.length==2) {
            if(args[0].equalsIgnoreCase("set")) {
                if(!(sender instanceof Player) || sender.hasPermission("globalgamerules.set")) {
                    for(World w : Bukkit.getWorlds()) {
                        return Arrays.stream(w.getGameRules()).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                    }
                }
            }
            if(args[0].equalsIgnoreCase("remove")) {
                if(!(sender instanceof Player) || sender.hasPermission("globalgamerules.remove")) {
                    String[] list = pluginUtil.getGamerules().toArray(new String[0]);
                    return Arrays.stream(list).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                }
            }
            if(args[0].equalsIgnoreCase("check")) {
                if(!(sender instanceof Player) || sender.hasPermission("globalgamerules.check")) {
                    for(World w : Bukkit.getWorlds()) {
                        return Arrays.stream(w.getGameRules()).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                    }
                }
            }
            if(args[0].equalsIgnoreCase("world")) {
                if(!(sender instanceof Player) || sender.hasPermission("globalgamerules.world")) {
                    String[] sub = {"add", "remove", "info"};
                    return Arrays.stream(sub).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                }
            }
        }
        if (args.length == 3) {
            if(!(sender instanceof Player) || sender.hasPermission("globalgamerules.world")) {
                if (args[1].equalsIgnoreCase("add")) {
                    List<String> worlds = new ArrayList<>();
                    for(World w: Bukkit.getWorlds()) {
                        if(pluginUtil.isExemptWorld(w.getName())) {
                            continue;
                        }
                        worlds.add(w.getName());
                    }
                    return worlds;
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    return pluginUtil.getExemptWorlds();
                }
            }
            if (args[0].equalsIgnoreCase("set")) {
                if (!(sender instanceof Player) || sender.hasPermission("globalgamerules.set")) {
                    if(pluginUtil.isBooleanGamerule(args[1])) {
                        String[] bool = {"true", "false"};
                        return Arrays.stream(bool).filter(s -> s.startsWith(args[2])).collect(Collectors.toList());
                    } else {
                        List<String> integ = new ArrayList<>();
                        integ.add("[Integer]");
                        integ.add("1");
                        integ.add("2");
                        return integ;
                    }
                }
            }
        }
        return new ArrayList<>();
    }
}
