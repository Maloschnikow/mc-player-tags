package io.maloschnikow.playertags;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;


public class CustomPlayerTagAddCommand implements Command<CommandSourceStack> {

    private Component createComponentOfTextAndColor(String text, NamedTextColor color) {
        GsonComponentSerializer gson = GsonComponentSerializer.gson();
        return gson.deserialize(text).color(color);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        //TODO implement

        //TODO check if custom tag looks like preset and prevent usage if so


        CommandSourceStack stack = (CommandSourceStack) context.getSource();
        CommandSender sender     = stack.getSender();

        //get targetPlayer argument
        PlayerSelectorArgumentResolver playerResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class); 
        Player targetPlayer = playerResolver.resolve(stack).getFirst();

        //get text argument (text which is displayed as tag)
        String tagText = context.getArgument("text", String.class);

        //get color argument (named color of the tag)
        NamedTextColor tagColor = context.getArgument("color", NamedTextColor.class);

        Component tagComponent = createComponentOfTextAndColor(tagText, tagColor);

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

        //TODO make a function to apply playertags


        return Command.SINGLE_SUCCESS;
    }
}
