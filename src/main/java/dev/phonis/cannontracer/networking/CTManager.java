package dev.phonis.cannontracer.networking;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import dev.phonis.cannontracer.CannonTracer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CTManager {

    private static final Set<UUID> subscribed = new HashSet<>();
    public static final String CTChannel = "cannontracer:main";

    public static void sendToPlayer(Player player, CTPacket packet) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeByte(packet.packetID());
            packet.toBytes(dos);

            byte[] bytes = baos.toByteArray();

            // System.out.println(packet.getClass().getName() + " " + bytes.length);

            player.sendPluginMessage(CannonTracer.instance, CTManager.CTChannel, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendToSubscribed(CTPacket packet) {
        for (UUID uuid : CTManager.subscribed) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) continue;

            CTManager.sendToPlayer(player, packet);
        }
    }

    public static void addToSubscribed(UUID uuid) {
        CTManager.subscribed.add(uuid);
    }

    public static boolean isSubscribed(UUID uuid) {
        return CTManager.subscribed.contains(uuid);
    }

    public static void removeFromSubscribed(UUID uuid) {
        CTManager.subscribed.remove(uuid);
    }

    public static void sendToPlayerIfSubscribed(Player player, CTPacket packet) {
        if (CTManager.subscribed.contains(player.getUniqueId())) {
            CTManager.sendToPlayer(player, packet);
        }
    }

}
