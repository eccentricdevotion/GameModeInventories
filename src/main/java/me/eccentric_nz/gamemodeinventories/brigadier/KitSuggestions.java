package me.eccentric_nz.gamemodeinventories.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class KitSuggestions {

    private static final Set<String> KIT_ARGS = Set.of(
            "load", "save"
    );

    public static CompletableFuture<Suggestions> get(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        KIT_ARGS.stream()
                .filter(c -> c.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
