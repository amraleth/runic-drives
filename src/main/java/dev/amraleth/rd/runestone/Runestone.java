package dev.amraleth.rd.runestone;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import dev.amraleth.rd.RunicDrives;
import dev.amraleth.rd.exception.RunestoneInsertionException;
import dev.amraleth.rd.string.Formatting;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a runestone capable of storing up to {@link Runestone#MAX_TYPES} different item types and up to {@link Runestone#size}
 * different amount of items
 *
 * @author amraleth
 */
@Getter
public class Runestone {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
            .create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Integer>>() {
    }.getType();

    /**
     * The maximum amount of types allowed on a runestone
     */
    public static final int MAX_TYPES = 64;

    // Keys for PersistentDataContainer
    public static final NamespacedKey RUNESTONE_MAX_TYPES = new NamespacedKey(RunicDrives.NAMESPACE_KEY, "runestone_max_types");
    public static final NamespacedKey RUNESTONE_SIZE = new NamespacedKey(RunicDrives.NAMESPACE_KEY, "runestone_size");
    public static final NamespacedKey RUNESTONE_UUID = new NamespacedKey(RunicDrives.NAMESPACE_KEY, "runestone_uuid");

    public static final NamespacedKey RUNESTONE_ITEM_MAP = new NamespacedKey(RunicDrives.NAMESPACE_KEY, "runestone_item_map");

    public static final NamespacedKey RUNESTONE_USED_ITEMS = new NamespacedKey(RunicDrives.NAMESPACE_KEY, "runestone_used_items");
    public static final NamespacedKey RUNESTONE_USED_TYPES = new NamespacedKey(RunicDrives.NAMESPACE_KEY, "runestone_used_types");

    /**
     * The {@link ItemStack} this runestone is bound to
     */
    private final ItemStack runestoneItemStack;

    /**
     * The size of the runestone
     */
    private final int size;

    /**
     * The internal representation of the runestone
     */
    private final Map<ItemStack, Integer> runestoneItems;

    /**
     * The id of the runestone, might be used later
     *
     * @deprecated Currently not in use, might be used later to store the runestone's contents in a database
     */
    @Deprecated
    private final UUID uuid;

    /**
     * The amount of used types on the runestone
     */
    private int usedTypes;

    /**
     * The amount of used items on the runestone
     */
    private int usedItems;

    protected Runestone(@NotNull ItemStack itemStack, int size, @NotNull Map<ItemStack, Integer> runestoneItems,
                        @NotNull UUID uuid, int usedTypes, int usedItems) {
        this.runestoneItemStack = itemStack;
        this.size = size;
        this.runestoneItems = runestoneItems;
        this.uuid = uuid;
        this.usedTypes = usedTypes;
        this.usedItems = usedItems;
    }

    /**
     * Creates an empty (new) runestone
     *
     * @param size The size this runestone should have
     * @return A new runestone with a new {@link ItemStack}
     */
    public static @NotNull Runestone createEmpty(int size) {
        UUID uuid = UUID.randomUUID();
        Map<ItemStack, Integer> runestoneItems = new HashMap<>();

        return new Runestone(
                constructRunestoneItemStackFrom(uuid, size, runestoneItems),
                size,
                runestoneItems,
                uuid,
                0,
                0
        );
    }

    /**
     * Creates a runestone from a given {@link ItemStack}, please check if the item is really a runestone beforehand
     *
     * @param itemStack The ItemStack
     * @return A new runestone
     */
    public static @NotNull Runestone fromItemStack(@NotNull ItemStack itemStack) {
        PersistentDataContainer dataContainer = itemStack.getItemMeta().getPersistentDataContainer();

        assert dataContainer.has(RUNESTONE_UUID);

        UUID uuid = UUID.fromString(dataContainer.getOrDefault(RUNESTONE_UUID, PersistentDataType.STRING, UUID.randomUUID().toString()));
        int size = dataContainer.getOrDefault(RUNESTONE_SIZE, PersistentDataType.INTEGER, 1024);
        Map<ItemStack, Integer> runestoneItems = convertStringToRunestoneItemMap(dataContainer.getOrDefault(RUNESTONE_ITEM_MAP, PersistentDataType.STRING, ""));
        int usedTypes = dataContainer.getOrDefault(RUNESTONE_USED_TYPES, PersistentDataType.INTEGER, 0);
        int usedItems = dataContainer.getOrDefault(RUNESTONE_USED_ITEMS, PersistentDataType.INTEGER, 0);

        return new Runestone(
                itemStack, size, runestoneItems, uuid, usedTypes, usedItems
        );
    }

    /**
     * Adds an item to the runestone
     *
     * @param itemStack The ItemStack to add
     * @throws RunestoneInsertionException If the runestone is either full or there are no more types available
     */
    public void addItem(@NotNull ItemStack itemStack) throws RunestoneInsertionException {
        if (this.usedItems + 1 > this.size) {
            throw new RunestoneInsertionException("Cannot insert item because there is no space free on runestone.");
        }

        if (!this.runestoneItems.containsKey(itemStack)) {
            if (this.usedTypes + 1 > MAX_TYPES) {
                throw new RunestoneInsertionException("Cannot insert item because there are no types free on runestone.");
            }
            this.runestoneItems.put(itemStack, 1);
            this.usedTypes += 1;
        } else {
            this.runestoneItems.put(itemStack, this.runestoneItems.get(itemStack) + 1);
        }
        this.usedItems += 1;

        updateItemStack();
    }

    /**
     * Adds multiple items of the same type to the runestone
     *
     * @param itemStack The {@link ItemStack} to add
     * @param count     The amount of items to add
     * @return The remaining items that could not be inserted if available, otherwise 0
     * @throws RunestoneInsertionException If the runestone has no free types
     */
    public int addMultipleItems(@NotNull ItemStack itemStack, int count) throws RunestoneInsertionException {
        int availableSpace = this.size - this.usedItems;
        int res = 0;

        if (count > availableSpace) {
            res = count - availableSpace;
            count = availableSpace;
        }

        if (!this.runestoneItems.containsKey(itemStack)) {
            if (this.usedTypes + 1 > MAX_TYPES) {
                throw new RunestoneInsertionException("Cannot insert item because there are no types free on runestone.");
            }
            this.runestoneItems.put(itemStack, count);
            this.usedTypes += 1;
        } else {
            this.runestoneItems.put(itemStack, this.runestoneItems.get(itemStack) + count);
        }
        this.usedItems += count;

        updateItemStack();
        return res;
    }

    /**
     * Updates the underlying item (lore and PersistentDataContainer)
     */
    private void updateItemStack() {
        ItemMeta itemMeta = this.runestoneItemStack.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

        dataContainer.set(RUNESTONE_MAX_TYPES, PersistentDataType.INTEGER, MAX_TYPES);
        dataContainer.set(RUNESTONE_SIZE, PersistentDataType.INTEGER, this.size);
        dataContainer.set(RUNESTONE_UUID, PersistentDataType.STRING, this.uuid.toString());
        dataContainer.set(RUNESTONE_ITEM_MAP, PersistentDataType.STRING, convertRunestoneItemMapToString(this.runestoneItems));
        dataContainer.set(RUNESTONE_USED_ITEMS, PersistentDataType.INTEGER, this.usedItems);
        dataContainer.set(RUNESTONE_USED_TYPES, PersistentDataType.INTEGER, this.usedTypes);

        itemMeta.lore(List.of(
                RunicDrives.MINI_MESSAGE.deserialize(
                        "<green>" + this.usedItems + "<gray> / <green>" + size + " items"
                ),
                RunicDrives.MINI_MESSAGE.deserialize(
                        "<green>" + this.usedTypes + "<gray> / <green>" + MAX_TYPES + " types"
                )
        ));

        this.runestoneItemStack.setItemMeta(itemMeta);
    }

    /**
     * Constructs a new runestone {@link ItemStack} from given input
     *
     * @param uuid           The uuid of the runestone
     * @param size           The size of the runestone
     * @param runestoneItems All items this runestone contains at this point
     * @return The {@link ItemStack}
     */
    private static @NotNull ItemStack constructRunestoneItemStackFrom(@NotNull UUID uuid, int size,
                                                                      @NotNull Map<ItemStack, Integer> runestoneItems) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();

        // internal item data
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(RUNESTONE_MAX_TYPES, PersistentDataType.INTEGER, MAX_TYPES);
        dataContainer.set(RUNESTONE_SIZE, PersistentDataType.INTEGER, size);
        dataContainer.set(RUNESTONE_UUID, PersistentDataType.STRING, uuid.toString());
        dataContainer.set(RUNESTONE_ITEM_MAP, PersistentDataType.STRING, convertRunestoneItemMapToString(runestoneItems));
        dataContainer.set(RUNESTONE_USED_ITEMS, PersistentDataType.INTEGER, 0);
        dataContainer.set(RUNESTONE_USED_TYPES, PersistentDataType.INTEGER, 0);

        itemMeta.displayName(RunicDrives.MINI_MESSAGE.deserialize(
                "<gray>" + Formatting.formatSizeToStringRepresentation(size) + " Runestone"
        ));

        itemMeta.lore(List.of(
                RunicDrives.MINI_MESSAGE.deserialize(
                        "<green>" + getItemCountFromRunestoneItemMap(runestoneItems) + "<gray> / <green>" + size + " items"
                ),
                RunicDrives.MINI_MESSAGE.deserialize(
                        "<green>" + runestoneItems.size() + "<gray> / <green>" + MAX_TYPES + " types"
                )
        ));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static int getItemCountFromRunestoneItemMap(@NotNull Map<ItemStack, Integer> runeStoneItems) {
        AtomicInteger count = new AtomicInteger();
        runeStoneItems.forEach((x, y) -> count.addAndGet(1));
        return count.get();
    }

    public static @NotNull String convertRunestoneItemMapToString(@NotNull Map<ItemStack, Integer> runestoneItems) {
        Map<String, Integer> stringMap = new HashMap<>();
        for (Map.Entry<ItemStack, Integer> entry : runestoneItems.entrySet()) {
            String encodedItem = gson.toJson(entry.getKey(), ItemStack.class);
            stringMap.put(encodedItem, entry.getValue());
        }
        return gson.toJson(stringMap);
    }

    public static @NotNull Map<ItemStack, Integer> convertStringToRunestoneItemMap(@NotNull String runestoneItems) {
        Map<ItemStack, Integer> res = new HashMap<>();
        Map<String, Integer> stringMap = gson.fromJson(runestoneItems, MAP_TYPE);
        if (stringMap != null) {
            for (Map.Entry<String, Integer> entry : stringMap.entrySet()) {
                ItemStack item = gson.fromJson(entry.getKey(), ItemStack.class);
                res.put(item, entry.getValue());
            }
        }
        return res;
    }

    private static class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

        @Contract("_, _, _ -> new")
        @Override
        public @NotNull JsonElement serialize(@NotNull ItemStack itemStack, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(Base64.getEncoder().encodeToString(itemStack.serializeAsBytes()));
        }

        @Override
        public @NotNull ItemStack deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return ItemStack.deserializeBytes(Base64.getDecoder().decode(jsonElement.getAsString()));
        }

    }

}
