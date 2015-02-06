package me.eccentric_nz.gamemodeinventories.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.eccentric_nz.gamemodeinventories.GMIDebug;
import me.eccentric_nz.gamemodeinventories.GameModeInventories;

public class GameModeInventoriesRecordingTask implements Runnable {

    private final GameModeInventories plugin;

    public GameModeInventoriesRecordingTask(GameModeInventories plugin) {
        this.plugin = plugin;
    }

    public void save() {
        if (!GameModeInventoriesRecordingQueue.getQueue().isEmpty()) {
            insertIntoDatabase();
        }
    }

    public void insertIntoDatabase() {
        PreparedStatement s = null;
        Connection conn = null;
        try {
            int perBatch = 1000;
            if (!GameModeInventoriesRecordingQueue.getQueue().isEmpty()) {
                plugin.debug("Beginning batch insert from queue. " + System.currentTimeMillis(), GMIDebug.INFO);
                conn = GameModeInventoriesConnectionPool.dbc();
                // Handle dead connections
                if (conn == null || conn.isClosed()) {
                    if (GameModeInventoriesRecordingManager.failedDbConnectionCount == 0) {
                        plugin.debug("GMI database error. Connection should be there but it's not. Leaving actions to log in queue.", GMIDebug.INFO);
                    }
                    GameModeInventoriesRecordingManager.failedDbConnectionCount++;
                    if (GameModeInventoriesRecordingManager.failedDbConnectionCount > 5) {
                        plugin.debug("Too many problems connecting. Giving up for a bit.", GMIDebug.INFO);
                        scheduleNextRecording();
                    }
                    plugin.debug("Database connection still missing, incrementing count.", GMIDebug.INFO);
                    return;
                } else {
                    GameModeInventoriesRecordingManager.failedDbConnectionCount = 0;
                }
                // Connection valid, proceed
                conn.setAutoCommit(false);
                s = conn.prepareStatement("INSERT INTO blocks (worldchunk,location) VALUES (?,?)");
                int i = 0;
                while (!GameModeInventoriesRecordingQueue.getQueue().isEmpty()) {
                    if (conn.isClosed()) {
                        plugin.debug("GMI database error. We have to bail in the middle of building primary bulk insert query.", GMIDebug.ERROR);
                        break;
                    }
                    final GameModeInventoriesQueueData a = GameModeInventoriesRecordingQueue.getQueue().poll();
                    // poll() returns null if queue is empty
                    if (a == null) {
                        break;
                    }
                    s.setString(1, a.getWorldChunk());
                    s.setString(2, a.getLocation());
                    s.addBatch();
                    // Break out of the loop and just commit what we have
                    if (i >= perBatch) {
                        plugin.debug("Recorder: Batch max exceeded, running insert. Queue remaining: " + GameModeInventoriesRecordingQueue.getQueue().size(), GMIDebug.INFO);
                        break;
                    }
                    i++;
                }
                s.executeBatch();
                if (conn.isClosed()) {
                    plugin.debug("GMI database error. We have to bail in the middle of building primary bulk insert query.", GMIDebug.ERROR);
                } else {
                    conn.commit();
                    plugin.debug("Batch insert was commit: " + System.currentTimeMillis(), GMIDebug.INFO);
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (final SQLException ignored) {
                }
            }
            if (conn != null && GameModeInventoriesConnectionPool.isIsMySQL()) {
                try {
                    conn.close();
                } catch (final SQLException ignored) {
                }
            }
        }
    }

    @Override
    public void run() {
        if (GameModeInventoriesRecordingManager.failedDbConnectionCount > 5) {
            try {
                GameModeInventoriesConnectionPool.rebuildPool();
            } catch (SQLException ex) {
                Logger.getLogger(GameModeInventoriesRecordingTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        save();
        scheduleNextRecording();
    }

    protected int getTickDelayForNextBatch() {

        // If we have too many rejected connections, increase the schedule
        if (GameModeInventoriesRecordingManager.failedDbConnectionCount > 5) {
            return GameModeInventoriesRecordingManager.failedDbConnectionCount * 20;
        }
        return 3;
    }

    protected void scheduleNextRecording() {
        if (!plugin.isEnabled()) {
            plugin.debug("Can't schedule new recording tasks as plugin is now disabled. If you're shutting down the server, ignore me.", GMIDebug.INFO);
            return;
        }
        plugin.recordingTask = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new GameModeInventoriesRecordingTask(plugin), getTickDelayForNextBatch());
    }
}
