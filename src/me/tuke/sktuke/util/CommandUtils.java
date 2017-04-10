package me.tuke.sktuke.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import me.tuke.sktuke.TuSKe;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

public class CommandUtils {
	private static SimpleCommandMap cmds = ReflectionUtils.invokeMethod(Bukkit.getServer().getClass(), "getCommandMap", Bukkit.getServer());
	private static String[] commands = new String[0];
	
	public static Command getCommand(String cmd){
		return cmds.getCommand(cmd);
	}

	public static String[] getCommandList() {
		Collection<Command> commandList = cmds.getCommands();
		if (commands.length != commandList.size()){
			final Set<String> cmdsList = commandList.stream().map(cmd -> cmd.getLabel().replaceFirst(".+?:","")).collect(Collectors.toSet());
			commands = cmdsList.toArray(new String[cmdsList.size()]);
			Arrays.sort(commands);
		}
		return commands;
	}
}
