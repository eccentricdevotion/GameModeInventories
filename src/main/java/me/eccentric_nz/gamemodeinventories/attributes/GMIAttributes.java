package me.eccentric_nz.gamemodeinventories.attributes;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.UUID;
import me.eccentric_nz.gamemodeinventories.attributes.GMINbtFactory.NbtCompound;
import me.eccentric_nz.gamemodeinventories.attributes.GMINbtFactory.NbtList;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Kristian
 */
public class GMIAttributes {

    // This may be modified
    public ItemStack stack;
    private final NbtList attributes;

    public GMIAttributes(ItemStack stack) {
        // Create a CraftItemStack (under the hood)
        this.stack = GMINbtFactory.getCraftItemStack(stack);

        // Load NBT
        NbtCompound nbt = GMINbtFactory.fromItemTag(this.stack);
        this.attributes = nbt.getList("AttributeModifiers", true);
    }

    /**
     * Retrieve the modified item stack.
     *
     * @return The modified item stack.
     */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * Retrieve the number of attributes.
     *
     * @return Number of attributes.
     */
    public int size() {
        return attributes.size();
    }

    /**
     * Add a new attribute to the list.
     *
     * @param attribute - the new attribute.
     */
    public void add(GMIAttribute attribute) {
        Preconditions.checkNotNull(attribute.getName(), "must specify an attribute name.");
        attributes.add(attribute.data);
    }

    /**
     * Remove the first instance of the given attribute.
     * <p>
     * The attribute will be removed using its UUID.
     *
     * @param attribute - the attribute to remove.
     * @return TRUE if the attribute was removed, FALSE otherwise.
     */
    public boolean remove(GMIAttribute attribute) {
        UUID uuid = attribute.getUUID();

        for (Iterator<GMIAttribute> it = values().iterator(); it.hasNext();) {
            if (Objects.equal(it.next().getUUID(), uuid)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public void clear() {
        attributes.clear();
    }

    /**
     * Retrieve the attribute at a given index.
     *
     * @param index - the index to look up.
     * @return The attribute at that index.
     */
    public GMIAttribute get(int index) {
        return new GMIAttribute((NbtCompound) attributes.get(index));
    }

    // We can't make GMIAttributes itself iterable without splitting it up into separate classes
    public Iterable<GMIAttribute> values() {
        return new Iterable<GMIAttribute>() {
            @Override
            public Iterator<GMIAttribute> iterator() {
                return Iterators.transform(attributes.iterator(),
                        new Function<Object, GMIAttribute>() {
                            @Override
                            public GMIAttribute apply(Object element) {
                                return new GMIAttribute((NbtCompound) element);
                            }
                        });
            }
        };
    }
}
