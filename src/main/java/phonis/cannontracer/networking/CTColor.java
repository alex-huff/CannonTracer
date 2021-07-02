package phonis.cannontracer.networking;

import java.io.Serializable;

public class CTColor implements Serializable {

    public final float r;
    public final float g;
    public final float b;

    public CTColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

}
