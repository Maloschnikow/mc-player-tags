package io.maloschnikow.playertags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class ApplyPlayerTagOnJoinListener implements Listener {

    private final Plugin plugin = PlayerTags.getPlugin();  

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        //TODO also apply custom tag

        Player player = event.getPlayer();
        GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();

        //get tags, which are already stored in player
        PersistentDataContainer playerDataContainer = player.getPersistentDataContainer();
        String playerTagListAsString = playerDataContainer.get(new NamespacedKey(plugin, "playerTagList"), PersistentDataType.STRING);
        List<String> playerTagStringList;

        //put tags from playerTagListAsString in a Linked List
        if(playerTagListAsString == null) {
            playerTagStringList = new LinkedList<String>();
        } else {
            playerTagStringList = new LinkedList<String>( Arrays.asList(playerTagListAsString.split("§")) );
        }

        //Serialize tags
        TreeSet<TagPreset> playerTags = new TreeSet<>(); //TreeSet sorts automatically and values are unique
        for (String tagString : playerTagStringList) {
            TagPreset tag = TagPreset.serialize(tagString);
            playerTags.add(tag);
        }

        //check if playerTags are empty and reset display and list name
        if (playerTags.isEmpty()) {
            player.displayName(null);
            player.playerListName(null);
            playerDataContainer.remove(new NamespacedKey(plugin, "playerTagList"));
            return;
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

    }
}
