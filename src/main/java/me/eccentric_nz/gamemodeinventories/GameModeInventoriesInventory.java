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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.eccentric_nz.gamemodeinventories.attributes.GMIAttribute;
import me.eccentric_nz.gamemodeinventories.attributes.GMIAttributeData;
import me.eccentric_nz.gamemodeinventories.attributes.GMIAttributeSerialization;
import me.eccentric_nz.gamemodeinventories.attributes.GMIAttributeType;
import me.eccentric_nz.gamemodeinventories.attributes.GMIAttributes;
import me.eccentric_nz.gamemodeinventories.queue.GameModeInventoriesConnectionPool;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
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

    GameModeInventoriesXPCalculator xpc;

    @SuppressWarnings("deprecation")
    public void switchInventories(Player p, Inventory inventory, boolean savexp, boolean savearmour, boolean saveender, boolean potions, boolean retain, GameMode newGM) {
        String uuid = p.getUniqueId().toString();
        String name = p.getName();
        String currentGM = p.getGameMode().name();
        if (savexp) {
            xpc = new GameModeInventoriesXPCalculator(p);
        }
        String inv = GameModeInventoriesBukkitSerialization.toDatabase(p.getInventory().getContents());
        String attr = "";
        if (retain) {
            attr = GMIAttributeSerialization.toDatabase(getAttributeMap(p.getInventory().getContents()));
        }
        try {
            Connection connection = GameModeInventoriesConnectionPool.dbc();
            Statement statement = connection.createStatement();
            PreparedStatement ps;
            // get their current gamemode inventory from database
            String getQuery = "SELECT id FROM inventories WHERE uuid = '" + uuid + "' AND gamemode = '" + currentGM + "'";
            ResultSet rsInv = statement.executeQuery(getQuery);
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
                ps.close();
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
                String arm_attr = "";
                if (retain) {
                    arm_attr = GMIAttributeSerialization.toDatabase(getAttributeMap(p.getInventory().getArmorContents()));
                }
                String armourQuery = "UPDATE inventories SET armour = ?, armour_attributes = ? WHERE id = ?";
                PreparedStatement psa = connection.prepareStatement(armourQuery);
                psa.setString(1, arm);
                psa.setString(2, arm_attr);
                psa.setInt(3, id);
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
                String getNewQuery = "SELECT * FROM inventories WHERE uuid = '" + uuid + "' AND gamemode = '" + newGM + "'";
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
                    if (retain) {
                        // reapply custom attributes
                        reapplyCustomAttributes(p, rsNewInv.getString("attributes"));
                    }
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
                        if (retain) {
                            // reapply custom attributes
                            reapplyCustomAttributes(p, rsNewInv.getString("armour_attributes"));
                        }
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
                if (GameModeInventoriesConnectionPool.isIsMySQL()) {
                    connection.close();
                }
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
        String attr = GMIAttributeSerialization.toDatabase(getAttributeMap(p.getInventory().getContents()));
        String arm_attr = GMIAttributeSerialization.toDatabase(getAttributeMap(p.getInventory().getArmorContents()));
        try {
            Connection connection = GameModeInventoriesConnectionPool.dbc();
            Statement statement = connection.createStatement();
            // get their current gamemode inventory from database
            String getQuery = "SELECT id FROM inventories WHERE uuid = '" + uuid + "' AND gamemode = '" + gm + "'";
            ResultSet rsInv = statement.executeQuery(getQuery);
            PreparedStatement ps;
            if (rsInv.next()) {
                // update it with their current inventory
                int id = rsInv.getInt("id");
                String updateQuery = "UPDATE inventories SET inventory = ?, armour = ?, attributes = ?, armour_attributes = ?  WHERE id = ?";
                ps = connection.prepareStatement(updateQuery);
                ps.setString(1, inv);
                ps.setString(2, arm);
                ps.setString(3, attr);
                ps.setString(4, arm_attr);
                ps.setInt(5, id);
                ps.executeUpdate();
                ps.close();
                rsInv.close();
            } else {
                // they haven't got an inventory saved yet so make one with their current inventory
                String invQuery = "INSERT INTO inventories (uuid, player, gamemode, inventory, armour, attributes, armour_attributes) VALUES (?, ?, ?, ?, ?, ?, ?)";
                ps = connection.prepareStatement(invQuery);
                ps.setString(1, uuid);
                ps.setString(2, name);
                ps.setString(3, gm);
                ps.setString(4, inv);
                ps.setString(5, arm);
                ps.setString(6, attr);
                ps.setString(7, arm_attr);
                ps.executeUpdate();
                ps.close();
            }
            statement.close();
            rsInv.close();
            if (GameModeInventoriesConnectionPool.isIsMySQL()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Could not save inventories on player death, " + e);
        }
    }

    public void restoreOnSpawn(Player p) {
        String uuid = p.getUniqueId().toString();
        String gm = p.getGameMode().name();
        // restore their inventory
        try {
            Connection connection = GameModeInventoriesConnectionPool.dbc();
            Statement statement = connection.createStatement();
            // get their current gamemode inventory from database
            String getQuery = "SELECT * FROM inventories WHERE uuid = '" + uuid + "' AND gamemode = '" + gm + "'";
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
                    reapplyCustomAttributes(p, rsInv.getString("attributes"));
                    String savedarmour = rsInv.getString("armour");
                    ItemStack[] a;
                    if (savedarmour.startsWith("[")) {
                        a = GameModeInventoriesJSONSerialization.toItemStacks(savedarmour);
                    } else {
                        a = GameModeInventoriesBukkitSerialization.fromDatabase(savedarmour);
                    }
                    p.getInventory().setArmorContents(a);
                    reapplyCustomAttributes(p, rsInv.getString("armour_attributes"));
                } catch (IOException e) {
                    System.err.println("Could not restore inventories on respawn, " + e);
                }
            }
            rsInv.close();
            statement.close();
            if (GameModeInventoriesConnectionPool.isIsMySQL()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Could not restore inventories on respawn, " + e);
        }
    }

    public boolean isInstanceOf(Entity e) {
        return e instanceof PoweredMinecart || e instanceof StorageMinecart || e instanceof HopperMinecart || e instanceof ItemFrame || e instanceof ArmorStand;
    }

    public boolean isInstanceOf(InventoryHolder h) {
        return h instanceof Horse;
    }

    private HashMap<Integer, List<GMIAttributeData>> getAttributeMap(ItemStack[] stacks) {
        HashMap<Integer, List<GMIAttributeData>> map = new HashMap<Integer, List<GMIAttributeData>>();
        int add = (stacks.length == 4) ? 36 : 0;
        for (int s = 0; s < stacks.length; s++) {
            ItemStack i = stacks[s];
            if (i != null && !i.getType().equals(Material.AIR)) {
                GMIAttributes attributes = new GMIAttributes(i);
                if (attributes.size() > 0) {
                    List<GMIAttributeData> ist = new ArrayList<GMIAttributeData>();
                    for (GMIAttribute a : attributes.values()) {
                        GMIAttributeData data = new GMIAttributeData(a.getName(), a.getAttributeType().getMinecraftId(), a.getAmount(), a.getOperation());
                        ist.add(data);
                    }
                    map.put(s + add, ist);
                }
            }
        }
        return map;
    }

    private void reapplyCustomAttributes(Player p, String data) {
        try {
            HashMap<Integer, List<GMIAttributeData>> cus = GMIAttributeSerialization.fromDatabase(data);
            for (Map.Entry<Integer, List<GMIAttributeData>> m : cus.entrySet()) {
                int slot = m.getKey();
                if (slot != -1) {
                    ItemStack is = p.getInventory().getItem(slot);
                    GMIAttributes attributes = new GMIAttributes(is);
                    for (GMIAttributeData ad : m.getValue()) {
                        attributes.add(GMIAttribute.newBuilder().name(ad.getAttribute()).type(GMIAttributeType.fromId(ad.getAttributeID())).operation(ad.getOperation()).amount(ad.getValue()).build());
                        p.getInventory().setItem(m.getKey(), attributes.getStack());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not reapply custom attributes, " + e);
        }
    }
}
