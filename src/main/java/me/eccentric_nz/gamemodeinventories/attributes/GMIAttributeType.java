/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories.attributes;

import com.google.common.collect.Maps;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Kristian
 */
public class GMIAttributeType {

    private static final ConcurrentMap<String, GMIAttributeType> LOOKUP = Maps.newConcurrentMap();
    public static final GMIAttributeType GENERIC_ARMOR = new GMIAttributeType("generic.armor").register();
    public static final GMIAttributeType GENERIC_ATTACK_DAMAGE = new GMIAttributeType("generic.attackDamage").register();
    public static final GMIAttributeType GENERIC_ATTACK_SPEED = new GMIAttributeType("generic.attackSpeed").register();
    public static final GMIAttributeType GENERIC_FOLLOW_RANGE = new GMIAttributeType("generic.followRange").register();
    public static final GMIAttributeType GENERIC_KNOCKBACK_RESISTANCE = new GMIAttributeType("generic.knockbackResistance").register();
    public static final GMIAttributeType GENERIC_LUCK = new GMIAttributeType("generic.luck").register();
    public static final GMIAttributeType GENERIC_MAX_HEALTH = new GMIAttributeType("generic.maxHealth").register();
    public static final GMIAttributeType GENERIC_MOVEMENT_SPEED = new GMIAttributeType("generic.movementSpeed").register();

    private final String minecraftId;

    /**
     * Construct a new attribute type.
     * <p>
     * Remember to {@link #register()} the type.
     *
     * @param minecraftId - the ID of the type.
     */
    public GMIAttributeType(String minecraftId) {
        this.minecraftId = minecraftId;
    }

    /**
     * Retrieve the associated minecraft ID.
     *
     * @return The associated ID.
     */
    public String getMinecraftId() {
        return minecraftId;
    }

    /**
     * Register the type in the central registry.
     *
     * @return The registered type.
     */
    // Constructors should have no side-effects!
    public GMIAttributeType register() {
        GMIAttributeType old = LOOKUP.putIfAbsent(minecraftId, this);
        return old != null ? old : this;
    }

    /**
     * Retrieve the attribute type associated with a given ID.
     *
     * @param minecraftId The ID to search for.
     * @return The attribute type, or NULL if not found.
     */
    public static GMIAttributeType fromId(String minecraftId) {
        return LOOKUP.get(minecraftId);
    }

    /**
     * Retrieve every registered attribute type.
     *
     * @return Every type.
     */
    public static Iterable<GMIAttributeType> values() {
        return LOOKUP.values();
    }
}
