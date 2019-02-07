package com.walrusone.customtnt.tnts.types;

import net.minecraft.server.v1_13_R2.NBTTagCompound;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.walrusone.customtnt.CustomTNT;

public class LuckyTNT extends ExplosionType {
	
	private int percent;
	
	public LuckyTNT(String name, String lore, float radius, int fuse, int percent, boolean throwable, boolean punchable) {
		this.name = name;
		this.permission = "customtnt.lucky";
		this.lore.add(lore);
		this.radius = radius;
		this.fuse = fuse;
		this.percent = percent;
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
        comp.setString("TntType", "Lucky");
        itemstack.setTag(comp);
        customTnt = CraftItemStack.asBukkitCopy(itemstack);
	}
	
	@Override
	public void onExplode(final Location location) {
		CustomTNT.getTntHandler().getLuckyExplosions().add(location.getBlock().getLocation());
		location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), radius, false, true);
	}
	
	public int getOdds() {
		return this.percent;
	}
	


}
