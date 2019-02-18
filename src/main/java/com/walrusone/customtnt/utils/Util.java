package com.walrusone.customtnt.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.DelayedRegionOverlapAssociation;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.customtnt.CustomTNT;

public class Util {

	private static Util instance;
	
	public static Util get() {
        if (Util.instance == null) {
            Util.instance = new Util();
        }
        return Util.instance;
	}
	public boolean hp(CommandSender sender, String s) {
        return sender.hasPermission("customtnt." + s);
    }

	public boolean isInteger(String s) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i), 10) < 0) return false;
	    }
	    return true;
	}        

    private List<Block> getBlocks(Location center, int radius) {
        List<Location> locs = circle(center, radius, radius);
        List<Block> blocks = new ArrayList<>();
 
        for (Location loc : locs) {
            blocks.add(loc.getBlock());
        }
 
        return blocks;
    }
 
    private List<Location> circle(Location loc, int radius, int height) {
        List<Location> circleblocks = new ArrayList<>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
 
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = (cy); y < (cy + height); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                    if (dist < radius * radius
                            && !(dist < (radius - 1) * (radius - 1))) {
                        Location l = new Location(loc.getWorld(), x, y,
                                z);
                        circleblocks.add(l);
                    }
                }
            }
        }
 
        return circleblocks;
    }
 
    public void surroundParticles(final Location l, final int r, final Particle particle){
    	if (CustomTNT.get().isEnabled()) {
    			final Random random = new Random();
    			new BukkitRunnable() {
					@Override
					public void run() {
						for (final Block b : getBlocks(l, r)){
						    for (int i = 0; i < 3; i++) {
                                b.getWorld().spawnParticle(particle, (float) b.getLocation().getX(), (float) b.getLocation().getY(), (float) b.getLocation().getZ(),
                                        random.nextInt((8 - 4) + 4) + 1, random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, null, true);
                            }
                        }
					}
     	    	}.runTaskAsynchronously(CustomTNT.get());
    	}
    }
    
	
	public ItemStack name(ItemStack itemStack, String name, String... lores) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!name.isEmpty()) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }

        if (lores.length > 0) {
            List<String> loreList = new ArrayList<>(lores.length);

            for (String lore : lores) {
                loreList.add(ChatColor.translateAlternateColorCodes('&', lore));
            }

            itemMeta.setLore(loreList);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public boolean allowPlacement(Location loc, Player player ) {
        WorldGuardPlugin wg = (WorldGuardPlugin) CustomTNT.get().getServer().getPluginManager().getPlugin("WorldGuard");
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.testState(BukkitAdapter.adapt(loc), wg.wrapPlayer(player), Flags.TNT);
    }

    public boolean allowExplosion(Location loc) {
        WorldGuardPlugin wg = (WorldGuardPlugin) CustomTNT.get().getServer().getPluginManager().getPlugin("WorldGuard");
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.testState(BukkitAdapter.adapt(loc), new DelayedRegionOverlapAssociation(query, BukkitAdapter.adapt(loc)), Flags.TNT);
    }

}
