package io.maloschnikow.playertags;

import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.brigadier.arguments.StringArgumentType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;


public class PlayerTags extends JavaPlugin {

    private static PlayerTags plugin;

    public static PlayerTags getPlugin() {
        return plugin;
    }

    public static Component applyPlayerTags(Player player) {
        GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();

        //get tags, which are already stored in player
        TreeSet<TagPreset> playerTags = getPresetPlayerTags(player); //TreeSet sorts automatically and values are unique

        //get custom player tag
        TagPreset customTagPreset = getCustomPlayerTag(player);

        //reset players display name and list name, if they have no tags stored
        if(playerTags.isEmpty() && customTagPreset == null) {
            player.displayName(null);
            player.playerListName(null);
            return player.displayName();
        }

        //add custom tag if player has one and add it as last element in list
        if(customTagPreset != null) {
            playerTags.add(customTagPreset);
        }


        //Get tag start, sepeator and end characters from config
        String tagStartChar     = plugin.getConfig().getString("tag-start-char", "{\"text\":\"[\",\"color\":\"gray\",\"bold\":false}");
        String tagEndChar       = plugin.getConfig().getString("tag-end-char",     "{\"text\":\"]\",\"color\":\"gray\",\"bold\":false}");
        String tagSeperatorChar = plugin.getConfig().getString("tag-seperator-char", "{\"text\":\"/\",\"color\":\"gray\",\"bold\":false}");

        
        //get the player name as text component (note: player team colors will get lost)
        Component playerNameAsTextComponent = gsonComponentSerializer.deserialize("{\"text\":\" " + player.getName() + "\"}");
        playerNameAsTextComponent           = playerNameAsTextComponent.color(NamedTextColor.WHITE)
                                                .decoration(TextDecoration.BOLD, false)
                                                .decoration(TextDecoration.ITALIC, false)
                                                .decoration(TextDecoration.OBFUSCATED, false)
                                                .decoration(TextDecoration.STRIKETHROUGH, false)
                                                .decoration(TextDecoration.UNDERLINED, false);


        boolean displayMultipleTags = plugin.getConfig().getBoolean("display-multiple-tags", false);

        //Construct display name
        Component displayName = gsonComponentSerializer.deserialize(tagStartChar); //Begin with tag start char
        List<TagPreset> playerTagsList = new ArrayList<TagPreset>(playerTags);
        
        //Construct display name with multiple player tags
        if( displayMultipleTags ) {
            for (int i = 0; i < playerTagsList.size(); ++i) {
                Component tagComponent = gsonComponentSerializer.deserialize(playerTagsList.get(i).getTagComponentString());
                displayName = displayName.append(tagComponent);
                //Apply tag seperator
                if (i < playerTagsList.size() - 1) {
                    displayName = displayName.append(gsonComponentSerializer.deserialize(tagSeperatorChar));
                }
            }
        } else {
            displayName = displayName.append( gsonComponentSerializer.deserialize(playerTags.first().getTagComponentString()) ); // set first of playerTags (should be the one with highest priority, because of TreeSet)
        }

        displayName = displayName.append(gsonComponentSerializer.deserialize(tagEndChar)); //End with tag end char
        displayName = displayName.append(playerNameAsTextComponent);

        // Apply player tags
        player.displayName(displayName);
        player.playerListName(displayName);

        return displayName;
    }

    public static TreeSet<TagPreset> getPresetPlayerTags(Player player) {
        //get tags, which are already stored in player
        PersistentDataContainer targetPlayerDataContainer = player.getPersistentDataContainer();
        String playerTagListAsString = targetPlayerDataContainer.get(new NamespacedKey(plugin, "playerTagList"), PersistentDataType.STRING);
        List<String> playerTagStringList;
        
        //put tags from playerTagListAsString in a Linked List
        if(playerTagListAsString == null) {
            playerTagStringList = new LinkedList<String>();
        } else {
            playerTagStringList = new LinkedList<String>( Arrays.asList(playerTagListAsString.split("§")) );
        }

        //Serialize tags
        TreeSet<TagPreset> playerTags = new TreeSet<TagPreset>(); //TreeSet sorts automatically and values are unique
        for (String tagString : playerTagStringList) {
            TagPreset tag = TagPreset.serialize(tagString);
            playerTags.add(tag);
        }

        return playerTags;
    }

    public static boolean addPresetPlayerTag(Player player, TagPreset presetTag) {

        //get player tags
        TreeSet<TagPreset> playerTags = getPresetPlayerTags(player);

        
        //add tag if not present
        if(playerTags.contains(presetTag)) {
            return false;
        }
        playerTags.add(presetTag);

        //Deserilize TreeSet of playerTags
        LinkedList<String> playerTagStringList = new LinkedList<>();
        for(TagPreset p : playerTags) {
            String pStr = TagPreset.deserialize(p);
            playerTagStringList.add(pStr);
        }
        
        //Store tags in player
        String playerTagListAsString = String.join("§", playerTagStringList);
        PersistentDataContainer targetPlayerDataContainer = player.getPersistentDataContainer();
        targetPlayerDataContainer.set(new NamespacedKey(plugin, "playerTagList"), PersistentDataType.STRING, playerTagListAsString);

        return true;
    }

