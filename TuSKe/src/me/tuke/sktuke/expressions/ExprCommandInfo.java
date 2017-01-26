package me.tuke.sktuke.expressions;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
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
import me.tuke.sktuke.TuSKe;

@Name("Command Info")
@Description("Get informations about a command.")
@Examples("if co")
@Since("1.6.9.6, 1.6.9.7")
public class ExprCommandInfo extends SimpleExpression<String>{

	private Expression<String> cmd;
	private int id = -1; // the regex index
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
	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	protected String[] get(Event e) {
		if (cmd.getSingle(e) != null){
			String cmd = this.cmd.getSingle(e);
			if (cmd.startsWith("/"))
				cmd = cmd.substring(1);
			PluginCommand c = Bukkit.getPluginCommand(cmd);
			if (c != null){
				switch(id){
				
				case 0: return new String[]{c.getDescription() != null && !c.getDescription().equalsIgnoreCase("") ? c.getDescription(): null};
				case 1: return new String[]{c.getLabel()};
				case 2: return new String[]{c.getPermission() != null && !c.getPermission().equalsIgnoreCase("") ? c.getPermission(): null};
				case 3: return new String[]{c.getPermissionMessage()};
				case 4: return new String[]{c.getPlugin().getName()};
				case 5: return new String[]{c.getUsage() != null ? c.getUsage().replaceAll("^/?<command>", "/"+ c.getName()) : null};
				case 6: return c.getAliases().toArray(new String[c.getAliases().size()]);
				case 7: 
					try {
						Field commands = Commands.class.getDeclaredField("commands");
						commands.setAccessible(true);
						Map<String, ScriptCommand> cmds = (Map<String, ScriptCommand>) commands.get(null);
						if (cmds.containsKey(cmd)){
							String result = cmds.get(cmd).getScript().getAbsolutePath();
							if (result.toLowerCase().contains("scripts"))
								result = result.split("scripts")[1].substring(1);
							return new String[]{result};
						}
						
					} catch (Exception ex){
						TuSKe.log("A error occurred while getting the script from command '" + cmd + "', report it to TuSKe (In Spigot or SkUnity). \n");
						ex.printStackTrace();
						
					}
				}
			}
		}
		return null;
	}
	/*public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		String cmd = this.cmd.getSingle(e);
		if (cmd != null && delta != null){
			PluginCommand c = Bukkit.getPluginCommand(cmd);
			for (int x = 0; x < delta.length; x++)
				if (delta[x] != null && ((String)delta[x]).startsWith("/"))
					delta[x] = ((String)delta[x]).substring(1);
			if (c != null){
				switch(id){
				case 0: c.setAliases(Arrays.asList((String[])delta));
				case 1: c.setDescription((String) delta[0]);
				case 3:c.setPermission((String) delta[0]);
				case 4:c.setPermissionMessage((String) delta[0]);
				case 6:c.setUsage((String) delta[0]);
				
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (id == 2 || id == 5)
			return null;
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(String[].class);
		return null;
	}*/

}
