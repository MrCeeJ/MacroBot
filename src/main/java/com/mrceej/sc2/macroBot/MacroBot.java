package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.ClientError;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.bot.setting.PlayerSettings;
import com.github.ocraft.s2client.protocol.game.Race;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.mrceej.sc2.CeejBot;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class MacroBot extends CeejBot {

    @Getter
    private final Utils utils;
    @Getter
    private final Debugger debugger;
    @Getter
    private final MacroManager macroManager;
    @Getter
    private final UnitManager unitManager;
    @Getter
    private final BuildManager buildManager;
    @Getter
    private final BuildUtils buildUtils;
    @Getter
    private final ArmyManager armyManager;
    @Getter
    private final Adviser adviser;

    public MacroBot(PlayerSettings opponent) {
        super(opponent, Race.ZERG);
        this.debugger = new Debugger(this);
        this.utils = new Utils(this);
        this.unitManager = new UnitManager(this);
        this.buildManager = new BuildManager(this);
        this.macroManager = new MacroManager(this);
        this.buildUtils = new BuildUtils(this);
        this.armyManager = new ArmyManager(this);
        this.adviser = new Adviser(this);
    }

    private void init() {
        adviser.init();
        macroManager.init();
        unitManager.init();
        buildManager.init();
        buildUtils.init();
        armyManager.init();
    }

    private void runAI() {
        adviser.update();
        macroManager.update();
        unitManager.update();
        armyManager.update();

    }

    @Override
    public void onGameStart() {
        debugger.debugMessage("Hello Starcraft II bots! MacroBot here!");
        init();
    }

    @Override
    public void onStep() {
        debugger.debugMessage("Game Loop step :" + this.observation().getGameLoop());
        runAI();
    }

    @Override
    public void onUnitCreated(UnitInPool unit) {
        unitManager.onUnitCreated(unit);
        buildManager.onUnitComplete(unit);
    }

    @Override
    public void onBuildingConstructionComplete(UnitInPool unit) {
        unitManager.onBuildingComplete(unit);
        buildManager.onUnitComplete(unit);

    }

    @Override
    public void onUnitIdle(UnitInPool unitInPool) {
        unitManager.onUnitIdle(unitInPool);
    }

    @Override
    public void onError(List<ClientError> clientErrors, List<String> protocolErrors) {
        clientErrors.forEach(log::error);
        protocolErrors.forEach(log::error);
    }

    @Override
    public void onUnitEnterVision(UnitInPool unit) {
        if (unit.unit().getAlliance().equals(Alliance.ENEMY)) {
            adviser.enemySpotted(unit);
        }
    }

    @Override
    public void onGameFullStart() {
    }

    @Override
    public void onGameEnd() {
    }

    @Override
    public void onUnitDestroyed(UnitInPool unit) {
        // TODO: Check if this should be based of tags as unit.unit() might not work.
        if (unit.unit().getAlliance().equals(Alliance.ENEMY)) {
            adviser.enemyDestroyed(unit);
        } else {
            unitManager.onUnitDestroyed(unit);
        }
    }

    @Override
    public void onNydusDetected() {
    }

    @Override
    public void onNuclearLaunchDetected() {
    }
}
