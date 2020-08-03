package me.starchier.globalgamerules.util;

import me.starchier.globalgamerules.Globalgamerules;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PluginUtil {
    private Globalgamerules plugin;
    public PluginUtil(Globalgamerules plugin) {
        this.plugin = plugin;
    }
    private List<String> keys = null;
    private FileConfiguration config = null;
    public String getVersion() {
        return plugin.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }
    public FileConfiguration getConfig() {
        if(config==null) {
            plugin.reloadConfig();
            config = plugin.getConfig();
        }
        return config;
    }
    public void resetConfigCache() {
        config = plugin.getConfig();
        keys = new ArrayList<>(getConfig().getConfigurationSection("gamerules").getKeys(true));
    }
    public boolean isLegacy() {
        String ver = getVersion().replace(".","").replace("_","").replace("v","").
                replace("_","").replace("R","");
        return Integer.parseInt(ver) < 1131;
    }
    public List<String> getGamerules() {
        try {
            if (keys == null) {
                keys = new ArrayList<>(getConfig().getConfigurationSection("gamerules").getKeys(true));
            }
            return keys;
        } catch (Exception e) {
            return new ArrayList<>();
        }
        /*
        List<String> gamerules = new ArrayList<>();
        for(String s: keys) {
            if(s.contains("gamerules.")) {
                gamerules.add(s.replace("gamerules.", ""));
            }
        }
        return gamerules;

         */
    }
    public boolean removeGamerule(String gamerule) {
        if(keys==null) {
            keys = new ArrayList<>(getConfig().getConfigurationSection("gamerules").getKeys(true));
        }
        if(keys.contains(gamerule)) {
            keys.remove(gamerule);
            plugin.saveConfig();
            return true;
        }
        return false;
    }

    public String getGameruleValue(String gamerule) {
        return getConfig().getString("gamerules."+gamerule, null);
    }
    public List<String> getExemptWorlds() {
        return getConfig().getStringList("exempt-worlds");
    }
    public String getLocale() {
        return getConfig().getString("locale", "en_US");
    }
    public void addGamerule(String gamerule, String value) {
        getConfig().set("gamerules."+gamerule, value);
        plugin.saveConfig();
        config = plugin.getConfig();
        keys = new ArrayList<>(getConfig().getConfigurationSection("gamerules").getKeys(true));
    }
    public boolean isBooleanGamerule(String gamerule) {
        if(isLegacy()) {
            Pattern p = Pattern.compile("[0-9]*");
            for(World w : Bukkit.getWorlds()) {
                return !p.matcher(w.getGameRuleValue(gamerule)).matches();
            }
        } else {
            for(World w : Bukkit.getWorlds()) {
                String value = w.getGameRuleValue(GameRule.getByName(gamerule)).toString();
                if(value.equals("true")||value.equals("false")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isExemptWorld(String world) {
        return getExemptWorlds().contains(world);
        /*
        for(String s : getExemptWorlds()) {
            if(Objects.equals(world, s)) {
                return true;
            }
        }
        return false;
        */
    }
    public boolean addExemptWorld(String world) {
        if(isExemptWorld(world)) {
            return false;
        }
        List<String> worlds = getExemptWorlds();
        worlds.add(world);
        getConfig().set("exempt-worlds", worlds);
        plugin.saveConfig();
        return true;
    }
    public boolean removeExemptWorld(String world) {
        if(isExemptWorld(world)) {
            List<String> worlds = getExemptWorlds();
            worlds.remove(world);
            getConfig().set("exempt-worlds", worlds);
            plugin.saveConfig();
            return true;
        }
        return false;
    }
    public String listExemptWorld(String world) {
        StringBuilder sb = new StringBuilder();
        for(String s : getExemptWorlds()) {
            if(s.equals(world)) {
                sb.append(" " + ChatColor.YELLOW + ChatColor.BOLD).append(s).append(", ");
                continue;
            }
            sb.append(" " + ChatColor.AQUA).append(s).append(", ");
        }
        return sb.toString();
    }
}
