package io.maloschnikow.playertags;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;


public class PlayerTagCommand implements Command<CommandSourceStack> {

    private final Plugin plugin;

    public PlayerTagCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        CommandSourceStack stack = (CommandSourceStack) context.getSource();

        CommandSender sender = stack.getSender();

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

        GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();
        Component tag = gsonComponentSerializer.deserialize(presetTag);
        
        //format the new display name
        Component playerNameAsTextComponent = gsonComponentSerializer.deserialize("{\"text\":\" " + targetPlayer.getName() + "\"}"); //"{\"text\":\" " + targetPlayer.getName() + "
        playerNameAsTextComponent = playerNameAsTextComponent.color(NamedTextColor.WHITE)
                                    .decoration(TextDecoration.BOLD, false)
                                    .decoration(TextDecoration.ITALIC, false)
                                    .decoration(TextDecoration.OBFUSCATED, false)
                                    .decoration(TextDecoration.STRIKETHROUGH, false)
                                    .decoration(TextDecoration.UNDERLINED, false);
        
        Component displayName = tag.append(playerNameAsTextComponent);

        //apply tag
        targetPlayer.displayName(displayName);
        targetPlayer.playerListName(displayName);

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
