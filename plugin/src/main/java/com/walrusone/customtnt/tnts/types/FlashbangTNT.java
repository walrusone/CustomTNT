package com.walrusone.customtnt.tnts.types;

import com.walrusone.customtnt.CustomTNT;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlashbangTNT extends ExplosionType {

	private int duration;
	private String particle;
	private List<PotionEffect> potionEffects = new ArrayList<>();

	public FlashbangTNT(String name, String lore, int radius, int fuse, int duration, String particle, boolean throwable, boolean punchable, List<String> potions) {
		this.name = name;
		this.permission = "customtnt.flashbang";
		this.lore.add(lore);
		this.radius = radius;
		this.fuse = fuse;
		this.duration = duration;
		this.throwable = throwable;
		this.punchable = punchable;
		this.particle = particle;

		for (String pot: potions) {
			List<String> potParts = Arrays.asList(pot.split(":"));
			potionEffects.add(CustomTNT.getNMS().getPotionEffect(potParts));
		}
		
		customTnt = new ItemStack(Material.TNT, 1);
		ItemMeta itemMeta = customTnt.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(this.lore);
		customTnt.setItemMeta(itemMeta);
		customTnt = CustomTNT.getNMS().setTntType(customTnt,"Flashbang");
	}
	
	@Override
	public void onExplode(final Location location) {
		location.getWorld().playSound(location, CustomTNT.getNMS().getSound(name), 1, 1);
		CustomTNT.getNMS().doCloudEffect(CustomTNT.get(),location, potionEffects, particle, radius, duration);
	}
}
