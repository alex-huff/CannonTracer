package phonis.cannontracer.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import phonis.cannontracer.CannonTracer;
import phonis.cannontracer.networking.CTManager;
import phonis.cannontracer.networking.CTSetWorld;

public class ChangeWorldEvent implements Listener {

    public ChangeWorldEvent(CannonTracer cannonTracer) {
        cannonTracer.getServer().getPluginManager().registerEvents(this, cannonTracer);
    }

    @EventHandler
    public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        CTManager.sendToPlayerIfSubscribed(player, new CTSetWorld(player.getWorld().getUID()));
    }

}
