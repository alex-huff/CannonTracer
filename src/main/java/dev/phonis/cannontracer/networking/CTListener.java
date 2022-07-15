package dev.phonis.cannontracer.networking;

import dev.phonis.cannontracer.CannonTracer;
import dev.phonis.cannontracer.serializable.TracerUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public
class CTListener implements PluginMessageListener
{

    @Override
    public
    void onPluginMessageReceived(String s, Player player, byte[] bytes)
    {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));

        try
        {
            byte packetId = dis.readByte();

            switch (packetId)
            {
                case Packets.registerID:
                    this.handlePacket(s, player, CTRegister.fromBytes(dis));

                    break;
                case Packets.unsupportedID:
                    this.handlePacket(s, player, CTUnsupported.fromBytes(dis));

                    break;
                case Packets.newLinesID:
                    this.handlePacket(s, player, CTNewLines.fromBytes(dis));

                    break;
                case Packets.clearID:
                    this.handlePacket(s, player, CTClear.fromBytes(dis));

                    break;
                case Packets.setWorldID:
                    this.handlePacket(s, player, CTSetWorld.fromBytes(dis));

                    break;
                default:
                    System.out.println("Unrecognised packet.");

                    break;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private
    void handlePacket(String s, Player player, CTPacket packet)
    {
        if (packet instanceof CTRegister)
        {
            CTRegister register = (CTRegister) packet;

            if (register.protocolVersion != CannonTracer.protocolVersion)
            {
                CTManager.sendToPlayer(player, new CTUnsupported(CannonTracer.protocolVersion));
                player.sendMessage("" + ChatColor.RED + ChatColor.BOLD +
                                   "You are using an unsupported version of Cannon Tracer Mod: " +
                                   register.protocolVersion + ". Server is on: " + CannonTracer.protocolVersion + ".");
                System.out.println(player.getName() + " is using an unsupported version.");
            }
            else
            {
                player.sendMessage("Thank you for using Cannon Tracer client.");
                TracerUser.getUser(player.getUniqueId()).clearParticles();
                CTManager.addToSubscribed(player.getUniqueId());
                CTManager.sendToPlayer(player, new CTSetWorld(player.getWorld().getUID()));
                System.out.println(player.getName() + " is using the modded client.");
            }
        }
    }

}
