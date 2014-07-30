package me.eccentric_nz.gamemodeinventories;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GameModeInventoriesListener implements Listener {

    private final GameModeInventories plugin;
    List<Material> containers = new ArrayList<Material>();
    Version bukkitversion;
    Version prewoodbuttonversion = new Version("1.5");
    Version preenchanttableversion = new Version("1.4.6");
    Version preanvilversion = new Version("1.4.2");
    Version preenderchestversion = new Version("1.3.1");

    public GameModeInventoriesListener(GameModeInventories plugin) {
        this.plugin = plugin;
        containers.add(Material.BREWING_STAND);
        containers.add(Material.CHEST);
        containers.add(Material.DISPENSER);
        containers.add(Material.FURNACE);
        containers.add(Material.JUKEBOX);
        String[] v = Bukkit.getServer().getBukkitVersion().split("-");
        bukkitversion = (!v[0].equalsIgnoreCase("unknown")) ? new Version(v[0]) : new Version("1.4.7");
        if (bukkitversion.compareTo(preenderchestversion) >= 0) {
            containers.add(Material.ENDER_CHEST);
        }
        if (bukkitversion.compareTo(preanvilversion) >= 0) {
            containers.add(Material.ANVIL);
            containers.add(Material.BEACON);
        }
        if (bukkitversion.compareTo(preenchanttableversion) >= 0) {
            containers.add(Material.ENCHANTMENT_TABLE);
        }
        if (bukkitversion.compareTo(prewoodbuttonversion) >= 0) {
            containers.add(Material.DROPPER);
            containers.add(Material.HOPPER);
            containers.add(Material.TRAPPED_CHEST);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player p = event.getPlayer();
        GameMode newGM = event.getNewGameMode();
        if (p.hasPermission("gamemodeinventories.use")) {
            boolean savexp = plugin.getConfig().getBoolean("xp");
            boolean savearmour = plugin.getConfig().getBoolean("armor");
            boolean saveenderchest = plugin.getConfig().getBoolean("enderchest");
            boolean potions = plugin.getConfig().getBoolean("remove_potions");
            if (p.isOnline()) {
                plugin.getInventoryHandler().switchInventories(p, p.getInventory(), savexp, savearmour, saveenderchest, potions, newGM);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(PlayerInteractEvent event) {
        if (plugin.getConfig().getBoolean("restrict_creative")) {
            Block b = event.getClickedBlock();
            if (b != null) {
                Material m = b.getType();
                Player p = event.getPlayer();
                GameMode gm = p.getGameMode();
                if (gm.equals(GameMode.CREATIVE) && containers.contains(m) && !p.hasPermission("gamemodeinventories.bypass") && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    event.setCancelled(true);
                    if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                        p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_INVENTORY"));
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (plugin.getConfig().getBoolean("no_drops")) {
            Inventory inv = event.getInventory();
            if (inv.getType().equals(InventoryType.WORKBENCH)) {
                Player p = (Player) event.getPlayer();
                if (p.getGameMode().equals(GameMode.CREATIVE) && !p.hasPermission("gamemodeinventories.bypass")) {
                    boolean empty = true;
                    for (ItemStack is : inv.getContents()) {
                        if (!is.getType().equals(Material.AIR)) {
                            empty = false;
                        }
                    }
                    if (!empty) {
                        inv.clear();
                        if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                            p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_WORKBENCH_DROPS"));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityClick(PlayerInteractEntityEvent event) {
        if (plugin.getConfig().getBoolean("restrict_creative")) {
            Entity entity = event.getRightClicked();
            Player p = event.getPlayer();
            if (p.getGameMode().equals(GameMode.CREATIVE) && plugin.getInventoryHandler().isInstanceOf(entity) && !p.hasPermission("gamemodeinventories.bypass")) {
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_INVENTORY"));
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (plugin.getConfig().getBoolean("no_drops")) {
            Player p = event.getPlayer();
            GameMode gm = p.getGameMode();
            if (gm.equals(GameMode.CREATIVE) && !p.hasPermission("gamemodeinventories.bypass")) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_PLAYER_DROPS"));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void noPickup(PlayerPickupItemEvent event) {
        if (plugin.getConfig().getBoolean("no_pickups")) {
            Player p = event.getPlayer();
            GameMode gm = p.getGameMode();
            if (gm.equals(GameMode.CREATIVE) && !p.hasPermission("gamemodeinventories.bypass")) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_PICKUP"));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void noHorseInventory(InventoryOpenEvent event) {
        if (plugin.getConfig().getBoolean("restrict_creative") && plugin.getInventoryHandler().isInstanceOf(event.getInventory().getHolder())) {
            Player p = (Player) event.getPlayer();
            GameMode gm = p.getGameMode();
            if (gm.equals(GameMode.CREATIVE) && !p.hasPermission("gamemodeinventories.bypass")) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_HORSE"));
                }
            }
        }
    }
}
