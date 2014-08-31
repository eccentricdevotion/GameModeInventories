/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories;

import me.botsko.prism.actionlibs.RecordingQueue;
import me.botsko.prism.actions.BlockAction;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesPrismHandler {

    public static void log(Location loc, Block block, String name) {
        BlockAction ba = new BlockAction();
        ba.setLoc(loc);
        ba.setBlock(block);
        ba.setActionType("block-break");
        ba.setPlayerName(name);
        RecordingQueue.addToQueue(ba);
    }
}
