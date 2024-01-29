package dev.phonis.cannontracer.serializable;

import dev.phonis.cannontracer.trace.*;
import org.bukkit.ChatColor;
import dev.phonis.cannontracer.CannonTracer;
import org.bukkit.World;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

public class TracerUser implements Serializable {

    public static HashMapData<UUID, TracerUser> hmd = new HashMapData<>(CannonTracer.path + "TracerUser.ser");

    private boolean trace;
    private boolean traceSand;
    private boolean traceTNT;
    private boolean tracePlayer;
    private boolean endPosSand;
    private boolean endPosTNT;
    private boolean startPosSand;
    private boolean startPosTNT;
    private boolean tickConnect;
    private boolean hypotenusal;
    private double minDistance;
    private double traceRadius;
    private int maxParticles;
    private int traceTime;
    private transient Map<World, ParticleSystem> particleSystems = new HashMap<>();

    private TracerUser(boolean trace, boolean traceSand, boolean traceTNT, boolean tracePlayer, boolean endPosSand, boolean endPosTNT, boolean startPosSand, boolean startPosTNT, boolean tickConnect, boolean hypotenusal, double minDistance, double traceRadius, int maxParticles, int traceTime) {
        this.trace = trace;
        this.traceSand = traceSand;
        this.traceTNT = traceTNT;
        this.tracePlayer = tracePlayer;
        this.endPosSand = endPosSand;
        this.endPosTNT = endPosTNT;
        this.startPosSand = startPosSand;
        this.startPosTNT = startPosTNT;
        this.tickConnect = tickConnect;
        this.hypotenusal = hypotenusal;
        this.minDistance = minDistance;
        this.traceRadius = traceRadius;
        this.maxParticles = maxParticles;
        this.traceTime = traceTime;
    }

    private TracerUser() {
        this(false, true, true, false, false, true, false, true, true, false, 5.0D, 100D, 1000, 100);
    }

    public static TracerUser getUser(UUID uuid) {
        TracerUser ret;

        ret = TracerUser.hmd.data.get(uuid);

        if (ret == null) {
            TracerUser.hmd.data.put(uuid, new TracerUser());

            ret = TracerUser.hmd.data.get(uuid);
        }

        return ret;
    }

    public boolean isTrace() {
        return trace;
    }

    public void toggleTrace() {
        this.trace = !this.trace;
    }

    public boolean isTraceSand() {
        return traceSand;
    }

    public void toggleTraceSand() {
        this.traceSand = !this.traceSand;
    }

    public boolean isTraceTNT() {
        return traceTNT;
    }

    public void toggleTraceTNT() {
        this.traceTNT = !this.traceTNT;
    }

    public boolean isTracePlayer() {
        return tracePlayer;
    }

    public void toggleTracePlayer() {
        this.tracePlayer = !this.tracePlayer;
    }

    public boolean isEndPosSand() {
        return endPosSand;
    }

    public void toggleEndPosSand() {
        this.endPosSand = !this.endPosSand;
    }

    public boolean isEndPosTNT() {
        return endPosTNT;
    }

    public void toggleEndPosTNT() {
        this.endPosTNT = !this.endPosTNT;
    }

    public boolean isStartPosSand() {
        return startPosSand;
    }

    public void toggleStartPosSand() {
        this.startPosSand = !this.startPosSand;
    }

    public boolean isStartPosTNT() {
        return startPosTNT;
    }

    public void toggleStartPosTNT() {
        this.startPosTNT = !this.startPosTNT;
    }

    public boolean isTickConnect() {
        return tickConnect;
    }

    public void toggleTickConnect() {
        this.tickConnect = !this.tickConnect;
    }

    public boolean isHypotenusal() {
        return this.hypotenusal;
    }

    public void toggleHypotenusal() {
        this.hypotenusal = !this.hypotenusal;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public double getTraceRadius() {
        return traceRadius;
    }

    public void setTraceRadius(double traceRadius) {
        this.traceRadius = traceRadius;
    }

    public int getMaxParticles() {
        return maxParticles;
    }

    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
    }

    public int getTraceTime() {
        return traceTime;
    }

    public void setTraceTime(int traceTime) {
        this.traceTime = traceTime;
    }

