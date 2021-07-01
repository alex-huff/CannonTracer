package phonis.cannontracer.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import phonis.cannontracer.CannonTracer;
import phonis.cannontracer.tasks.Tick;
import phonis.cannontracer.trace.ChangeType;
import phonis.cannontracer.trace.LocationChange;

public class SandBlockEvent implements Listener {
    private final Tick tick;

    public SandBlockEvent(CannonTracer cannonTracer, Tick tick) {
        cannonTracer.getServer().getPluginManager().registerEvents(this, cannonTracer);

        this.tick = tick;
    }

    @EventHandler
    public void onEntityBlockFormEvent(EntityChangeBlockEvent event) {
        if (!event.getTo().equals(Material.AIR)) {
            Entity entity = event.getEntity();

            if (entity.getType().compareTo(EntityType.FALLING_BLOCK) == 0) {
                Location loc = entity.getLocation();
                LocationChange change = new LocationChange(entity.getWorld(), loc, loc, EntityType.FALLING_BLOCK, ChangeType.END, entity.getVelocity().length());

                this.tick.addChange(change);
            }
        }
    }

}
