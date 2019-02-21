package com.walrusone.customtnt.menus;

import java.util.List;

import com.walrusone.customtnt.tnts.TNTManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.walrusone.customtnt.CustomTNT;
import com.walrusone.customtnt.utils.Messaging;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class TimebombMenu {

    private static final int menuSlotsPerRow = 9;
    private static final String menuName = new Messaging.MessageFormatter().format("tnt.timebomb.menu-title");
    
    public TimebombMenu(final Player player, final Location location) {

        CustomTNT.getIC().create(player, menuName, menuSlotsPerRow, event -> {
            if (event.getName() == null) {
                return;
            }

            int fuse = Integer.valueOf(ChatColor.stripColor(event.getName()).replace(" Seconds", ""));

            event.setWillClose();
            event.setWillDestroy();

                Block block = player.getWorld().getBlockAt(location);
                if (CustomTNT.getTntHandler().getTNTBlocks().contains(block)) {
                    block.setType(Material.AIR);
                    Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
                    TNTPrimed tnt = player.getWorld().spawn(loc, TNTPrimed.class);
                    if(CustomTNT.getTntHandler().getExplosionType(TNTManager.TNTType.TIMEBOMB).isPunchable()) {
                        CustomTNT.getTntHandler().getPunchables().add(tnt);
                    }
                tnt.setFuseTicks(fuse * 20);
                }
        });

            List<String> loreList = Lists.newLinkedList();

            CustomTNT.getIC().setOption(
                    player,
                    0,
                    new ItemStack(CustomTNT.getNMS().getClockMaterial(), CustomTNT.get().getConfig().getInt("tntTypes.timebomb.option1")),
                    new Messaging.MessageFormatter().setVariable("length", "" + CustomTNT.get().getConfig().getInt("tntTypes.timebomb.option1") + " Seconds").format("tnt.timebomb.menu-time-color"),
                    loreList.toArray(new String[0]));
            
            CustomTNT.getIC().setOption(
                    player,
                    2,
                    new ItemStack(CustomTNT.getNMS().getClockMaterial(), CustomTNT.get().getConfig().getInt("tntTypes.timebomb.option2")),
                    new Messaging.MessageFormatter().setVariable("length", "" + CustomTNT.get().getConfig().getInt("tntTypes.timebomb.option2") + " Seconds").format("tnt.timebomb.menu-time-color"),
                    loreList.toArray(new String[0]));
            
            CustomTNT.getIC().setOption(
                    player,
                    4,
                    new ItemStack(CustomTNT.getNMS().getClockMaterial(), CustomTNT.get().getConfig().getInt("tntTypes.timebomb.option3")),
                    new Messaging.MessageFormatter().setVariable("length", "" + CustomTNT.get().getConfig().getInt("tntTypes.timebomb.option3") + " Seconds").format("tnt.timebomb.menu-time-color"),
                    loreList.toArray(new String[0]));
            
            CustomTNT.getIC().setOption(
                    player,
                    6,
                    new ItemStack(CustomTNT.getNMS().getClockMaterial(), CustomTNT.get().getConfig().getInt("tntTypes.timebomb.option4")),
                    new Messaging.MessageFormatter().setVariable("length", "" + CustomTNT.get().getConfig().getInt("tntTypes.timebomb.option4") + " Seconds").format("tnt.timebomb.menu-time-color"),
                    loreList.toArray(new String[0]));
            
            CustomTNT.getIC().setOption(
                    player,
                    8,
                    new ItemStack(CustomTNT.getNMS().getClockMaterial(), CustomTNT.get().getConfig().getInt("tntTypes.timebomb.option5")),
                    new Messaging.MessageFormatter().setVariable("length", "" + CustomTNT.get().getConfig().getInt("tntTypes.timebomb.option5") + " Seconds").format("tnt.timebomb.menu-time-color"),
                    loreList.toArray(new String[0]));

        CustomTNT.getIC().show(player);
    }
        

}
