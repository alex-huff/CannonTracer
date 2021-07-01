package phonis.cannontracer.trace;

import org.bukkit.Location;

public class SandTrace extends BlockTrace {

    public SandTrace(Location start, Location finish, boolean isStart, boolean isFinish, boolean isConnected, boolean isHypotenusal) {
        super(start, finish, isStart, isFinish, isConnected, isHypotenusal);
    }

    @Override
    protected ParticleType getType() {
        return ParticleType.SAND;
    }

    @Override
    protected ParticleType getSType() {
        return ParticleType.SAND;
    }

    @Override
    protected ParticleType getFType() {
        return ParticleType.SANDENDPOS;
    }

}
