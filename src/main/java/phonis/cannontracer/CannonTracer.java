package phonis.cannontracer;

import org.bukkit.plugin.java.JavaPlugin;
import phonis.cannontracer.commands.CommandTracer;
import phonis.cannontracer.listeners.ExplosionEvent;
import phonis.cannontracer.listeners.SandBlockEvent;
import phonis.cannontracer.serializable.TracerUser;
import phonis.cannontracer.tasks.Tick;
import phonis.cannontracer.util.SerializationUtil;

import java.io.File;
import java.util.logging.Logger;

public class CannonTracer extends JavaPlugin {

    public static final String path = "plugins/EntityTracer/";

    private final Logger log = getLogger();

    @Override
    public void onEnable() {
        File f = new File(CannonTracer.path);

        if (!f.exists()) {
            if (f.mkdirs()) {
                this.log.info("Creating directory: " + path);
            }
        } else {
            SerializationUtil.deserialize(TracerUser.hmd, this.log);
        }

        Tick tick = new Tick(this);

        new CommandTracer(this, "tracer");

        new ExplosionEvent(this, tick);
        new SandBlockEvent(this, tick);

        tick.start();
    }

    @Override
    public void onDisable() {
        SerializationUtil.serialize(TracerUser.hmd, this.log);
    }

}
