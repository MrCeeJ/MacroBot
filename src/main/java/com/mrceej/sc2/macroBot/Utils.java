package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.mrceej.sc2.CeejBot;

import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.*;

public class Utils {

    CeejBot agent;

    public Utils(CeejBot agent) {
        this.agent = agent;
    }

    public List<UnitInPool> getDrones() {
        return getAllUnitsOfType(Units.ZERG_DRONE);
    }

    public List<UnitInPool> getBases() {
        List<UnitInPool> hatcheries = getAllUnitsOfType(ZERG_HATCHERY);
        hatcheries.addAll(getAllUnitsOfType(ZERG_LAIR));
        hatcheries.addAll(getAllUnitsOfType(ZERG_HIVE));
        return hatcheries;
    }

    public List<UnitInPool> getAllUnitsOfType(Units unit) {
        return agent.observation().getUnits(Alliance.SELF, (unitInPool -> unitInPool.unit().getType().equals(unit)));
    }

}
