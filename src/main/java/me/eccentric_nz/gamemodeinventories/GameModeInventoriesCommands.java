package me.eccentric_nz.gamemodeinventories;

import com.google.common.collect.ImmutableList;
import me.eccentric_nz.gamemodeinventories.database.GameModeInventoriesConnectionPool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GameModeInventoriesCommands implements CommandExecutor, TabCompleter {

    private final GameModeInventories plugin;
    private final HashMap<String, String> firstArgs = new HashMap<>();
    private final ImmutableList<String> ROOT_SUBS;

    public GameModeInventoriesCommands(GameModeInventories plugin) {
        this.plugin = plugin;
        firstArgs.put("armor", "armor");
        firstArgs.put("attached_block", "track_creative_place.attached_block");
        firstArgs.put("break_bedrock", "break_bedrock");
        firstArgs.put("break_no_drop", "track_creative_place.break_no_drop");
        firstArgs.put("bypass.blacklist", "bypass.blacklist");
        firstArgs.put("bypass.commands", "bypass.commands");
        firstArgs.put("bypass.inventories", "bypass.inventories");
        firstArgs.put("bypass.items", "bypass.items");
        firstArgs.put("bypass.survival", "bypass.survival");
        firstArgs.put("bypass.trades", "bypass.trades");
        firstArgs.put("command_blacklist", "command_blacklist");
        firstArgs.put("creative_blacklist", "creative_blacklist");
        firstArgs.put("debug", "debug");
        firstArgs.put("dont_spam_chat", "dont_spam_chat");
        firstArgs.put("enderchest", "enderchest");
        firstArgs.put("no_creative_pvp", "no_creative_pvp");
        firstArgs.put("no_drops", "no_drops");
        firstArgs.put("no_falling_drops", "no_falling_drops");
        firstArgs.put("no_pickups", "no_pickups");
        firstArgs.put("no_villager_trade", "no_villager_trade");
        firstArgs.put("no_golem_spawn", "no_golem_spawn");
        firstArgs.put("no_wither_spawn", "no_wither_spawn");
        firstArgs.put("no_seeds_from_pumpkin", "track_creative_place.no_seeds_from_pumpkin");
        firstArgs.put("no_piston_move", "track_creative_place.no_piston_move");
        firstArgs.put("remove_potions", "remove_potions");
        firstArgs.put("restrict_creative", "restrict_creative");
        firstArgs.put("restrict_spectator", "restrict_spectator");
        firstArgs.put("save_on_death", "save_on_death");
        firstArgs.put("survival_on_world_change", "survival_on_world_change");
        firstArgs.put("switch_to", "creative_world.switch_to");
        firstArgs.put("track_creative_place", "track_creative_place.enabled");
        firstArgs.put("xp", "xp");
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
                    try (
                            Connection connection = GameModeInventoriesConnectionPool.dbc();
                            PreparedStatement statement = connection.prepareStatement("SELECT id FROM " + plugin.getPrefix() + "inventories WHERE uuid = ? AND gamemode = 'SURVIVAL'");
                    ) {
                        if (args[1].toLowerCase().equals("save")) {
                            String inv = GameModeInventoriesBukkitSerialization.toDatabase(p.getInventory().getContents());
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
                                p.sendMessage(plugin.MY_PLUGIN_NAME + "Kit inventory saved.");
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
                                            p.getInventory().setContents(i);
                                        } catch (IOException e) {
                                            plugin.debug("Could not set inventory for kit, " + e);
                                        }
                                    }
                                    p.sendMessage(plugin.MY_PLUGIN_NAME + "Kit inventory loaded.");
                                }
                            }
                        }
                    } catch (SQLException e) {
                        plugin.debug("Could not " + args[1].toLowerCase() + " inventory for kit, " + e);
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
        return StringUtil.copyPartialMatches(token, from, new ArrayList<>(from.size()));
    }
}
