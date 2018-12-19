package com.mrceej.sc2.builds;

import com.github.ocraft.s2client.protocol.data.Units;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.*;

public class Plan {

    public static List<SimpleEntry<Integer, Units>> buildMilestones = List.of(
            new SimpleEntry<>(14, ZERG_SPAWNING_POOL),
    //        new SimpleEntry<>(14, ZERG_EXTRACTOR),
            new SimpleEntry<>(25, ZERG_EVOLUTION_CHAMBER));

    public static List<Units> getCurrentState(int workers) {
        List<Units> build = new ArrayList<>();
        for (SimpleEntry<Integer, Units> e : buildMilestones) {
            if (workers >= e.getKey()) {
                build.add(e.getValue());
            }
        }
        return build;
    }
}