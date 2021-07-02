package phonis.cannontracer.serializable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import phonis.cannontracer.CannonTracer;
import phonis.cannontracer.networking.CTManager;
import phonis.cannontracer.trace.Line;
import phonis.cannontracer.trace.ParticleLocation;
import phonis.cannontracer.trace.ParticleLocationComparator;
import phonis.cannontracer.trace.ParticleType;

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
    private transient boolean unlimitedRadius = false;
    private double minDistance;
    private double traceRadius;
    private transient double viewRadius;
    private int maxParticles;
    private int traceTime;
    private transient Set<ParticleLocation> pLocs;

    private TracerUser(boolean trace, boolean traceSand, boolean traceTNT, boolean tracePlayer, boolean endPosSand, boolean endPosTNT, boolean startPosSand, boolean startPosTNT, boolean tickConnect, boolean hypotenusal, double minDistance, double traceRadius, double viewRadius, int maxParticles, int traceTime) {
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
        this.viewRadius = viewRadius;
        this.maxParticles = maxParticles;
        this.traceTime = traceTime;
    }

    private TracerUser() {
        this(false, true, true, false, false, true, false, true, true, false, 5.0D, 100D, 0D, 1000, 100);
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

    public boolean isUnlimitedRadius() {
        return this.unlimitedRadius;
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

    public double getViewRadius() {
        return viewRadius;
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

    public Set<ParticleLocation> getParticleLocations() {
        this.unNull();

        return this.pLocs;
    }

    public void unNull() {
        if (this.pLocs == null) {
            this.pLocs = new HashSet<>();
        }
    }

    public void clearParticles() {
        this.unNull();

        this.pLocs.clear();
    }

    public void clearTNT() {
        this.unNull();

        this.pLocs.removeIf(pLocation -> pLocation.getType().compareTo(ParticleType.TNT) == 0 || pLocation.getType().compareTo(ParticleType.TNTENDPOS) == 0);
    }

    public void clearSand() {
        this.unNull();

        this.pLocs.removeIf(pLocation -> pLocation.getType().equals(ParticleType.SAND) || pLocation.getType().equals(ParticleType.SANDENDPOS));
    }

    public void clearPlayer() {
        this.unNull();

        this.pLocs.removeIf(pLocation -> pLocation.getType().equals(ParticleType.PLAYER));
    }

    public void addLine(Line line) {
        this.addAllParticles(line.getParticles(this.getTraceTime()));
    }

    private void addAllParticles(Iterable<ParticleLocation> pI) {
        this.unNull();

        if (this.pLocs.size() != 0 && !(this.pLocs.iterator().next().getLocation().getWorld() == pI.iterator().next().getLocation().getWorld())) {
            this.pLocs.clear();
        }

        for (ParticleLocation pl : pI) {
            this.pLocs.remove(pl);
            this.pLocs.add(pl);
        }
    }

    public void updateRadius(Location loc) {
        this.unNull();

        if (this.pLocs.size() != 0 && !(loc.getWorld() == this.pLocs.iterator().next().getLocation().getWorld())) {
            this.pLocs.clear();

            return;
        }

        if (this.maxParticles > this.pLocs.size()) {
            this.unlimitedRadius = true;
        } else {
            this.unlimitedRadius = false;

            List<ParticleLocation> pLAL = new ArrayList<>(this.pLocs);
            PriorityQueue<ParticleLocation> pq = new PriorityQueue<>((new ParticleLocationComparator(loc)).reversed());
            pq.addAll(pLAL.subList(0, this.maxParticles));

            for (int i = this.maxParticles; i < this.pLocs.size(); i++) {
                if (pq.comparator().compare(pLAL.get(i), pq.peek()) > 0) {
                    pq.poll();
                    pq.add(pLAL.get(i));
                }
            }

            if (pq.peek() == null) return;

            this.viewRadius = loc.distance(pq.peek().getLocation());
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

}
