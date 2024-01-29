package dev.phonis.cannontracer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Profiling {
    final int TicksBetweenLogs = 200;
    public static long Run = 0;
    public static long ProcessEntities = 0;
    public static long HandleTraces = 0;
    public static long SendPackets = 0;
    public static long GetClosestParticles = 0;

    private final Logger logger;
    private int ticksUntilLog = TicksBetweenLogs;
    private long lastRun = 0;

    public Profiling(Logger logger){
        this.logger = logger;
    }

    public void Tick(){
        --ticksUntilLog;
        if (ticksUntilLog < 1){
            ticksUntilLog = TicksBetweenLogs;
            logger.log(Level.INFO,
                    "Profiling Statistics:" +
                        "\n    Run:                   " + Run / 1000000 + " ms (+" + (Run - lastRun) / 1000000 + " ms)" +
                        "\n    ProcessEntities:       " + (float) ProcessEntities / Run * 100 + " %" +
                        "\n    HandleTraces:          " + (float) HandleTraces / Run * 100 + " %" +
                        "\n    SendPackets:           " + (float) SendPackets / Run * 100 + " %" +
                        "\n      GetClosestParticles: " + (float) GetClosestParticles / Run * 100 + " %");
            lastRun = Run;
        }
    }
}
