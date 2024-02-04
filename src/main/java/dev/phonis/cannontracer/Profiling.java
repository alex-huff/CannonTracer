package dev.phonis.cannontracer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Profiling {
    final int TicksBetweenLogs = 200;
    public static long Run = 0;
    public static long ProcessEntities = 0;
    public static long BuildLineSystem = 0;
    public static long AddToLineSystem = 0;
    public static int LastChangedHits = 0;
    public static int LastChangedMisses = 0;
    public static long HandleLineSystem = 0;
    public static long UpdateParticles = 0;
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
                        "\n    Run:                  " + Run / 1000000 + " ms (+" + (Run - lastRun) / 1000000 + " ms)" +
                        "\n    ProcessEntities:      " + (float) ProcessEntities / Run * 100 + " %" +
                        "\n    BuildLineSystem:      " + (float) BuildLineSystem / Run * 100 + " %" +
                        "\n      LastChangedHitRate:   " + divToPercent(LastChangedHits, LastChangedMisses) +
                        "\n      AddToLineSystem:      " + (float) AddToLineSystem / Run * 100 + " %" +
                        "\n    HandleLineSystem:     " + (float) HandleLineSystem / Run * 100 + " %" +
                        "\n      UpdateParticles:      " + (float) UpdateParticles / Run * 100 + " %" +
                        "\n    SendPackets:          " + (float) SendPackets / Run * 100 + " %" +
                        "\n      GetClosestParticles:  " + (float) GetClosestParticles / Run * 100 + " %");
            lastRun = Run;
        }
    }

    public String divToPercent(long v1, long v2) {
        if (v2 == 0) {
            if (v1 == 0) return "NaN";
            return "100 %";
        }
        return "" + (float) v1 / v2 * 100 + " %";
    }

    public String getLastChangedHitRate() {
        if (LastChangedMisses == 0) {
            if (LastChangedHits == 0) return "NaN";
            return "100";
        }
        return "" + (float) LastChangedHits / LastChangedMisses * 100;
    }
}
