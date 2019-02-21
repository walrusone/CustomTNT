package com.walrusone.customtnt.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public interface NMS {

	public boolean allowPlacement(Location loc, Player player, Plugin plugin);
	public boolean allowExplosion(Location loc, Plugin plugin);
	public boolean hasKey(ItemStack itemInHand, String tntType);
	public String getKeyType(ItemStack itemInHand, String tntType);
	public ItemStack setTntType(ItemStack item, String type);
	public Material getSpawerMaterial();
	public ItemStack getMainHandItem(Player player);
	public void discoverRecipes(Player player);
	public void addRecipe(Plugin plugin, String key, FileConfiguration config, ItemStack item);
	public void setMainHandItem(Player player, ItemStack item);
	public PotionEffect getPotionEffect(List<String> potParts);
	public void doCloudEffect(Plugin plugin, Location location, List<PotionEffect> potionEffects, String particle, float radius, int duration);
	public Sound getSound(String tntType);
	public Material getClockMaterial();
}
