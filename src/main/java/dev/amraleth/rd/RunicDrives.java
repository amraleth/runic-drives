package dev.amraleth.rd;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.amraleth.rd.serialisation.ItemStackListSerializerDeserializer;
import dev.amraleth.rd.serialisation.ItemStackSerializerDeserializer;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Main class responsible for loading and configuring the plugin
 *
 * @author amraleth
 */
public class RunicDrives extends JavaPlugin {
    public static final String VERSION = "1.0-alpha";
    public static final Logger LOGGER = LoggerFactory.getLogger(RunicDrives.class);
    public static final String NAMESPACE_KEY = "runicdrives";
    public static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .postProcessor(comp -> comp.decoration(TextDecoration.ITALIC, false))
            .build();
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ItemStack.class, new ItemStackSerializerDeserializer())
            .registerTypeAdapter(new TypeToken<List<String>>() {
            }.getType(), new ItemStackListSerializerDeserializer())
            .create();

    @Override
    public void onEnable() {
        LOGGER.info("Starting Runic Drives version {}.", VERSION);
    }

    @Override
    public void onDisable() {
        LOGGER.info("Stopping Runic Drives version {}.", VERSION);
    }
}
