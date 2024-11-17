package io.maloschnikow.playertags;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.plugin.Plugin;

public final class AvailableTagPresets {
    public static Hashtable<String, TagPreset> tagHashTable = new Hashtable<String, TagPreset>();;
    public static Plugin plugin = PlayerTags.getPlugin();

    //TODO typesafty and stuff, because prob the server will more or less crash when config.yml is not correctly configured

    public static void loadPresetsFromConfig() {
        List<LinkedHashMap<?, ?>> presetList = (List<LinkedHashMap<?, ?>>) plugin.getConfig().getList("preset-player-tags");
        for(LinkedHashMap<?, ?> preset : presetList) {
            
            String presetName = (String) preset.get("name");
            String presetComponent = (String) preset.get("tag");
            String presetPermission = (String) preset.get("permission");
            int presetPriority = (int) preset.get("priority");
            if ( presetName != null ) {
                tagHashTable.put(presetName, new TagPreset(presetName, presetComponent, presetPermission, presetPriority));
            }
        }
    }

    public static TagPreset valueOf(String presetName) {
        TagPreset r = tagHashTable.get(presetName);
        if( r == null)  {
            throw new IllegalArgumentException("A preset tag named " + presetName + " doesn't exists");
        }
        return tagHashTable.get(presetName);
    }

    public static Set<String> values() {
        return tagHashTable.keySet();
    }
}
