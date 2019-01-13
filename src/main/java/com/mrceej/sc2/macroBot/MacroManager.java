package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.builds.Build;
import com.mrceej.sc2.builds.BuildOrderEntry;
import com.mrceej.sc2.builds.Plan;
import com.mrceej.sc2.builds.PureMacro;
import com.mrceej.sc2.things.Base;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.*;

@Log4j2
public
class MacroManager {
    private BuildManager buildManager;
    private Build defaultBuild;
    private Utils utils;
    private final MacroBot agent;
    private LinkedList<BuildRequest> requests;
    private BuildUtils buildUtils;
    private int minerals;
    private int gas;
    private int supplyUsed;
    private int supplyCap;
    private int droneCount;
    private Collection<Base> bases;
    private UnitManager unitManager;
    @Getter
    private int workers;
    private Adviser adviser;
    private Plan plan;


    public MacroManager(MacroBot agent) {
        this.agent = agent;
    }

    public void init() {
        this.defaultBuild = new PureMacro(agent);
        this.utils = agent.getUtils();
        this.buildManager = agent.getBuildManager();
        this.requests = new LinkedList<>();
        this.buildUtils = agent.getBuildUtils();
        this.unitManager = agent.getUnitManager();
        this.adviser = agent.getAdviser();
        this.plan = adviser.getCurrentPlan();
    }

    public void update() {
        updateData();
        plan.update(this);
        // move to plans
        checkGassesByBase();
        queuePlanRequests();
        updateBuilds();
    }


    private void checkUnitRequests() {

    }

    private void queuePlanRequests() {
        List<BuildOrderEntry> build = plan.getBuildRequests(this);
        for (BuildOrderEntry entry : build) {
            if (entry.isUnit) {
                // TODO: Double check this test, why not queue them all?
                if (utils.getAllUnitsOfType(entry.getUnit()).size() == 0) {
                    queueRequest(new BuildRequest(entry.getUnit(), entry.isUnique()));
                }
            }

        }
    }
    private void checkGassesByBase() {
        for (Base base : bases) {
            if (base.countMineralWorkers() > 16 &&
                    base.countGasWorkers() == 3 &&
                    base.getExtractors().size() < 2 &&
                    !buildManager.buildingUnit(ZERG_EXTRACTOR)) {
                queueRequest(new BuildRequest(ZERG_EXTRACTOR, base, false));
            } else if (base.countMineralWorkers() > 14 &&
                    base.getExtractors().size() < 1) {
                queueRequest(new BuildRequest(ZERG_EXTRACTOR, base, false));
            }
        }
    }


    private void checkGassesByIncome() {
        float mineralIncome = agent.observation().getScore().getDetails().getCollectionRateMinerals() + 1;
        if (workers > 16 && mineralIncome > 400) {
            float gasIncome = agent.observation().getScore().getDetails().getCollectionRateVespene() + 1;
            float desiredGas = mineralIncome / 3.5f;
            if (desiredGas > gasIncome && !buildManager.buildingUnit(ZERG_EXTRACTOR)) {
                for (Base base : bases) {
                    if (base.getExtractors().size() < 2) {
                        queueRequest(new BuildRequest(ZERG_EXTRACTOR, base, false));
                        break;
                    }
                }
            }
        }
    }


    void onBuildingComplete(Units type) {
        plan.onBuildingComplete(type);
    }

    void onUnitComplete(Units type) {
        plan.onUnitComplete(type);
    }

    private void updateData() {
        this.minerals = agent.observation().getMinerals();
        this.gas = agent.observation().getVespene();
        this.supplyUsed = agent.observation().getFoodUsed();
        this.supplyCap = agent.observation().getFoodCap();
        this.droneCount = utils.getDrones().size();
        this.bases = unitManager.getBases().values();
        this.workers = utils.getAllUnitsOfType(Units.ZERG_DRONE).size();
        this.plan = adviser.getCurrentPlan();

    }


    private void updateBuilds() {
        if (!handleRequests()) {
            defaultBuild.build();
        }
    }



    void requestQueen(Base base) {
        if (buildUtils.checkCanMakeUnit(ZERG_QUEEN, minerals, gas)) {
            queueRequest(new BuildRequest(ZERG_QUEEN, base, false));
        }
    }

    public void queueRequest(BuildRequest request) {
        if (!requests.contains(request)) {
            if (request.isUnique() && buildManager.buildingUnit(request.type)) {
                log.info("Already building a :" + request.type);
            } else {
                if (request.type == ZERG_LAIR || request.type == ZERG_HIVE) {
                    request.setBase(unitManager.getMain());
                }
                log.info("Adding request for a :" + request.type);
                this.requests.add(request);
            }
        } else {
            log.info("Already have request for a :" + request.type);
        }
    }


    private boolean handleRequests() {
        if (requests.size() == 0) {
            return false;
        } else {
            BuildRequest handled = null;
            boolean success = false;
            for (BuildRequest request : requests) {
                if (buildUtils.checkCanMakeUnit(request.type, minerals, gas)) {
                    handled = request;
                    success = buildManager.incrementalHandleRequest(handled);
                    this.minerals -= buildUtils.getMineralCost(request.type);
                    this.gas -= buildUtils.getGasCost(request.type);
                    break;
                }
            }
            if (handled != null && success) {
                if (handled.count == 0) {
                    requests.remove(handled);
                    log.info("Finished request for :" + handled.type + " requests queue is now " + (requests.size() == 0 ? "empty" : requests.size() + " item(s)"));
                } else {
                    log.info("Processed request for a :" + handled.type + ", " + handled.count + " more to go!");
                }
            }
            return success;
        }
    }


}
