package com.mrceej.sc2.builds;

import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.macroBot.BuildingRequest;
import com.mrceej.sc2.macroBot.MacroManager;

import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.*;
import static com.github.ocraft.s2client.protocol.data.Units.ZERG_LAIR;

public class PoolFirstExpand extends Plan {

    private String STATE = "";

    public PoolFirstExpand() {
        buildMilestones = List.of(
                new BuildOrderEntry(14, ZERG_SPAWNING_POOL, true),
                new BuildOrderEntry(20, ZERG_ROACH_WARREN, true),
                new BuildOrderEntry(25, ZERG_EVOLUTION_CHAMBER, true),
                new BuildOrderEntry(30, ZERG_LAIR, true)
        );
    }

    @Override
    public void update(MacroManager manager) {
        if (STATE.equals("POOL_COMPLETE")) {
            manager.queueRequest(new BuildingRequest(ZERG_ZERGLING, 6, false));
            STATE = "LINGS_REQUESTED";
        } else if (STATE.equals("ROACH_WARREN_COMPLETE")) {
            manager.queueRequest(new BuildingRequest(ZERG_ROACH, 8, false));
            STATE = "ROACHES_REQUESTED";
        }
    }

    @Override
    public void onBuildingComplete(Units type) {
        switch (type) {
            case ZERG_SPAWNING_POOL:
                this.STATE = "POOL_COMPLETE";
                break;
            case ZERG_ROACH_WARREN:
                this.STATE = "ROACH_WARREN_COMPLETE";
                break;
        }
    }

    @Override
    public void onUnitComplete(Units type) {
        switch (type) {
            case ZERG_ZERGLING:
                break;
            case ZERG_ROACH:
                checkRoachTimingAttack();
                break;
        }
    }


    private void checkRoachTimingAttack() {

    }
}
