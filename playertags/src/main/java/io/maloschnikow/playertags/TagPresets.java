package io.maloschnikow.playertags;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;

public final class TagPresets {
    public static Hashtable<String, Preset> tagHashTable = new Hashtable<String, Preset>();;
    private static Plugin plugin;

    public static void setPlugin(Plugin plugin) {
        TagPresets.plugin = plugin;
    }

    //TODO typesafty and stuff, because prob the server will more or less crash when config.yml is not correctly configured

    public static void loadPresetsFromConfig() {
        /* List<LinkedHashMap<?, ?>> presetList = (List<LinkedHashMap<?, ?>>) plugin.getConfig().getList("preset-player-tags");
        for(LinkedHashMap<?, ?> preset : presetList) {
            
            String presetName = (String) preset.get(0);
            Component presetComponent = (Component) preset.get(1);
            String presetPermission = (String) preset.get(2);
            if ( presetName != null ) {
                tagHashTable.put(presetName, new Preset(presetName, presetComponent, presetPermission));
            }
        } */
        tagHashTable.put("admin", new Preset("admin", "[{\"text\":\"[\",\"color\":\"gray\",\"bold\":false},{\"text\":\"Admin\",\"color\":\"red\",\"bold\":false},{\"text\":\"]\",\"color\":\"gray\",\"bold\":false}]", "permissions.adminTag"));
        tagHashTable.put("developer", new Preset("developer", "[{\"text\":\"[\",\"color\":\"gray\",\"bold\":false},{\"text\":\"Developer\",\"color\":\"yellow\",\"bold\":false},{\"text\":\"]\",\"color\":\"gray\",\"bold\":false}]", "permissions.adminTag"));

    }

    public static Preset valueOf(String presetName) {
        return tagHashTable.get(presetName);
    }

    public static Set<String> values() {
        return tagHashTable.keySet();
    }



}
