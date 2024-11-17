package io.maloschnikow.playertags;

import java.util.Hashtable;

import java.util.Set;
import java.util.TreeSet;

import org.bukkit.entity.Player;

public class AppliedPresetTags {
    public Hashtable<String, TagPreset> tagHashTable = new Hashtable<String, TagPreset>();;

    public AppliedPresetTags() {
    }

    public AppliedPresetTags(Player player) {
        TreeSet<TagPreset> presetList = PlayerTags.getPresetPlayerTags(player);
        for(TagPreset preset : presetList) {
            tagHashTable.put(preset.getName(), preset);
        }
    }

    public TagPreset valueOf(String presetName) {
        return tagHashTable.get(presetName);
    }

    public Set<String> values() {
        return tagHashTable.keySet();
    }
}