package me.eccentric_nz.gamemodeinventories.brigadier;

import me.eccentric_nz.gamemodeinventories.GameModeInventories;
import me.eccentric_nz.gamemodeinventories.GameModeInventoriesBukkitSerialization;
import me.eccentric_nz.gamemodeinventories.GameModeInventoriesJSONSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KitCommand {

    public void process(GameModeInventories plugin, String action, Player player) {
        String uuid = "00000000-0000-0000-0000-000000000000";
        try (
                Connection connection = plugin.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT id FROM " + plugin.getPrefix() + "inventories WHERE uuid = ? AND gamemode = 'SURVIVAL'");
        ) {
            if (action.equals("save")) {
                String inv = GameModeInventoriesBukkitSerialization.toDatabase(player.getInventory().getContents());
                // get their current gamemode inventory from database
                statement.setString(1, uuid);
                try (ResultSet rsInv = statement.executeQuery();) {
                    int id;
                    if (rsInv.next()) {
                        String updateQuery = "UPDATE " + plugin.getPrefix() + "inventories SET inventory = ? WHERE id = ?";
                        // update it with their current inventory
                        id = rsInv.getInt("id");
                        try (PreparedStatement ps = connection.prepareStatement(updateQuery);) {
                            ps.setString(1, inv);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                        }
                    } else {
                        // there is no 'kit' inventory saved yet so make one with the player's current inventory
                        String insertQuery = "INSERT INTO " + plugin.getPrefix() + "inventories (uuid, player, gamemode, inventory) VALUES (?, 'kit', 'SURVIVAL', ?)";
                        try (PreparedStatement ps = connection.prepareStatement(insertQuery);) {
                            ps.setString(1, uuid);
                            ps.setString(2, inv);
                            ps.executeUpdate();
                        }
                    }
                    player.sendMessage(plugin.MY_PLUGIN_NAME + "Kit inventory saved.");
                }
            } else {
                // load
                try (PreparedStatement loadStatement = connection.prepareStatement("SELECT " + plugin.getPrefix() + "inventory FROM inventories WHERE uuid = ? AND gamemode = 'SURVIVAL'");) {
                    loadStatement.setString(1, uuid);
                    try (ResultSet rsNewInv = loadStatement.executeQuery();) {
                        if (rsNewInv.next()) {
                            try {
                                // set the inventory to the kit
                                String savedinventory = rsNewInv.getString("inventory");
                                ItemStack[] i;
                                if (savedinventory.startsWith("[")) {
                                    i = GameModeInventoriesJSONSerialization.toItemStacks(savedinventory);
                                } else {
                                    i = GameModeInventoriesBukkitSerialization.fromDatabase(savedinventory);
                                }
                                player.getInventory().setContents(i);
                            } catch (IOException e) {
                                plugin.debug("Could not set inventory for kit, " + e);
                            }
                        }
                        player.sendMessage(plugin.MY_PLUGIN_NAME + "Kit inventory loaded.");
                    }
                }
            }
        } catch (
                SQLException e) {
            plugin.debug("Could not " + action + " inventory for kit, " + e);
        }
    }
}
