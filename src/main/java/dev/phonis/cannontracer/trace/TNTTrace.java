package dev.phonis.cannontracer.trace;

import org.bukkit.Location;

public class TNTTrace extends BlockTrace {

    public TNTTrace(Location start, Location finish, boolean isStart, boolean isFinish, boolean isConnected, boolean isHypotenusal) {
        super(start, finish, isStart, isFinish, isConnected, isHypotenusal);
    }

    @Override
    protected ParticleType getType() {
        return ParticleType.TNT;
    }

    @Override
    protected ParticleType getSType() {
        return ParticleType.TNT;
    }

    @Override
    protected ParticleType getFType() {
        return ParticleType.TNTENDPOS;
    }

}
