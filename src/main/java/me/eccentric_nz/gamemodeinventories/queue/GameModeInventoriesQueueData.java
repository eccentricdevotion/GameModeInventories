/*
 *  Copyright 2015 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories.queue;

/**
 *
 * @author eccentric_nz
 */
public class GameModeInventoriesQueueData {

    private final String worldchunk;
    private final String location;

    public GameModeInventoriesQueueData(String worldchunk, String location) {
        this.worldchunk = worldchunk;
        this.location = location;
    }

    public String getWorldChunk() {
        return worldchunk;
    }

    public String getLocation() {
        return location;
    }
}
