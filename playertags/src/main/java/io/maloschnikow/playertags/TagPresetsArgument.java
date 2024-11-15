package io.maloschnikow.playertags;

import java.util.concurrent.CompletableFuture;

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

public class TagPresetsArgument implements CustomArgumentType.Converted<TagPreset, String> {

    @Override
    public @NotNull TagPreset convert(String nativeType) throws CommandSyntaxException {
        try {
            return AvailableTagPresets.valueOf(nativeType);
        } catch (IllegalArgumentException ignored) {
            Message message = MessageComponentSerializer.message().serialize(Component.text("Invalid flavor %s!".formatted(nativeType), NamedTextColor.RED));

            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String flavorKey : AvailableTagPresets.values()) {
            String flavorName = AvailableTagPresets.valueOf(flavorKey).getName();
            builder.suggest(flavorName, MessageComponentSerializer.message().serialize(Component.text("Chose a preset tag!", NamedTextColor.YELLOW)));
        }

        return builder.buildFuture();
    }
}