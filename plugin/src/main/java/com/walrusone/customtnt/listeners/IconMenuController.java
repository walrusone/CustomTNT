package com.walrusone.customtnt.listeners;

import com.google.common.collect.Maps;
import com.walrusone.customtnt.menus.IconMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

//https://github.com/Wietje/SkyWars/blob/master/src/main/java/vc/pvp/skywars/controllers/IconMenuController.java/
public class IconMenuController implements Listener {

    private final Map<Player, IconMenu> menu = Maps.newHashMap();

    public IconMenuController() {
    }

    public void create(Player player, String name, int size, IconMenu.OptionClickEventHandler handler) {
        if (player != null) {
        	destroy(player);
            menu.put(player, new IconMenu(name, size, handler));
        }
    }

    public void show(Player player) {
        if (menu.containsKey(player)) {
            menu.get(player).open(player);
        }
    }

    public void setOption(Player player, int position, ItemStack icon, String name, String[] info) {
        if (menu.containsKey(player)) {
            menu.get(player).setOption(position, icon, name, info);
        }
    }

    private void destroy(Player player) {
        if (menu.containsKey(player)) {
            menu.remove(player).destroy();
            player.getOpenInventory().close();
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && menu.containsKey(event.getWhoClicked())) {
            menu.get(event.getWhoClicked()).onInventoryClick(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player && menu.containsKey(event.getPlayer())) {
                destroy((Player) event.getPlayer());
        }
    }
}