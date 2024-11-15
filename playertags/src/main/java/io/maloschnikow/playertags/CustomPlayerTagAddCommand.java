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
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;





public class CustomPlayerTagAddCommand implements Command<CommandSourceStack> {

    private final Plugin plugin = PlayerTags.getPlugin();

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        //TODO implement
        
        //TODO argument text(string)
        //TODO argument color

        //TODO check if custom tag looks like preset and prevent usage if so

        return Command.SINGLE_SUCCESS;
    }
}
