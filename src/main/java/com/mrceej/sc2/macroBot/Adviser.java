package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.protocol.game.raw.StartRaw;
import com.github.ocraft.s2client.protocol.response.ResponseGameInfo;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.mrceej.sc2.builds.Plan;
import com.mrceej.sc2.builds.PoolFirstExpand;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Adviser {


    private MacroBot agent;
    private Plan currentPlan;
    private UnitManager unitManager;

    Adviser(MacroBot macroBot) {
        this.agent = macroBot;
    }

    public void init() {
        currentPlan = new PoolFirstExpand(agent);
        unitManager = agent.getUnitManager();
    }

    public void update() {
        // Check if plan needs changing

    }

    public boolean isSafe() {
        return true;
    }

    public Plan getCurrentPlan() {
        return currentPlan;
    }

    public Point2d getAttackTarget() {
        //TODO: Find decent targets

        // Default to start position
        ResponseGameInfo gameInfo = agent.observation().getGameInfo();
        Optional<StartRaw> startRaw = gameInfo.getStartRaw();
        if (startRaw.isPresent()) {
            Set<Point2d> startLocations = new HashSet<>(startRaw.get().getStartLocations());
            startLocations.remove(agent.observation().getStartLocation().toPoint2d());
            if (!startLocations.isEmpty()) {
                return (Point2d) startLocations.toArray()[ThreadLocalRandom.current().nextInt(startLocations.size())];
            }
        }
        return null;
    }

    public Point2d getRetreatTarget() {
        return unitManager.getMain().getBase().unit().getPosition().toPoint2d();
    }
}
