package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.mrceej.sc2.things.*;

import java.util.List;
import java.util.Set;

public class ArmyManager {

    private final Utils utils;
    private final QueenArmy queens;
    private Army mainArmy;
    private ScoutArmy groundScouts;
    private MacroBot agent;
    private List<UnitInPool> towers;
    private Set<Point2d> enemyBases;
    private Set<Point2d> enemyNaturals;

    ArmyManager(MacroBot agent) {
        this.agent = agent;
        this.mainArmy = new Army(agent);
        this.utils = agent.getUtils();
        this.groundScouts = new ScoutArmy(agent);
        this.queens = new QueenArmy(agent);
    }


    public void init() {
        this.towers = agent.observation().getUnits(Alliance.NEUTRAL, unitInPool -> unitInPool.unit().getType().equals(Units.NEUTRAL_XELNAGA_TOWER));
        this.enemyBases = utils.findEnemyStartLocation();
        this.enemyNaturals = utils.getNaturalsForBases(this.enemyBases);
        groundScouts.setTargets(enemyBases, enemyNaturals, towers);
    }

    public void update() {
        this.groundScouts.update();
        this.mainArmy.update();
    }

    void addUnit(UnitInPool unit) {
        Units type = (Units) unit.unit().getType();
        switch (type) {
            case ZERG_ZERGLING:
                if (needsGroundScout()) {
                    allocateGroundScout(unit);
                } else {
                    allocateArmy(unit);
                }
                break;
            case ZERG_QUEEN:
                allocateQueen(unit);
                break;
            case ZERG_ROACH:
            case ZERG_HYDRALISK:
            case ZERG_MUTALISK:
            case ZERG_ULTRALISK:
            case ZERG_CORRUPTOR:
            case ZERG_BROODLORD:
            case ZERG_RAVAGER:
            case ZERG_LURKER_MP:
            default:
                allocateArmy(unit);
                break;
        }
        // check which army needed it
        // handle requests
        // allocate unit

        this.mainArmy.add(unit);
    }

    private void allocateQueen(UnitInPool unit) {
        this.queens.add(new Queen(agent, unit));
    }

    private void allocateGroundScout(UnitInPool unit) {
        this.groundScouts.add(new Scout(agent, unit));
    }

    private void allocateArmy(UnitInPool unit) {

    }

    private boolean needsGroundScout() {
        return enemyBases.size() + enemyNaturals.size() > groundScouts.size();
    }

    public void requestArmy(List<Units> composition) {

    }

    public void onUnitDestroyed(UnitInPool unitInPool) {
        groundScouts.onUnitDestroyed(unitInPool);
        mainArmy.remove(unitInPool);
    }
}
