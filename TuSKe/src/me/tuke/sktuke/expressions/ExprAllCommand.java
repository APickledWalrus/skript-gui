package me.tuke.sktuke.expressions;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.help.HelpTopic;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprAllCommand extends SimpleExpression<String>{

	private Expression<String> command = null;
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		if (arg1 > 0)
			command = (Expression<String>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "all commands";
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		if (command != null && command.getSingle(e) != null){
			PluginCommand c = Bukkit.getPluginCommand(command.getSingle(e));
			return c != null ? c.getAliases().toArray(new String[c.getAliases().size()]) : null;
		}
		ArrayList<String> cmds = new ArrayList<>();
		for (HelpTopic ht : Bukkit.getHelpMap().getHelpTopics())
			cmds.add(ht.getName().replaceAll("/", ""));
		return cmds.toArray(new String[cmds.size()]);
	}

}
