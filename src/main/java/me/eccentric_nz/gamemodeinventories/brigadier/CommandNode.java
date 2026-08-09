package me.eccentric_nz.gamemodeinventories.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.eccentric_nz.gamemodeinventories.GameModeInventories;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;

public class CommandNode {

    private final GameModeInventories plugin;
    private final HashMap<String, String> firstArgs = new HashMap<>();

    public CommandNode(GameModeInventories plugin) {
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
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("gmi")
                .requires(ctx -> ctx.getSender().hasPermission("gamemodeinventories.admin"))
                .then(Commands.argument("option", StringArgumentType.word())
                        .suggests(ConfigSuggestions::get)
                        .executes(ctx -> {
                            String option = ctx.getArgument("option", String.class).toLowerCase(Locale.ROOT);
                            if (firstArgs.containsKey(option)) {
                                boolean bool = !plugin.getConfig().getBoolean(firstArgs.get(option));
                                plugin.getConfig().set(firstArgs.get(option), bool);
                                ctx.getSource().getSender().sendMessage(plugin.MY_PLUGIN_NAME + String.format(plugin.getM().getMessage().get("CONFIG_SET"), option, bool));
                                plugin.saveConfig();
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("kit")
                        .requires(ctx -> ctx.getSender() instanceof Player)
                        .then(Commands.argument("action", StringArgumentType.word())
                        .suggests(KitSuggestions::get)
                        .executes(ctx -> {
                            String action = ctx.getArgument("action", String.class).toLowerCase(Locale.ROOT);
                            Player player = (Player) ctx.getSource().getSender();
                            new KitCommand().process(plugin, action, player);
                            return Command.SINGLE_SUCCESS;
                        }))
                );
        return command.build();
    }
}
