package dev.amraleth.rd;

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

    @Override
    public void onEnable() {
        LOGGER.info("Starting Runic Drives version {}.", VERSION);
    }

    @Override
    public void onDisable() {
        LOGGER.info("Stopping Runic Drives version {}.", VERSION);
    }
}
