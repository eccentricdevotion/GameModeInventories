/*
 * Copyright (C) 2014 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.gamemodeinventories;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesSerialization {

    public static String toDatabase(ItemStack[] inventory) {
        byte[] bytes = ItemStack.serializeItemsAsBytes(inventory);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static ItemStack[] fromDatabase(String data) throws IOException {
        try {
            byte[] decoded = Base64.getDecoder().decode(data);
            return ItemStack.deserializeItemsFromBytes(decoded);
        } catch (IllegalArgumentException tryLegacy) {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getMimeDecoder().decode(data));
                ItemStack[] inventory;
                try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                    int size = dataInput.readInt();
                    inventory = new ItemStack[size];
                    // Read the serialized inventory
                    for (int i = 0; i < size; i++) {
                        inventory[i] = (ItemStack) dataInput.readObject();
                    }
                }
                return inventory;
            } catch (ClassNotFoundException e) {
                throw new IOException("Unable to decode class type.", e);
            }
        }
    }
}
