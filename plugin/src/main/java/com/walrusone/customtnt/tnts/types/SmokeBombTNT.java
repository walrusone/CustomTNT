package com.walrusone.customtnt.tnts.types;

import com.walrusone.customtnt.CustomTNT;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

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
		customTnt = CustomTNT.getNMS().setTntType(customTnt,"SmokeBomb");
	}
	
	@Override
	public void onExplode(final Location location) {
		location.getWorld().playSound(location, CustomTNT.getNMS().getSound(name), 1, 1);
		List<PotionEffect> potionEffects = new ArrayList<>();
		CustomTNT.getNMS().doCloudEffect(CustomTNT.get(), location, potionEffects, "SMOKE_LARGE", radius, duration);
	}
}
