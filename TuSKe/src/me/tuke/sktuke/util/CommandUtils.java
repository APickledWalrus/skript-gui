package me.tuke.sktuke.util;

import java.util.Collection;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

public class CommandUtils {
	private static SimpleCommandMap cmds = ReflectionUtils.invokeMethod(Bukkit.getServer().getClass(), "getCommandMap", Bukkit.getServer(), SimpleCommandMap.class);
	private static String[] commands = new String[0];
	
	public static Command getCommand(String cmd){
		return cmds.getCommand(cmd);
	}
	public static String[] getCommandList(){
		Collection<Command> commandList = cmds.getCommands();
		if (commands.length != commandList.size()){
			commands = new String[commandList.size()];
			commandList.forEach(new Consumer<Command>(){

				int x = 0;
				@Override
				public void accept(Command cmd) {
					commands[x++] = cmd.getName();
				}});
		}
			
		return commands;
	}
}
