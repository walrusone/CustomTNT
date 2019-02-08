package com.walrusone.customtnt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import com.walrusone.customtnt.commands.CmdManager;
import com.walrusone.customtnt.listeners.IconMenuController;
import com.walrusone.customtnt.listeners.TNTListener;
import com.walrusone.customtnt.tnts.TNTManager;
import com.walrusone.customtnt.tnts.TNTScheduler;
import com.walrusone.customtnt.utils.Messaging;

import java.util.ArrayList;
import java.util.List;


public class CustomTNT extends JavaPlugin {

	private static CustomTNT instance;
	private Messaging messaging;
	private static TNTManager tntHandler;
	private IconMenuController ic;
	private static List<NamespacedKey> namespacedKeys = new ArrayList<>();
	
	public void onEnable() {
    	instance = this;
    	
    	getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();
        reloadConfig();
        load();
        ic = new IconMenuController();
        
        new TNTScheduler(this).runTaskTimer(this, 0L, 1L);
        this.getServer().getPluginManager().registerEvents(ic, this);
        this.getServer().getPluginManager().registerEvents(new TNTListener(), this);
        getCommand("customtnt").setExecutor(new CmdManager());
        addRecipes();
	}

    private void addRecipes() {
        NamespacedKey timebombkey = new NamespacedKey(this, "timebomb");
        namespacedKeys.add(timebombkey);
        ShapelessRecipe timebombrecipe = new ShapelessRecipe(timebombkey, CustomTNT.getTntHandler().getExplosionType(TNTManager.TNTType.TIMEBOMB).getItem());
        timebombrecipe.addIngredient(Material.CLOCK);
        timebombrecipe.addIngredient(Material.TNT);
        Bukkit.addRecipe(timebombrecipe);
    }

    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        messaging = null;
        tntHandler = null;
	}
	
	public void load() {
		reloadConfig();
		messaging = new Messaging(this);
		new TNTScheduler(this).runTaskTimer(this, 0L, 1L);
		tntHandler = new TNTManager(); 
	}
	
    public static CustomTNT get() {
        return instance;
    }
    
    public static TNTManager getTntHandler() {
        return tntHandler;
    }
    
	public static Messaging getMessaging() {
	     return instance.messaging;
	}
	
    public static IconMenuController getIC() {
    	return instance.ic;
    }

    public static List<NamespacedKey> getNamespcedKeys() {
	    return namespacedKeys;
    }
}
