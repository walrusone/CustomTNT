package com.walrusone.customtnt.tnts.types;

import net.minecraft.server.v1_13_R2.NBTTagCompound;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SmokeBombTNT extends ExplosionType {
	
	private int duration;
	
	public SmokeBombTNT(String name, String lore, int radius, int fuse, int duration, boolean throwable, boolean punchable) {
		this.name = name;
		this.permission = "customtnt.smokebomb";
		this.lore.add(lore);
		this.radius = radius;
		this.fuse = fuse;
		this.duration = duration;
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
        comp.setString("TntType", "SmokeBomb");
        itemstack.setTag(comp);
        customTnt = CraftItemStack.asBukkitCopy(itemstack);
	}
	
	@Override
	public void onExplode(final Location location) {
		location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		for (int i = 0; i < 3; i++) {
			doEffectCould(location, i);
		}
	}

	private void doEffectCould(Location location, int y) {
		AreaEffectCloud aef =(AreaEffectCloud) location.getWorld().spawnEntity(location.add(0, y, 0), EntityType.AREA_EFFECT_CLOUD);
		aef.setParticle(Particle.SMOKE_LARGE);
		aef.setRadius(radius);
		aef.setDuration(duration);
	}

}
