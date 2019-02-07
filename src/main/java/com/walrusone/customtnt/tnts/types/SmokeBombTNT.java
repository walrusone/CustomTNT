package com.walrusone.customtnt.tnts.types;

import net.minecraft.server.v1_13_R2.NBTTagCompound;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.walrusone.customtnt.utils.Util;

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
		Util.get().surroundParticles(location, (int) radius, Particle.SMOKE_LARGE);
		Util.get().surroundParticles(location, 1, Particle.FLAME);
		for (Player player: location.getWorld().getPlayers()) {
			if (location.distance(player.getLocation()) < radius) {
				Vector vector = player.getLocation().toVector().subtract(location.toVector());
				player.setVelocity(vector.multiply(.4).setY(.25));
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration * 20, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration * 20, 1));
			}
		}
	}

}
