package phonis.cannontracer.networking;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import phonis.cannontracer.CannonTracer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class CTListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            CTPacket packet = (CTPacket) ois.readObject();

            this.handlePacket(s, player, packet);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(String s, Player player, CTPacket packet) {
        if (packet instanceof CTRegister) {
            CTRegister register = (CTRegister) packet;

            if (register.protocolVersion != CannonTracer.protocolVersion) {
                CTManager.sendToPlayer(player, new CTUnsupported(CannonTracer.protocolVersion));
                player.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "You are using an unsupported version of Cannon Tracer Mod: " + register.protocolVersion + ". Server is on: " + CannonTracer.protocolVersion + ".");
                System.out.println(player.getName() + " is using an unsupported version.");
            } else {
                player.sendMessage("Thank you for using Cannon Tracer client.");
                CTManager.addToSubscribed(player.getUniqueId());
                System.out.println(player.getName() + " is using the modded client.");
            }
        }
    }

}
