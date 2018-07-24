/*
 * Kristian S. Stangeland aadnk
 * Norway
 * kristian@comphenix.net
 * thtp://www.comphenix.net/
 */
package me.eccentric_nz.gamemodeinventories;

import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesConnectionPool;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameModeInventoriesInventory {

    private final GameModeInventories plugin;
    GameModeInventoriesXPCalculator xpc;
    private final boolean saveXP;
    private final boolean saveArmour;
    private final boolean saveEnderChest;
    private final boolean potions;

    public GameModeInventoriesInventory(GameModeInventories plugin) {
        this.plugin = plugin;
        saveXP = this.plugin.getConfig().getBoolean("xp");
        saveArmour = this.plugin.getConfig().getBoolean("armor");
        saveEnderChest = this.plugin.getConfig().getBoolean("enderchest");
        potions = this.plugin.getConfig().getBoolean("remove_potions");
    }

    public void switchInventories(Player p, GameMode newGM) {
        String uuid = p.getUniqueId().toString();
        String name = p.getName();
        String currentGM = p.getGameMode().name();
        if (saveXP) {
            xpc = new GameModeInventoriesXPCalculator(p);
        }
        String inv = GameModeInventoriesBukkitSerialization.toDatabase(p.getInventory().getContents());
        String attr = "";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rsInv = null;
        PreparedStatement ps = null;
        ResultSet idRS = null;
        PreparedStatement psx = null;
        PreparedStatement psa = null;
        PreparedStatement pse = null;
        ResultSet rsNewInv = null;
        try {
            connection = GameModeInventoriesConnectionPool.dbc();
            if (connection != null && !connection.isClosed()) {
                statement = connection.prepareStatement("SELECT * FROM inventories WHERE uuid = ? AND gamemode = ?");
                // get their current gamemode inventory from database
                statement.setString(1, uuid);
                statement.setString(2, currentGM);
                rsInv = statement.executeQuery();
                int id = 0;
                if (rsInv.next()) {
                    // update it with their current inventory
                    id = rsInv.getInt("id");
                    String updateQuery = "UPDATE inventories SET inventory = ?, attributes = ? WHERE id = ?";
                    ps = connection.prepareStatement(updateQuery);
                    ps.setString(1, inv);
                    ps.setString(2, attr);
                    ps.setInt(3, id);
                    ps.executeUpdate();
                } else {
                    // they haven't got an inventory saved yet so make one with their current inventory
                    String insertQuery = "INSERT INTO inventories (uuid, player, gamemode, inventory, attributes) VALUES (?, ?, ?, ?, ?)";
                    ps = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(1, uuid);
                    ps.setString(2, name);
                    ps.setString(3, currentGM);
                    ps.setString(4, inv);
                    ps.setString(5, attr);
                    ps.executeUpdate();
                    idRS = ps.getGeneratedKeys();
                    if (idRS.next()) {
                        id = idRS.getInt(1);
                    }
                }
                if (saveXP) {
                    // get players XP
                    int a = xpc.getCurrentExp();
                    String xpQuery = "UPDATE inventories SET xp = ? WHERE id = ?";
                    psx = connection.prepareStatement(xpQuery);
                    psx.setInt(1, a);
                    psx.setInt(2, id);
                    psx.executeUpdate();
                }
                if (saveArmour) {
                    // get players armour
                    String arm = GameModeInventoriesBukkitSerialization.toDatabase(p.getInventory().getArmorContents());
                    String arm_attr = "";
                    String armourQuery = "UPDATE inventories SET armour = ?, armour_attributes = ? WHERE id = ?";
                    psa = connection.prepareStatement(armourQuery);
                    psa.setString(1, arm);
                    psa.setString(2, arm_attr);
                    psa.setInt(3, id);
                    psa.executeUpdate();
                }
                if (saveEnderChest) {
                    // get players enderchest
                    Inventory ec = p.getEnderChest();
                    if (ec != null) {
                        String ender = GameModeInventoriesBukkitSerialization.toDatabase(ec.getContents());
                        String enderQuery = "UPDATE inventories SET enderchest = ? WHERE id = ?";
                        pse = connection.prepareStatement(enderQuery);
                        pse.setString(1, ender);
                        pse.setInt(2, id);
                        pse.executeUpdate();
                    }
                }
                if (potions && currentGM.equals("CREATIVE") && newGM.equals(GameMode.SURVIVAL)) {
                    // remove all potion effects
                    p.getActivePotionEffects().forEach((effect) -> {
                        p.removePotionEffect(effect.getType());
                    });
                }
                // check if they have an inventory for the new gamemode
                try {
                    statement.setString(1, uuid);
                    statement.setString(2, newGM.name());
                    rsNewInv = statement.executeQuery();
                    int amount;
                    if (rsNewInv.next()) {
                        // set their inventory to the saved one
                        String savedinventory = rsNewInv.getString("inventory");
                        ItemStack[] i;
                        if (savedinventory.startsWith("[")) {
                            i = GameModeInventoriesJSONSerialization.toItemStacks(savedinventory);
                        } else {
                            i = GameModeInventoriesBukkitSerialization.fromDatabase(savedinventory);
                        }
                        p.getInventory().setContents(i);
                        amount = rsNewInv.getInt("xp");
                        if (saveArmour) {
                            String savedarmour = rsNewInv.getString("armour");
                            if (savedarmour != null) {
                                ItemStack[] a;
                                if (savedarmour.startsWith("[")) {
                                    a = GameModeInventoriesJSONSerialization.toItemStacks(savedarmour);
                                } else {
                                    a = GameModeInventoriesBukkitSerialization.fromDatabase(savedarmour);
                                }
                                p.getInventory().setArmorContents(a);
                            }
                        }
                        if (saveEnderChest) {
                            String savedender = rsNewInv.getString("enderchest");
                            if (savedender == null || savedender.equals("[Null]") || savedender.equals("") || savedender.isEmpty()) {
                                // empty inventory
                                savedender = "[\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\",\"null\"]";
                            }
                            ItemStack[] e;
                            if (savedender.startsWith("[")) {
                                e = GameModeInventoriesJSONSerialization.toItemStacks(savedender);
                            } else {
                                e = GameModeInventoriesBukkitSerialization.fromDatabase(savedender);
                            }
                            Inventory echest = p.getEnderChest();
                            echest.setContents(e);
                        }
                    } else {
                        // start with an empty inventory
                        p.getInventory().clear();
                        if (saveArmour) {
                            p.getInventory().setBoots(null);
                            p.getInventory().setChestplate(null);
                            p.getInventory().setLeggings(null);
                            p.getInventory().setHelmet(null);
                        }
                        if (saveEnderChest) {
                            Inventory echest = p.getEnderChest();
                            echest.clear();
                        }
                        amount = 0;
                    }

                    if (saveXP) {
                        xpc.setExp(amount);
                    }
                    p.updateInventory();
                } catch (IOException ex) {
                    GameModeInventories.plugin.debug("Could not restore inventory on gamemode change, " + ex);
                }
            } else {
                GameModeInventories.plugin.debug("Database connection was NULL or closed");
            }
        } catch (SQLException e) {
            GameModeInventories.plugin.debug("Could not save inventory on gamemode change, " + e);
        } finally {
            try {
                if (rsNewInv != null) {
                    rsNewInv.close();
                }
                if (pse != null) {
                    pse.close();
                }
                if (psa != null) {
                    psa.close();
                }
                if (psx != null) {
                    psx.close();
                }
                if (idRS != null) {
                    idRS.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rsInv != null) {
                    rsInv.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null && GameModeInventoriesConnectionPool.isIsMySQL()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Could not close resultsets, statements or connection, " + e);
            }
        }
    }

    public void saveOnDeath(Player p) {
        String uuid = p.getUniqueId().toString();
        String name = p.getName();
        String gm = p.getGameMode().name();
        String inv = GameModeInventoriesBukkitSerialization.toDatabase(p.getInventory().getContents());
        String arm = GameModeInventoriesBukkitSerialization.toDatabase(p.getInventory().getArmorContents());
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rsInv = null;
        PreparedStatement ps = null;
        try {
            connection = GameModeInventoriesConnectionPool.dbc();
            statement = connection.prepareStatement("SELECT id FROM inventories WHERE uuid = ? AND gamemode = ?");
            // get their current gamemode inventory from database
            statement.setString(1, uuid);
            statement.setString(2, gm);
            rsInv = statement.executeQuery();
            if (rsInv.next()) {
                // update it with their current inventory
                int id = rsInv.getInt("id");
                String updateQuery = "UPDATE inventories SET inventory = ?, armour = ?, attributes = ?, armour_attributes = ?  WHERE id = ?";
                ps = connection.prepareStatement(updateQuery);
                ps.setString(1, inv);
                ps.setString(2, arm);
                ps.setInt(5, id);
                ps.executeUpdate();
            } else {
                // they haven't got an inventory saved yet so make one with their current inventory
                String invQuery = "INSERT INTO inventories (uuid, player, gamemode, inventory, armour, attributes, armour_attributes) VALUES (?, ?, ?, ?, ?, ?, ?)";
                ps = connection.prepareStatement(invQuery);
                ps.setString(1, uuid);
                ps.setString(2, name);
                ps.setString(3, gm);
                ps.setString(4, inv);
                ps.setString(5, arm);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            GameModeInventories.plugin.debug("Could not save inventories on player death, " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rsInv != null) {
                    rsInv.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null && GameModeInventoriesConnectionPool.isIsMySQL()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Could not close resultsets, statements or connection, " + e);
            }
        }
    }

    public void restoreOnSpawn(Player p) {
        String uuid = p.getUniqueId().toString();
        String gm = p.getGameMode().name();
        // restore their inventory
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rsInv = null;
        try {
            connection = GameModeInventoriesConnectionPool.dbc();
            statement = connection.prepareStatement("SELECT * FROM inventories WHERE uuid = ? AND gamemode = ?");
            // get their current gamemode inventory from database
            statement.setString(1, uuid);
            statement.setString(2, gm);
            rsInv = statement.executeQuery();
            if (rsInv.next()) {
                try {
                    // set their inventory to the saved one
                    String savedinventory = rsInv.getString("inventory");
                    ItemStack[] i;
                    if (savedinventory.startsWith("[")) {
                        i = GameModeInventoriesJSONSerialization.toItemStacks(savedinventory);
                    } else {
                        i = GameModeInventoriesBukkitSerialization.fromDatabase(savedinventory);
                    }
                    p.getInventory().setContents(i);
                    String savedarmour = rsInv.getString("armour");
                    ItemStack[] a;
                    if (savedarmour.startsWith("[")) {
                        a = GameModeInventoriesJSONSerialization.toItemStacks(savedarmour);
                    } else {
                        a = GameModeInventoriesBukkitSerialization.fromDatabase(savedarmour);
                    }
                    p.getInventory().setArmorContents(a);
                } catch (IOException e) {
                    GameModeInventories.plugin.debug("Could not restore inventories on respawn, " + e);
                }
            }
        } catch (SQLException e) {
            GameModeInventories.plugin.debug("Could not restore inventories on respawn, " + e);
        } finally {
            try {
                if (rsInv != null) {
                    rsInv.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null && GameModeInventoriesConnectionPool.isIsMySQL()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Could not close resultsets, statements or connection, " + e);
            }
        }
    }

    public boolean isInstanceOf(Entity e) {
        return e instanceof PoweredMinecart || e instanceof StorageMinecart || e instanceof HopperMinecart || e instanceof ItemFrame || e instanceof ArmorStand;
    }

    public boolean isInstanceOf(InventoryHolder h) {
        return (h instanceof AbstractHorse);
    }
}
