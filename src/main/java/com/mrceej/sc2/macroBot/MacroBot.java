package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.ClientError;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.bot.setting.PlayerSettings;
import com.github.ocraft.s2client.protocol.game.Race;
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
    private final Mastermind mastermind;
    @Getter
    private final Overseer overseer;
    @Getter
    private final BuildManager buildManager;
    @Getter
    private final BuildUtils buildUtils;

    public MacroBot(PlayerSettings opponent) {
        super(opponent, Race.ZERG);
        this.debugger = new Debugger(this);
        this.utils = new Utils(this);
        this.overseer = new Overseer(this);
        this.buildManager = new BuildManager(this);
        this.mastermind = new Mastermind(this);
        this.buildUtils = new BuildUtils(this);
    }

    private void init() {
        mastermind.init();
        overseer.init();
        buildManager.init();
        buildUtils.init();
    }

    private void runAI() {
        mastermind.update();
        overseer.update();
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
        overseer.onUnitCreated(unit);
        buildManager.onUnitComplete(unit);
    }

    @Override
    public void onBuildingConstructionComplete(UnitInPool unit) {
        overseer.onBuildingComplete(unit);
        buildManager.onUnitComplete(unit);

    }

    @Override
    public void onUnitIdle(UnitInPool unitInPool) {
        overseer.onUnitIdle(unitInPool);
    }

    @Override
    public void onError(List<ClientError> clientErrors, List<String> protocolErrors) {
        clientErrors.forEach(log::error);
        protocolErrors.forEach(log::error);
    }

    @Override
    public void onUnitEnterVision(UnitInPool unitInPool) {
    }

    @Override
    public void onGameFullStart() {
    }

    @Override
    public void onGameEnd() {
    }

    @Override
    public void onUnitDestroyed(UnitInPool unitInPool) {
        overseer.onUnitDestroyed(unitInPool);
    }

    @Override
    public void onNydusDetected() {
    }

    @Override
    public void onNuclearLaunchDetected() {
    }
}
