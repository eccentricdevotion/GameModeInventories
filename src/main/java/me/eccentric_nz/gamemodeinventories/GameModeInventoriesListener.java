package me.eccentric_nz.gamemodeinventories;

import java.util.ArrayList;
import java.util.List;
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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GameModeInventoriesListener implements Listener {

    private final GameModeInventories plugin;
    List<Material> containers = new ArrayList<Material>();

    public GameModeInventoriesListener(GameModeInventories plugin) {
        this.plugin = plugin;
        containers.add(Material.ANVIL);
        containers.add(Material.BEACON);
        containers.add(Material.BREWING_STAND);
        containers.add(Material.BURNING_FURNACE);
        containers.add(Material.CHEST);
        containers.add(Material.DISPENSER);
        containers.add(Material.DROPPER);
        containers.add(Material.ENCHANTMENT_TABLE);
        containers.add(Material.ENDER_CHEST);
        containers.add(Material.FURNACE);
        containers.add(Material.HOPPER);
        containers.add(Material.JUKEBOX);
        containers.add(Material.TRAPPED_CHEST);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player p = event.getPlayer();
        GameMode newGM = event.getNewGameMode();
        if (newGM.equals(GameMode.SPECTATOR) && plugin.getConfig().getBoolean("restrict_spectator") && !p.hasPermission("gamemodeinventories.spectator")) {
            event.setCancelled(true);
            p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_SPECTATOR"));
            return;
        }
        if (p.hasPermission("gamemodeinventories.use")) {
            boolean savexp = plugin.getConfig().getBoolean("xp");
            boolean savearmour = plugin.getConfig().getBoolean("armor");
            boolean saveenderchest = plugin.getConfig().getBoolean("enderchest");
            boolean potions = plugin.getConfig().getBoolean("remove_potions");
            boolean attributes = plugin.getConfig().getBoolean("custom_attributes");
            if (p.isOnline()) {
                plugin.getInventoryHandler().switchInventories(p, p.getInventory(), savexp, savearmour, saveenderchest, potions, attributes, newGM);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(PlayerInteractEvent event) {
        if (plugin.getConfig().getBoolean("restrict_creative")) {
            Block b = event.getClickedBlock();
            if (b != null) {
                Player p = event.getPlayer();
                if (p.isSneaking() && isBlock(p.getItemInHand().getType())) {
                    return;
                }
                Material m = b.getType();
                GameMode gm = p.getGameMode();
                if (gm.equals(GameMode.CREATIVE) && containers.contains(m) && !GameModeInventoriesBypass.canBypass(p, "inventories", plugin) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
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
                if (p.getGameMode().equals(GameMode.CREATIVE) && !GameModeInventoriesBypass.canBypass(p, "inventories", plugin)) {
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
            if (p.getGameMode().equals(GameMode.CREATIVE) && plugin.getInventoryHandler().isInstanceOf(entity) && !GameModeInventoriesBypass.canBypass(p, "inventories", plugin)) {
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_INVENTORY"));
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        //treat it the same as interacting with an entity in general
        this.onEntityClick((PlayerInteractEntityEvent) event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (plugin.getConfig().getBoolean("no_drops")) {
            Player p = event.getPlayer();
            GameMode gm = p.getGameMode();
            if (gm.equals(GameMode.CREATIVE) && !GameModeInventoriesBypass.canBypass(p, "items", plugin)) {
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
            if (gm.equals(GameMode.CREATIVE) && !GameModeInventoriesBypass.canBypass(p, "items", plugin)) {
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
            if (gm.equals(GameMode.CREATIVE) && !GameModeInventoriesBypass.canBypass(p, "inventories", plugin)) {
                event.setCancelled(true);
                if (!plugin.getConfig().getBoolean("dont_spam_chat")) {
                    p.sendMessage(plugin.MY_PLUGIN_NAME + plugin.getM().getMessage().get("NO_CREATIVE_HORSE"));
                }
            }
        }
    }

    private boolean isBlock(Material m) {
        return !m.equals(Material.AIR) && m.isBlock();
    }
}
