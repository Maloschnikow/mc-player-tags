package io.maloschnikow.playertags;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;


public class CustomPlayerTagAddCommand implements Command<CommandSourceStack> {

    private Component createComponentOfTextAndColor(String text, NamedTextColor color) {
        GsonComponentSerializer gson = GsonComponentSerializer.gson();
        return gson.deserialize(text).color(color);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        CommandSourceStack stack = (CommandSourceStack) context.getSource();
        CommandSender sender     = stack.getSender();

        //get targetPlayer argument
        Player targetPlayer = context.getArgument("player", Player.class); 

        //get text argument (text which is displayed as tag)
        String tagText = context.getArgument("text", String.class);

        //get color argument (named color of the tag)
        NamedTextColor tagColor = context.getArgument("color", NamedTextColor.class);

        Component tagComponent = createComponentOfTextAndColor(tagText, tagColor);

        //check is sender is player
        if ( sender instanceof Player) {
            Player executer = (Player) sender;

            //check if target player has permission to use a custom tag
            if (!executer.hasPermission("permissions.customPlayerTag")) {
                executer.sendMessage(targetPlayer.getName() + " can't have a custom tag.");
                return Command.SINGLE_SUCCESS;
            }

            //check if command executer is different than targetPlayer
            //check if they have permission to change other peoples tag
            if (executer.getUniqueId() != targetPlayer.getUniqueId()) {
                if (!executer.hasPermission("permissions.setOtherPlayerTag")) {
                    executer.sendMessage("You can not set another player's tag");
                    return Command.SINGLE_SUCCESS;
                }
            }
        }

        GsonComponentSerializer gson = GsonComponentSerializer.gson();
        PlainTextComponentSerializer ser = PlainTextComponentSerializer.plainText();
        TagPreset customTagPreset = new TagPreset(tagText, gson.serialize(tagComponent), "permissions.customPlayerTag", Integer.MAX_VALUE);

        //check if text of custom tag is a text of a preset tag to prevent forgery
        for (String key : AvailableTagPresets.values()) {
            String componentString = AvailableTagPresets.valueOf(key).getTagComponentString();

            Component presetTagTextComponent = gson.deserialize(componentString);
            String presetTagString = ser.serialize(presetTagTextComponent);
            
            if(presetTagString.toLowerCase().equals(customTagPreset.getName().toLowerCase())) {
                sender.sendMessage("The tag text you're trying to use is already used in a preset.");
                return Command.SINGLE_SUCCESS;
            }
        }

        PlayerTags.setCustomPlayerTag(targetPlayer, customTagPreset);
        PlayerTags.applyPlayerTags(targetPlayer);
        PlayerTags.sendVerificationMessage(sender, targetPlayer);

        return Command.SINGLE_SUCCESS;
    }
}
