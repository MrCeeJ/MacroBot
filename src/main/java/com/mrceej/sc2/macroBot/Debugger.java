package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.debug.Color;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;
import lombok.extern.log4j.Log4j2;

@Log4j2
class Debugger {
    private static final boolean DEBUG_ENABLED = true;
    private static final Point2d DEFAULT_LOCATION = Point2d.of(0.5f, 0.5f);
    private static final Color DEFAULT_COLOUR = Color.GREEN;
    private static final int DEFAULT_SIZE = 5;
    private MacroBot macroBot;

    public Debugger(MacroBot macroBot) {

        this.macroBot = macroBot;
    }

    public void debugMessage(String message) {
        if (DEBUG_ENABLED) {
            macroBot.debug().debugTextOut(message, DEFAULT_LOCATION, DEFAULT_COLOUR, DEFAULT_SIZE);

        }
    }

    public void debugMessage(String message, UnitInPool unit) {
        if (DEBUG_ENABLED) {
            debugMessage(message, unit.unit());
        }
    }

    public void debugMessage(String message, Unit unit) {
        if (DEBUG_ENABLED) {
            debugMessage(message, unit.getPosition().toPoint2d());
        }
    }

    public void debugMessage(String message, Point2d location) {
        if (DEBUG_ENABLED) {
            macroBot.debug().debugTextOut(message, location, DEFAULT_COLOUR, DEFAULT_SIZE);
        }
    }
}
