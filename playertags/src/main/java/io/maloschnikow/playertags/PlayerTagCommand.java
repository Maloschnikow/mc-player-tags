package io.maloschnikow.playertags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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


public class PlayerTagCommand implements Command<CommandSourceStack> {

    private final Plugin plugin;

    public PlayerTagCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    public static void applyTagsToPlayer(Player player, List<Preset> tagList) {

    }




    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        CommandSourceStack stack = (CommandSourceStack) context.getSource();
        CommandSender sender     = stack.getSender();

        //get preset tag
        Preset preset = context.getArgument("tag", Preset.class);
        String requiredPermission = preset.getPermission();

        //get targetPlayer argument
        PlayerSelectorArgumentResolver playerResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class); 
        Player targetPlayer = playerResolver.resolve(stack).getFirst();

        
        //check 
        if ( sender instanceof Player) {
            Player executer = (Player) sender;

            //check is targetPlayer has permission to use the tag
            if ( !requiredPermission.isBlank() && !requiredPermission.contains("none") && !executer.hasPermission(new Permission(requiredPermission))){
                executer.sendMessage(targetPlayer.getName() + " doesn't have permission to use this tag.");
                return Command.SINGLE_SUCCESS;
            }

            //check if command executer is different than targetPlayer
            //check if they have permission to change other peoples tag
            if (executer.getUniqueId() != targetPlayer.getUniqueId()) {
                if (!executer.hasPermission( new Permission("permissions.setOtherPlayerTag") )) {
                    executer.sendMessage("You can not set another player's tag");
                    return Command.SINGLE_SUCCESS;
                }
            }
        }

        //get tag string
        String presetTag = preset.getTagComponentString();

        //get tags, which are already stored in player
        PersistentDataContainer targetPlayerDataContainer = targetPlayer.getPersistentDataContainer();
        String playerTagListAsString = targetPlayerDataContainer.get(new NamespacedKey(plugin, "playerTagList"), PersistentDataType.STRING);
        List<String> playerTagStringList;
        
        //add tag from command to player's tags
        if(playerTagListAsString == null) {
            playerTagStringList = new LinkedList<String>();
        } else {
            playerTagStringList = new LinkedList<String>( Arrays.asList(playerTagListAsString.split("§")) );
        }
        plugin.getLogger().info(playerTagStringList.toString()); //todo remove debug lines
        plugin.getLogger().info(Preset.deserialize(preset));

        playerTagStringList.add(Preset.deserialize(preset));

        //Serialize tags
        List<Preset> playerTagList = new ArrayList<Preset>();
        for (String tagString : playerTagStringList) {
            playerTagList.add(Preset.serialize(tagString));
        }
        
        //Get tag start, sepeator and end characters from config
        String tagStartChar     = plugin.getConfig().getString("tag-start-char", "{\"text\":\"[\",\"color\":\"gray\",\"bold\":false}");
        String tagEndChar       = plugin.getConfig().getString("tag-end-char",     "{\"text\":\"]\",\"color\":\"gray\",\"bold\":false}");
        String tagSeperatorChar = plugin.getConfig().getString("tag-seperator-char", "{\"text\":\"/\",\"color\":\"gray\",\"bold\":false}");



        GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();
        Component tagArg = gsonComponentSerializer.deserialize(presetTag);
        
        //get the player name as text component (note: player team colors will get lost)
        Component playerNameAsTextComponent = gsonComponentSerializer.deserialize("{\"text\":\" " + targetPlayer.getName() + "\"}"); //"{\"text\":\" " + targetPlayer.getName() + "
        playerNameAsTextComponent           = playerNameAsTextComponent.color(NamedTextColor.WHITE)
                                                .decoration(TextDecoration.BOLD, false)
                                                .decoration(TextDecoration.ITALIC, false)
                                                .decoration(TextDecoration.OBFUSCATED, false)
                                                .decoration(TextDecoration.STRIKETHROUGH, false)
                                                .decoration(TextDecoration.UNDERLINED, false);

        //todo check if multiple tags need to be displayed

        //todo check if tag is already applied

        //sort playerTagList by priority
        Collections.sort(playerTagList);

        //Construct display name
        Component displayName = gsonComponentSerializer.deserialize(tagStartChar); //Begin with tag start char
        
        for (int i = 0; i < playerTagList.size(); ++i) {
            Component tagComponent = gsonComponentSerializer.deserialize(playerTagList.get(i).getTagComponentString());
            displayName = displayName.append(tagComponent);
            //Apply tag seperator
            if (i < playerTagList.size() - 1) {
                displayName = displayName.append(gsonComponentSerializer.deserialize(tagSeperatorChar));
            }
        }
        displayName = displayName.append(gsonComponentSerializer.deserialize(tagEndChar)); //End with tag end char
        displayName = displayName.append(playerNameAsTextComponent);

        // Apply player tags
        targetPlayer.displayName(displayName);
        targetPlayer.playerListName(displayName);

        //Store tags in player
        playerTagListAsString = String.join("§", playerTagStringList);
        targetPlayerDataContainer.set(new NamespacedKey(plugin, "playerTagList"), PersistentDataType.STRING, playerTagListAsString);


        //send success message to command sender
        PlainTextComponentSerializer plainTextComponentSerializer = PlainTextComponentSerializer.plainText();
        targetPlayer.sendMessage(
            plainTextComponentSerializer.deserialize("You will now be displayed as: \"").append(
                displayName
            ).append(
                plainTextComponentSerializer.deserialize("\"")
            )
        );
        return Command.SINGLE_SUCCESS;
    }
}