    public Map<World, ParticleSystem> getParticleSystems() {
        return particleSystems;
    }

    public ParticleSystem getParticleSystem(World world) {
        ParticleSystem particleSystem = this.particleSystems.get(world);
        if (particleSystem == null) {
            particleSystem = new ParticleSystem();
            particleSystems.put(world, particleSystem);
        }
        return particleSystem;
    }

    public void clearParticles() {
        this.particleSystems.clear();
    }

    public void clearTNT() {
        for (ParticleSystem particleSystem : particleSystems.values()) {
            particleSystem.removeIf(p -> p.getType() == ParticleType.TNT || p.getType() == ParticleType.TNTENDPOS);
        }
    }

    public void clearSand() {
        for (ParticleSystem particleSystem : particleSystems.values()) {
            particleSystem.removeIf(p -> p.getType() == ParticleType.SAND || p.getType() == ParticleType.SANDENDPOS);
        }
    }

    public void clearPlayer() {
        for (ParticleSystem particleSystem : particleSystems.values()) {
            particleSystem.removeIf(p -> p.getType() == ParticleType.PLAYER);
        }
    }

    public void addLine(World world, Line line) {
        this.addAllParticles(world, line.getParticles(this.getTraceTime()));
    }

    private void addAllParticles(World world, Iterable<TraceParticle> pI) {
        ParticleSystem particleSystem = getParticleSystem(world);
        for (TraceParticle pl : pI) {
            particleSystem.writeParticle(pl);
        }
    }

    public String printSettings() {
        String message = "" + ChatColor.BOLD + ChatColor.BLUE + "Settings:" + ChatColor.RESET + "\n";

        message +=
            ChatColor.AQUA + "Trace enabled:             " + ChatColor.WHITE + this.isTrace() + "\n" +
                ChatColor.AQUA + "Sand trace enabled:        " + ChatColor.WHITE + this.isTraceSand() + "\n" +
                ChatColor.AQUA + "TNT trace enabled:         " + ChatColor.WHITE + this.isTraceTNT() + "\n" +
                ChatColor.AQUA + "Player trace enabled:      " + ChatColor.WHITE + this.isTracePlayer() + "\n" +
                ChatColor.AQUA + "Sand end positions:        " + ChatColor.WHITE + this.isEndPosSand() + "\n" +
                ChatColor.AQUA + "TNT end positions:         " + ChatColor.WHITE + this.isEndPosTNT() + "\n" +
                ChatColor.AQUA + "Sand start positions:      " + ChatColor.WHITE + this.isStartPosSand() + "\n" +
                ChatColor.AQUA + "TNT start positions:       " + ChatColor.WHITE + this.isStartPosTNT() + "\n" +
                ChatColor.AQUA + "Connect ticks:             " + ChatColor.WHITE + this.isTickConnect() + "\n" +
                ChatColor.AQUA + "Hypotenusal:               " + ChatColor.WHITE + this.isHypotenusal() + "\n" +
                ChatColor.AQUA + "Minimum distance traveled: " + ChatColor.WHITE + this.getMinDistance() + "\n" +
                ChatColor.AQUA + "Trace radius:              " + ChatColor.WHITE + this.getTraceRadius() + "\n" +
                ChatColor.AQUA + "Maximum particles:         " + ChatColor.WHITE + this.getMaxParticles() + "\n" +
                ChatColor.AQUA + "Trace time in ticks:       " + ChatColor.WHITE + this.getTraceTime() + "\n";

        return message;
    }

    public void copySettings(TracerUser other) {
        this.trace = other.trace;
        this.traceSand = other.traceSand;
        this.traceTNT = other.traceTNT;
        this.tracePlayer = other.tracePlayer;
        this.endPosSand = other.endPosSand;
        this.endPosTNT = other.endPosTNT;
        this.startPosSand = other.startPosSand;
        this.startPosTNT = other.startPosTNT;
        this.tickConnect = other.tickConnect;
        this.hypotenusal = other.hypotenusal;
        this.minDistance = other.minDistance;
        this.traceRadius = other.traceRadius;
        this.maxParticles = other.maxParticles;
        this.traceTime = other.traceTime;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        particleSystems = new HashMap<>();
    }
}
