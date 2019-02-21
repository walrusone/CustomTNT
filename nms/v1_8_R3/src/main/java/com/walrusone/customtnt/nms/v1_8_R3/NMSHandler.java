package com.walrusone.customtnt.nms.v1_8_R3;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.walrusone.customtnt.api.NMS;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
		net.minecraft.server.v1_8_R3.ItemStack itemstack = CraftItemStack.asNMSCopy(itemInHand);
		return (itemstack.getTag() != null && itemstack.getTag().hasKey("TntType"));
	}

	@Override
	public String getKeyType(ItemStack itemInHand, String tntType) {
		net.minecraft.server.v1_8_R3.ItemStack itemstack = CraftItemStack.asNMSCopy(itemInHand);
		if (itemstack.getTag() != null && itemstack.getTag().hasKey("TntType")) {
			return itemstack.getTag().getString("TntType");
		}
		return "";
	}

	@Override
	public ItemStack setTntType(ItemStack item, String type) {
		net.minecraft.server.v1_8_R3.ItemStack itemstack = CraftItemStack.asNMSCopy(item);
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
		return player.getInventory().getItemInHand();
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
	public void setMainHandItem(Player player, ItemStack item) {player.getInventory().setItemInHand(item);}

	@Override
	public PotionEffect getPotionEffect(List<String> potParts) {
		return new PotionEffect(PotionEffectType.getByName(potParts.get(0)), Integer.valueOf(potParts.get(1)) * 20, Integer.valueOf(potParts.get(2)), true);
	}

	@Override
	public void doCloudEffect(Plugin plugin, Location location, List<PotionEffect> potionEffects, String particle, float radius, int duration) {
		surroundParticles(plugin, location, (int)radius, particle);
		for (Player player: location.getWorld().getPlayers()) {
			if (location.distance(player.getLocation()) < radius) {
				for (PotionEffect pe: potionEffects) {
					player.addPotionEffect(pe);
				}
			}
		}
	}

	@Override
	public Sound getSound(String tntType) {
		switch(tntType) {
			case "healing": return Sound.EXPLODE;
			case "concussion": return Sound.EXPLODE;
			case "flashbang": return Sound.EXPLODE;
			default: return Sound.EXPLODE;
		}
	}

	@Override
	public Material getClockMaterial() {
		return Material.WATCH;
	}

	private void surroundParticles(Plugin plugin, Location l, int r, String type){
		if (plugin.isEnabled()) {
			final Random random = new Random();
			new BukkitRunnable() {
				@Override
				public void run() {
					for (final Block b : getBlocks(l, r)){
						sendParticles(b.getWorld(), type, (float) b.getLocation().getX(), (float) b.getLocation().getY(), (float) b.getLocation().getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1);
						sendParticles(b.getWorld(), type, (float) b.getLocation().getX(), (float) b.getLocation().getY(), (float) b.getLocation().getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1);
						sendParticles(b.getWorld(), type, (float) b.getLocation().getX(), (float) b.getLocation().getY(), (float) b.getLocation().getZ(), random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1);
					}
				}
			}.runTaskAsynchronously(plugin);
		}
	}

	private List<Block> getBlocks(Location center, int radius) {
		List<Location> locs = circle(center, radius, radius);
		List<Block> blocks = new ArrayList<>();

		for (Location loc : locs) {
			blocks.add(loc.getBlock());
		}

		return blocks;
	}

	private List<Location> circle(Location loc, int radius, int height) {
		List<Location> circleblocks = new ArrayList<>();
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();

		for (int x = cx - radius; x <= cx + radius; x++) {
			for (int z = cz - radius; z <= cz + radius; z++) {
				for (int y = (cy); y < (cy + height); y++) {
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
					if (dist < radius * radius
							&& !(dist < (radius - 1) * (radius - 1))) {
						Location l = new Location(loc.getWorld(), x, y,
								z);
						circleblocks.add(l);
					}
				}
			}
		}
		return circleblocks;
	}

	private void sendParticles(World world, String type, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float data, int amount) {
		EnumParticle particle = EnumParticle.valueOf(type);
		PacketPlayOutWorldParticles particles = new PacketPlayOutWorldParticles(particle, false, x, y, z, offsetX, offsetY, offsetZ, data, amount, 1);
		for (Player player: world.getPlayers()) {
			CraftPlayer start = (CraftPlayer) player; //Replace player with your player.
			EntityPlayer target = start.getHandle();
			PlayerConnection connect = target.playerConnection;
			connect.sendPacket(particles);
		}
	}

}
