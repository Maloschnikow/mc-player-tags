package io.maloschnikow.playertags;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.chat.TextColor;

public final class TagPresets {
    public static Hashtable<String, Preset> tagHashTable = new Hashtable<String, Preset>();;
    public static Plugin plugin;

    public static void setPlugin(Plugin plugin) {
        TagPresets.plugin = plugin;
    }

    //TODO typesafty and stuff, because prob the server will more or less crash when config.yml is not correctly configured

    public static void loadPresetsFromConfig() {
        List<LinkedHashMap<?, ?>> presetList = (List<LinkedHashMap<?, ?>>) plugin.getConfig().getList("preset-player-tags");
        for(LinkedHashMap<?, ?> preset : presetList) {
            
            String presetName = (String) preset.get("name");
            String presetComponent = (String) preset.get("tag");
            String presetPermission = (String) preset.get("permission");
            if ( presetName != null ) {
                tagHashTable.put(presetName, new Preset(presetName, presetComponent, presetPermission));
            }

            //plugin.getLogger().info(presetList.toString());
            //plugin.getLogger().info(preset.get("name").toString());
            
            //plugin.getLogger().info(preset.getClass().toString());
        }

    }

    public static Preset valueOf(String presetName) {
        return tagHashTable.get(presetName);
    }

    public static Set<String> values() {
        return tagHashTable.keySet();
    }



}
