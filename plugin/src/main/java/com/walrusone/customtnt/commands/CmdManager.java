package com.walrusone.customtnt.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.walrusone.customtnt.utils.Util;

public class CmdManager implements CommandExecutor {

	private List<BaseCmd> cmds = new ArrayList<>();

	//Add New Commands Here
	public CmdManager() {
		cmds.add(new GiveCmd());
		cmds.add(new ReloadCmd());
	}

	public boolean onCommand(CommandSender s, Command command, String label, String[] args) { 
		if (args.length == 0 || getCommands(args[0]) == null) {
			s.sendMessage(ChatColor.GRAY + "-------------------[" + ChatColor.RED + "Custom TNT" + ChatColor.GRAY + "]------------------");
			for (BaseCmd cmd : cmds) {
				if (Util.get().hp(s, cmd.cmdName)) s.sendMessage(ChatColor.GRAY + "- " + ChatColor.RED + "/ctnt " + cmd.helper());
			}
			s.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
			return false;
		} else {
			getCommands(args[0]).processCmd(s, args);
		}
		return true;
	}

	private BaseCmd getCommands(String s) {
		for (BaseCmd cmd : cmds) {
			if (cmd.cmdName.equalsIgnoreCase(s)) {
				return cmd;
			}
		}
		return null;
	}




}

