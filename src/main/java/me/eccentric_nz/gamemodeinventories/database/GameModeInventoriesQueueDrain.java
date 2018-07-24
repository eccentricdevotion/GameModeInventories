package me.eccentric_nz.gamemodeinventories.database;

import me.eccentric_nz.gamemodeinventories.GMIDebug;
import me.eccentric_nz.gamemodeinventories.GameModeInventories;

public class GameModeInventoriesQueueDrain {

    /**
     *
     */
    private final GameModeInventories plugin;

    /**
     * @param plugin
     */
    public GameModeInventoriesQueueDrain(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    /**
     *
     */
    public void forceDrainQueue() {

        plugin.debug("Forcing recorder queue to run a new batch before shutdown...", GMIDebug.INFO);

        final GameModeInventoriesRecordingTask recorderTask = new GameModeInventoriesRecordingTask(plugin);

        // Force queue to empty
        while (!GameModeInventoriesRecordingQueue.getQUEUE().isEmpty()) {

            plugin.debug("Starting drain batch...", GMIDebug.INFO);
            plugin.debug("Current queue size: " + GameModeInventoriesRecordingQueue.getQUEUE().size(), GMIDebug.INFO);

            // run insert
            try {
                recorderTask.insertIntoDatabase();
            } catch (final Exception e) {
//                e.printStackTrace();
                plugin.debug("Stopping queue drain due to caught exception. Queue items lost: " + GameModeInventoriesRecordingQueue.getQUEUE().size(), GMIDebug.INFO);
                break;
            }

            if (GameModeInventoriesRecordingManager.failedDbConnectionCount > 0) {
                plugin.debug("Stopping queue drain due to detected database error. Queue items lost: " + GameModeInventoriesRecordingQueue.getQUEUE().size(), GMIDebug.INFO);
            }
        }
    }
}
