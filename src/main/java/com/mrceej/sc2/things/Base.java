package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.mrceej.sc2.CeejBot;
import com.mrceej.sc2.macroBot.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
public class Base {
    private final CeejBot agent;
    private final Utils utils;
    @Getter
    private UnitInPool base;
    @Getter
    private final Tag tag;
    final List<UnitInPool> minerals;
    final List<UnitInPool> gases;
    final List<UnitInPool> extractors;
    final List<UnitInPool> mineralWorkers;
    final List<UnitInPool> queens;

    public Base(CeejBot agent, Utils utils, UnitInPool base) {
        this.agent = agent;
        this.utils = utils;
        this.base = base;
        this.tag = base.getTag();
        this.minerals = findMineralPatches();
        this.gases = findGases();
        this.extractors = new ArrayList<>();
        this.mineralWorkers = new ArrayList<>();
        this.queens = new ArrayList<>();
    }

    public void allocateWorker(UnitInPool unit) {
        this.mineralWorkers.add(unit);

        for (UnitInPool mineral : minerals) {
            if (mineral.unit().getAssignedHarvesters().isPresent()) {
                if (mineral.unit().getAssignedHarvesters().get() < 2) {
                    agent.actions().unitCommand(unit.unit(), Abilities.SMART, mineral.unit(), false);
                    return;
                }
            }
        }
        Unit target = minerals.get(0).unit();
        agent.actions().unitCommand(unit.unit(), Abilities.SMART, target, false);
    }

    public UnitInPool getWorker() {
        UnitInPool worker;
        if (mineralWorkers.size() > 0) {
            worker = mineralWorkers.remove(0);
            return worker;
        }
        return null;
    }

    public int countMineralWorkers() {
        return this.mineralWorkers.size();
    }


    private List<UnitInPool> findGases() {
        return agent.observation().getUnits(Alliance.NEUTRAL, unitInPool -> unitInPool.unit().getType().equals(Units.NEUTRAL_VESPENE_GEYSER)
                && unitInPool.unit().getPosition().toPoint2d().distance(base.unit().getPosition().toPoint2d()) < 10);
    }

    private List<UnitInPool> findMineralPatches() {
        return agent.observation().getUnits(Alliance.NEUTRAL, unitInPool -> unitInPool.unit().getType().equals(Units.NEUTRAL_MINERAL_FIELD)
                && unitInPool.unit().getPosition().toPoint2d().distance(base.unit().getPosition().toPoint2d()) < 10);
    }

    public void updateUnit(UnitInPool unit) {
        this.base = unit;
    }

    public void removeWorker(UnitInPool unitInPool) {
        mineralWorkers.remove(unitInPool);
    }
}
