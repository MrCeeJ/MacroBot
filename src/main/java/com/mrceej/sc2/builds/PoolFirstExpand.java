package com.mrceej.sc2.builds;

import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.macroBot.BuildRequest;
import com.mrceej.sc2.macroBot.MacroBot;
import com.mrceej.sc2.macroBot.MacroManager;
import com.mrceej.sc2.macroBot.Utils;

import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.*;

public class PoolFirstExpand extends Plan {

    private final Utils utils;
    private String STATE = "";
    private boolean roachTimingAttackReady = false;

    public PoolFirstExpand(MacroBot agent) {
        super(agent);
        this.utils = agent.getUtils();
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
            manager.queueRequest(new BuildRequest(ZERG_ZERGLING, 6, false));
            STATE = "LINGS_REQUESTED";
        } else if (STATE.equals("ROACH_WARREN_COMPLETE")) {
            manager.queueRequest(new BuildRequest(ZERG_ROACH, 8, false));
            STATE = "ROACHES_REQUESTED";
        } else if (STATE.equals("LAIR_COMPLETE")) {
            manager.queueRequest(new BuildRequest(ZERG_ROACH, 16, false));
            STATE = "ROACHES_REQUESTED_2";
        }

    }

    @Override
    public boolean shouldAttack() {
        if (STATE.equals("ROACHES_REQUESTED")) {
            return utils.getAllUnitsOfType(ZERG_ROACH).size() > 7;
        } else if (STATE.equals("ROACHES_REQUESTED_2")) {
            return utils.getAllUnitsOfType(ZERG_ROACH).size() > 15;
        }
        return false;
    }

    @Override
    public boolean shouldRetreat() {
        if (utils.getAllUnitsOfType(ZERG_ROACH).size() < 3) {
            if (STATE.equals("ROACHES_REQUESTED")){
                STATE="ROACH_WARREN_COMPLETE";
            } else if (STATE.equals("ROACHES_REQUESTED_2")){
                STATE="LAIR_COMPLETE";
            }
            return true;
        }
        return false;
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
            case ZERG_LAIR:
                this.STATE = "LAIR_COMPLETE";
        }
    }

    @Override
    public void onUnitComplete(Units type) {
        switch (type) {
            case ZERG_ZERGLING:
                break;
            case ZERG_ROACH:
                break;
        }
    }

}
