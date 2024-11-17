package io.maloschnikow.playertags;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.papermc.paper.command.brigadier.CommandSourceStack;

public class PlayerTagClearCommand implements Command<CommandSourceStack> {

    private final Plugin plugin = PlayerTags.getPlugin();

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        //TODO also clear custom tag

        CommandSourceStack stack = (CommandSourceStack) context.getSource();
        CommandSender sender     = stack.getSender();

        //get targetPlayer argument
        Player targetPlayer = context.getArgument("player", Player.class); 
        
        //check is sender is player
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

        // Apply player tags
        targetPlayer.displayName(null);
        targetPlayer.playerListName(null);

        //remove all stored tags
        PersistentDataContainer targetPlayerDataContainer = targetPlayer.getPersistentDataContainer();
        targetPlayerDataContainer.remove(new NamespacedKey(plugin, "playerTagList"));

        //send success message to command sender
        targetPlayer.sendMessage(targetPlayer.getName() + "'s tags were removed.");

        return Command.SINGLE_SUCCESS;
    }
}
