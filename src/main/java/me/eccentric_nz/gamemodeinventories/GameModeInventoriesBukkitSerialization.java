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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author eccentric_nz
 */
public class GameModeInventoriesBukkitSerialization {

    public static String toDatabase(ItemStack[] inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // Write the size of the inventory
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                // Write the size of the inventory
                dataOutput.writeInt(inventory.length);
                // Save every element in the list
                for (ItemStack is : inventory) {
                    // check for player heads with no data...
                    if (is != null && is.getType().equals(Material.PLAYER_HEAD)) {
                        if (is.hasItemMeta()) {
                            SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
                            if (skullMeta.getOwnerProfile() == null) {
                                // remove item meta
                                is.setItemMeta(null);
                            }
                        }
                    }
                    dataOutput.writeObject(is);
                }
                // Serialize that array
            }
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] fromDatabase(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
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
