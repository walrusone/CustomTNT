package com.walrusone.customtnt.tnts.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public abstract class ExplosionType {

	protected String name;
	protected String permission;
	protected List<String> lore = new ArrayList<>();
	protected float radius;
	protected int fuse;
	protected ItemStack customTnt;
	protected boolean throwable;
	protected boolean punchable;
	
	public abstract void onExplode(final Location location);

	public String getName() {
		return name;
	}
	
	public List<String> getLore() {
		return lore;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public int getFuse() {
		return fuse;
	}
	
	public ItemStack getItem() {
		return customTnt.clone();
	}
	
	public boolean isThrowable() {
		return throwable;
	}
	
	public boolean isPunchable() {
		return punchable;
	}
	
	public String getPermission() {
		return permission;
	}

}
