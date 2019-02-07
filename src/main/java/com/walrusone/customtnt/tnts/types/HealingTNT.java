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

import com.walrusone.customtnt.utils.Util;

public class HealingTNT extends ExplosionType {
	
	private int healingPower;
	
	public HealingTNT(String name, String lore, int radius, int fuse, int amount, boolean throwable, boolean punchable) {
		this.name = name;
		this.permission = "customtnt.healing";
		this.lore.add(lore);
		this.radius = radius;
		this.fuse = fuse;
		this.healingPower = amount;
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
        comp.setString("TntType", "Healing");
        itemstack.setTag(comp);
        customTnt = CraftItemStack.asBukkitCopy(itemstack);
	}
	
	@Override
	public void onExplode(final Location location) {
		location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		Util.get().surroundParticles(location, (int) radius/2, Particle.HEART);
		Util.get().surroundParticles(location, 1, Particle.FLAME);
		for (Player player: location.getWorld().getPlayers()) {
			if (location.distance(player.getLocation()) < radius) {
				if (player.getHealth() + healingPower > 20) {
					player.setHealth(20);
				} else {
					player.setHealth(player.getHealth() + healingPower);
				}
			}
		}
	}

}
