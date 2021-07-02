package phonis.cannontracer.networking;

import java.io.Serializable;

public class CTVec3 implements Serializable {

    public double x;
    public double y;
    public double z;

    public CTVec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CTVec3 plus(CTVec3 other) {
        return new CTVec3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

}
