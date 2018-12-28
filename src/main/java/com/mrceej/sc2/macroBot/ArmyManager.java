package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.things.Army;

import java.util.List;

public class ArmyManager {

    public ArmyManager() {
        this.currentArmy = new Army();
    }

    private Army currentArmy;

    public void addUnit(UnitInPool unit) {

        // check which army needed it
        // handle requests
        // allocate unit

        this.currentArmy.add(unit);
    }

    public void requestArmy(List<Units> composition) {

    }

}
