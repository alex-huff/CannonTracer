package phonis.cannontracer.trace;

import org.bukkit.Location;

public class PlayerTrace extends EntityMoveTrace {

    public PlayerTrace(Location start, Location finish, boolean isConnected, boolean isHypotenusal) {
        super(start.clone().add(0, .25, 0), finish.clone().add(0, .25, 0), isConnected, isHypotenusal);
    }

    @Override
    protected ParticleType getType() {
        return ParticleType.PLAYER;
    }

    @Override
    protected ParticleType getStartType() {
        return null;
    }

    @Override
    protected ParticleType getFinishType() {
        return null;
    }

    @Override
    protected OffsetType getStartOffsetType() {
        return null;
    }

    @Override
    protected OffsetType getFinishOffsetType() {
        return null;
    }

}
