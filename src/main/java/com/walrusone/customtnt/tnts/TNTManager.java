package com.walrusone.customtnt.tnts;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;

import com.walrusone.customtnt.CustomTNT;
import com.walrusone.customtnt.tnts.types.ChemicalTNT;
import com.walrusone.customtnt.tnts.types.DrillTNT;
import com.walrusone.customtnt.tnts.types.ExplosionType;
import com.walrusone.customtnt.tnts.types.HealingTNT;
import com.walrusone.customtnt.tnts.types.LuckyTNT;
import com.walrusone.customtnt.tnts.types.MultiTNT;
import com.walrusone.customtnt.tnts.types.SmokeBombTNT;
import com.walrusone.customtnt.tnts.types.SniperTNT;
import com.walrusone.customtnt.tnts.types.SuicideTNT;
import com.walrusone.customtnt.tnts.types.TimeBombTNT;
import com.walrusone.customtnt.utils.Messaging;

public class TNTManager {

	
	private ArrayList<Block> tntBlocks = new ArrayList<>();
	private ArrayList<Location> luckyExplosions = new ArrayList<>();
	private ArrayList<TNTPrimed> punchableTNTs = new ArrayList<>();
	private HashMap<TNTType, ExplosionType> types = new HashMap<>();
	
	public TNTManager() {
		load();
	}
	
	private void load() {
		types.clear();
		types.put(TNTType.MULTI, new MultiTNT(new Messaging.MessageFormatter().format("tnt.multi.display-name"),
				new Messaging.MessageFormatter().format("tnt.multi.lore"),
				(float) CustomTNT.get().getConfig().getDouble("tntTypes.multi.explosionPower"), 
				(int) (CustomTNT.get().getConfig().getDouble("tntTypes.multi.fuse") * 20), 
				CustomTNT.get().getConfig().getInt("tntTypes.multi.numOfExplosions"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.multi.throwable"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.multi.punchable")));
		
		types.put(TNTType.CHEMICAL, new ChemicalTNT(new Messaging.MessageFormatter().format("tnt.chemical.display-name"),
				new Messaging.MessageFormatter().format("tnt.chemical.lore"), 
				CustomTNT.get().getConfig().getInt("tntTypes.chemical.effectRadius"), 
				(int) (CustomTNT.get().getConfig().getDouble("tntTypes.chemical.fuse") * 20), 
				CustomTNT.get().getConfig().getInt("tntTypes.chemical.effectDuration"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.chemical.throwable"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.chemical.punchable")));
		
		types.put(TNTType.SMOKEBOMB, new SmokeBombTNT(new Messaging.MessageFormatter().format("tnt.smokebomb.display-name"),
				new Messaging.MessageFormatter().format("tnt.smokebomb.lore"), 
				CustomTNT.get().getConfig().getInt("tntTypes.smokebomb.effectRadius"), 
				(int) (CustomTNT.get().getConfig().getDouble("tntTypes.smokebomb.fuse") * 20), 
				CustomTNT.get().getConfig().getInt("tntTypes.smokebomb.effectDuration"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.smokebomb.throwable"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.smokebomb.punchable")));
		
		types.put(TNTType.SNIPER, new SniperTNT(new Messaging.MessageFormatter().format("tnt.sniper.display-name"),
				new Messaging.MessageFormatter().format("tnt.sniper.lore"), 
				(float) CustomTNT.get().getConfig().getDouble("tntTypes.sniper.explosionPower"), 
				(int) (CustomTNT.get().getConfig().getDouble("tntTypes.sniper.fuse") * 20),
				CustomTNT.get().getConfig().getBoolean("tntTypes.sniper.throwable"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.sniper.punchable")));
		
		types.put(TNTType.DRILL, new DrillTNT(new Messaging.MessageFormatter().format("tnt.drill.display-name"),
				new Messaging.MessageFormatter().format("tnt.drill.lore"), 5, 
				(int) (CustomTNT.get().getConfig().getDouble("tntTypes.drill.fuse") * 20), 
				CustomTNT.get().getConfig().getInt("tntTypes.drill.depth"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.drill.throwable"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.drill.punchable")));
		
		types.put(TNTType.TIMEBOMB, new TimeBombTNT(new Messaging.MessageFormatter().format("tnt.timebomb.display-name"),
				new Messaging.MessageFormatter().format("tnt.timebomb.lore"), 
				(float) CustomTNT.get().getConfig().getDouble("tntTypes.timebomb.explosionPower"), 
				5,
				false,
				CustomTNT.get().getConfig().getBoolean("tntTypes.timebomb.punchable")));
		
		types.put(TNTType.HEALING, new HealingTNT(new Messaging.MessageFormatter().format("tnt.healing.display-name"),
				new Messaging.MessageFormatter().format("tnt.healing.lore"), 
				CustomTNT.get().getConfig().getInt("tntTypes.healing.effectRadius"), 
				(int) (CustomTNT.get().getConfig().getDouble("tntTypes.healing.fuse") * 20), 
				CustomTNT.get().getConfig().getInt("tntTypes.healing.healingAmount"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.healing.throwable"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.healing.punchable")));
		
		types.put(TNTType.LUCKY, new LuckyTNT(new Messaging.MessageFormatter().format("tnt.lucky.display-name"),
				new Messaging.MessageFormatter().format("tnt.lucky.lore"), 
				CustomTNT.get().getConfig().getInt("tntTypes.lucky.explosionPower"), 
				(int) (CustomTNT.get().getConfig().getDouble("tntTypes.lucky.fuse") * 20), 
				CustomTNT.get().getConfig().getInt("tntTypes.lucky.chanceOfSpawnerDrop"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.lucky.throwable"),
				CustomTNT.get().getConfig().getBoolean("tntTypes.lucky.punchable")));
		
		types.put(TNTType.SUICIDE, new SuicideTNT(new Messaging.MessageFormatter().format("tnt.suicide.display-name"),
				new Messaging.MessageFormatter().format("tnt.suicide.lore"), 
				CustomTNT.get().getConfig().getInt("tntTypes.suicide.explosionPower"), 
				3, 
				true,
				false));
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
	
	public enum TNTType {
		MULTI,
		SNIPER,
		DRILL,
		CHEMICAL,
		TIMEBOMB,
		SMOKEBOMB,
		HEALING,
		LUCKY,
		SUICIDE
	}
	
	public ExplosionType getExplosionType(TNTType type) {
		return types.get(type);
	}

}


