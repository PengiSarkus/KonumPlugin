package com.sarkus.konumplugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KonumCommand implements CommandExecutor {

    private Map<UUID, Map<String, Location>> playerLocations;
    private KonumGuiManager guiManager;

    public KonumCommand(Map<UUID, Map<String, Location>> playerLocations, KonumGuiManager guiManager) {
        this.playerLocations = playerLocations;
        this.guiManager = guiManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Sadece oyuncular kullanabilir.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        playerLocations.putIfAbsent(playerUUID, new HashMap<>());
        Map<String, Location> locations = playerLocations.get(playerUUID);

        if (args.length == 1) {
            String locationName = args[0].toLowerCase();

            if (locationName.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Konum adı boş olamaz.");
                return true;
            }

            Location currentLocation = player.getLocation();

            if (locations.containsKey(locationName)) {
                player.sendMessage(ChatColor.YELLOW + "'" + args[0] + "' adında bir konum kaydınız var. Üzerine yazılıyor.");
            }

            int maxLocations = 10;
            if (locations.size() >= maxLocations && !locations.containsKey(locationName)) {
                player.sendMessage(ChatColor.RED + "Maksimum " + maxLocations + " konum kaydedebilirsiniz.");
                return true;
            }

            locations.put(locationName, currentLocation);

            player.sendMessage(ChatColor.GREEN + "Konum '" + args[0] + "' kaydedildi!");

            return true;

        } else if (args.length == 0) {
            if (locations.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "Henüz hiçbir konum kaydetmediniz. '/konum <ad>' kullanarak kaydedebilirsiniz.");
                return true;
            }

            guiManager.openKonumGui(player, locations);

            return true;

        } else {
            player.sendMessage(ChatColor.RED + "Yanlış kullanım. Doğru kullanım: /konum <ad> (kaydetmek için) veya /konum (listelemek ve ışınlanmak için)");
            return false;
        }
    }
}