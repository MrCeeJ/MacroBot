package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.builds.Build;
import com.mrceej.sc2.builds.Plan;
import com.mrceej.sc2.builds.PureMacro;
import com.mrceej.sc2.things.Base;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;
import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.ZERG_QUEEN;

@Log4j2
class Intel {
    private BuildManager buildManager;
    private Build currentBuild;
    private Utils utils;
    private final MacroBot agent;
    private LinkedList<BuildingRequest> requests;
    private BuildUtils buildUtils;
    private int minerals;
    private int gas;
    private int supplyUsed;
    private int supplyCap;
    private int droneCount;
    private List<UnitInPool> bases;


    public Intel(MacroBot agent) {
        this.agent = agent;
    }

    public void init() {
        this.currentBuild = new PureMacro(agent);
        this.utils = agent.getUtils();
        this.buildManager = agent.getBuildManager();
        this.requests = new LinkedList<>();
        this.buildUtils = agent.getBuildUtils();
    }

    public void update() {
        updateData();
        currentBuild.update();
        checkTechRequests();
        checkUnitRequests();
        updateBuilds();
    }

    private void updateData() {
        this.minerals = agent.observation().getMinerals();
        this.gas = agent.observation().getVespene();
        this.supplyUsed = agent.observation().getFoodUsed();
        this.supplyCap = agent.observation().getFoodCap();
        this.droneCount = utils.getDrones().size();
        this.bases = utils.getBases();
    }

    private void checkUnitRequests() {

    }

    private void checkTechRequests() {
        int workers = utils.getAllUnitsOfType(Units.ZERG_DRONE).size();
        List<Units> build = Plan.getCurrentState(workers);
        for (Units type : build) {
            if (utils.getAllUnitsOfType(type).size() == 0) {
                queueRequest(new BuildingRequest(type));
            }
        }
    }

    private void updateBuilds() {
        if (!handleRequests()) {
            currentBuild.build();
        }
    }

    private boolean handleRequests() {
        if (requests.size() == 0) {
            return false;
        } else {
            BuildingRequest handled = null;
            boolean success = false;
            for (BuildingRequest request : requests) {
                if (buildUtils.checkCanMakeUnit(request.type, minerals, gas)) {
                    handled = request;
                    success = buildManager.handleRequest(handled);
                    this.minerals -= buildUtils.getMineralCost(request.type);
                    this.gas -= buildUtils.getGasCost(request.type);
                    break;
                }
            }
            if (handled != null && success) {
                requests.remove(handled);
                log.info("Processed request for a :" + handled.type);
                return true;
            }
            return false;
        }
    }

    void requestQueen(Base base) {
        if (buildUtils.checkCanMakeUnit(ZERG_QUEEN, minerals, gas)) {
            queueRequest(new BuildingRequest(ZERG_QUEEN, base));
        }
    }

    private void queueRequest(BuildingRequest request) {
        if (!requests.contains(request)) {
            if (buildManager.buildingUnit(request.type)) {
                log.info("Already have request for a :" + request.type);
            } else {
                log.info("Adding request for a :" + request.type);
                this.requests.add(request);
            }
        } else {
            log.info("Already have request for a :" + request.type);
        }
    }
}
