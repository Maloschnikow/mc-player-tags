package io.maloschnikow.playertags;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.plugin.Plugin;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;


public class PlayerTags extends JavaPlugin {

    private static PlayerTags plugin;

    public static PlayerTags getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        this.getLogger().info("TagPresets.plugin=" + AvailableTagPresets.plugin);

        //load preset tags required for autocompletion of /playertag
        AvailableTagPresets.loadPresetsFromConfig();

        //register Event Handler(s)
        getServer().getPluginManager().registerEvents(new ApplyPlayerTagOnJoinListener(), this);
        
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();            

            // Register /playertag command
            commands.register(
                Commands.literal("playertag")
                .then(
                    Commands.argument("player", ArgumentTypes.player()) //player argument
                    .then(
                        Commands.literal("add") //tag should be added
                        .then(
                            Commands.literal("preset") //a preset tag should be added
                            .then(
                                Commands.argument("tag", new TagPresetsArgument())
                                .executes(new PresetPlayerTagAddCommand())
                            )
                        )
                        .then(
                            Commands.literal("custom") //a custom tag should be added
                            .then(
                                Commands.argument("tag", ArgumentTypes.component())
                            )
                        )
                        
                    )
                    .then(
                        Commands.literal("remove") //tag should be removed
                        .then(
                            Commands.literal("preset") //a preset tag should be removed
                            .then(
                                Commands.argument("tag", new TagPresetsArgument()) //TODO make a argument type that suggests the player's tags
                                .executes(new PresetPlayerTagRemoveCommand()) 
                            )
                        )
                        .then(
                            Commands.literal("custom") //the custom tag should be removed
                            .executes(new CustomPlayerTagRemoveCommand())
                        )
                    )
                    .then(
                        Commands.literal("clear")
                        .executes(new PlayerTagClearCommand())
                    )
                )
                .build(),
                //TODO removing playertag permission (but should this require a permission?)
                "(experimental) Apply a tag to a player."
            );

        });
    }
}