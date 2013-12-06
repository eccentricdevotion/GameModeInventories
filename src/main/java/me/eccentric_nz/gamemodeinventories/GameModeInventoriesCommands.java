package me.eccentric_nz.gamemodeinventories;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GameModeInventoriesCommands implements CommandExecutor {

    private final GameModeInventories plugin;
    private final List<String> firstArgs = new ArrayList<String>();

    public GameModeInventoriesCommands(GameModeInventories plugin) {
        this.plugin = plugin;
        firstArgs.add("armor");
        firstArgs.add("debug");
        firstArgs.add("dont_spam_chat");
        firstArgs.add("enderchest");
        firstArgs.add("no_drops");
        firstArgs.add("no_pickups");
        firstArgs.add("remove_potions");
        firstArgs.add("restrict_creative");
        firstArgs.add("save_on_death");
        firstArgs.add("survival_on_world_change");
        firstArgs.add("xp");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("gmi")) {
            if (args.length == 0) {
                sender.sendMessage(GameModeInventoriesConstants.MY_PLUGIN_NAME + GameModeInventoriesConstants.HELP.split("\n"));
                return true;
            }
            if (sender.hasPermission("gamemodeinventories.admin")) {
                String option = args[0].toLowerCase(Locale.ENGLISH);
                if (args.length == 1 && firstArgs.contains(option)) {
                    boolean bool = !plugin.getConfig().getBoolean(option);
                    plugin.getConfig().set(option, bool);
                    sender.sendMessage(GameModeInventoriesConstants.MY_PLUGIN_NAME + option + " was set to: " + bool);
                    plugin.saveConfig();
                    return true;
                }
            } else {
                sender.sendMessage(GameModeInventoriesConstants.MY_PLUGIN_NAME + "You do not have permission to run that command!");
                return true;
            }
        }
        return false;
    }
}
