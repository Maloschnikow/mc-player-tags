package io.maloschnikow.playertags;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class ApplyPlayerTagOnJoinListener implements Listener {

    private final Plugin plugin = PlayerTags.getPlugin();  

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PersistentDataContainer playerDataContainer = player.getPersistentDataContainer();
        String displayNameString = playerDataContainer.get(new NamespacedKey(plugin, "displayName"), PersistentDataType.STRING);
        if (displayNameString != null) {
            GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();
            Component displayName = gsonComponentSerializer.deserialize(displayNameString);

            player.displayName(displayName);
            player.playerListName(displayName);
        }

    }
}
