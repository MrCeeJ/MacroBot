package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.mrceej.sc2.macroBot.Adviser;
import com.mrceej.sc2.macroBot.BuildUtils;
import com.mrceej.sc2.macroBot.MacroBot;
import com.mrceej.sc2.macroBot.MacroManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

import static com.github.ocraft.s2client.protocol.data.Abilities.ATTACK_ATTACK;
import static com.github.ocraft.s2client.protocol.data.Abilities.MOVE;
import static com.github.ocraft.s2client.protocol.data.Units.ZERG_ZERGLING;

@EqualsAndHashCode
public class Army {

    private final BuildUtils buildUtils;
    private Adviser adviser;
    private MacroManager macroManager;
    private final MacroBot agent;
    @Getter
    @Setter
    private String STATE = "Defending";
    private Point2d currentRetreatTarget;
    private Point2d currentAttackTarget;
    private HashMap<Tag, Dude> dudes;

    @Getter
    private int armySupply;

    @Getter
    private int armyValue;

    public Army(MacroBot agent) {
        this.agent = agent;
        this.dudes = new HashMap<>();
        this.buildUtils = agent.getBuildUtils();
    }


    public void add(UnitInPool unit) {
        Dude dude = new Dude(agent, unit);
        this.dudes.put(dude.getTag(), dude);
        Units type = (Units) unit.unit().getType();
        if (type.equals(ZERG_ZERGLING)) {
            armySupply += 1;
            armyValue += 25;
        } else {
            armySupply += 2;
            armyValue += buildUtils.queryMineralCost(type);
            armyValue += buildUtils.queryGasCost(type) * 2;
        }
        if (STATE.equals("Attacking")) {
            dude.giveCommand(new Command(ATTACK_ATTACK, currentAttackTarget));
        } else if (STATE.equals("Defending")) {
            dude.giveCommand(new Command(MOVE, currentRetreatTarget));
        }
    }

    public void remove(UnitInPool unit) {
        if (dudes.containsKey(unit.getTag())) {
            dudes.remove(unit.getTag());
            Units type = (Units) unit.unit().getType();
            if (type.equals(ZERG_ZERGLING)) {
                armySupply -= 1;
                armyValue -= 25;
            } else {
                armySupply -= 2;
                armyValue -= buildUtils.queryMineralCost(type);
                armyValue -= buildUtils.queryGasCost(type) * 2;
            }
        }
    }

    void giveOrder(Abilities order, Point2d target) {
        for (Dude dude : dudes.values()) {
            dude.giveCommand(new Command(order, target));
        }
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
                currentAttackTarget = adviser.getAttackTarget();
                giveOrder(ATTACK_ATTACK, currentAttackTarget);
            }
            // Check for defence
            // Patrol
        } else if (STATE.equals("Attacking")) {
            if (adviser.getCurrentPlan().shouldRetreat()) {
                STATE = "Defending";
                currentRetreatTarget = adviser.getRetreatTarget();
                giveOrder(MOVE, currentRetreatTarget);
            } else {
                Point2d newTarget = adviser.getAttackTarget();
                if (newTarget != currentAttackTarget) {
                    currentAttackTarget = newTarget;
                    giveOrder(ATTACK_ATTACK, currentRetreatTarget);
                }
            }

        }
    }
}
