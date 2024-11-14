package io.maloschnikow.playertags;

public class Preset {

    private final String name;
    private final String tagComponentString;
    private final String permission;

    public Preset(String name, String tagComponentString, String permission) {
        this.name = name;
        this.tagComponentString = tagComponentString;
        this.permission = permission;
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
}
