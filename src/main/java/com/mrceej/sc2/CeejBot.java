package com.mrceej.sc2;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.setting.PlayerSettings;
import com.github.ocraft.s2client.protocol.game.Race;
import lombok.Getter;

public class CeejBot extends S2Agent {

    @Getter
    final Race playerRace;
    @Getter
    final Race opponentRace;
    @Getter
    final String opponentName;


    public CeejBot(PlayerSettings opponent, Race playerRace) {
        this.opponentName = opponent.getPlayerName()!=null?opponent.getPlayerName() : "AI-"+opponent.getDifficulty().name();
        this.opponentRace = opponent.getRace();
        this.playerRace = playerRace;
    }
}
