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

        this.getLogger().info("TagPresets.plugin=" + TagPresets.plugin);

        //load preset tags required for autocompletion of /playertag
        TagPresets.loadPresetsFromConfig();

        //register Event Handler(s)
        getServer().getPluginManager().registerEvents(new ApplyPlayerTagOnJoinListener(), this);
        
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
                        .executes(new PlayerTagCommand())
                    )
                    .executes(new PlayerTagCommand())
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
                        .executes(new PlayerTagCustomCommand())
                    )
                    .executes(new PlayerTagCustomCommand())
                )
                .requires(new Permission("permissions.customPlayerTag"))
                .build(),
                "Apply a custom tag to a player. (https://minecraft.wiki/w/Raw_JSON_text_format)"
            );

            // Register /playertagclear command
            commands.register(
                Commands.literal("playertagclear")
                .then(
                    Commands.argument("player", ArgumentTypes.player())
                    .executes(new PlayerTagClearCommand())
                )
                .requires(new Permission("permissions.clearPlayerTag"))
                .build(),
                "Apply a tag to a player."
            );

            // Register /playertagremove command
            commands.register(
                Commands.literal("playertagremove")
                .then(
                    Commands.argument("player", ArgumentTypes.player())
                    .then(
                        Commands.argument("tag", new TagPresetsArgument())
                        .executes(new PlayerTagRemoveCommand())
                    )
                )
                .build(),
                //TODO removing playertag permission (but should this require a permission?)
                "Apply a tag to a player."
            );
        });
    }

}