package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.ClientError;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.bot.setting.PlayerSettings;
import com.github.ocraft.s2client.protocol.game.Race;
import com.mrceej.sc2.CeejBot;
import com.mrceej.sc2.builds.Build;
import com.mrceej.sc2.builds.PureMacro;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class MacroBot extends CeejBot {

    private Logger logger;
    private Intel intel;
    private Build build;
    private Overseer overseer;
    private Utils utils;
    private BuildManager buildManager;

    public MacroBot(PlayerSettings opponent) {
        super(opponent, Race.ZERG);
    }

    private void init() {
        this.logger = new Logger();
        this.intel = new Intel();
        this.utils = new Utils(this);
        this.buildManager = new BuildManager(this, utils);
        this.build = new PureMacro(this, buildManager);
        this.overseer = new Overseer(this, intel, build, utils);
    }

    private void runAI() {
        intel.update();
        build.update();
        overseer.update();
    }

    @Override
    public void onGameStart() {
        log.info("Hello world of Starcraft II bots! RetBot here!");
        init();
    }

    @Override
    public void onStep() {
        logger.debug();
        runAI();
    }

    @Override
    public void onUnitCreated(UnitInPool unit) {
        overseer.onUnitCreated(unit);
    }

    @Override
    public void onBuildingConstructionComplete(UnitInPool unit) {
        overseer.onBuildingComplete(unit);

    }

    @Override
    public void onUnitIdle(UnitInPool unitInPool) {

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
    }

    @Override
    public void onNydusDetected() {
    }

    @Override
    public void onNuclearLaunchDetected() {
    }
}
