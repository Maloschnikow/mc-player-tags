package io.maloschnikow.playertags;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.plugin.Plugin;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;


public class PlayerTags extends JavaPlugin {

    @Override
    public void onEnable() {

        saveDefaultConfig();

        TagPresets.setPlugin(this);
        TagPresets.loadPresetsFromConfig();
        
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();


            // Register /playertag command
            commands.register(
                Commands.literal("playertag")
                .then(
                    Commands.argument("player", ArgumentTypes.player())
                    .then(
                        Commands.argument("tag", new TagPresetsArgument())
                        .executes(new PlayerTagCommand(this))
                    )
                    .executes(new PlayerTagCommand(this))
                )
                .requires(new Permission("permissions.setPlayerTag"))
                .build(),
                "Apply a tag to a player."
            );

            // Register /playertagcustom command
            commands.register(
                Commands.literal("playertagcustom")
                .then(
                    Commands.argument("player", ArgumentTypes.player())
                    .then(
                        Commands.argument("tag", ArgumentTypes.component())
                        .executes(new PlayerTagCustomCommand(this))
                    )
                    .executes(new PlayerTagCustomCommand(this))
                )
                .requires(new Permission("permissions.customPlayerTag"))
                .build(),
                "Apply a custom tag to a player. (https://minecraft.wiki/w/Raw_JSON_text_format)"
            );
        });
    }

}