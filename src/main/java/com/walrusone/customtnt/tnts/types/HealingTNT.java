package com.walrusone.customtnt.tnts.types;

import net.minecraft.server.v1_13_R2.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HealingTNT extends ExplosionType {
	
	private int duration;
	private Particle particle;
	private List<PotionEffect> potionEffects = new ArrayList<>();
	
	public HealingTNT(String name, String lore, int radius, int fuse, int duration, Particle particle, boolean throwable, boolean punchable, List<String> potions) {
		this.name = name;
		this.permission = "customtnt.healing";
		this.lore.add(lore);
		this.radius = radius;
		this.fuse = fuse;
		this.duration = duration;
		this.throwable = throwable;
		this.punchable = punchable;
		this.particle = particle;

		for (String pot: potions) {
			List<String> potParts = Arrays.asList(pot.split(":"));
			potionEffects.add(new PotionEffect(PotionEffectType.getByName(potParts.get(0)), Integer.valueOf(potParts.get(1)) * 20, Integer.valueOf(potParts.get(2)), true, true, true));
		}
		
		customTnt = new ItemStack(Material.TNT, 1);
		ItemMeta itemMeta = customTnt.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(this.lore);
		customTnt.setItemMeta(itemMeta);
		net.minecraft.server.v1_13_R2.ItemStack itemstack = CraftItemStack.asNMSCopy(customTnt);
        NBTTagCompound comp = itemstack.getTag();
        if(comp == null)
            comp = new NBTTagCompound();
        comp.setString("TntType", "Healing");
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
		for (PotionEffect pe: potionEffects) {
			aef.addCustomEffect(pe, true);
		}
		aef.setParticle(particle);
		aef.setRadius(radius);
		aef.setDuration(duration);
	}

}
