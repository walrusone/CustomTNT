package com.walrusone.customtnt;

import com.walrusone.customtnt.api.NMS;
import com.walrusone.customtnt.utils.Messaging;
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
    private NMS nmsHandler;
	
	public void onEnable() {
    	instance = this;

        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        try {
            final Class<?> clazz = Class.forName("com.walrusone.customtnt.nms." + version + ".NMSHandler");
            // Check if we have a NMSHandler class at that location.
            if (NMS.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
                this.nmsHandler = (NMS) clazz.getConstructor().newInstance(); // Set our handler
            }
        } catch (final Exception e) {
            e.printStackTrace();
            this.getLogger().severe("Could not find support for this CraftBukkit version: " + version + ".");
            this.getLogger().info("Check for updates at https://www.spigotmc.org/resources/skywarsreloaded.3796/");
            this.setEnabled(false);
            return;
        }
        this.getLogger().info("Loading support for " + version);
    	
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

    public static NMS getNMS() {
        return instance.nmsHandler;
    }
}
