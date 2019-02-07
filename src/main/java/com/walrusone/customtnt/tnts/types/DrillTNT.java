package com.walrusone.customtnt.tnts.types;

import java.util.ArrayList;

import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.customtnt.CustomTNT;
import com.walrusone.customtnt.utils.Util;

public class DrillTNT extends ExplosionType {
	
	private int depth;
	
	public DrillTNT(String name, String lore, int radius, int fuse, int depth, boolean throwable, boolean punchable) {
		this.name = name;
		this.permission = "customtnt.drill";
		this.lore.add(lore);
		this.radius = radius;
		this.fuse = fuse;
		this.depth = depth;
		this.throwable = throwable;
		this.punchable = punchable;
		
		customTnt = new ItemStack(Material.TNT, 1);
		ItemMeta itemMeta = customTnt.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(this.lore);
		customTnt.setItemMeta(itemMeta);
		net.minecraft.server.v1_13_R2.ItemStack itemstack = CraftItemStack.asNMSCopy(customTnt);
        NBTTagCompound comp = itemstack.getTag();
        if(comp == null)
            comp = new NBTTagCompound();
        comp.setString("TntType", "Drill");
        itemstack.setTag(comp);
        customTnt = CraftItemStack.asBukkitCopy(itemstack);
	}
	
	@Override
	public void onExplode(final Location location) {
		location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		Util.get().surroundParticles(location, 5, Particle.SMOKE_LARGE);
		Util.get().surroundParticles(location, 1, Particle.FLAME);
		new BukkitRunnable() {
			public void run() {
				ArrayList<Location> breakable = new ArrayList<>();

				breakable.add(location);
				breakable.add(location.clone().add(1, 0, 0));
				breakable.add(location.clone().add(2, 0, 0));
				breakable.add(location.clone().add(-1, 0, 0));
				breakable.add(location.clone().add(-2, 0, 0));
				breakable.add(location.clone().add(0, 0, 1));
				breakable.add(location.clone().add(0, 0, 2));
				breakable.add(location.clone().add(0, 0, -1));
				breakable.add(location.clone().add(0, 0, -2));
				breakable.add(location.clone().add(-1, 0, 1));
				breakable.add(location.clone().add(-1, 0, -1));
				breakable.add(location.clone().add(1, 0, -1));
				breakable.add(location.clone().add(1, 0, 1));

				ArrayList<BlockState> blocks = new ArrayList<>();
				for (int i = 0; i < depth; i++) {
					blocks.clear();
					for (Location loc: breakable) {
						blocks.add(loc.getWorld().getBlockAt(loc.add(0, -1, 0)).getState());
					}
					final ArrayList<BlockState> blocksToRemove = new ArrayList<>(blocks);
					new BukkitRunnable() {
						public void run() {
							removeBlocks(blocksToRemove);							
						}
					}.runTask(CustomTNT.get());
				}
				
			}
		}.runTaskAsynchronously(CustomTNT.get());
	}

	private void removeBlocks(ArrayList<BlockState> blocks) {
		for (BlockState block: blocks) {
			if (!block.getType().equals(Material.BEDROCK) && !block.getType().equals(Material.AIR)) {
				block.setType(Material.AIR);
				block.update(true);
			}
		}
	}
}
