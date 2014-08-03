/*
 *  Copyright 2014 eccentric_nz.
 */
package me.eccentric_nz.gamemodeinventories.attributes;

import java.util.UUID;

/**
 *
 * @author Kristian
 */
// Makes it easier to construct an attribute
public class GMIAttributeBuilder {

    public double amount;
    public GMIAttributeOperation operation = GMIAttributeOperation.ADD_NUMBER;
    public GMIAttributeType type;
    public String name;
    public UUID uuid;

    public GMIAttributeBuilder() {
        // Don't make this accessible
    }

    public GMIAttributeBuilder amount(double amount) {
        this.amount = amount;
        return this;
    }

    public GMIAttributeBuilder operation(GMIAttributeOperation operation) {
        this.operation = operation;
        return this;
    }

    public GMIAttributeBuilder type(GMIAttributeType type) {
        this.type = type;
        return this;
    }

    public GMIAttributeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public GMIAttributeBuilder uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public GMIAttribute build() {
        return new GMIAttribute(this);
    }
}
