/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories.attributes;

import com.google.common.base.Preconditions;
import java.util.UUID;

/**
 *
 * @author Kristian
 */
public class GMIAttribute {

    public GMINbtFactory.NbtCompound data;

    public GMIAttribute(GMIAttributeBuilder builder) {
        data = GMINbtFactory.createCompound();
        setAmount(builder.amount);
        setOperation(builder.operation);
        setAttributeType(builder.type);
        setName(builder.name);
        setUUID(builder.uuid);
    }

    public GMIAttribute(GMINbtFactory.NbtCompound data) {
        this.data = data;
    }

    public double getAmount() {
        return data.getDouble("Amount", 0.0);
    }

    public final void setAmount(double amount) {
        data.put("Amount", amount);
    }

    public GMIAttributeOperation getOperation() {
        return GMIAttributeOperation.fromId(data.getInteger("Operation", 0));
    }

    public final void setOperation(GMIAttributeOperation operation) {
        Preconditions.checkNotNull(operation, "operation cannot be NULL.");
        data.put("Operation", operation.getId());
    }

    public GMIAttributeType getAttributeType() {
        return GMIAttributeType.fromId(data.getString("AttributeName", null));
    }

    public final void setAttributeType(GMIAttributeType type) {
        Preconditions.checkNotNull(type, "type cannot be NULL.");
        data.put("AttributeName", type.getMinecraftId());
    }

    public String getName() {
        return data.getString("Name", null);
    }

    public final void setName(String name) {
        Preconditions.checkNotNull(name, "name cannot be NULL.");
        data.put("Name", name);
    }

    public UUID getUUID() {
        return new UUID(data.getLong("UUIDMost", null), data.getLong("UUIDLeast", null));
    }

    public final void setUUID(UUID id) {
        Preconditions.checkNotNull("id", "id cannot be NULL.");
        data.put("UUIDLeast", id.getLeastSignificantBits());
        data.put("UUIDMost", id.getMostSignificantBits());
    }

    /**
     * Construct a new attribute builder with a random UUID and default
     * operation of adding numbers.
     *
     * @return The attribute builder.
     */
    public static GMIAttributeBuilder newBuilder() {
        return new GMIAttributeBuilder().uuid(UUID.randomUUID()).operation(GMIAttributeOperation.ADD_NUMBER);
    }

}
