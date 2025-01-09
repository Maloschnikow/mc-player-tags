package io.maloschnikow.playertags;

import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerArgument implements CustomArgumentType.Converted<Player, String> {

    @Override
    public @NotNull Player convert(String nativeType) throws CommandSyntaxException {
        try {
            Player r = PlayerTags.getPlugin().getServer().getPlayer(nativeType);
            if( r == null) {
                throw new IllegalArgumentException(nativeType + " doesn't exists");
            }
            return r;
        } catch (IllegalArgumentException ignored) {
            Message message = MessageComponentSerializer.message().serialize(Component.text("Invalid player \"%s\"".formatted(nativeType), NamedTextColor.RED));

            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

        for (Player player : PlayerTags.getPlugin().getServer().getOnlinePlayers()) {
            builder.suggest(player.getName(), MessageComponentSerializer.message().serialize(player.displayName()));
        }
        // Rückgabe der Vorschläge
        return builder.buildFuture();
    }

}