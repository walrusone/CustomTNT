package com.walrusone.customtnt.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.walrusone.customtnt.utils.Messaging;
import com.walrusone.customtnt.utils.Util;

public abstract class BaseCmd {

	BaseCmd() {
		
	}
	
	CommandSender sender;
	String[] args;
	String cmdName;
	int argLength = 0;
	boolean forcePlayer = true;
	String usage = "";
	public Player player;
	String desc = "";

	void processCmd(CommandSender s, String[] arg) {
		sender = s;
		args = arg;

		if (forcePlayer) {
			if (!(s instanceof Player))  {
				sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
			} else {
				player = (Player) s;
			}
		}
		
		if (!Util.get().hp(sender, cmdName))
			sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
		else if (argLength > arg.length)
			s.sendMessage(ChatColor.DARK_RED + "Wrong usage: " + ChatColor.GRAY +"/ctnt " + helper());
		else run();
	}

	public abstract boolean run();
	
	
	String helper() {
		return ChatColor.RED + cmdName + " " + usage + " "+ ChatColor.GRAY + desc;
	}
}
