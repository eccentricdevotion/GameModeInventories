package me.eccentric_nz.gamemodeinventories.database;

import java.util.concurrent.LinkedBlockingQueue;

public class GameModeInventoriesRecordingQueue {

    private static final LinkedBlockingQueue<GameModeInventoriesQueueData> queue = new LinkedBlockingQueue<GameModeInventoriesQueueData>();

    /**
     *
     * @return the size of the queue
     */
    public static int getQueueSize() {
        return queue.size();
    }

    /**
     *
     * @param data
     */
    public static void addToQueue(final GameModeInventoriesQueueData data) {
        if (data == null) {
            return;
        }
        queue.add(data);
    }

    /**
     *
     * @return the queue
     */
    public static LinkedBlockingQueue<GameModeInventoriesQueueData> getQueue() {
        return queue;
    }
}
