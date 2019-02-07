package com.walrusone.customtnt.commands;

public class Cmd extends BaseCmd { 
	
	public Cmd() {
		forcePlayer = true;
		cmdName = "";
		argLength = 2; //counting cmdName
		usage = "<>";
		desc = ":: ";

	}

	@Override
	public boolean run() {
		return true;
	}

}
