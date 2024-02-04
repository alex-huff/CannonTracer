package dev.phonis.cannontracer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import dev.phonis.cannontracer.commands.CommandTracer;
import dev.phonis.cannontracer.listeners.ChangeWorldEvent;
import dev.phonis.cannontracer.listeners.LeaveEvent;
import dev.phonis.cannontracer.listeners.EntityChangeFormEvent;
import dev.phonis.cannontracer.networking.CTListener;
import dev.phonis.cannontracer.networking.CTManager;
import dev.phonis.cannontracer.serializable.TracerUser;
import dev.phonis.cannontracer.tasks.Tick;
import dev.phonis.cannontracer.util.SerializationUtil;

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

        Tick tick = new Tick(this, log);

        new CommandTracer(this, "tracer");

        new EntityChangeFormEvent(this, tick);
        new ChangeWorldEvent(this);
        new LeaveEvent(this);

        tick.start();
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, CTManager.CTChannel, this.ctListener);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, CTManager.CTChannel);
        SerializationUtil.serialize(TracerUser.hmd, this.log);
    }

}
