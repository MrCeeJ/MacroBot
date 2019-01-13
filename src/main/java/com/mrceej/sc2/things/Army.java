package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.mrceej.sc2.macroBot.Adviser;
import com.mrceej.sc2.macroBot.MacroBot;
import com.mrceej.sc2.macroBot.MacroManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
public class Army {

    private Adviser adviser;
    private MacroManager macroManager;
    private final MacroBot agent;
    @Getter
    @Setter
    private String STATE = "Defending";

    public Army(MacroBot agent) {
        this.agent = agent;
        this.units = new ArrayList<>();
    }

    List<UnitInPool> units;

    public void add(UnitInPool unit) {
        this.units.add(unit);
    }

    public void update() {
        if (adviser == null) {
            adviser = agent.getAdviser();
        }
        if (macroManager == null) {
            macroManager = agent.getMacroManager();
        }

        if (STATE.equals("Defending")) {
            // Check for attack
            if (adviser.isSafe() && adviser.getCurrentPlan().shouldAttack()) {
                STATE = "Attacking";
                Point2d attackTarget = adviser.getAttackTarget();
                for (UnitInPool unit : units) {
                    agent.actions().unitCommand(unit.unit(), Abilities.ATTACK_ATTACK, attackTarget, false);
                }
            }
            // Check for defence
            // Patrol
        } else if (STATE.equals("Attacking")) {
            if (adviser.getCurrentPlan().shouldRetreat()) {
                STATE = "Defending";
                for (UnitInPool unit : units) {
                    Point2d retreatTarget = adviser.getRetreatTarget();
                    agent.actions().unitCommand(unit.unit(), Abilities.MOVE, retreatTarget, false);
                }
            }
        }
    }
}
