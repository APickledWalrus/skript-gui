package com.github.tukenuke.tuske.util;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.command.Argument;
import ch.njol.skript.command.Commands;
import ch.njol.skript.config.Config;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.log.LogEntry;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.variables.Variables;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * @author Tuke_Nuke on 24/06/2017
 */
public class Evaluate {
	private static Evaluate ourInstance = new Evaluate();

	public static Evaluate getInstance() {
		return ourInstance;
	}

	private Pattern[] filterSyntaxes = null;
	private Predicate<String> comparator = null;
	private Evaluate() {
	}

	public void parseConfig(FileConfiguration config) {
		List<String> syntaxes = config.getStringList("evaluate_filter.syntaxes");
		if (syntaxes == null || syntaxes.size() == 0)
			return;
		comparator = config.getString("evaluate_filter.mode").equalsIgnoreCase("whitelist") ?
				s ->{
					for (Pattern p : filterSyntaxes)
						if (p.matcher(s).find())
							return true;
					return false;
				} : s -> {
					for (Pattern p : filterSyntaxes)
						if (p.matcher(s).find())
							return false;
					return true;
		};
		filterSyntaxes = new Pattern[syntaxes.size()];
		int index = 0;
		for (String str : syntaxes) {
			str = "(?i).*" + str
					.replaceAll("%.+?%", ".+?") //replace all %player% with .+? to match everything
					.replaceAll("\\[(.+?)]", "($1)?") //convert skript optional [], to ()?
					.replaceAll("\\s+", "\\\\s+") +//trims all spaces to \\s+
					".*"; //This way it can find expressions too
			filterSyntaxes[index++] = Pattern.compile(str);
		}
	}

	public void evaluate(String code, Event e, Variable results, boolean parseString, Config currentScript, List<Argument<?>> args, TriggerItem source, boolean runWithSafety) {
		if (code != null && !code.isEmpty()) {
			final RetainingLogHandler log = SkriptLogger.startRetainingLog();
			try {
				Commands.currentArguments = args; //In case it is evaluated in a command, it will make the arguments work
				if (parseString) {
					ScriptLoader.currentScript = currentScript;
					VariableString vs = VariableString.newInstance(code.replaceAll("\"", "\"\""));
					if (vs != null)
						code = vs.getSingle(e);
				}
				code = code
						.replaceAll("\\\\n(?=[^\"]*(?:\"[^\"]*\"[^\"]*)*$)", "\n")
						.replaceAll("\\\\t(?=[^\"]*(?:\"[^\"]*\"[^\"]*)*$)", "\t")
				;
				Config c = new Config(code, "TuSKe/evaluate.sk", true, false, ":");
				// Using reflection here to not need to write the code to the file to evaluate
				// but also not let a null instance of file there.
				// Not needed to create the file, so far Skript do not use it to read.
				ReflectionUtils.setField(c.getClass(), c, "file", new File("TuSKe/evaluate.sk"));
				// Setting the current 'script'.
				SectionNode node = c.getMainNode();
				if (runWithSafety && filterSyntaxes != null) {
					List<Node> toRemove = new ArrayList<>();
					for (Node n : node) {
						if (comparator.test(n.getKey()))
							continue;
						log.log(new LogEntry(Level.SEVERE, "You don't have permission to use: " + n.getKey()));
						toRemove.add(n);
					}
					toRemove.forEach(Node::remove);
				}
				ScriptLoader.currentScript = c;
				ScriptLoader.setCurrentEvent("evaluate effect", e.getClass());
				TriggerSection ts = new TriggerSection(c.getMainNode()) {
					@Override
					protected TriggerItem walk(Event event) {
						return walk(event, true);
					}

					@Override
					public String toString(Event event, boolean b) {
						return "evaluate effect";
					}
				};
				ScriptLoader.deleteCurrentEvent();
				ScriptLoader.currentScript = null;
				Commands.currentArguments = null;
				setVariable(log, e, results);
				TriggerItem.walk(ts, e);

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				log.stop();
			}
		}
	}
	public void setVariable(RetainingLogHandler log, Event e, Variable results) {
		if (results != null) {
			int x = 1;
			String name = ((VariableString) ReflectionUtils.getField(Variable.class, results, "name")).getSingle(e).toLowerCase();
			String varName = name.substring(0, name.length() - 1); // Removes the "*" from a list var
			for (LogEntry lg : log.getErrors())
				Variables.setVariable(varName + x++, lg.getMessage(), e, results.isLocal());
		}
	}

}
