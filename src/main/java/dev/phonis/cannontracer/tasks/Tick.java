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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tick implements Runnable {

    private static final int maxPayloadSize = 30000;

    private final Logger logger;
    private final Profiling profiler;
    private final Set<LocationChange> changes = new HashSet<>();
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

    private void processEntity(World world, Entity entity) {
        Location loc = entity.getLocation();
        int id = entity.getEntityId();
        EntityLocation el;

        if (this.locations.containsKey(id)) {
            Location old = this.locations.get(id).getLocation();

            if (!Objects.equals(old.getWorld(), loc.getWorld())) {
                el = new EntityLocation(loc, true, entity.getType());
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

                el = new EntityLocation(loc, false, entity.getType());
            }
        } else {
            el = new EntityLocation(loc, true, entity.getType());
        }

        this.locations.put(entity.getEntityId(), el);
    }

    private void processEntities() {
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity entity : world.getEntitiesByClasses(FallingBlock.class, TNTPrimed.class, Player.class)) {
                this.processEntity(world, entity);
            }
        }

        Set<Integer> keys = this.locations.keySet();
        List<Integer> removeList = new ArrayList<>();

        for (int key : keys) {
            if (this.locations.get(key).getState()) {
                this.locations.get(key).kill();
            } else {
                removeList.add(key);
            }
        }

        for (int key : removeList) {
            this.locations.remove(key);
        }
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

    private void handleTraces(List<Trace> traces, TracerUser tu, boolean isSubscribed, Player player) {
        Map<LineEq, LineSet> culledLines = new HashMap<>();
        int culledLineCount = 0;

        for (Trace trace : traces) {
            List<Line> lines = trace.getLines();

            for (Line line : lines) {
                if (!culledLines.containsKey(line.getLineEq())) {
                    culledLines.put(line.getLineEq(), new LineSet(tu.isTickConnect()));
                }

                culledLineCount += culledLines.get(line.getLineEq()).add(line) ? 0 : 1;
            }
        }

        // System.out.println("Culled lines: " + culledLineCount);

        List<CTLine> totalLines = new ArrayList<>();
        Set<CTArtifact> totalArtifacts = new HashSet<>();

        for (LineEq lineEq : culledLines.keySet()) {
            for (Line line : culledLines.get(lineEq)) {
                if (isSubscribed) {
                    // ticks does not matter since it is inferred by client from NewLines/Artifacts packet
                    totalLines.add(CTAdapter.fromLine(line, (short) -1));
                    totalArtifacts.addAll(CTAdapter.artifactsFromLine(line, (short) -1));
                } else {
                    tu.addLine(player.getWorld(), line);
                }
            }
        }

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

    @Override
    public void run() {
        long runStart = System.nanoTime();
        this.processEntities();
        Profiling.ProcessEntities += System.nanoTime() - runStart;

        Set<UUID> keySet = TracerUser.hmd.data.keySet();

        for (UUID uuid : keySet) {
            TracerUser tu = TracerUser.getUser(uuid);

            if (tu.isTrace()) {
                // Despawn expired particles
                if (tickCount % 20 == 0) {
                    for (ParticleSystem particleSystem : tu.getParticleSystems().values()) {
                        particleSystem.processDespawnQueue();
                        particleSystem.rebuildIfSaturated();
                    }
                }

                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {
                    List<Trace> traces = new ArrayList<>();

                    for (LocationChange change : this.changes) {
                        if (
                            change.getWorld() == player.getWorld()
                                && change.getVelocity() >= tu.getMinDistance()
                                && player.getLocation().distance(change.getStart()) <= tu.getTraceRadius()
                        ) {
                            if (change.getType().equals(EntityType.PRIMED_TNT) && tu.isTraceTNT()) {
                                boolean start = false;
                                boolean finish = false;

                                if (change.getChangeType().equals(ChangeType.END)) {
                                    finish = tu.isEndPosTNT();
                                } else if (change.getChangeType().equals(ChangeType.START)) {
                                    start = tu.isStartPosTNT();
                                }


                                traces.add(
                                    new TNTTrace(
                                        change.getStart(),
                                        change.getFinish(),
                                        start,
                                        finish,
                                        tu.isTickConnect(),
                                        tu.isHypotenusal()
                                    )
                                );
                            } else if (change.getType().equals(EntityType.FALLING_BLOCK) && tu.isTraceSand()) {
                                boolean start = false;
                                boolean finish = false;

                                if (change.getChangeType().equals(ChangeType.END)) {
                                    finish = tu.isEndPosSand();
                                } else if (change.getChangeType().equals(ChangeType.START)) {
                                    start = tu.isStartPosSand();
                                }

                                traces.add(
                                    new SandTrace(
                                        change.getStart(),
                                        change.getFinish(),
                                        start,
                                        finish,
                                        tu.isTickConnect(),
                                        tu.isHypotenusal()
                                    )
                                );
                            } else if (change.getType().equals(EntityType.PLAYER) && tu.isTracePlayer()) {
                                traces.add(
                                    new PlayerTrace(
                                        change.getStart(),
                                        change.getFinish(),
                                        tu.isTickConnect(),
                                        tu.isHypotenusal()
                                    )
                                );
                            }
                        }
                    }

                    boolean isSubscribed = CTManager.isSubscribed(player.getUniqueId());
                    long startHandleTraces = System.nanoTime();
                    this.handleTraces(traces, tu, isSubscribed, player);
                    Profiling.HandleTraces += System.nanoTime() - startHandleTraces;
                    if (!isSubscribed) {
                        long startSendPackets = System.nanoTime();
                        if (tickCount % 5 == 0) this.sendPackets(tu, player);
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

}
