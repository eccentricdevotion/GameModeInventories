/*
 * Kristian S. Stangeland aadnk
 * Norway
 * kristian@comphenix.net
 * thtp://www.comphenix.net/
 */
package me.eccentric_nz.gamemodeinventories;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class GameModeInventoriesInventory {

    GameModeInventoriesDBConnection service = GameModeInventoriesDBConnection.getInstance();
    GameModeInventoriesXPCalculator xpc;

    @SuppressWarnings("deprecation")
    public void switchInventories(Player p, Inventory inventory, boolean savexp, boolean savearmour, boolean saveender, boolean potions, GameMode newGM) {
        String uuid = p.getUniqueId().toString();
        String name = p.getName();
        String currentGM = p.getGameMode().name();
        if (savexp) {
            xpc = new GameModeInventoriesXPCalculator(p);
        }
        String inv = GameModeInventoriesBukkitSerialization.toDatabase(p.getInventory().getContents());
        try {
            Connection connection = service.getConnection();
            service.testConnection(connection);
            Statement statement = connection.createStatement();
            PreparedStatement ps;
            // get their current gamemode inventory from database
            String getQuery = "SELECT id FROM inventories WHERE uuid = '" + uuid + "' AND gamemode = '" + currentGM + "'";
            ResultSet rsInv = statement.executeQuery(getQuery);
            int id = 0;
            if (rsInv.next()) {
                // update it with their current inventory
                id = rsInv.getInt("id");
                String updateQuery = "UPDATE inventories SET inventory = ? WHERE id = ?";
                ps = connection.prepareStatement(updateQuery);
                ps.setString(1, inv);
                ps.setInt(2, id);
                ps.executeUpdate();
                ps.close();
            } else {
                // they haven't got an inventory saved yet so make one with their current inventory
                String insertQuery = "INSERT INTO inventories (uuid, player, gamemode, inventory) VALUES (?, ?, ?, ?)";
                ps = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, uuid);
                ps.setString(2, name);
                ps.setString(3, currentGM);
                ps.setString(4, inv);
                ps.executeUpdate();
                ResultSet idRS = ps.getGeneratedKeys();
                if (idRS.next()) {
                    id = idRS.getInt(1);
                }
                ps.close();
            }
            rsInv.close();
            if (savexp) {
                // get players XP
                int a = xpc.getCurrentExp();
                String xpQuery = "UPDATE inventories SET xp = ? WHERE id = ?";
                PreparedStatement psx = connection.prepareStatement(xpQuery);
                psx.setInt(1, a);
                psx.setInt(2, id);
                psx.executeUpdate();
                psx.close();
            }
            if (savearmour) {
                // get players armour
                String arm = GameModeInventoriesBukkitSerialization.toDatabase(p.getInventory().getArmorContents());
                String armourQuery = "UPDATE inventories SET armour = ? WHERE id = ?";
                PreparedStatement psa = connection.prepareStatement(armourQuery);
                psa.setString(1, arm);
                psa.setInt(2, id);
                psa.executeUpdate();
                psa.close();
            }
            if (saveender) {
                // get players enderchest
                Inventory ec = p.getEnderChest();
                if (ec != null) {
                    String ender = GameModeInventoriesBukkitSerialization.toDatabase(ec.getContents());
                    String enderQuery = "UPDATE inventories SET enderchest = ? WHERE id = ?";
                    PreparedStatement pse = connection.prepareStatement(enderQuery);
                    pse.setString(1, ender);
                    pse.setInt(2, id);
                    pse.executeUpdate();
                    pse.close();
                }
            }
            if (potions && currentGM.equals("CREATIVE") && newGM.equals(GameMode.SURVIVAL)) {
                // remove all potion effects
                for (PotionEffect effect : p.getActivePotionEffects()) {
                    p.removePotionEffect(effect.getType());
                }
            }
            // check if they have an inventory for the new gamemode
            try {
                String getNewQuery = "SELECT inventory, xp, armour, enderchest FROM inventories WHERE uuid = '" + uuid + "' AND gamemode = '" + newGM + "'";
                ResultSet rsNewInv = statement.executeQuery(getNewQuery);
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
                    if (savearmour) {
                        String savedarmour = rsNewInv.getString("armour");
                        ItemStack[] a;
                        if (savedarmour.startsWith("[")) {
                            a = GameModeInventoriesJSONSerialization.toItemStacks(savedarmour);
                        } else {
                            a = GameModeInventoriesBukkitSerialization.fromDatabase(savedarmour);
                        }
                        p.getInventory().setArmorContents(a);
                    }
                    if (saveender) {
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
                    if (savearmour) {
                        p.getInventory().setBoots(null);
                        p.getInventory().setChestplate(null);
                        p.getInventory().setLeggings(null);
                        p.getInventory().setHelmet(null);
                    }
                    if (saveender) {
                        Inventory echest = p.getEnderChest();
                        echest.clear();
                    }
                    amount = 0;
                }
                rsNewInv.close();
                statement.close();
                if (savexp) {
                    xpc.setExp(amount);
                }
                p.updateInventory();
            } catch (IOException ex) {
                System.err.println("Could not restore inventory on gamemode change, " + ex);
            }
        } catch (SQLException e) {
            System.err.println("Could not save inventory on gamemode change, " + e);
        }
    }

    public void saveOnDeath(Player p) {
        String uuid = p.getUniqueId().toString();
        String name = p.getName();
        String gm = p.getGameMode().name();
        String inv = GameModeInventoriesBukkitSerialization.toDatabase(p.getInventory().getContents());
        String arm = GameModeInventoriesBukkitSerialization.toDatabase(p.getInventory().getArmorContents());
        try {
            Connection connection = service.getConnection();
            service.testConnection(connection);
            Statement statement = connection.createStatement();
            // get their current gamemode inventory from database
            String getQuery = "SELECT id FROM inventories WHERE uuid = '" + uuid + "' AND gamemode = '" + gm + "'";
            ResultSet rsInv = statement.executeQuery(getQuery);
            PreparedStatement ps;
            if (rsInv.next()) {
                // update it with their current inventory
                int id = rsInv.getInt("id");
                String updateQuery = "UPDATE inventories SET inventory = ?, armour = ? WHERE id = ?";
                ps = connection.prepareStatement(updateQuery);
                ps.setString(1, inv);
                ps.setString(2, arm);
                ps.setInt(3, id);
                ps.executeUpdate();
                ps.close();
                rsInv.close();
            } else {
                // they haven't got an inventory saved yet so make one with their current inventory
                String invQuery = "INSERT INTO inventories (uuid, player, gamemode, inventory, armour) VALUES (?, ?, ?, ?, ?)";
                ps = connection.prepareStatement(invQuery);
                ps.setString(1, uuid);
                ps.setString(2, name);
                ps.setString(3, gm);
                ps.setString(4, inv);
                ps.setString(5, arm);
                ps.executeUpdate();
                ps.close();
            }
            statement.close();
        } catch (SQLException e) {
            System.err.println("Could not save inventories on player death, " + e);
        }
    }

    public void restoreOnSpawn(Player p) {
        String uuid = p.getUniqueId().toString();
        String gm = p.getGameMode().name();
        // restore their inventory
        try {
            Connection connection = service.getConnection();
            service.testConnection(connection);
            Statement statement = connection.createStatement();
            // get their current gamemode inventory from database
            String getQuery = "SELECT inventory, armour FROM inventories WHERE uuid = '" + uuid + "' AND gamemode = '" + gm + "'";
            ResultSet rsInv = statement.executeQuery(getQuery);
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
                    System.err.println("Could not restore inventories on respawn, " + e);
                }
            }
            rsInv.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Could not restore inventories on respawn, " + e);
        }
    }

    public boolean isInstanceOf(Entity e) {
        return e instanceof PoweredMinecart || e instanceof StorageMinecart || e instanceof HopperMinecart || e instanceof ItemFrame;
    }

    public boolean isInstanceOf(InventoryHolder h) {
        return h instanceof Horse;
    }
}
