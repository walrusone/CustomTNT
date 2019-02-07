package com.walrusone.customtnt.events;

import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class TNTPrimedEvent extends Event implements Cancellable
{
    private static final HandlerList handlers;
    private int x;
    private int y;
    private int z;
    private TNTPrimed tnt;
    private boolean cancelled;
    
    static {
        handlers = new HandlerList();
    }
    
    public TNTPrimedEvent(final int a1, final int a2, final int a3, final TNTPrimed a4) {
        this.x = a1;
        this.y = a2;
        this.z = a3;
        this.tnt = a4;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public TNTPrimed getTNT() {
        return this.tnt;
    }
    
    public HandlerList getHandlers() {
        return TNTPrimedEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return TNTPrimedEvent.handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean a1) {
        this.cancelled = a1;
    }
}
