package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.ocraft.s2client.protocol.data.Units.*;

@Log4j2
public class BuildManager {

    private final MacroBot agent;
    private Utils utils;
    private Overseer overseer;
    private final Map<UnitType, Integer> buildingCounts;

    public BuildManager(MacroBot agent) {
        this.agent = agent;
        this.buildingCounts = new HashMap<>();
    }
    public void init() {
        this.utils = agent.getUtils();
        this.overseer = agent.getOverseer();
    }

    public void build(UnitType unit) {
        Units item = (Units) unit;
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
        if (buildingCounts.containsKey(ZERG_HATCHERY) && buildingCounts.get(ZERG_HATCHERY) > 0) {
            log.info("Not placing hatchery, building " + buildingCounts.get(ZERG_HATCHERY) + " already.");
        } else {
            Point2d location = utils.getNearestExpansionLocationTo(agent.observation().getStartLocation().toPoint2d());
            Optional<UnitInPool> unitOptional = getNearestFreeDrone(location);
            if (unitOptional.isEmpty()) {
                log.info("Insufficient free workers");
            } else if (agent.observation().getMinerals() < 250) {
                log.info("Insufficient minerals :" + agent.observation().getMinerals());
            } else {
                UnitInPool unit = unitOptional.get();
                log.info("Drone "+unit.getTag()+" placing hatchery at :" + location);
                overseer.removeDroneFromBase(unit);
                agent.actions().unitCommand(unit.unit(), Abilities.MOVE, location, false);
                agent.actions().unitCommand(unit.unit(), Abilities.BUILD_HATCHERY, location, true);
                buildingCounts.put(ZERG_HATCHERY, 1);
            }
        }
    }

    private Optional<UnitInPool> getNearestFreeDrone(Point2d location) {
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

    private void buildUnit(UnitType unit) {
        List<UnitInPool> larvae = utils.getAllUnitsOfType(Units.ZERG_LARVA);
        if (larvae.size() > 0) {
            agent.actions().unitCommand(larvae.get(0).unit(), getAbilityToMakeUnit(unit), false);
        }
    }

    private Ability getAbilityToMakeUnit(UnitType unitType) {
        return agent.observation().getUnitTypeData(false).get(unitType).getAbility().orElse(Abilities.INVALID);
    }

    public boolean buildingUnit(UnitType units) {
        Ability ability = getAbilityToMakeUnit(units);
        return agent.observation().getUnits(Alliance.SELF, u -> u.unit().getType().equals(ZERG_EGG)).stream()
                .anyMatch(e -> e.unit().getOrders().stream().anyMatch(order -> order.getAbility().equals(ability)));
    }

    public void onBuildingComplete(UnitInPool unit) {
        UnitType type = unit.unit().getType();
        if (buildingCounts.containsKey(type)) {
            int number = buildingCounts.get(type);
            buildingCounts.put(type, number - 1);
        } else {
            buildingCounts.put(type, 0);
        }

    }


}
