package dev.phonis.cannontracer.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public
class CTRegister implements CTPacket
{

    public final int protocolVersion;

    public
    CTRegister(int protocolVersion)
    {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public
    void toBytes(DataOutputStream dos) throws IOException
    {
        dos.writeInt(this.protocolVersion);
    }

    public static
    CTRegister fromBytes(DataInputStream dis) throws IOException
    {
        return new CTRegister(dis.readInt());
    }

    @Override
    public
    byte packetID()
    {
        return Packets.registerID;
    }

}
