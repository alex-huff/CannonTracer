package dev.phonis.cannontracer.listeners;

import dev.phonis.cannontracer.CannonTracer;
import dev.phonis.cannontracer.networking.CTManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public
class LeaveEvent implements Listener
{

    public
    LeaveEvent(CannonTracer plugin)
    {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public
    void onPlayerQuit(PlayerQuitEvent event)
    {
        CTManager.removeFromSubscribed(event.getPlayer().getUniqueId());
    }

}