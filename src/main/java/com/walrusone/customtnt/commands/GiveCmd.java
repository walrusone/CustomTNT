package com.walrusone.customtnt.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.walrusone.customtnt.CustomTNT;
import com.walrusone.customtnt.tnts.TNTManager.TNTType;
import com.walrusone.customtnt.tnts.types.ExplosionType;
import com.walrusone.customtnt.utils.Messaging;
import com.walrusone.customtnt.utils.Util;

public class GiveCmd extends BaseCmd { 
	private List<String> types = new ArrayList<>();
	
	GiveCmd() {
		types.add("sniper");
		types.add("drill");
		types.add("timebomb");
		types.add("smokebomb");
		types.add("healing");
		types.add("lucky");
		types.add("suicide");
		types.add("concussion");
		types.add("flashbang");
		
		forcePlayer = false;
		cmdName = "give";
		argLength = 3; //counting cmdName
		usage = "<player> <TNTType> <Amount>";
		desc = ":: Gives you <amount> number of the TNT of type <TNTType>";
	}

	@Override
	public boolean run() {
		Player invitee = null;
		for (Player playerMatch: Bukkit.getOnlinePlayers()) {
			if (ChatColor.stripColor(playerMatch.getName()).equalsIgnoreCase(ChatColor.stripColor(args[1]))) {
				invitee = playerMatch;
			}
		}
   	 	if (invitee == null) {
            sender.sendMessage(new Messaging.MessageFormatter().format("error.player-not-found"));
            return true; 
   	 	}
   	 	
   	 	if (types.contains(args[2])) {
   	 		TNTType type = TNTType.valueOf(args[2].toUpperCase());
   	 		int amt = 1;
			if (args.length > 3) {
				if (Util.get().isInteger(args[3])) {
					amt = Integer.valueOf(args[3]);
				}
			}

			ExplosionType tntType = CustomTNT.getTntHandler().getExplosionType(type);
			ItemStack tnt = tntType.getItem();
			tnt.setAmount(amt);
			invitee.getInventory().addItem(tnt);
			return true;
   	 	} else {
   	 		sender.sendMessage(ChatColor.RED + "TNTType must be either: multi, sniper, timebomb, drill, lucky, healing, chemical, suicide, or smokebomb");
   	 		return true;
   	 	}
		
	}

}