    public static boolean removePresetPlayerTag(Player player, TagPreset presetTag) {

        PersistentDataContainer targetPlayerDataContainer = player.getPersistentDataContainer();

        //get player tags
        TreeSet<TagPreset> playerTags = getPresetPlayerTags(player);

        //remove tag if present
        if(!playerTags.contains(presetTag)) {
            return false;
        }
        playerTags.remove(presetTag);

        //if playerTags are now empty remove the entry from player's persistent data container
        // or else getPresetPlayerTags() would try to read a empty string which leads to errors
        if(playerTags.isEmpty()) {
            targetPlayerDataContainer.remove(new NamespacedKey(plugin, "playerTagList"));
            return true;
        }

        //Deserilize TreeSet of playerTags
        LinkedList<String> playerTagStringList = new LinkedList<>();
        for(TagPreset p : playerTags) {
            String pStr = TagPreset.deserialize(p);
            playerTagStringList.add(pStr);
        }
        
        //Store tags in player
        String playerTagListAsString = String.join("§", playerTagStringList);
        
        targetPlayerDataContainer.set(new NamespacedKey(plugin, "playerTagList"), PersistentDataType.STRING, playerTagListAsString);

        return true;
    }

    public static void sendVerificationMessage(CommandSender sender, Player targetPlayer) {
       //send success message to target player
        PlainTextComponentSerializer plainTextComponentSerializer = PlainTextComponentSerializer.plainText();
        targetPlayer.sendMessage(
            plainTextComponentSerializer.deserialize("You will now be displayed as: \"").append(
                targetPlayer.displayName()
            ).append(
                plainTextComponentSerializer.deserialize("\"")
            )
        );

        //send success message to command sender, if sender is not target player
        if ( !(sender instanceof Player) || ((Player)sender).getUniqueId() != targetPlayer.getUniqueId()) {
            sender.sendMessage(
                plainTextComponentSerializer.deserialize(targetPlayer.getName() + " will now be displayed as: \"").append(
                    targetPlayer.displayName()
                ).append(
                    plainTextComponentSerializer.deserialize("\"")
                )
            );
        }
    }

    public static TagPreset getCustomPlayerTag(Player player) {
        PersistentDataContainer playerDataContainer = player.getPersistentDataContainer();
        String tagPresetString = playerDataContainer.get(new NamespacedKey(PlayerTags.getPlugin(), "customTag"), PersistentDataType.STRING);
        if( tagPresetString == null ) { return null; }
        return TagPreset.serialize(tagPresetString);
    }

    public static void setCustomPlayerTag(Player player, TagPreset customTagPreset) {
        PersistentDataContainer playerDataContainer = player.getPersistentDataContainer();
        playerDataContainer.set(new NamespacedKey(PlayerTags.getPlugin(), "customTag"), PersistentDataType.STRING, TagPreset.deserialize(customTagPreset));
    }

    public static boolean removeCustomPlayerTag(Player player) {
        PersistentDataContainer playerDataContainer = player.getPersistentDataContainer();
        if(!playerDataContainer.has(new NamespacedKey(PlayerTags.getPlugin(), "customTag"))) {
            return false;
        }
        playerDataContainer.remove(new NamespacedKey(PlayerTags.getPlugin(), "customTag"));
        return true;
    }


    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        //load preset tags required for autocompletion of /playertag
        AvailableTagPresets.loadPresetsFromConfig();

        //register Event Handler(s)
        getServer().getPluginManager().registerEvents(new ApplyPlayerTagOnJoinListener(), this);
        
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();            

            // Register /playertag command
            commands.register(
                Commands.literal("playertag").requires(new Permission("permissions.PlayerTag"))
                .then(
                    Commands.argument("player", new PlayerArgument()) //player argument
                    .then(
                        Commands.literal("preset") //tag should be added
                        .then(
                            Commands.literal("add") //a preset tag should be added
                            .then(
                                Commands.argument("tag", new AvailableTagPresetsArgument())
                                .executes(new PresetPlayerTagAddCommand())
                            )
                        )
                        .then(
                            Commands.literal("remove") //a custom tag should be added
                            .then(
                                Commands.argument("tag", new AppliedPresetTagsArgument())
                                .executes(new PresetPlayerTagRemoveCommand())
                            )
                        )
                    )
                    .then(
                        Commands.literal("custom") //tag should be removed
                        .then(
                            Commands.literal("set") //a preset tag should be removed
                            .then(
                                Commands.argument("text", StringArgumentType.string())
                                .then(
                                    Commands.argument("color", ArgumentTypes.namedColor())
                                    .executes(new CustomPlayerTagAddCommand())
                                )
                            )
                        )
                        .then(
                            Commands.literal("remove") //the custom tag should be removed
                            .executes(new CustomPlayerTagRemoveCommand())
                        )
                    )
                    .then(
                        Commands.literal("clear")
                        .executes(new PlayerTagClearCommand())
                        .requires(new Permission("permissions.clearPlayerTag"))
                    )
                )
                .build(),
                "Manipulate a player's tags."
            );

        });
    }
}