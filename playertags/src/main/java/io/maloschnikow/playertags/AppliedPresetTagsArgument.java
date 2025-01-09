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
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class AppliedPresetTagsArgument implements CustomArgumentType.Converted<TagPreset, String> {

    @Override
    public @NotNull TagPreset convert(String nativeType) throws CommandSyntaxException {
        
        try {
            return AvailableTagPresets.valueOf(nativeType);
        } catch (IllegalArgumentException ignored) {
            Message message = MessageComponentSerializer.message().serialize(Component.text("Invalid tag \"%s\". Was the tag removed from the config file?".formatted(nativeType), NamedTextColor.RED));

            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        // player argument is in child of context
        CommandContext<?> childContext = context.getChild();

        try {

            // Hier rufen wir das "player" Argument im ChildContext ab
            Player player = childContext.getArgument("player", Player.class);

            // suggest tags the player already has
            AppliedPresetTags appliedPresetTags = new AppliedPresetTags(player);
            GsonComponentSerializer gson = GsonComponentSerializer.gson();

            for (String flavorKey : appliedPresetTags.values()) {

                PlayerTags.getPlugin().getLogger().info(flavorKey);

                String flavorName = appliedPresetTags.valueOf(flavorKey).getName();
                Component preview = gson.deserialize(appliedPresetTags.valueOf(flavorKey).getTagComponentString());

                builder.suggest(flavorName, MessageComponentSerializer.message().serialize(preview));
            }

        } catch (IllegalArgumentException e) {
            // Log occuring errors
            PlayerTags.getPlugin().getLogger().info(e.getMessage());
        }

        // return suggestions
        return builder.buildFuture();
    }

}