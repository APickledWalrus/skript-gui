package me.tuke.sktuke.expressions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.help.HelpTopic;

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

@Name("All commands")
@Description({"Used to get all registered commands.\nNew in 1.7.1: You can get only script commands if you need it."})
@Examples("command /help [<text>]:\n\ttrigger:\n\t\tif all commands doesn't contain arg:\n\t\t\tsend \"Unknown command!\"")
@Since("1.6.9.7")
public class ExprAllCommand extends SimpleExpression<String>{

	private boolean scriptsOnly = false;
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		scriptsOnly = arg3.expr.toLowerCase().contains("script");
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return scriptsOnly ? "all scripts command": "all commands";
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	protected String[] get(Event e) {
		if (scriptsOnly){
			try {
				Field commands = Commands.class.getDeclaredField("commands");
				commands.setAccessible(true);
				Map<String, ScriptCommand> cmds = (Map<String, ScriptCommand>) commands.get(null);
				return cmds.keySet().toArray(new String[cmds.size()]);
			} catch (Exception ee){
				
			}
		} else {
			ArrayList<String> cmds = new ArrayList<>();
			for (HelpTopic ht : Bukkit.getHelpMap().getHelpTopics())
				cmds.add((ht.getName().startsWith("/") ? ht.getName().substring(1) : ht.getName()));
			return cmds.toArray(new String[cmds.size()]);
		}
		return null;
	}

}
