package com.github.tukenuke.tuske.effects;

import com.github.tukenuke.tuske.util.CommandUtils;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffExecutePermission extends Effect{
	static {
		Registry.newEffect(EffExecutePermission.class,
				"[execute] [the] command %strings% by %players% with perm[ission] %strings%",
				"[execute] [the] %players% command %strings% with perm[ission] %strings%",
				"(let|make) %players% execute [[the] command] %strings% with perm[ission] %strings%");
	}

	private Expression<Player> p;
	private Expression<String> cmd;
	private Expression<String> perm;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		if (arg1 == 0){
			cmd = (Expression<String>) arg[0];
			p = (Expression<Player>) arg[1];
		} else {
			p = (Expression<Player>) arg[0];
			cmd = (Expression<String>) arg[1];			
		}
		perm = (Expression<String>) arg[2];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "execute " + p + " command " + cmd + " with permission " + perm;
	}

	@Override
	protected void execute(Event e) {
		Player[] p = this.p.getArray(e);
		String[] cmd = this.cmd.getArray(e);
		String perm = this.perm.getSingle(e);
		if (p != null && cmd != null && perm != null){
			for (Player player : p)
				for (String command : cmd)
						CommandUtils.runCommand(player, command, perm);
		}
	}

}
