package me.starchier.globalgamerules.util;

import me.starchier.globalgamerules.Globalgamerules;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class CommandUtil {
    private Globalgamerules plugin;
    private LanguageUtil languageUtil;
    private PluginUtil pluginUtil;
    public CommandUtil(Globalgamerules plugin, LanguageUtil languageUtil, PluginUtil pluginUtil) {
        this.plugin = plugin;
        this.languageUtil = languageUtil;
        this.pluginUtil = pluginUtil;
    }
    public boolean hasPermission(CommandSender sender) {
        if(sender.hasPermission("globalgamerules.admin")) {
            return true;
        }
        boolean set = sender.hasPermission("globalgamerules.set");
        boolean reload = sender.hasPermission("globalgamerules.reload");
        boolean world = sender.hasPermission("globalgamerules.world");
        boolean check = sender.hasPermission("globalgamerules.check");
        return set||reload||world||check;
    }
    public boolean sendNoPermMsg(CommandSender sender, String perm) {
        if(sender.hasPermission(perm)||!(sender instanceof Player)) {
            return true;
        }
        sender.sendMessage(languageUtil.getMsg("no-permission"));
        return false;
    }
    public void processCurrentWorld(CommandSender sender, boolean isAdd) {
        if(isAdd) {
            if (pluginUtil.addExemptWorld(((Player) sender).getWorld().getName())) {
                sender.sendMessage(languageUtil.getMsg("world-added").replace("%s",((Player) sender).getWorld().getName()));
            } else {
                sender.sendMessage(languageUtil.getMsg("world-added-already").replace("%s",((Player) sender).getWorld().getName()));
            }
            return;
        }
        if (pluginUtil.removeExemptWorld(((Player) sender).getWorld().getName())) {
            sender.sendMessage(languageUtil.getMsg("world-removed").replace("%s",((Player) sender).getWorld().getName()));
        } else {
            sender.sendMessage(languageUtil.getMsg("world-not-in-list").replace("%s",((Player) sender).getWorld().getName()));
        }
    }
    public void processWorld(CommandSender sender, String world, boolean isAdd) {
        if(isAdd) {
            if (pluginUtil.addExemptWorld(world)) {
                sender.sendMessage(languageUtil.getMsg("world-added").replace("%s",world));
            } else {
                sender.sendMessage(languageUtil.getMsg("world-added-already").replace("%s",world));
            }
            return;
        }
        if (pluginUtil.removeExemptWorld(world)) {
            sender.sendMessage(languageUtil.getMsg("world-removed").replace("%s",world));
        } else {
            sender.sendMessage(languageUtil.getMsg("world-not-in-list").replace("%s",world));
        }
    }
    public void checkGamerule(String gamerule, CommandSender sender) {
        if(gamerule==null) {
            if(pluginUtil.getGamerules().isEmpty()) {
                sender.sendMessage(languageUtil.getMsg("no-global-gamerules"));
                return;
            }
            sender.sendMessage(languageUtil.getMsg("global-gamerule"));
            boolean isGamerule = true;
            for(String s : pluginUtil.getGamerules()) {
                for(World w : Bukkit.getWorlds()) {
                    if(!w.isGameRule(s)) {
                        isGamerule = false;
                    } else {
                        isGamerule = true;
                    }
                    break;
                }
                if(!isGamerule) {
                    continue;
                }
                String value = pluginUtil.getGameruleValue(s);
                sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + s + ChatColor.WHITE + "  :  " +
                        (value.equals("true")?ChatColor.GREEN+value:(value.equals("false")?ChatColor.RED+value:ChatColor.AQUA+value)));
            }
            return;
        }
        for(World world : Bukkit.getWorlds()) {
            if(!world.isGameRule(gamerule)) {
                sender.sendMessage(languageUtil.getMsg("invalid-gamerule").replace("%s",gamerule));
                return;
            }
            break;
        }
        sender.sendMessage(languageUtil.getMsg("check-gamerule").replace("%s",gamerule));
        for(World world : Bukkit.getWorlds()) {
            String s;
            if(pluginUtil.isExemptWorld(world.getName())) {
                s = ChatColor.GRAY + "" + ChatColor.ITALIC + world.getName() + languageUtil.getMsg("exempt");
            } else {
                s = ChatColor.DARK_GREEN + world.getName();
            }
            String value;
            if (pluginUtil.isLegacy()) {
                value = world.getGameRuleValue(gamerule);
            } else {
                value = world.getGameRuleValue(GameRule.getByName(gamerule)).toString();
            }
            String fixedText = (value.equals("true") ? ChatColor.GREEN + value : (value.equals("false") ? ChatColor.RED + value:ChatColor.AQUA+value));
            sender.sendMessage(ChatColor.GRAY + " - " + s + ChatColor.WHITE + " : " + fixedText);
        }
    }
    public void setGamerule(String gamerule, String value, CommandSender sender) {
        if(pluginUtil.isBooleanGamerule(gamerule)) {
            if(value.equalsIgnoreCase("true")||value.equalsIgnoreCase("false")) {
                pluginUtil.addGamerule(gamerule, value);
                sender.sendMessage(languageUtil.getMsg("gamerule-set").replace("%s", gamerule).replace("%v",value));
            } else {
                sender.sendMessage(languageUtil.getMsg("boolean-required"));
            }
        } else {
            Pattern p = Pattern.compile("[0-9]*");
            if(p.matcher(value).matches()) {
                pluginUtil.addGamerule(gamerule, value);
                sender.sendMessage(languageUtil.getMsg("gamerule-set").replace("%s", gamerule).replace("%v",value));
            } else {
                sender.sendMessage(languageUtil.getMsg("int-required"));
            }
        }
    }
    public void getValidSetting() {
        for(World w : Bukkit.getWorlds()) {
            for(String s : pluginUtil.getGamerules()) {
                if(!w.isGameRule(s)) {
                    plugin.getLogger().warning(languageUtil.getMsg("invalid-gamerule").replace("%s",s));
                } else {
                    if(!pluginUtil.isBooleanGamerule(s)) {
                        if (pluginUtil.getGameruleValue(s).equalsIgnoreCase("true") || pluginUtil.getGameruleValue(s).equalsIgnoreCase("false")) {
                            plugin.getLogger().warning(languageUtil.getMsg("invalid-gamerule-value").replace("%s",s));
                        }
                    } else {
                        if(!(pluginUtil.getGameruleValue(s).equalsIgnoreCase("true") || pluginUtil.getGameruleValue(s).equalsIgnoreCase("false"))) {
                            plugin.getLogger().warning(languageUtil.getMsg("invalid-gamerule-value").replace("%s",s));
                        }
                    }
                }
            }
            break;
        }
    }
}
