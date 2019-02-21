package com.walrusone.customtnt.commands;

import org.bukkit.ChatColor;

import com.walrusone.customtnt.CustomTNT;

public class ReloadCmd extends BaseCmd { 
	
	ReloadCmd() {
		forcePlayer = false;
		cmdName = "reload";
		argLength = 1; //counting cmdName
		usage = "";
		desc = ":: Reloads the config and messages.yml for CustomTNT";

	}

	@Override
	public boolean run() {
		CustomTNT.get().onDisable();
		CustomTNT.get().load();
		sender.sendMessage(ChatColor.GREEN + "Config and Messages have been reloaded!");
		return true;
	}

}
