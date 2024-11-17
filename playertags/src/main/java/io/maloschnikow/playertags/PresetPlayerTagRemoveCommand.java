package io.maloschnikow.playertags;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.papermc.paper.command.brigadier.CommandSourceStack;


public class PresetPlayerTagRemoveCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        CommandSourceStack stack = (CommandSourceStack) context.getSource();
        CommandSender sender     = stack.getSender();

        //get preset tag to be removed
        TagPreset preset = context.getArgument("tag", TagPreset.class);

        //get targetPlayer argument
        Player targetPlayer = context.getArgument("player", Player.class); 
        
        //check if sender is player
        if ( sender instanceof Player) {
            Player executer = (Player) sender;

            //check if command executer is different than targetPlayer
            //check if they have permission to change other peoples tag
            if (executer.getUniqueId() != targetPlayer.getUniqueId()) {
                if (!executer.hasPermission("permissions.setOtherPlayerTag")) {
                    executer.sendMessage("You can not set another player's tag");
                    return Command.SINGLE_SUCCESS;
                }
            }
        }


        boolean removedTag = PlayerTags.removePresetPlayerTag(targetPlayer, preset);
        if(!removedTag) {
            sender.sendMessage(targetPlayer.getName() + " doesn't have this tag.");
            return Command.SINGLE_SUCCESS;
        }
        PlayerTags.applyPlayerTags(targetPlayer);

        //send success message to command sender
        PlayerTags.sendVerificationMessage(sender, targetPlayer);
        
        return Command.SINGLE_SUCCESS;
    }
}
