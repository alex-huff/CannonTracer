package phonis.cannontracer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import phonis.cannontracer.commands.CommandTracer;
import phonis.cannontracer.listeners.ExplosionEvent;
import phonis.cannontracer.listeners.SandBlockEvent;
import phonis.cannontracer.networking.CTListener;
import phonis.cannontracer.networking.CTManager;
import phonis.cannontracer.serializable.TracerUser;
import phonis.cannontracer.tasks.Tick;
import phonis.cannontracer.util.SerializationUtil;

import java.io.File;
import java.util.logging.Logger;

public class CannonTracer extends JavaPlugin {

    public static final String path = "plugins/EntityTracer/";
    public static final int protocolVersion = 1;
    public static CannonTracer instance;

    private final Logger log = getLogger();
    private final PluginMessageListener ctListener = new CTListener();

    @Override
    public void onEnable() {
        CannonTracer.instance = this;
        File f = new File(CannonTracer.path);

        if (!f.exists()) {
            if (f.mkdirs()) {
                this.log.info("Creating directory: " + path);
            }
        } else {
            SerializationUtil.deserialize(TracerUser.hmd, this.log);
        }

        Bukkit.getMessenger().registerIncomingPluginChannel(this, CTManager.CTChannel, this.ctListener);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CTManager.CTChannel);

        Tick tick = new Tick(this);

        new CommandTracer(this, "tracer");

        new ExplosionEvent(this, tick);
        new SandBlockEvent(this, tick);

        tick.start();
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, CTManager.CTChannel, this.ctListener);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, CTManager.CTChannel);
        SerializationUtil.serialize(TracerUser.hmd, this.log);
    }

}
