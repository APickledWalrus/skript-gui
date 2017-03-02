package me.tuke.sktuke.expressions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Event;

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

@Name("All commands")
@Description({
	"Used to get all registered commands.",
	"New in 1.7.1: You can get only script commands if you need it."})
@Examples({
		"command /help [<text>]:",
		"\ttrigger:",
		"\tall commands doesn't contain arg",
		"\t\tsend \"Unknown command!\""})
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

	@Override
	@Nullable
	protected String[] get(Event e) {
		if (scriptsOnly){
			Map<String, ScriptCommand> cmds = ReflectionUtils.getField(Commands.class, null, "commands");
			if (cmds == null)
				cmds = new HashMap<>(); // Just in case the field is renamed or return a null object;
			return cmds.keySet().toArray(new String[cmds.size()]);
		} else 
			return CommandUtils.getCommandList();
	}

}
