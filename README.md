##GameModeInventories

A CraftBukkit plugin for Minecraft Server.

Allow players to have separate inventories for each game mode (Creative, Survival and Adventure).

This plugin (and the GMIDatabaseConverter plugin) are available for download as a single ZIP file from the [GameModeInventories page on BukkitDev](http://dev.bukkit.org/bukkit-plugins/gamemodeinventories).

###Warning

This version of GameModeInventories uses a different storage format when saving inventories. Before installing this version, you should first run the [GMIDatabaseConverter](https://github.com/eccentricdevotion/GMIDatabaseConverter/blob/master/README.md) plugin on your CraftBukkit 1.6.4 server to update your GameModeInventories database.

###How do I update my GMI database?

**_Before_** upgrading your server to CraftBukkit 1.7.x and installing GameModeInventories version 2.x, you should run GMIDatabaseConverter on your **1.6.4** server.

1. Install GMIDatabaseConverter.jar to the server's plugins folder
2. Start the server
   * The plugin will attempt to find and backup your old GameModeInventories database file
   * It will then read the existing inventory data
   * The existing data will be converted to the new format
   * The new data will be written back to the database
3. Once conversion is complete, you can update your GameModeInventories plugin to version 2.x and restart the server
4. If you are satisfied that GameModeInventories version 2.x is functioning correctly, you can safely remove GMIDatabaseConverter


###Why did you change the storage format?

The format change is a result of code changes removing the reliance on using net.minecraft.server and org.craftbukkit code directly within the plugin (instead of using only the Bukkit API). This led to the plugin breaking with every Minecraft/CraftBukkit update. These code changes mean the plugin should no longer break between versions.
