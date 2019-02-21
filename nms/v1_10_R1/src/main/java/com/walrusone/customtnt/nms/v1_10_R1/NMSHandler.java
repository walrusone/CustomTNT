package com.walrusone.customtnt.nms.v1_10_R1;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.walrusone.customtnt.api.NMS;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

public class NMSHandler implements NMS {

	@Override
	public boolean allowPlacement(Location loc, Player player, Plugin plugin) {
		ApplicableRegionSet set = getApplicableSet(loc, plugin);
		return (set.allows(DefaultFlag.TNT, WorldGuardPlugin.inst().wrapPlayer(player)) && set.allows(DefaultFlag.BUILD, WorldGuardPlugin.inst().wrapPlayer(player)));
	}

	@Override
	public boolean allowExplosion(Location loc, Plugin plugin) {
		ApplicableRegionSet set = getApplicableSet(loc, plugin);
		return set.allows(DefaultFlag.TNT);
	}

	public ApplicableRegionSet getApplicableSet(Location loc, Plugin plugin) {
		WorldGuardPlugin wg = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		Vector pt = toVector(loc); // This also takes a location
		RegionManager regionManager = wg.getRegionManager(loc.getWorld());
		return regionManager.getApplicableRegions(pt);
	}

	@Override
	public boolean hasKey(ItemStack itemInHand, String tntType) {
		net.minecraft.server.v1_10_R1.ItemStack itemstack = CraftItemStack.asNMSCopy(itemInHand);
		return (itemstack.getTag() != null && itemstack.getTag().hasKey("TntType"));
	}

	@Override
	public String getKeyType(ItemStack itemInHand, String tntType) {
		net.minecraft.server.v1_10_R1.ItemStack itemstack = CraftItemStack.asNMSCopy(itemInHand);
		if (itemstack.getTag() != null && itemstack.getTag().hasKey("TntType")) {
			return itemstack.getTag().getString("TntType");
		}
		return "";
	}

	@Override
	public ItemStack setTntType(ItemStack item, String type) {
		net.minecraft.server.v1_10_R1.ItemStack itemstack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound comp = itemstack.getTag();
		if(comp == null)
			comp = new NBTTagCompound();
		comp.setString("TntType", type);
		itemstack.setTag(comp);
		return CraftItemStack.asBukkitCopy(itemstack);
	}

	@Override
	public Material getSpawerMaterial() {
		return Material.MOB_SPAWNER;
	}

	@Override
	public ItemStack getMainHandItem(Player player) {
		return player.getInventory().getItemInMainHand();
	}

	@Override
	public void setMainHandItem(Player player, ItemStack item) {player.getInventory().setItemInMainHand(item);}

	@Override
	public PotionEffect getPotionEffect(List<String> potParts) {
		return new PotionEffect(PotionEffectType.getByName(potParts.get(0)), Integer.valueOf(potParts.get(1)) * 20, Integer.valueOf(potParts.get(2)), true);
	}

	@Override
	public void doCloudEffect(Plugin plugin, Location location, List<PotionEffect> potionEffects, String particle, float radius, int duration) {
		for (int i = 0; i < 3; i++) {
			AreaEffectCloud aef =(AreaEffectCloud) location.getWorld().spawnEntity(location.add(0, i, 0), EntityType.AREA_EFFECT_CLOUD);
			for (PotionEffect pe: potionEffects) {
				aef.addCustomEffect(pe, true);
			}
			aef.setParticle(Particle.valueOf(particle.toUpperCase()));
			aef.setRadius(radius);
			aef.setDuration(duration);
		}
	}

	@Override
	public Sound getSound(String tntType) {
		switch(tntType) {
			case "healing": return Sound.ENTITY_GENERIC_EXPLODE;
			case "concussion": return Sound.ENTITY_GENERIC_EXPLODE;
			case "flashbang": return Sound.ENTITY_GENERIC_EXPLODE;
			default: return Sound.ENTITY_GENERIC_EXPLODE;
		}
	}

	@Override
	public void discoverRecipes(Player player) {

	}

	@Override
	public void addRecipe(Plugin plugin, String key, FileConfiguration config, ItemStack item) {
		ShapedRecipe recipe = new ShapedRecipe(item);
		recipe.shape("ABC", "DEF", "GHI");
		List<String> slots = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I") ;
		List<String> items = Arrays.asList(config.getString("tntTypes." + key + ".recipe").split(":"));
		for (int i = 0; i < 9; i++) {
			recipe.setIngredient(slots.get(i).charAt(0), Material.valueOf(items.get(i)));
		}
		for (Iterator<Recipe> iter = Bukkit.recipeIterator(); iter.hasNext();) {
			Recipe rep = iter.next();
			if (rep.getResult().equals(item)) {
				iter.remove();
			}
		}
		Bukkit.addRecipe(recipe);
	}

	@Override
	public Material getClockMaterial() {
		return Material.WATCH;
	}

}
