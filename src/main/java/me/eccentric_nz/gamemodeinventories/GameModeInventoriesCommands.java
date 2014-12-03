package me.eccentric_nz.gamemodeinventories;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

public class GameModeInventoriesCommands implements CommandExecutor, TabCompleter {

    private final GameModeInventories plugin;
    private final HashMap<String, String> firstArgs = new HashMap<String, String>();
    private final ImmutableList<String> ROOT_SUBS;

    public GameModeInventoriesCommands(GameModeInventories plugin) {
        this.plugin = plugin;
        firstArgs.put("armor", "armor");
        firstArgs.put("break_bedrock", "break_bedrock");
        firstArgs.put("bypass.inventories", "bypass.inventories");
        firstArgs.put("bypass.items", "bypass.items");
        firstArgs.put("bypass.blacklist", "bypass.blacklist");
        firstArgs.put("bypass.survival", "bypass.survival");
        firstArgs.put("command_blacklist", "command_blacklist");
        firstArgs.put("creative_blacklist", "creative_blacklist");
        firstArgs.put("custom_attributes", "custom_attributes");
        firstArgs.put("debug", "debug");
        firstArgs.put("dont_spam_chat", "dont_spam_chat");
        firstArgs.put("enderchest", "enderchest");
        firstArgs.put("no_drops", "no_drops");
        firstArgs.put("no_falling_drops", "no_falling_drops");
        firstArgs.put("no_pickups", "no_pickups");
        firstArgs.put("remove_potions", "remove_potions");
        firstArgs.put("restrict_creative", "restrict_creative");
        firstArgs.put("restrict_spectator", "restrict_spectator");
        firstArgs.put("save_on_death", "save_on_death");
        firstArgs.put("survival_on_world_change", "survival_on_world_change");
        firstArgs.put("xp", "xp");
        firstArgs.put("track_creative_place", "track_creative_place.enabled");
        firstArgs.put("break_no_drop", "track_creative_place.break_no_drop");
        firstArgs.put("no_piston_move", "track_creative_place.no_piston_move");
        ROOT_SUBS = ImmutableList.copyOf(firstArgs.keySet());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("gmi")) {
            if (args.length == 0) {
                sender.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("HELP"));
                return true;
            }
            if (sender.hasPermission("gamemodeinventories.admin")) {
                String option = args[0].toLowerCase(Locale.ENGLISH);
                if (args.length == 1 && firstArgs.containsKey(option)) {
                    boolean bool = !plugin.getConfig().getBoolean(firstArgs.get(option));
                    plugin.getConfig().set(firstArgs.get(option), bool);
                    sender.sendMessage(plugin.MY_PLUGIN_NAME + String.format(plugin.getM().getMessage().get("CONFIG_SET"), option, bool));
                    plugin.saveConfig();
                    return true;
                } else if (args.length == 2 && option.equals("kit")) {
                    String uuid = "00000000-0000-0000-0000-000000000000";
                    Player p = (Player) sender;
                    GameModeInventoriesDBConnection service = GameModeInventoriesDBConnection.getInstance();
                    try {
                        Connection connection = service.getConnection();
                        service.testConnection(connection);
                        Statement statement = connection.createStatement();
                        if (args[1].toLowerCase().equals("save")) {
                            String inv = GameModeInventoriesBukkitSerialization.toDatabase(p.getInventory().getContents());
                            PreparedStatement ps;
                            // get their current gamemode inventory from database
                            String getQuery = "SELECT id FROM inventories WHERE uuid = '" + uuid + "' AND gamemode = 'SURVIVAL'";
                            ResultSet rsInv = statement.executeQuery(getQuery);
                            int id;
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
                                // there is no 'kit' inventory saved yet so make one with the player's current inventory
                                String insertQuery = "INSERT INTO inventories (uuid, player, gamemode, inventory) VALUES (?, 'kit', 'SURVIVAL', ?)";
                                ps = connection.prepareStatement(insertQuery);
                                ps.setString(1, uuid);
                                ps.setString(2, inv);
                                ps.executeUpdate();
                                ps.close();
                            }
                            rsInv.close();
                            p.sendMessage(plugin.MY_PLUGIN_NAME + "Kit inventory saved.");
                        } else {
                            // load
                            String getNewQuery = "SELECT inventory FROM inventories WHERE uuid = '" + uuid + "' AND gamemode = 'SURVIVAL'";
                            ResultSet rsNewInv = statement.executeQuery(getNewQuery);
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
                                    p.getInventory().setContents(i);
                                } catch (IOException e) {
                                    plugin.debug("Could not set inventory for kit, " + e);
                                }
                            }
                            rsNewInv.close();
                            p.sendMessage(plugin.MY_PLUGIN_NAME + "Kit inventory loaded.");
                        }
                        statement.close();
                    } catch (SQLException e) {
                        plugin.debug("Could not save inventory for kit, " + e);
                    }
                    return true;
                }
            } else {
                sender.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_PERMISSION"));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            return partial(args[0], ROOT_SUBS);
        }
        return ImmutableList.of();
    }

    private List<String> partial(String token, Collection<String> from) {
        return StringUtil.copyPartialMatches(token, from, new ArrayList<String>(from.size()));
    }
}
