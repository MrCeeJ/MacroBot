package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.mrceej.sc2.macroBot.BuildUtils;
import com.mrceej.sc2.macroBot.MacroBot;
import com.mrceej.sc2.macroBot.Utils;
import lombok.Getter;

import java.util.HashMap;

import static com.github.ocraft.s2client.protocol.data.Units.ZERG_ZERGLING;

public class EnemyUnits {
    private final BuildUtils buildUtils;
    private final Utils utils;
    private HashMap<Tag, EnemyDude> enemyDudes;
    private MacroBot agent;

    @Getter
    private int enemySupply;

    @Getter
    private int enemyArmyValue;

    public EnemyUnits(MacroBot agent) {

        this.agent = agent;
        this.enemyDudes = new HashMap<>();
        this.buildUtils = agent.getBuildUtils();
        this.utils = agent.getUtils();
    }

    public void add(UnitInPool unit) {

        if (!enemyDudes.containsKey(unit.getTag())) {
//            if (unit.unit().getType()
            enemyDudes.put(unit.getTag(), new EnemyDude(agent, unit));
            Units type = (Units)unit.unit().getType();
            if (type.equals(ZERG_ZERGLING)) {
                enemySupply += 1;
                enemyArmyValue += 25;
            } else {
                enemySupply += 2;
                enemyArmyValue += buildUtils.queryMineralCost(type);
                enemyArmyValue += buildUtils.queryGasCost(type) * 2;
            }
        }
    }

    public void remove(UnitInPool unit) {
        enemyDudes.remove(unit.getTag());
        Units type = (Units)unit.unit().getType();

        if (type.equals(ZERG_ZERGLING)) {
            enemySupply -= 1;
            enemyArmyValue -= 25;

        } else {
            enemySupply -= 2;
            enemyArmyValue -= buildUtils.queryMineralCost(type);
            enemyArmyValue -= buildUtils.queryGasCost(type) * 2;
        }
    }

    public UnitInPool getNearestBuildingTo(Point2d point) {
        return enemyDudes.values().stream()
                .map(EnemyDude::getUnitInPool)
                // TODO: filter for buildings
//                .filter(unitInPool -> unitInPool.unit().getType())
                .min(utils.getLinearDistanceComparatorForUnitInPool(point))
                .orElse(null);
    }
}