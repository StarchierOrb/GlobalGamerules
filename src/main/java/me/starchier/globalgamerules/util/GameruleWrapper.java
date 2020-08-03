package me.starchier.globalgamerules.util;

import org.bukkit.GameRule;

public class GameruleWrapper {
    public GameRule getGamerule(String gamerule) {
        return GameRule.getByName(gamerule);
    }
}
