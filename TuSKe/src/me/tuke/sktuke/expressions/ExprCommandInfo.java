package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import ch.njol.skript.command.Commands;
import ch.njol.skript.command.ScriptCommand;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.util.CommandUtils;
import me.tuke.sktuke.util.ReflectionUtils;

@Name("Command Info")
@Description("Get informations about a command.")
@Examples("if co")
@Since("1.6.9.6, 1.6.9.7")
public class ExprCommandInfo extends SimpleExpression<String>{
	static {
		NewRegister.newSimple(ExprCommandInfo.class,
				"[the] description of command %string%", "command %string%'[s] description",
				"[the] main [command] of command %string%", "command %string%'[s] main [command]",
				"[the] permission of command %string%", "command %string%'[s] permission",
				"[the] permission message of command %string%", "command %string%'[s] permission message",
				"[the] plugin [owner] of command %string%", "command %string%'[s] plugin [owner]",
				"[the] usage of command %string%", "command %string%'[s] usage",
				"[the] aliases of command %string%", "command %string%'[s] aliases",
				"[the] file [location] of command %string%", "command %string%'[s] file location");
	}

	private Expression<String> cmd;
	private int id = -1; // the matched pattern
	private String expr;
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return id != 6;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		id = arg1/2;
		cmd = (Expression<String>) arg[0];
		expr = arg3.expr.toLowerCase().split("command")[0] + "command";
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return expr;
	}
	
	@Override
	@Nullable
	protected String[] get(Event e) {
		String cmd = this.cmd.getSingle(e);
		if (cmd != null){
			cmd = cmd.toLowerCase();
			if (cmd.startsWith("/"))
				cmd = cmd.substring(1);
			if (id < 7){
				Command c = CommandUtils.getCommand(cmd);
				if (c != null){
					switch(id){
					
						case 0: return new String[]{c.getDescription() != null && !c.getDescription().equalsIgnoreCase("") ? c.getDescription(): null};
						case 1: return new String[]{c.getLabel()};
						case 2: return new String[]{c.getPermission() != null && !c.getPermission().equalsIgnoreCase("") ? c.getPermission(): null};
						case 3: return new String[]{c.getPermissionMessage()};
						case 4:
							if (c instanceof PluginCommand)
								return new String[]{((PluginCommand)c).getPlugin().getName()};
							break;
						case 5: return new String[]{c.getUsage() != null ? c.getUsage().replaceAll("^/?<command>", "/"+ c.getName()) : null};
						case 6: return c.getAliases().toArray(new String[c.getAliases().size()]);
					}
				}
			} else {
				Map<String, ScriptCommand> cmds = ReflectionUtils.getField(Commands.class, null, "commands");
				if (cmds == null)
					cmds = new HashMap<>();
				if (cmds.containsKey(cmd)){
					String result = cmds.get(cmd).getScript().getAbsolutePath();
					if (result.toLowerCase().contains("scripts"))
						result = result.split("scripts")[1].substring(1);
					return new String[]{result};
				}
			}
		}
		return null;
	}

}
