package phonis.cannontracer.serializable;

import java.io.Serializable;
import java.util.HashMap;

public class HashMapData<K, V extends Serializable> {

    public HashMap<K, V> data = new HashMap<>();
    public String filename;

    public HashMapData(String filename) {
        this.filename = filename;
    }

}
