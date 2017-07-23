package me.tuke.sktuke.expressions;

import java.util.HashMap;
import java.util.Map;

import me.tuke.sktuke.util.Registry;
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
@Description("Returns a list containing all registered commands or scripts commands only.")
@Examples({
		"send \"Current amount of script commands: %size of all script commands%",
		"send \"Current amount of non script commands: %size of commansd - size of script commands%",
		"send \"Total: %size of all commands%\""})
@Since("1.6.9.7")
public class ExprAllCommand extends SimpleExpression<String>{
	static {
		Registry.newSimple(ExprAllCommand.class, "[all] [registered] [script] commands");
	}

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
