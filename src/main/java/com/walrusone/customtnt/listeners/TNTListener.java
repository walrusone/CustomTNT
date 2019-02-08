package com.walrusone.customtnt.listeners;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.material.Dispenser;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.walrusone.customtnt.CustomTNT;
import com.walrusone.customtnt.events.TNTPrimedEvent;
import com.walrusone.customtnt.menus.TimebombMenu;
import com.walrusone.customtnt.tnts.TNTManager.TNTType;
import com.walrusone.customtnt.utils.Messaging;
import com.walrusone.customtnt.tnts.types.ExplosionType;
import com.walrusone.customtnt.tnts.types.LuckyTNT;

public class TNTListener implements Listener
{
    private HashMap<UUID, TNTPrimed> tracking = new HashMap<>();
    private HashMap<UUID, Long> sendMessage = new HashMap<>();
    private HashMap<UUID, Long> suicideCooldown = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onExplosion(final BlockExplodeEvent event) {
    	if (CustomTNT.getTntHandler().getLuckyExplosions().contains(event.getBlock().getLocation())) {
    		CustomTNT.getTntHandler().getLuckyExplosions().remove(event.getBlock().getLocation());
    		for (Block block: event.blockList()) {
    			if (block.getType().equals(Material.SPAWNER)) {
    				Random ran = new Random();
    				int randomNum = ran.nextInt((100 - 1) + 1) + 1;
    				if (randomNum <= ((LuckyTNT) CustomTNT.getTntHandler().getExplosionType(TNTType.LUCKY)).getOdds()) {
    					CreatureSpawner cs = (CreatureSpawner) block.getState();
    					EntityType et = cs.getSpawnedType();
						ItemStack mobSpawner = new ItemStack(Material.SPAWNER, 1);
						BlockStateMeta meta = (BlockStateMeta) mobSpawner.getItemMeta();
						BlockState bs = meta.getBlockState();
						CreatureSpawner newCs = (CreatureSpawner)bs;
						newCs.setSpawnedType(et);
						meta.setDisplayName(ChatColor.DARK_GREEN + et.name() + " SPAWNER");
						meta.setBlockState(newCs);
						mobSpawner.setItemMeta(meta);
    					block.getWorld().dropItemNaturally(block.getLocation(), mobSpawner);
    				}
    			}
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onExplosivePrimed(final ExplosionPrimeEvent event) {
		if (event.getEntityType() == EntityType.PRIMED_TNT) {
			final TNTPrimed primedTNT = (TNTPrimed) event.getEntity();
			if (primedTNT.hasMetadata("TNTType")) {
				event.setCancelled(true);
				final String tntType = primedTNT.getMetadata("TNTType").get(0).asString();
				final TNTType type = TNTType.valueOf(tntType.toUpperCase());
				if (primedTNT.hasMetadata("Owner")) {
					new BukkitRunnable() {
						public void run() {
							CustomTNT.getTntHandler().getExplosionType(type).onExplode(event.getEntity().getLocation());
						}
					}.runTask(CustomTNT.get());
				} else {
					new BukkitRunnable() {
						public void run() {
							CustomTNT.getTntHandler().getExplosionType(type).onExplode(event.getEntity().getLocation());
						}
					}.runTask(CustomTNT.get());
				}
				CustomTNT.getTntHandler().getPunchables().remove(primedTNT);
			}
		}
	}
    
    @EventHandler
    public void onTNTPrimed(final TNTPrimedEvent event) {
        final Block tntBlock = event.getTNT().getWorld().getBlockAt(event.getX(), event.getY(), event.getZ());
        CustomTNT.getTntHandler().getTNTBlocks().remove(tntBlock);
        if (tntBlock.hasMetadata("TNTType")) {
            event.getTNT().setMetadata("TNTType", tntBlock.getMetadata("TNTType").get(0));
            if (tntBlock.hasMetadata("Owner")) {
                event.getTNT().setMetadata("Owner", tntBlock.getMetadata("Owner").get(0));
            }
            final TNTPrimed primedTNT = event.getTNT();
            String tntType = tntBlock.getMetadata("TNTType").get(0).asString();
            TNTType type = TNTType.valueOf(tntType.toUpperCase());
            if(CustomTNT.getTntHandler().getExplosionType(type).isPunchable()) {
            	CustomTNT.getTntHandler().getPunchables().add(primedTNT);
            }
            if (type != TNTType.TIMEBOMB) {
                primedTNT.setFuseTicks(CustomTNT.getTntHandler().getExplosionType(type).getFuse());
            }
            if (primedTNT.getFuseTicks() > 80) {
            	tracking.put(primedTNT.getUniqueId(), primedTNT);
            	double repeat = (double)primedTNT.getFuseTicks()/70 - 1;
            		if (repeat < 1) {
            			continueTNTAnimation((int)(70 * repeat), primedTNT.getUniqueId());
            		} else {
            			continueTNTAnimation(primedTNT.getFuseTicks() - 70, primedTNT.getUniqueId());
            		}
            }
        }
        tntBlock.removeMetadata("TNTType", CustomTNT.get());
        tntBlock.removeMetadata("Owner", CustomTNT.get());
    }
    
    private void continueTNTAnimation(final int fuse, final UUID uuid) {
		new BukkitRunnable() {
			public void run() {
				TNTPrimed tntOld = tracking.get(uuid);
				TNTPrimed tnt = tntOld.getWorld().spawn(tntOld.getLocation(), TNTPrimed.class);
		        tnt.setVelocity(tntOld.getVelocity());
		        tnt.setMetadata("TNTType", tntOld.getMetadata("TNTType").get(0));
		        if (tntOld.hasMetadata("Owner")) {
			        tnt.setMetadata("Owner", tntOld.getMetadata("Owner").get(0));
		        }
		        if (CustomTNT.getTntHandler().getPunchables().contains(tntOld)) {
			        CustomTNT.getTntHandler().getPunchables().remove(tntOld);
			        CustomTNT.getTntHandler().getPunchables().add(tnt);
		        }		        
		        tntOld.remove();
		        tracking.remove(uuid);
	            tnt.setFuseTicks(fuse);
	            if (fuse > 80) {
	            	tracking.put(tnt.getUniqueId(), tnt);
	            	double repeat = (double)tnt.getFuseTicks()/70 - 1;
	            		if (repeat < 1) {
	            			continueTNTAnimation((int)(70 * repeat), tnt.getUniqueId());
	            		} else {
	            			continueTNTAnimation(tnt.getFuseTicks() - 70, tnt.getUniqueId());
	            		}
	            }
			}
			
		}.runTaskLater(CustomTNT.get(), 70L);
	}

	@EventHandler
    public void onBlockPlaced(final BlockPlaceEvent e) {
        if (e.getItemInHand().getType() == Material.TNT) {
			net.minecraft.server.v1_13_R2.ItemStack itemstack = CraftItemStack.asNMSCopy(e.getItemInHand());
        	 if (itemstack.getTag() != null && itemstack.getTag().hasKey("TntType")) {
        		String type = itemstack.getTag().getString("TntType");
        		if(e.getPlayer().hasPermission(CustomTNT.getTntHandler().getExplosionType(TNTType.valueOf(type.toUpperCase())).getPermission())) {
                	final Block block = e.getBlockPlaced();
                    block.setMetadata("TNTType", new FixedMetadataValue(CustomTNT.get(), type));
                    block.setMetadata("Owner", new FixedMetadataValue(CustomTNT.get(), e.getPlayer().getUniqueId().toString()));
                    CustomTNT.getTntHandler().getTNTBlocks().add(block);
        		} else {
        			e.setCancelled(true);
        			e.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.tnt-no-perm"));
        		}
        	}
        }
    }
    
    @EventHandler
    public void onBlockBroken(final BlockBreakEvent event) {
    	if (event.getBlock().getType().equals(Material.TNT)) {
            if (CustomTNT.getTntHandler().getTNTBlocks().contains(event.getBlock())) {
            	CustomTNT.getTntHandler().getTNTBlocks().remove(event.getBlock());
                final String tnt = event.getBlock().getMetadata("TNTType").get(0).asString();
                final TNTType type = TNTType.valueOf(tnt.toUpperCase());
                ExplosionType tntType = CustomTNT.getTntHandler().getExplosionType(type);
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), tntType.getItem());
            }
            event.getBlock().removeMetadata("TNTType", CustomTNT.get());
            event.getBlock().removeMetadata("Owner", CustomTNT.get());
    	}
    }
    
    @EventHandler
    public void onDispense(final BlockDispenseEvent event) {
        if (event.getItem().getType() == Material.TNT) {
			net.minecraft.server.v1_13_R2.ItemStack itemstack = CraftItemStack.asNMSCopy(event.getItem());
        	if (itemstack.getTag() != null && itemstack.getTag().hasKey("TntType")) {
           		String type = itemstack.getTag().getString("TntType");
                final BlockFace blockFace = ((Dispenser) event.getBlock().getState().getData()).getFacing();
                final Block block = event.getBlock().getRelative(blockFace);
                block.setMetadata("TNTType", new FixedMetadataValue(CustomTNT.get(), type));
                CustomTNT.getTntHandler().getTNTBlocks().add(block);
        	}
        }
    }
    
    @EventHandler 
    public void onPlayerInteract(PlayerInteractEvent e) {
		ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        Player player = e.getPlayer();
        if ( e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block tntBlock = e.getClickedBlock();
            if (tntBlock.getType().equals(Material.TNT) && tntBlock.hasMetadata("TNTType")) {
            	final String tntType = tntBlock.getMetadata("TNTType").get(0).asString();
            	if (tntType.equalsIgnoreCase("timebomb")) {
            		e.setCancelled(true);
            		final String owner = tntBlock.getMetadata("Owner").get(0).asString();
            		if (e.getPlayer().getUniqueId().toString().equals(owner)) {
                		new TimebombMenu(player, tntBlock.getLocation());
                		return;
            		}
            	}
            }
        }
        
        if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && item.getType().equals(Material.TNT)) {
			net.minecraft.server.v1_13_R2.ItemStack itemstack = CraftItemStack.asNMSCopy(item);
            if (itemstack.getTag() != null && itemstack.getTag().hasKey("TntType")) {
        		String type = itemstack.getTag().getString("TntType");
        		if(e.getPlayer().hasPermission(CustomTNT.getTntHandler().getExplosionType(TNTType.valueOf(type.toUpperCase())).getPermission())) {
        			if (CustomTNT.getTntHandler().getExplosionType(TNTType.valueOf(type.toUpperCase())).isThrowable()) {
        				if (type.equalsIgnoreCase("suicide")) {
        					if (suicideCooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis()/1000 - suicideCooldown.get(player.getUniqueId())/1000 < 5) {
        						e.setCancelled(true);
        						return;
        					}
        				}
            			if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            				e.setCancelled(true);
            				if (type.equalsIgnoreCase("suicide")) {
            					doSuicideBombing(player, type);   
            				} else {
                				player.sendMessage(new Messaging.MessageFormatter().format("error.cannot-place-block"));
            				}
            			}
            			if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            				if (type.equalsIgnoreCase("suicide")) {
            					doSuicideBombing(player, type);   
            				} else {
                				int amount = player.getInventory().getItemInMainHand().getAmount();
                				if (amount > 1) {
                					player.getInventory().getItemInMainHand().setAmount(amount - 1);
                				} else {
                					player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
                				}
								TNTPrimed tnt = player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);
                				tnt.setMetadata("TNTType", new FixedMetadataValue(CustomTNT.get(), type));
                				tnt.setMetadata("Owner", new FixedMetadataValue(CustomTNT.get(), e.getPlayer().getUniqueId().toString()));
                    		    tnt.setVelocity(player.getLocation().getDirection().normalize().multiply(1));
            				}
            			}
            		}
                } else {
                	if (CustomTNT.getTntHandler().getExplosionType(TNTType.valueOf(type.toUpperCase())).isThrowable() && e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                    	e.getPlayer().sendMessage(new Messaging.MessageFormatter().format("error.tnt-no-perm"));
                	}
                }
        		
            }
        }
    }

    private void doSuicideBombing(Player player, String type) {
		int amount = player.getInventory().getItemInMainHand().getAmount();
		if (amount > 1) {
			player.getInventory().getItemInMainHand().setAmount(amount - 1);
		} else {
			player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
		}
		suicideCooldown.put(player.getUniqueId(), System.currentTimeMillis());
		Block block = player.getEyeLocation().getBlock();
		block.setMetadata("TNTType", new FixedMetadataValue(CustomTNT.get(), type));
		block.setMetadata("Owner", new FixedMetadataValue(CustomTNT.get(), player.getUniqueId().toString()));
		CustomTNT.getTntHandler().getTNTBlocks().add(block);
		player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);
		player.setHealth(0);
    }
    
    @EventHandler
    public void onPlayerLeftClick(PlayerAnimationEvent e) {
		if(e.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) {
			for (TNTPrimed tnt: CustomTNT.getTntHandler().getPunchables()) {
				if (e.getPlayer().getLocation().distance(tnt.getLocation()) <= 4) {
					Vector toEntity = tnt.getLocation().toVector().subtract(e.getPlayer().getEyeLocation().toVector());
					Vector direction = e.getPlayer().getEyeLocation().getDirection();
					double dot = toEntity.normalize().dot(direction);
					if (dot > 0.9) {
						if (tnt.isOnGround() && direction.getY() < 0) {
							double y = -direction.getY();
							if (y < 0.4) {
								tnt.setVelocity(e.getPlayer().getLocation().getDirection().normalize().multiply(1).setY(0.4));
							} else {
								tnt.setVelocity(e.getPlayer().getLocation().getDirection().normalize().multiply(1).setY(y));
							}
						} else {
							tnt.setVelocity(e.getPlayer().getLocation().getDirection().normalize().multiply(1));
						}
					}
				}
			}
		}
    }

    @EventHandler 
    public void onPlayerPickupItem(EntityPickupItemEvent e) {
    	if(e.getEntity() instanceof Player) {
    		Player p = (Player) e.getEntity();
			if (e.getItem().getItemStack().getType() == Material.TNT) {
				net.minecraft.server.v1_13_R2.ItemStack itemstack = CraftItemStack.asNMSCopy(e.getItem().getItemStack());
				if (itemstack.getTag() != null && itemstack.getTag().hasKey("TntType")) {
					String type = itemstack.getTag().getString("TntType");
					if(!p.hasPermission(CustomTNT.getTntHandler().getExplosionType(TNTType.valueOf(type.toUpperCase())).getPermission())) {
						e.setCancelled(true);
						if (sendMessage.get(p.getUniqueId()) == null || System.currentTimeMillis() - sendMessage.get(p.getUniqueId()) >= 30000) {
							p.sendMessage(new Messaging.MessageFormatter().format("error.cannot-pickup"));
							sendMessage.put(p.getUniqueId(), System.currentTimeMillis());
						}
					}
				}
			}
		}
    }

    @EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
    	e.getPlayer().discoverRecipes(CustomTNT.getNamespacedKeys());
	}

	@EventHandler
	public void damageTNT(EntityDamageByEntityEvent e) {
    	System.out.println("GOT HERE");
	}

}
