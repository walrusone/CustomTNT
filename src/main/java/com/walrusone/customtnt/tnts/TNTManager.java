package com.walrusone.customtnt.tnts;

import java.util.*;

import com.walrusone.customtnt.tnts.types.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.TNTPrimed;

import com.walrusone.customtnt.CustomTNT;
import com.walrusone.customtnt.utils.Messaging;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class TNTManager {

	
	private ArrayList<Block> tntBlocks = new ArrayList<>();
	private ArrayList<Location> luckyExplosions = new ArrayList<>();
	private ArrayList<TNTPrimed> punchableTNTs = new ArrayList<>();
	private HashMap<TNTType, ExplosionType> types = new HashMap<>();
	private FileConfiguration config;
	
	public TNTManager() {
		load();
	}
	
	private void load() {
		config = CustomTNT.get().getConfig();
		types.clear();
		for (String key: config.getConfigurationSection("tntTypes").getKeys(false)) {
			types.put(TNTType.valueOf(key.toUpperCase()), getTNT(key));
			if (config.getBoolean("tntTypes." + key + ".enableCrafting")) {
				addRecipe(key);
			}
		}
	}
	
	public ArrayList<Block> getTNTBlocks() {
		return tntBlocks;
	}
	
	public ArrayList<Location> getLuckyExplosions() {
		return luckyExplosions;
	}
	
	public ArrayList<TNTPrimed> getPunchables() {
		return punchableTNTs;
	}
	
	@SuppressWarnings("unused")
	public enum TNTType {
		SNIPER,
		TIMEBOMB,
		SMOKEBOMB,
		HEALING,
		LUCKY,
		SUICIDE,
		CONCUSSION,
		FLASHBANG
	}
	
	public ExplosionType getExplosionType(TNTType type) {
		return types.get(type);
	}

	private ExplosionType getTNT(String key) {
		switch (key) {
			case "smokebomb": return new SmokeBombTNT(getDisplayName(key), getLore(key), getRadius(key), getFuse(key), getDuration(key), getThrowable(key), getPunchable(key));
			case "sniper": return new SniperTNT(getDisplayName(key), getLore(key), getExplosionPower(key), getFuse(key), getThrowable(key), getPunchable(key));
			case "timebomb": return new TimeBombTNT(getDisplayName(key), getLore(key), getExplosionPower(key), 5, false, getPunchable(key));
			case "lucky": return new LuckyTNT(getDisplayName(key), getLore(key), getExplosionPower(key), getFuse(key), getDropChance(key), getThrowable(key), getPunchable(key));
			case "suicide": return new SuicideTNT(getDisplayName(key), getLore(key), getExplosionPower(key), 3, true, false);
			case "healing": return new HealingTNT(getDisplayName(key), getLore(key), getRadius(key), getFuse(key), getDuration(key), getParticle(key), getThrowable(key), getPunchable(key), getPotionEffects(key));
			case "concussion": return new ConcussionTNT(getDisplayName(key), getLore(key), getRadius(key), getFuse(key), getDuration(key), getParticle(key), getThrowable(key), getPunchable(key), getPotionEffects(key));
			case "flashbang": return new FlashbangTNT(getDisplayName(key), getLore(key), getRadius(key), getFuse(key), getDuration(key), getParticle(key), getThrowable(key), getPunchable(key), getPotionEffects(key));
			default: return null;
		}
	}

	private String getDisplayName(String key) {
		return new Messaging.MessageFormatter().format("tnt." + key + ".display-name");
	}

	private String getLore(String key) {
		return new Messaging.MessageFormatter().format("tnt." + key + ".lore");
	}

	private int getRadius(String key) {
		return config.getInt("tntTypes." + key + ".particleRadius");
	}

	private int getDuration(String key) {
		return config.getInt("tntTypes." + key + ".particleDuration") * 20;
	}

	private int getFuse(String key) {
		return (int) (config.getDouble("tntTypes." + key + ".fuse") * 20);
	}

	private boolean getThrowable(String key) {
		return config.getBoolean("tntTypes." + key + ".throwable");
	}

	private boolean getPunchable(String key) {
		return config.getBoolean("tntTypes." + key + ".punchable");
	}

	private int getExplosionPower(String key) {
		return config.getInt("tntTypes." + key + ".explosionPower");
	}

	private int getDropChance(String key) {
		return config.getInt("tntTypes." + key + ".chanceOfSpawnerDrop");
	}

	private Particle getParticle(String key) {
		return Particle.valueOf(config.getString("tntTypes." + key + ".particleType"));
	}

	private List<String> getPotionEffects(String key) {
		return config.getStringList("tntTypes." + key + ".effectTypes");
	}

	private void addRecipe(String key) {
		NamespacedKey nKey = new NamespacedKey(CustomTNT.get(), key);
		CustomTNT.getNamespacedKeys().add(nKey);
		ShapedRecipe recipe = new ShapedRecipe(nKey, this.getExplosionType(TNTManager.TNTType.valueOf(key.toUpperCase())).getItem());
		recipe.shape("ABC", "DEF", "GHI");
		List<String> slots = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I") ;
		List<String> items = Arrays.asList(config.getString("tntTypes." + key + ".recipe").split(":"));
		for (int i = 0; i < 9; i++) {
			recipe.setIngredient(slots.get(i).charAt(0), Material.valueOf(items.get(i)));
		}
		for (Iterator<Recipe> iter = Bukkit.recipeIterator(); iter.hasNext();) {
			Recipe rep = iter.next();
			if (rep.getResult().equals(this.getExplosionType(TNTManager.TNTType.valueOf(key.toUpperCase())).getItem())) {
				iter.remove();
			}
		}
		Bukkit.addRecipe(recipe);
	}

	public Set<TNTType> getTypes() {
		return types.keySet();
	}
}


