package com.github.tukenuke.tuske.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import ch.njol.skript.Skript;
import com.github.tukenuke.tuske.TuSKe;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

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
	public static void runCommand(final CommandSender sender, String cmd, String... perms){
		if (sender != null && cmd != null){
			if (sender instanceof Player && perms != null && !sender.isOp()){
				for (String perm : perms)
					if (perm != null) sender.addAttachment(TuSKe.getInstance(), perm, true, 0);
			}
			if (cmd.startsWith("/"))
				cmd = cmd.substring(1);
			Skript.dispatchCommand(sender, cmd);
		}
	}
}
