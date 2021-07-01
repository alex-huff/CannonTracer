package phonis.cannontracer.tasks;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import phonis.cannontracer.CannonTracer;
import phonis.cannontracer.serializable.TracerUser;
import phonis.cannontracer.trace.*;

import java.util.*;

public class Tick implements Runnable {

    private final Set<LocationChange> changes = new HashSet<>();
    private final Set<EntityLocation> lastTicks = new HashSet<>();
    private final Map<Integer, EntityLocation> locations = new HashMap<>();
    private final CannonTracer cannonTracer;
    private int tickCount = 0;

    public Tick(CannonTracer cannonTracer) {
        this.cannonTracer = cannonTracer;
    }

    public Set<EntityLocation> getLastTicks() {
        return this.lastTicks;
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

                if (entity.getType().equals(EntityType.PRIMED_TNT) && entity.getTicksLived() == 80) {
                    this.lastTicks.add(el);
                }
            }
        } else {
            el = new EntityLocation(loc, true, entity.getType());
        }

        this.locations.put(entity.getEntityId(), el);
    }

    private void processEntities() {
        this.lastTicks.clear();

        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity entity : world.getEntitiesByClasses(FallingBlock.class, TNTPrimed.class, Player.class)) {
                processEntity(world, entity);
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

    private void updateTick() {
        if (this.tickCount == 5) {
            this.tickCount = 0;
        } else {
            this.tickCount += 1;
        }
    }

    private void sendPackets(TracerUser tu, Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        Iterator<ParticleLocation> it = tu.getParticleLocations().iterator();

        while (it.hasNext()) {
            ParticleLocation pLocation = it.next();

            if (pLocation.getLocation().getWorld() != null && pLocation.getLocation().getWorld().equals(player.getWorld())) {
                if (tickCount == 5 && (pLocation.getLocation().distance(player.getLocation()) < tu.getViewRadius() || tu.isUnlimitedRadius())) {
                    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                        EnumParticle.REDSTONE,
                        true,
                        (float) pLocation.getLocation().getX(),
                        (float) pLocation.getLocation().getY(),
                        (float) pLocation.getLocation().getZ(),
                        pLocation.getType().getRGB().getR() / 255f - 1,
                        pLocation.getType().getRGB().getG() / 255f,
                        pLocation.getType().getRGB().getB() / 255f,
                        1,
                        0
                    );

                    connection.sendPacket(packet);
                }
            }

            pLocation.decLife();

            if (pLocation.getLife() == 0) {
                it.remove();
            }
        }
    }

    private void handleTraces(List<Trace> traces, TracerUser tu) {
        Map<LineEq, LineSet> culledLines = new HashMap<>();

        for (Trace trace : traces) {
            List<Line> lines = trace.getLines();

            for (Line line : lines) {
                if (!culledLines.containsKey(line.getLineEq())) {
                    culledLines.put(line.getLineEq(), new LineSet(tu.isTickConnect()));
                }

                culledLines.get(line.getLineEq()).add(line);
            }
        }

        for (LineEq lineEq : culledLines.keySet()) {
            for (Line line : culledLines.get(lineEq)) {
                tu.addLine(line);
            }
        }
    }

    @Override
    public void run() {
        this.processEntities();

        Set<UUID> keySet = TracerUser.hmd.data.keySet();

        for (UUID uuid : keySet) {
            TracerUser tu = TracerUser.getUser(uuid);

            if (tu.isTrace()) {
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

                    this.handleTraces(traces, tu);

                    tu.updateRadius(player.getLocation());
                    this.sendPackets(tu, player);
                }
            }
        }

        this.changes.clear();
        this.updateTick();
    }

}
