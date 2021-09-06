package dev.phonis.cannontracer.util;

import dev.phonis.cannontracer.serializable.HashMapData;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

public class SerializationUtil {

    public static <K, V extends Serializable> void serialize(HashMapData<K, V> pd, Logger log) {
        try {
            FileOutputStream file = new FileOutputStream(pd.filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(pd.data);

            out.close();
            file.close();
        } catch (IOException e) {
            log.warning("IOException during serialization of object");
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V extends Serializable> void deserialize(HashMapData<K, V> pd, Logger log) {
        try {
            FileInputStream file = new FileInputStream(pd.filename);
            ObjectInputStream in = new ObjectInputStream(file);

            try {
                pd.data = (HashMap<K, V>) in.readObject();
            } catch (ClassNotFoundException e) {
                log.warning("Error during deserialization of object");
            }

            in.close();
            file.close();
        } catch (IOException e) {
            log.warning("IOException during deserialization of objects");
        }
    }

}

