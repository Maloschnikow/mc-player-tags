package io.maloschnikow.playertags;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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
        
        //FinePositionResolver pos = (FinePositionResolver) context.getArgument("coordinates", FinePositionResolver.class);

        sender.sendMessage("DAS PLUGIN LEBT, ES LEEEEBT!");

        //get player argument
        PlayerSelectorArgumentResolver playerResolver = context.getArgument("player", PlayerSelectorArgumentResolver.class); 
        Player player = playerResolver.resolve(stack).getFirst();


        //get tag
        Component tag = context.getArgument("tag", Component.class);
        
        //set player display name and player list name
        GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();
        Component playerNameAsTextComponent = gsonComponentSerializer.deserialize("{\"text\":\" " + player.getName() + "\"}"); //"{\"text\":\" " + player.getName() + "
        playerNameAsTextComponent = playerNameAsTextComponent.color(NamedTextColor.WHITE)
                                    .decoration(TextDecoration.BOLD, false)
                                    .decoration(TextDecoration.ITALIC, false)
                                    .decoration(TextDecoration.OBFUSCATED, false)
                                    .decoration(TextDecoration.STRIKETHROUGH, false)
                                    .decoration(TextDecoration.UNDERLINED, false);
        
        Component displayName = tag.append(playerNameAsTextComponent);

        player.displayName(displayName);
        player.playerListName(displayName);


        PlainTextComponentSerializer plainTextComponentSerializer = PlainTextComponentSerializer.plainText();
        player.sendMessage(
            plainTextComponentSerializer.deserialize("Du wirst jetzt angezeigt als: \"").append(
                displayName
            ).append(
                plainTextComponentSerializer.deserialize("\"")
            )
        );
        return Command.SINGLE_SUCCESS;
    }
}
