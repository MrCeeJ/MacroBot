package com.mrceej.sc2.builds;

import java.util.ArrayList;
import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.ZERG_EVOLUTION_CHAMBER;
import static com.github.ocraft.s2client.protocol.data.Units.ZERG_SPAWNING_POOL;

public class Plan {

    private static final List<BuildOrderEntry> buildMilestones = List.of(
            new BuildOrderEntry(14, ZERG_SPAWNING_POOL, true),
    //        new SimpleEntry<>(14, ZERG_EXTRACTOR),
            new BuildOrderEntry(25, ZERG_EVOLUTION_CHAMBER, true));

    public static List<BuildOrderEntry> getCurrentState(int workers) {
        List<BuildOrderEntry> build = new ArrayList<>();
        for (BuildOrderEntry e : buildMilestones) {
            if (workers >= e.getWorkers()) {
                build.add(e);
            }
        }
        return build;
    }
}