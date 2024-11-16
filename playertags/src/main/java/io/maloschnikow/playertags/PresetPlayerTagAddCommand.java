package io.maloschnikow.playertags;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;


public class PresetPlayerTagAddCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        CommandSourceStack stack = (CommandSourceStack) context.getSource();
        CommandSender sender     = stack.getSender();

        //get preset tag
        TagPreset preset = context.getArgument("tag", TagPreset.class);
        String requiredPermission = preset.getPermission();

        //get targetPlayer argument
        PlayerSelectorArgumentResolver playerResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class); 
        Player targetPlayer = playerResolver.resolve(stack).getFirst();

        //check if sender is player
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

        boolean addedTag = PlayerTags.addPresetPlayerTag(targetPlayer, preset);
        if (!addedTag) {
            sender.sendMessage(targetPlayer.getName() + " already has this tag.");
            return Command.SINGLE_SUCCESS;
        }
        PlayerTags.applyPlayerTags(targetPlayer);

        PlayerTags.sendVerificationMessage(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }
}
