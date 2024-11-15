package io.maloschnikow.playertags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;


public class PresetPlayerTagRemoveCommand implements Command<CommandSourceStack> {

    private final Plugin plugin = PlayerTags.getPlugin();


    private void sendVerificationMessage(Player player) {
        PlainTextComponentSerializer plainTextComponentSerializer = PlainTextComponentSerializer.plainText();
        player.sendMessage(
            plainTextComponentSerializer.deserialize("You will now be displayed as: \"").append(
                player.displayName()
            ).append(
                plainTextComponentSerializer.deserialize("\"")
            )
        );
    }


    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();
        CommandSourceStack stack = (CommandSourceStack) context.getSource();
        CommandSender sender     = stack.getSender();

        //get preset tag to be removed
        TagPreset preset = context.getArgument("tag", TagPreset.class);

        //get targetPlayer argument
        PlayerSelectorArgumentResolver playerResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class); 
        Player targetPlayer = playerResolver.resolve(stack).getFirst();

        
        //check if sender is player
        if ( sender instanceof Player) {
            Player executer = (Player) sender;

            //check if command executer is different than targetPlayer
            //check if they have permission to change other peoples tag
            if (executer.getUniqueId() != targetPlayer.getUniqueId()) {
                if (!executer.hasPermission( new Permission("permissions.setOtherPlayerTag") )) {
                    executer.sendMessage("You can not set another player's tag");
                    return Command.SINGLE_SUCCESS;
                }
            }
        }

        //get tags, which are already stored in player
        PersistentDataContainer targetPlayerDataContainer = targetPlayer.getPersistentDataContainer();
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

        //check if tag is not in playerTags
        if(!playerTags.contains(preset)) {
            sender.sendMessage(targetPlayer.getName() + " doesn't have this tag applied.");
            return Command.SINGLE_SUCCESS;
        }
        //remove tag of command from TreeSet
        playerTags.remove(preset);

        //check if playerTags are empty and reset display and list name
        if (playerTags.isEmpty()) {
            targetPlayer.displayName(null);
            targetPlayer.playerListName(null);
            targetPlayerDataContainer.remove(new NamespacedKey(plugin, "playerTagList"));
            sendVerificationMessage(targetPlayer);
            return Command.SINGLE_SUCCESS;
        }
        
        //Get tag start, sepeator and end characters from config
        String tagStartChar     = plugin.getConfig().getString("tag-start-char", "{\"text\":\"[\",\"color\":\"gray\",\"bold\":false}");
        String tagEndChar       = plugin.getConfig().getString("tag-end-char",     "{\"text\":\"]\",\"color\":\"gray\",\"bold\":false}");
        String tagSeperatorChar = plugin.getConfig().getString("tag-seperator-char", "{\"text\":\"/\",\"color\":\"gray\",\"bold\":false}");

        
        //get the player name as text component (note: player team colors will get lost)
        Component playerNameAsTextComponent = gsonComponentSerializer.deserialize("{\"text\":\" " + targetPlayer.getName() + "\"}");
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
        targetPlayer.displayName(displayName);
        targetPlayer.playerListName(displayName);

        //Deserilize TreeSet of playerTags
        playerTagStringList.clear();
        for(TagPreset p : playerTags) {
            String pStr = TagPreset.deserialize(p);
            playerTagStringList.add(pStr);
        }
        
        //Store tags in player
        playerTagListAsString = String.join("§", playerTagStringList);
        targetPlayerDataContainer.set(new NamespacedKey(plugin, "playerTagList"), PersistentDataType.STRING, playerTagListAsString);


        //send success message to command sender
        sendVerificationMessage(targetPlayer);
        
        return Command.SINGLE_SUCCESS;
    }
}
