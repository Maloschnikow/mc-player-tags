package io.maloschnikow.playertags;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ApplyPlayerTagOnJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        //TODO also apply custom tag

        Player player = event.getPlayer();
        PlayerTags.applyPlayerTags(player);
    }
}
