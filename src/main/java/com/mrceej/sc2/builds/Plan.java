package com.mrceej.sc2.builds;

import java.util.ArrayList;
import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.*;

public class Plan {

    private static final List<BuildOrderEntry> buildMilestones = List.of(
            new BuildOrderEntry(14, ZERG_SPAWNING_POOL, true),
            new BuildOrderEntry(20, ZERG_ROACH_WARREN, true),
            new BuildOrderEntry(25, ZERG_EVOLUTION_CHAMBER, true),
            new BuildOrderEntry(30, ZERG_LAIR, true)
    );


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