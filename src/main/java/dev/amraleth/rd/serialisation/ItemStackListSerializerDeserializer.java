package dev.amraleth.rd.serialisation;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ItemStackListSerializerDeserializer implements JsonSerializer<List<ItemStack>>, JsonDeserializer<List<ItemStack>> {
    public static final Type TYPE_TOKEN = new TypeToken<List<ItemStack>>() {}.getType();

    @Override
    public JsonElement serialize(@NotNull List<ItemStack> itemStacks, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray array = new JsonArray();
        for (ItemStack item : itemStacks) {
            String base64  = Base64.getEncoder().encodeToString(item.serializeAsBytes());
            array.add(new JsonPrimitive(base64));
        }
        return array;
    }

    @Override
    public List<ItemStack> deserialize(@NotNull JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        List<ItemStack> itemStacks = new ArrayList<>();
        JsonArray array = jsonElement.getAsJsonArray();
        for (JsonElement element : array) {
            String base64 = element.getAsString();
            ItemStack item = ItemStack.deserializeBytes(Base64.getDecoder().decode(base64));
        }
        return itemStacks;
    }
}
