package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Buffs;
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
import java.util.Random;

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
    private Random random = new Random();

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
        agent.actions().unitCommand(base.unit(), Abilities.RALLY_WORKERS, utils.findNearestMineralPatch(base.unit().getPosition().toPoint2d()).unit(), false);
    }

    public void update() {
        if (hasQueen() && needsInject()) {
            for (UnitInPool queen : queens) {
                if (queen.unit().getEnergy().orElse(0f) >= 25) {
                    agent.actions().unitCommand(queen.unit(), Abilities.EFFECT_INJECT_LARVA, this.base.unit(), false);
                    break;
                }
            }
        }
    }

    private boolean needsInject() {
        return !this.base.unit().getBuffs().contains(Buffs.QUEEN_SPAWN_LARVA_TIMER);
    }


    public boolean hasQueen() {
        return queens.size() > 0;
    }

    public void allocateQueen(UnitInPool queen) {
        this.queens.add(queen);
    }

    public void removeQueen(UnitInPool queen) {
        this.queens.remove(queen);
    }

    public void allocateWorker(UnitInPool unit) {
        this.mineralWorkers.add(unit);

        //TODO: Not picking up assigned workers
        for (UnitInPool mineral : minerals) {
            if (mineral.unit().getAssignedHarvesters().isPresent()) {
                if (mineral.unit().getAssignedHarvesters().get() < 2) {
                    agent.actions().unitCommand(unit.unit(), Abilities.SMART, mineral.unit(), false);
                    return;
                }
            }
        }
        // Backup random selection
        int choice = random.nextInt(minerals.size());
        Unit target = minerals.get(choice).unit();
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
