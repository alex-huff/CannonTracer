package dev.phonis.cannontracer.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public
class CTUnsupported implements CTPacket
{

    public final int protocolVersion;

    public
    CTUnsupported(int protocolVersion)
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
    CTUnsupported fromBytes(DataInputStream dis) throws IOException
    {
        return new CTUnsupported(dis.readInt());
    }

    @Override
    public
    byte packetID()
    {
        return Packets.unsupportedID;
    }

}
