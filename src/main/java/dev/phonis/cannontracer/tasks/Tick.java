package dev.phonis.cannontracer.tasks;

import dev.phonis.cannontracer.Profiling;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import dev.phonis.cannontracer.CannonTracer;
import dev.phonis.cannontracer.networking.*;
import dev.phonis.cannontracer.serializable.TracerUser;
import dev.phonis.cannontracer.trace.*;

import java.util.*;
import java.util.logging.Logger;

public class Tick implements Runnable {

    private static final int maxPayloadSize = 30000;

    private final Logger logger;
    private final Profiling profiler;
    private final List<LocationChange> changes = new ArrayList<>();
    public final Map<Integer, EntityLocation> locations = new HashMap<>();
    private final CannonTracer cannonTracer;
    private int tickCount = 0;

    public Tick(CannonTracer cannonTracer, Logger logger) {
        this.cannonTracer = cannonTracer;
        this.logger = logger;
        this.profiler = new Profiling(logger);
    }

    public void start() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.cannonTracer, this, 0L, 1L);
    }

    public void addChange(LocationChange lc) {
        this.changes.add(lc);
    }

    @Override
    public void run() {
        long runStart = System.nanoTime();
        this.processEntities();
        Profiling.ProcessEntities += System.nanoTime() - runStart;

        Set<UUID> keySet = TracerUser.hmd.data.keySet();

        int tickOffset = 0;
        for (UUID uuid : keySet) {
            tickOffset += 3;
            TracerUser tu = TracerUser.getUser(uuid);

            if (tu.isTrace()) {
                // Despawn expired particles
                if ((tickCount + tickOffset) % 20 == 0) {
                    for (ParticleSystem particleSystem : tu.getParticleSystems().values()) {
                        particleSystem.processDespawnQueue();
                        particleSystem.rebuildIfSaturated();
                    }
                }

                Player player = Bukkit.getPlayer(uuid);

                if (player != null && player.isOnline()) {
                    long startBuildLineSystem = System.nanoTime();
                    Map<LineIntercepts, LineSet> lineSystem = buildLineSystem(tu, player);
                    Profiling.BuildLineSystem += System.nanoTime() - startBuildLineSystem;

                    boolean isSubscribed = CTManager.isSubscribed(player.getUniqueId());
                    long startHandleLineSystem = System.nanoTime();
                    this.handleLineSystem(lineSystem, tu, isSubscribed, player);
                    Profiling.HandleLineSystem += System.nanoTime() - startHandleLineSystem;
                    if (!isSubscribed) {
                        long startSendPackets = System.nanoTime();
                        if ((tickCount + tickOffset) % 5 == 0) this.sendPackets(tu, player);
                        Profiling.SendPackets += System.nanoTime() - startSendPackets;
                    }
                }
            }
        }

        this.changes.clear();
        ++tickCount;
        Profiling.Run += System.nanoTime() - runStart;
        profiler.Tick();
    }

    private void processEntity(World world, Entity entity) {
        Location loc = entity.getLocation();
        int id = entity.getEntityId();
        EntityLocation el;

        if (this.locations.containsKey(id)) {
            Location old = this.locations.get(id).getLocation();

            if (!Objects.equals(old.getWorld(), loc.getWorld())) {
                el = new EntityLocation(loc, tickCount, true, entity.getType());
            } else {
                LocationChange change;

                if (this.locations.get(id).getNew()) {
                    change = new LocationChange(world, old, loc, entity.getType(), ChangeType.START);
                } else {
                    change = new LocationChange(world, old, loc, entity.getType(), ChangeType.NORMAL);
                }

                if (change.getChangeType().compareTo(ChangeType.NORMAL) != 0 || old.distance(loc) != 0) {
                    this.changes.add(change);
                }

                el = new EntityLocation(loc, tickCount, false, entity.getType());
            }
        } else {
            el = new EntityLocation(loc, tickCount, true, entity.getType());
        }

        this.locations.put(entity.getEntityId(), el);
    }

    private void processEntities() {
        int entityCount = 0;
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity entity : world.getEntitiesByClasses(FallingBlock.class, TNTPrimed.class, Player.class)) {
                this.processEntity(world, entity);
                ++entityCount;
            }
        }

        if (locations.size() > entityCount * 3)
            this.locations.values().removeIf(next -> next.getLastUpdated() != tickCount);
    }

    private void sendPackets(TracerUser tu, Player player) {
        World playerWorld = player.getWorld();
        if (playerWorld == null) return;

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        ParticleSystem particleSystem = tu.getParticleSystem(playerWorld);
        long startGetClosestParticles = System.nanoTime();
        List<TraceParticle> particles = particleSystem.getClosestParticles(player.getLocation(), tu.getMaxParticles());
        Profiling.GetClosestParticles += System.nanoTime() - startGetClosestParticles;
        for (TraceParticle particle : particles) {
            Location pLoc = particle.getLocation();
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                    EnumParticle.REDSTONE,
                    true,
                    (float) pLoc.getX(),
                    (float) pLoc.getY(),
                    (float) pLoc.getZ(),
                    particle.getType().getRGB().getR() / 255f - 1,
                    particle.getType().getRGB().getG() / 255f,
                    particle.getType().getRGB().getB() / 255f,
                    1,
                    0
            );

            connection.sendPacket(packet);
        }
    }

    private Map<LineIntercepts, LineSet> buildLineSystem(TracerUser tu, Player player) {
        Map<LineIntercepts, LineSet> lineSystem = new HashMap<>();

        LocationChange lastChange = null;
        for (LocationChange change : this.changes) {
            if (change.equals(lastChange)) {
                Profiling.LastChangedHits++;
                continue;
            } else Profiling.LastChangedMisses++;
            lastChange = change;

            if (
                    change.getWorld() == player.getWorld()
                            && change.getVelocity() >= tu.getMinDistance()
                            && player.getLocation().distance(change.getStart()) <= tu.getTraceRadius()
            ) {
                if (tu.isTraceTNT() && change.getType().equals(EntityType.PRIMED_TNT)) {
                    boolean start = false;
                    boolean finish = false;

                    if (change.getChangeType().equals(ChangeType.END)) {
                        finish = tu.isEndPosTNT();
                    } else if (change.getChangeType().equals(ChangeType.START)) {
                        start = tu.isStartPosTNT();
                    }

                    addToLineSystem(
                            lineSystem,
                            new TNTTrace(
                                    change.getStart(),
                                    change.getFinish(),
                                    start,
                                    finish,
                                    tu.isTickConnect(),
                                    tu.isHypotenusal()
                            ).getLines(),
                            tu.isTickConnect()
                    );

                } else if (tu.isTraceSand() && change.getType().equals(EntityType.FALLING_BLOCK)) {
                    boolean start = false;
                    boolean finish = false;

                    if (change.getChangeType().equals(ChangeType.END)) {
                        finish = tu.isEndPosSand();
                    } else if (change.getChangeType().equals(ChangeType.START)) {
                        start = tu.isStartPosSand();
                    }

                    addToLineSystem(
                            lineSystem,
                            new SandTrace(
                                    change.getStart(),
                                    change.getFinish(),
                                    start,
                                    finish,
                                    tu.isTickConnect(),
                                    tu.isHypotenusal()
                            ).getLines(),
                            tu.isTickConnect()
                    );

                } else if (tu.isTracePlayer() && change.getType().equals(EntityType.PLAYER)) {
                    addToLineSystem(
                            lineSystem,
                            new PlayerTrace(
                                    change.getStart(),
                                    change.getFinish(),
                                    tu.isTickConnect(),
                                    tu.isHypotenusal()
                            ).getLines(),
                            tu.isTickConnect()
                    );
                }
            }
        }
        return lineSystem;
    }

    private void addToLineSystem(Map<LineIntercepts, LineSet> lineSystem, List<Line> lines, boolean isTickConnect) {
        long start = System.nanoTime();
        for (Line line : lines) {
            LineIntercepts lineIntercepts = line.getLineIntercepts();

            LineSet lineSet = lineSystem.get(lineIntercepts);
            if (lineSet == null) {
                lineSet = new LineSet(isTickConnect);
                lineSystem.put(lineIntercepts,lineSet);
            }
            lineSet.add(line);
        }
        Profiling.AddToLineSystem += System.nanoTime() - start;
    }

    private void handleLineSystem(Map<LineIntercepts, LineSet> lineSystem, TracerUser tu, boolean isSubscribed, Player player) {
        List<CTLine> totalLines = new ArrayList<>();
        Set<CTArtifact> totalArtifacts = new HashSet<>();

        long startUpdateParticles = System.nanoTime();
        for (LineSet lineSet : lineSystem.values()) {
            for (Line line : lineSet) {
                if (isSubscribed) {
                    // ticks does not matter since it is inferred by client from NewLines/Artifacts packet
                    totalLines.add(CTAdapter.fromLine(line, (short) -1));
                    totalArtifacts.addAll(CTAdapter.artifactsFromLine(line, (short) -1));
                } else {
                    tu.addLine(player.getWorld(), line);
                }
            }
        }
        Profiling.UpdateParticles += System.nanoTime() - startUpdateParticles;

        if (!isSubscribed) return;

        if (totalLines.size() > 0) {
            int currentSize = 0;
            Iterator<CTLine> iterator = totalLines.iterator();
            List<CTLine> currentPacket = new ArrayList<>();
            short ticks = (short) tu.getTraceTime();

            while (iterator.hasNext()) {
                CTLine current = iterator.next();

                if (current.start.equals(current.finish)) continue;

                if (currentSize + current.size() < Tick.maxPayloadSize) {
                    currentSize += current.size();
                } else {
                    CTManager.sendToPlayer(player, new CTNewLines(player.getWorld().getUID(), ticks, currentPacket));

                    currentPacket = new ArrayList<>();
                    currentSize = current.size();

                }

                currentPacket.add(current);
            }

            if (currentPacket.size() > 0)
                CTManager.sendToPlayer(player, new CTNewLines(player.getWorld().getUID(), ticks, currentPacket));
        }

        if (totalArtifacts.size() > 0) {
            int currentSize = 0;
            Iterator<CTArtifact> iterator = totalArtifacts.iterator();
            List<CTArtifact> currentPacket = new ArrayList<>();
            short ticks = (short) tu.getTraceTime();

            while (iterator.hasNext()) {
                CTArtifact current = iterator.next();

                if (currentSize + current.size() < Tick.maxPayloadSize) {
                    currentSize += current.size();
                } else {
                    CTManager.sendToPlayer(player, new CTNewArtifacts(player.getWorld().getUID(), ticks, currentPacket));

                    currentPacket = new ArrayList<>();
                    currentSize = current.size();

                }

                currentPacket.add(current);
            }

            if (currentPacket.size() > 0)
                CTManager.sendToPlayer(player, new CTNewArtifacts(player.getWorld().getUID(), ticks, currentPacket));
        }
    }
}
