package com.walrusone.customtnt.tnts;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.walrusone.customtnt.CustomTNT;
import com.walrusone.customtnt.events.TNTPrimedEvent;

public class TNTScheduler extends BukkitRunnable
{
    private CustomTNT plugin;
    
    public TNTScheduler(final CustomTNT a1) {
        this.plugin = a1;
    }
    
    public void run() {
        for (final World world : Bukkit.getServer().getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                if (entity.getType() == EntityType.PRIMED_TNT && !entity.hasMetadata("Marked")) {
                    final TNTPrimed primedTNT = (TNTPrimed)entity;
                    primedTNT.setMetadata("Marked", new FixedMetadataValue(this.plugin, "Marked"));
                    final TNTPrimedEvent primedTNTEvent = new TNTPrimedEvent(primedTNT.getLocation().getBlockX(), primedTNT.getLocation().getBlockY(), primedTNT.getLocation().getBlockZ(), primedTNT);
                    Bukkit.getServer().getPluginManager().callEvent(primedTNTEvent);
                }
            }
        }
    }
}
