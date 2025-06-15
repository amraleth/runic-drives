package dev.amraleth.rd.serialisation;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Base64;

public class ItemStackSerializerDeserializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

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
