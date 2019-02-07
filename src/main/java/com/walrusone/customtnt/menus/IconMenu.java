package com.walrusone.customtnt.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.walrusone.customtnt.CustomTNT;
import com.walrusone.customtnt.utils.Util;

// https://forums.bukkit.org/threads/icon-menu.108342/
public class IconMenu {

    private String name;
    private int size;
    private OptionClickEventHandler handler;
    private String[] optionNames;
    private ItemStack[] optionIcons;

    public IconMenu(String name, int size, OptionClickEventHandler handler) {
        this.name = name;
        this.size = size;
        this.handler = handler;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
    }

    public void setOption(int position, ItemStack icon, String name, String[] info) {
        this.optionNames[position] = name;
        this.optionIcons[position] = Util.get().name(icon, name, info);
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, this.size, this.name);
        for (int iii = 0; iii < this.optionIcons.length; iii++) {
            if (this.optionIcons[iii] != null) {
                inventory.setItem(iii, this.optionIcons[iii]);
            }
        }
        player.openInventory(inventory);
    }

    public void destroy() {
        this.handler = null;
        this.optionNames = null;
        this.optionIcons = null;
    }

    public void onInventoryClick(InventoryClickEvent event) {

        if (!event.getWhoClicked().getOpenInventory().getTitle().equals(name)) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
 
        try {
            if (!(slot >= 0 && slot < size && optionNames[slot] != null)) {
                return;
            }
        } catch (NullPointerException e) {
        	return;
        }


        OptionClickEvent clickEvent = new OptionClickEvent((Player) event.getWhoClicked(), optionNames[slot]);
        handler.onOptionClick(clickEvent);

        if (clickEvent.willClose()) {
            final Player player = (Player) event.getWhoClicked();

            Bukkit.getScheduler().runTaskLater(CustomTNT.get(), player::closeInventory, 1L);
        }

        if (clickEvent.willDestroy()) {
            destroy();
        }
    }

    public String getName() {
        return this.name;
    }

    public static class OptionClickEvent {

        private Player player;
        private String name;
        private boolean close;
        private boolean destroy;

        OptionClickEvent(Player player, String name) {
            this.player = player;
            this.name = name;
            this.close = false;
            this.destroy = false;
        }

        public Player getPlayer() {
            return this.player;
        }

        public String getName() {
            return this.name;
        }

        boolean willClose() {
            return this.close;
        }

        boolean willDestroy() {
            return this.destroy;
        }

        void setWillClose() {
            this.close = true;
        }

        void setWillDestroy() {
            this.destroy = true;
        }
    }

    public interface OptionClickEventHandler {

        void onOptionClick(IconMenu.OptionClickEvent event);
    }
}