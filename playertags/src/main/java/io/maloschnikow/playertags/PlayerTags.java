package io.maloschnikow.playertags;

import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.brigadier.arguments.ArgumentType;

import org.bukkit.plugin.Plugin;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;


public class PlayerTags extends JavaPlugin {

    @Override
    public void onEnable() {

        saveDefaultConfig();
        
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            // Register /playertag command
            commands.register(
                Commands.literal("playertag")
                .then(
                    Commands.argument("player", ArgumentTypes.player())
                    .then(
                        Commands.argument("type", new TagTypeArgument()).requires(new Permission("permissions.setCustomPlayerTag"))
                        .then(
                            Commands.argument("tag", ArgumentTypes.component())
                            .executes(new PlayerTagCommand(this))
                        )
                    )
                    .executes(new PlayerTagCommand(this))
                )
                .build(),
                "Set a tag of a player."
            );
        });
    }

}