package com.walrusone.customtnt.tnts.types;

import com.walrusone.customtnt.CustomTNT;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SuicideTNT extends ExplosionType {

	public SuicideTNT(String name, String lore, float radius, int fuse, boolean throwable, boolean punchable) {
		this.name = name;
		this.permission = "customtnt.suicide";
		this.lore.add(lore);
		this.radius = radius;
		this.fuse = fuse;
		this.throwable = throwable;
		this.punchable = punchable;
		
		customTnt = new ItemStack(Material.TNT, 1);
		ItemMeta itemMeta = customTnt.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(this.lore);
		customTnt.setItemMeta(itemMeta);
		customTnt = CustomTNT.getNMS().setTntType(customTnt,"Suicide");
	}
	
	@Override
	public void onExplode(final Location location) {
		location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), radius, false, true);
	}

}
