package me.eccentric_nz.gamemodeinventories.database;

import java.util.concurrent.LinkedBlockingQueue;

public class GameModeInventoriesRecordingQueue {

    private static final LinkedBlockingQueue<GameModeInventoriesQueueData> QUEUE = new LinkedBlockingQueue<GameModeInventoriesQueueData>();

    /**
     * @return the size of the QUEUE
     */
    public static int getQueueSize() {
        return QUEUE.size();
    }

    /**
     * @param data
     */
    public static void addToQueue(final GameModeInventoriesQueueData data) {
        if (data == null) {
            return;
        }
        QUEUE.add(data);
    }

    /**
     * @return the QUEUE
     */
    public static LinkedBlockingQueue<GameModeInventoriesQueueData> getQUEUE() {
        return QUEUE;
    }
}
