package me.eccentric_nz.gamemodeinventories;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

/**
 * <p>
 * Complete rewrite of the class to match new Exp logic. Removes the lookup tables and makes it way lighter.
 * For the future refer to the <a href="https://minecraft.wiki/w/Experience">Minecraft Wiki</a>
 * <p>
 */
public class GameModeInventoriesXPCalculator {

    private final WeakReference<Player> player;
    private final String playerName;

    /**
     * Create a new GameModeInventoriesXPCalculator for the given player.
     *
     * @param player the player for this GameModeInventoriesXPCalculator object
     * @throws IllegalArgumentException if the player is null
     */
    GameModeInventoriesXPCalculator(Player player) {
        Preconditions.checkNotNull(player, "Player cannot be null");
        this.player = new WeakReference<>(player);
        playerName = player.getName();
    }

    /**
     * Get the Player associated with this GameModeInventoriesXPCalculator.
     *
     * @return the Player object
     * @throws IllegalStateException if the player is no longer online
     */
    public Player getPlayer() {
        Player p = player.get();
        if (p == null) {
            throw new IllegalStateException("Player " + playerName + " is not online");
        }
        return p;
    }

    /**
     * Set the player's fractional experience.
     *
     * @param amt Amount of XP, should not be negative
     */
    public void setExp(double amt) {
        setExp(0, amt);
    }

    private void setExp(double base, double amt) {
        double xp = Math.max(base + amt, 0);

        Player p = getPlayer();
        int curLvl = p.getLevel();
        int newLvl = getLevelForExp(xp);

        // Increment level
        if (curLvl != newLvl) {
            p.setLevel(newLvl);
        }

        p.setExp(getFractionalExp(newLvl, xp));
    }

    /**
     * Get the player's current XP total.
     *
     * @return the player's total XP
     */
    double getCurrentExp() {
        Player p = getPlayer();

        int lvl = p.getLevel();
        return getXpForLevel(lvl) + Math.round(getXpNeededToLevelUp(lvl) * p.getExp());
    }

    /**
     * Equivalent to Player#getExp
     *
     * @return Gets the players current experience points towards the next level.
     */
    public static float getFractionalExp(int level, double exp) {
        return (float) ((exp - getXpForLevel(level))/getXpNeededToLevelUp(level));
    }

    /**
     * Get the level
     * Theoretically the decimal part could be used for setExp, but since it's an approximation
     * sometimes it's wrong by a single exp point, e.g. if you set your exp to 1520 the points will slowly
     * increase by one when switching gamemodes
     *
     * @param exp the amount to check for
     * @return the level that a player with this amount total XP would be
     * @throws IllegalArgumentException if the given XP is less than 0
     */
    public static int getLevelForExp(double exp) {
        Preconditions.checkArgument(exp >= 0, "Experience may not be negative.");
        return exp > 1507
            ? (int)((325 + Math.sqrt(72 * exp - 54215)) / 18)
            : exp > 352
                ? (int)((81 + Math.sqrt(40 * exp - 7839)) / 10)
                : (int)(Math.sqrt(exp + 9) - 3);
    }

    /**
     * Retrieves the amount of experience the experience bar can hold at the given level.
     *
     * @param level the level to check
     * @return the amount of experience at this level in the level bar
     * @throws IllegalArgumentException if the level is less than 0
     */
    public static double getXpNeededToLevelUp(int level) {
        Preconditions.checkArgument(level >= 0, "Level may not be negative.");
        return level > 30
            ? 9 * level - 158
            : level > 15
                ? 5 * level - 38
                : 2 * level + 7;
    }

    /**
     * Return the total XP needed to be the given level.
     *
     * @param level The level to check for.
     * @return The amount of XP needed for the level.
     * @throws IllegalArgumentException if the level is less than 0
     */
    public static double getXpForLevel(int level) {
        Preconditions.checkArgument(level >= 0, "Level may not be negative.");
        return level > 31
            ? 4.5 * level * level - 162.5 * level + 2220
            : level > 16
                ? 2.5 * level * level - 40.5 * level + 360
                : (double)level * level + 6 * level;
    }
}