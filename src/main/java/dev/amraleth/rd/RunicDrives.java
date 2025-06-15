package dev.amraleth.rd;

import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public void onEnable() {
        LOGGER.info("Starting Runic Drives version {}.", VERSION);


    }

    @Override
    public void onDisable() {
        LOGGER.info("Stopping Runic Drives version {}.", VERSION);
    }
}
