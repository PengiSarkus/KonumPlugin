package com.sarkus.konumplugin;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KonumPlugin extends JavaPlugin {

    private Map<UUID, Map<String, Location>> playerLocations;
    private KonumGuiManager guiManager;
    private File locationsFile;
    private FileConfiguration locationsConfig;

    @Override
    public void onEnable() {
        getLogger().info("KonumPlugin started!");

        locationsFile = new File(getDataFolder(), "locations.yml");
        if (!locationsFile.exists()) {
            locationsFile.getParentFile().mkdirs();
            try {
                locationsFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Could not create locations.yml file!");
                e.printStackTrace();
            }
        }
        locationsConfig = YamlConfiguration.loadConfiguration(locationsFile);

        this.playerLocations = loadLocations();
        getLogger().info("Loaded " + playerLocations.size() + " player(s) locations.");

        this.guiManager = new KonumGuiManager(this.playerLocations);

        this.getCommand("konum").setExecutor(new KonumCommand(this.playerLocations, this.guiManager));

        getServer().getPluginManager().registerEvents(guiManager, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("KonumPlugin stopping, saving data...");

        saveLocations();
        getLogger().info("Location data saved.");

        getLogger().info("KonumPlugin stopped.");
    }

    private Map<UUID, Map<String, Location>> loadLocations() {
        Map<UUID, Map<String, Location>> loadedLocations = new HashMap<>();

        ConfigurationSection playersSection = locationsConfig.getConfigurationSection("players");
        if (playersSection == null) {
            return loadedLocations;
        }

        for (String playerUUIDString : playersSection.getKeys(false)) {
            UUID playerUUID;
            try {
                playerUUID = UUID.fromString(playerUUIDString);
            } catch (IllegalArgumentException e) {
                getLogger().warning("Invalid UUID found in config: " + playerUUIDString);
                continue;
            }

            ConfigurationSection playerSection = playersSection.getConfigurationSection(playerUUIDString);
            if (playerSection == null) continue;

            Map<String, Location> savedLocationsForPlayer = new HashMap<>();

            for (String locationName : playerSection.getKeys(false)) {
                ConfigurationSection locationSection = playerSection.getConfigurationSection(locationName);
                if (locationSection == null) continue;

                String worldName = locationSection.getString("world");
                double x = locationSection.getDouble("x");
                double y = locationSection.getDouble("y");
                double z = locationSection.getDouble("z");
                double pitch = locationSection.getDouble("pitch", 0.0);
                double yaw = locationSection.getDouble("yaw", 0.0);

                org.bukkit.World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    getLogger().warning("World '" + worldName + "' not found for location '" + locationName + "' for player " + playerUUIDString + ". Skipping.");
                    continue;
                }

                Location location = new Location(world, x, y, z, (float) yaw, (float) pitch);

                savedLocationsForPlayer.put(locationName, location);
            }

            loadedLocations.put(playerUUID, savedLocationsForPlayer);
        }

        return loadedLocations;
    }

    private void saveLocations() {
        locationsConfig.set("players", null);

        ConfigurationSection playersSection = locationsConfig.createSection("players");

        for (Map.Entry<UUID, Map<String, Location>> playerEntry : playerLocations.entrySet()) {
            UUID playerUUID = playerEntry.getKey();
            Map<String, Location> savedLocationsForPlayer = playerEntry.getValue();

            if (savedLocationsForPlayer == null || savedLocationsForPlayer.isEmpty()) {
                continue;
            }

            ConfigurationSection playerSection = playersSection.createSection(playerUUID.toString());

            for (Map.Entry<String, Location> locationEntry : savedLocationsForPlayer.entrySet()) {
                String locationName = locationEntry.getKey();
                Location location = locationEntry.getValue();

                ConfigurationSection locationSection = playerSection.createSection(locationName);

                locationSection.set("world", location.getWorld().getName());
                locationSection.set("x", location.getX());
                locationSection.set("y", location.getY());
                locationSection.set("z", location.getZ());
                locationSection.set("pitch", location.getPitch());
                locationSection.set("yaw", location.getYaw());
            }
        }

        try {
            locationsConfig.save(locationsFile);
        } catch (IOException e) {
            getLogger().severe("Could not save locations.yml file!");
            e.printStackTrace();
        }
    }
}