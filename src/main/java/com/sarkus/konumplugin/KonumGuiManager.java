package com.sarkus.konumplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KonumGuiManager implements Listener {

    public static final String GUI_TITLE = ChatColor.BLUE + "Kaydedilen Konumlar";

    private Map<UUID, Map<String, Location>> playerLocations;

    public KonumGuiManager(Map<UUID, Map<String, Location>> playerLocations) {
        this.playerLocations = playerLocations;
    }

    public void openKonumGui(Player player, Map<String, Location> playerSavedLocations) {
        if (playerSavedLocations == null || playerSavedLocations.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Henüz hiçbir konum kaydetmediniz.");
            return;
        }

        int numberOfLocations = playerSavedLocations.size();
        int guiSize = ((numberOfLocations + 8) / 9) * 9;
        if (guiSize > 54) guiSize = 54;

        Inventory gui = Bukkit.createInventory(null, guiSize, GUI_TITLE);

        int slot = 0;
        for (Map.Entry<String, Location> entry : playerSavedLocations.entrySet()) {
            if (slot >= guiSize) break;

            String locationName = entry.getKey();
            Location location = entry.getValue();

            ItemStack locationItem = new ItemStack(Material.COMPASS);

            ItemMeta meta = locationItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + locationName);

                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Dünya: " + ChatColor.WHITE + location.getWorld().getName());
                lore.add(ChatColor.GRAY + "Koordinatlar:");
                lore.add(ChatColor.WHITE + "X: " + (int) location.getX() + ", Y: " + (int) location.getY() + ", Z: " + (int) location.getZ());
                lore.add("");
                lore.add(ChatColor.GREEN + "Tıklayarak ışınlanın!");

                meta.setLore(lore);
                locationItem.setItemMeta(meta);
            }

            gui.setItem(slot, locationItem);
            slot++;
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType().isAir()) {
                return;
            }

            Player player = (Player) event.getWhoClicked();

            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return;
            }

            String locationNameWithColor = meta.getDisplayName();
            String locationName = ChatColor.stripColor(locationNameWithColor).toLowerCase();

            Map<String, Location> savedLocationsForThisPlayer = playerLocations.get(player.getUniqueId());
            if (savedLocationsForThisPlayer == null) {
                player.sendMessage(ChatColor.RED + "Konum verisi alınamadı.");
                player.closeInventory();
                return;
            }
            Location targetLocation = savedLocationsForThisPlayer.get(locationName);

            if (targetLocation != null) {
                player.closeInventory();
                player.teleport(targetLocation);
                player.sendMessage(ChatColor.GREEN + "'" + ChatColor.stripColor(locationNameWithColor) + "' konumuna ışınlanıldı!");
            } else {
                player.sendMessage(ChatColor.RED + "Hedef konum bulunamadı!");
                player.closeInventory();
            }
        }
    }
}