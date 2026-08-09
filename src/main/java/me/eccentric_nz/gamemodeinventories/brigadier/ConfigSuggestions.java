package me.eccentric_nz.gamemodeinventories.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ConfigSuggestions {

    private static final Set<String> CMD_ARGS = Set.of(
            "armor", "attached_block", "break_bedrock", "break_no_drop", "bypass.blacklist",
            "bypass.commands", "bypass.inventories", "bypass.items", "bypass.survival", "bypass.trades",
            "command_blacklist", "creative_blacklist", "debug", "dont_spam_chat", "enderchest",
            "no_creative_pvp", "no_drops", "no_falling_drops", "no_pickups", "no_villager_trade",
            "no_golem_spawn", "no_wither_spawn", "no_seeds_from_pumpkin", "no_piston_move",
            "remove_potions", "restrict_creative", "restrict_spectator", "save_on_death",
            "survival_on_world_change", "switch_to", "track_creative_place", "xp"
    );

    public static CompletableFuture<Suggestions> get(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        CMD_ARGS.stream()
                .filter(c -> c.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
