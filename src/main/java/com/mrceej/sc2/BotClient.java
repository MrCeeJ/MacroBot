package com.mrceej.sc2;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.S2Coordinator;
import com.github.ocraft.s2client.bot.setting.PlayerSettings;
import com.github.ocraft.s2client.protocol.game.BattlenetMap;
import com.github.ocraft.s2client.protocol.game.Difficulty;
import com.github.ocraft.s2client.protocol.game.Race;
import com.mrceej.sc2.macroBot.MacroBot;

class BotClient {

    private static final Race preferredOpponentRace = Race.ZERG;
    private static final Difficulty preferredOpponentDifficulty = Difficulty.MEDIUM_HARD;
    private static final Race PLAYER_RACE = Race.ZERG;

    public static void main(String[] args) {

        PlayerSettings opponent = getComputerOpponent();
        S2Agent playerBot = getPlayerBot(opponent);
        Race playerRace = getPlayerRace();

        S2Coordinator s2Coordinator = S2Coordinator.setup()
                .loadSettings(args)
                .setParticipants(S2Coordinator.createParticipant(playerRace, playerBot),opponent)
                .launchStarcraft()
                .startGame(BattlenetMap.of("Cloud Kingdom LE"));

        //noinspection StatementWithEmptyBody
        while (s2Coordinator.update()) {
        }
        s2Coordinator.quit();
    }

    private static Race getPlayerRace() {
        return PLAYER_RACE;
    }

    private static S2Agent getPlayerBot(PlayerSettings opponent) {
        if(getPlayerRace().equals(Race.ZERG)) {
            return new MacroBot(opponent);
        }
        return new MacroBot(opponent);
    }

    private static PlayerSettings getComputerOpponent() {
        return S2Coordinator.createComputer(preferredOpponentRace, preferredOpponentDifficulty);
    }
}
