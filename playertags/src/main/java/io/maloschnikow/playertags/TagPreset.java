package io.maloschnikow.playertags;

import java.util.Arrays;
import java.util.List;

public class TagPreset implements Comparable<TagPreset> {

    private final String name;
    private final String tagComponentString;
    private final String permission;
    private final int priotity;

    public TagPreset(String name, String tagComponentString, String permission, int priotity) {
        this.name = name;
        this.tagComponentString = tagComponentString;
        this.permission = permission;
        this.priotity = priotity;
    }

    public String getName() {
        return name;
    }
    public String getTagComponentString() {
        return tagComponentString;
    }
    public String getPermission() {
        return permission;
    }
    public int getPriority() {
        return priotity;
    }

    public static String deserialize (TagPreset preset) {
        return "\\{" + preset.name + "\\," + preset.tagComponentString + "\\," + preset.permission + "\\," + String.valueOf(preset.priotity) + "\\}";
    }

    public static TagPreset serialize(String presetString) {
        presetString = presetString.replace("\\{", "").replace("\\}", "");
        List<String> list = Arrays.asList(presetString.split("\\\\,")); //need to be 4 because of regex things

        String name = list.get(0);
        String tagComponentString = list.get(1);
        String permission = list.get(2);

        int priority = Integer.parseInt(list.get(3));

        return new TagPreset(name, tagComponentString, permission, priority);
    }

    @Override
    public int compareTo(TagPreset other) {
        return Integer.compare(this.priotity, other.priotity);
    }
}
