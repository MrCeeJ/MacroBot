package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.UnitTypeData;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.mrceej.sc2.macroBot.BuildUtils;
import com.mrceej.sc2.macroBot.MacroBot;
import com.mrceej.sc2.macroBot.Utils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static com.github.ocraft.s2client.protocol.data.UnitAttribute.STRUCTURE;
import static com.github.ocraft.s2client.protocol.data.Units.ZERG_ZERGLING;

public class EnemyUnits {
    private final BuildUtils buildUtils;
    private final Utils utils;
    private final HashMap<Tag, EnemyDude> enemyDudes;
    private final MacroBot agent;
    private Map<UnitType, UnitTypeData> data;

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
            Units type = (Units) unit.unit().getType();
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
        Units type = (Units) unit.unit().getType();

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
        this.data = agent.observation().getUnitTypeData(false);
        return enemyDudes.values().stream()
                .map(EnemyDude::getUnitInPool)
                .filter(unitInPool -> data.get(unitInPool.unit().getType()).getAttributes().contains(STRUCTURE))
                .min(utils.getLinearDistanceComparatorForUnitInPool(point))
                .orElse(null);
    }
}
