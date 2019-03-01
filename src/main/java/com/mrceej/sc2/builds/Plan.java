package com.mrceej.sc2.builds;

import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.macroBot.MacroBot;
import com.mrceej.sc2.macroBot.MacroManager;

import java.util.ArrayList;
import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.*;

public abstract class Plan {

    List<BuildOrderEntry> buildMilestones;
    private final MacroBot agent;

    Plan(MacroBot agent) {
        this.agent = agent;
        buildMilestones = List.of(
                new BuildOrderEntry(14, ZERG_SPAWNING_POOL, true),
                new BuildOrderEntry(20, ZERG_ROACH_WARREN, true),
                new BuildOrderEntry(25, ZERG_EVOLUTION_CHAMBER, true),
                new BuildOrderEntry(30, ZERG_LAIR, true)
        );
    }

    public List<BuildOrderEntry> getBuildRequests(MacroManager manager) {
        List<BuildOrderEntry> build = new ArrayList<>();
        for (BuildOrderEntry e : buildMilestones) {
            if (manager.getWorkers() >= e.getWorkers()) {
                build.add(e);
            }
        }
        return build;
    }

    public abstract void onBuildingComplete(Units type);

    public abstract void onUnitComplete(Units type);

    public abstract void update(MacroManager manager);


    public abstract boolean shouldAttack();

    public abstract boolean shouldRetreat();
}