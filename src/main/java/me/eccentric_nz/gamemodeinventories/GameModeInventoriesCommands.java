package me.eccentric_nz.gamemodeinventories;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class GameModeInventoriesCommands implements CommandExecutor, TabCompleter {

    private final GameModeInventories plugin;
    private final HashMap<String, String> firstArgs = new HashMap<String, String>();
    private final ImmutableList<String> ROOT_SUBS;

    public GameModeInventoriesCommands(GameModeInventories plugin) {
        this.plugin = plugin;
        firstArgs.put("armor", "armor");
        firstArgs.put("creative_blacklist", "creative_blacklist");
        firstArgs.put("debug", "debug");
        firstArgs.put("dont_spam_chat", "dont_spam_chat");
        firstArgs.put("enderchest", "enderchest");
        firstArgs.put("no_drops", "no_drops");
        firstArgs.put("no_falling_drops", "no_falling_drops");
        firstArgs.put("no_pickups", "no_pickups");
        firstArgs.put("remove_potions", "remove_potions");
        firstArgs.put("restrict_creative", "restrict_creative");
        firstArgs.put("save_on_death", "save_on_death");
        firstArgs.put("xp", "xp");
        firstArgs.put("track_creative_place", "track_creative_place.enabled");
        firstArgs.put("break_no_drop", "track_creative_place.break_no_drop");
        ROOT_SUBS = ImmutableList.copyOf(firstArgs.keySet());
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
                if (args.length == 1 && firstArgs.containsKey(option)) {
                    boolean bool = !plugin.getConfig().getBoolean(firstArgs.get(option));
                    plugin.getConfig().set(firstArgs.get(option), bool);
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
