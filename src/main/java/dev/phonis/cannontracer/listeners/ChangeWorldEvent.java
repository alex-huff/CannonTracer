package dev.phonis.cannontracer.listeners;

import dev.phonis.cannontracer.CannonTracer;
import dev.phonis.cannontracer.networking.CTManager;
import dev.phonis.cannontracer.networking.CTSetWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public
class ChangeWorldEvent implements Listener
{

    public
    ChangeWorldEvent(CannonTracer cannonTracer)
    {
        cannonTracer.getServer().getPluginManager().registerEvents(this, cannonTracer);
    }

    @EventHandler
    public
    void onPlayerChangeWorldEvent(PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();

        CTManager.sendToPlayerIfSubscribed(player, new CTSetWorld(player.getWorld().getUID()));
    }

}
