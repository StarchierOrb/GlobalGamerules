package me.starchier.globalgamerules.util;

import me.starchier.globalgamerules.Globalgamerules;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LanguageUtil {
    private Globalgamerules plugin;
    private PluginUtil pluginUtil;
    public LanguageUtil(Globalgamerules plugin, PluginUtil pluginUtil) {
        this.plugin = plugin;
        this.pluginUtil = pluginUtil;
    }
    public void initLang() {
        String locale = pluginUtil.getLocale();
        File path = new File(plugin.getDataFolder()+"\\languages", locale+".yml");
        if(!path.exists()) {
            /*
            if(plugin.getResource("languages\\"+locale+".yml")==null) {
                plugin.saveResource("languages\\en_US.yml", false);
                return;            }
             */
            try {
                plugin.saveResource("languages\\" + locale + ".yml", false);
            } catch (Exception e) {
                plugin.getLogger().warning("Language file "+pluginUtil.getLocale()+" not found!");
                plugin.getLogger().warning("Now using language en_US.");
                plugin.saveResource("languages\\" + "en_US.yml", false);
            }
        }
    }
    public boolean validLocale() {
        File lang = new File(plugin.getDataFolder()+"\\languages", pluginUtil.getLocale()+".yml");
        return lang.exists();
    }
    public FileConfiguration getLang() {
        File lang;
        if(validLocale()) {
            lang = new File(plugin.getDataFolder()+"\\languages", pluginUtil.getLocale()+".yml");
        } else {
            lang = new File(plugin.getDataFolder() + "\\languages", "en_US.yml");
        }
        return YamlConfiguration.loadConfiguration(lang);
    }
    public String getMsg(String path) {
        return getLang().getString("messages."+path, "messages."+path).replace("&","§");
    }
    public List<String> getMsgList(String path) {
        List<String> list = new ArrayList<>();
        for(String s : getLang().getStringList("messages."+path)) {
            list.add(s.replace("&","§"));
        }
        return list;
    }
}
