package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.mrceej.sc2.CeejBot;

import java.util.List;
import java.util.Optional;

import static com.github.ocraft.s2client.protocol.data.Units.ZERG_DRONE;

public class BuildManager {

    private CeejBot agent;
    private Utils utils;

    public BuildManager(CeejBot agent, Utils utils) {
        this.agent = agent;
        this.utils = utils;
    }

    public void build(UnitType unit) {
        Units item = (Units)unit;
        switch (item) {
            case ZERG_DRONE:
            case ZERG_OVERLORD:
                buildUnit(unit);
                break;
            case ZERG_HATCHERY:
                buildHatchery();
                break;
            case ZERG_SPAWNING_POOL:
                buildBuilding(unit);
                break;

        }
    }
    private void buildHatchery() {
        Point2d location = utils.getNearestExpansionLocationTo(agent.observation().getStartLocation().toPoint2d());
        Optional<UnitInPool> unitOptional = getNearestFreeDrone(location);
        unitOptional.ifPresent(unit -> agent.actions().unitCommand(unit.unit(), Abilities.BUILD_HATCHERY, location, false));
    }

    Optional<UnitInPool> getNearestFreeDrone(Point2d location) {
        return agent.observation().getUnits(Alliance.SELF, UnitInPool.isUnit(ZERG_DRONE)).stream()
                .filter(unit -> unit.unit().getOrders().size() == 1)
                .filter(unit -> unit.unit().getOrders().get(0).getAbility().equals(Abilities.HARVEST_GATHER))
                .min(utils.getLinearDistanceComparatorForUnit(location));
    }

    private void buildBuilding(UnitType unit) {
        List<UnitInPool> drones = utils.getAllUnitsOfType(Units.ZERG_DRONE);
        if (drones.size() > 0) {
            agent.actions().unitCommand(drones.get(0).unit(), getAbilityToMakeUnit(unit), false);
        }
    }

    public void buildUnit(UnitType unit) {
        List<UnitInPool> larvae = utils.getAllUnitsOfType(Units.ZERG_LARVA);
        if (larvae.size() > 0) {
            agent.actions().unitCommand(larvae.get(0).unit(), getAbilityToMakeUnit(unit), false);
        }
    }

    private Ability getAbilityToMakeUnit(UnitType unitType) {
        return agent.observation().getUnitTypeData(false).get(unitType).getAbility().orElse(Abilities.INVALID);
    }

    public boolean buildingUnit(UnitType units) {
        //TODO: determine what egg turns into
//        return agent.observation().getUnits(Alliance.SELF, u -> u.unit().getType().equals(ZERG_EGG)).stream()
//                .anyMatch(e -> e.unit().);
        return false;
    }
}
