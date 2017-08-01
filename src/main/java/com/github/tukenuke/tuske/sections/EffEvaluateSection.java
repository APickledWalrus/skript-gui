package com.github.tukenuke.tuske.sections;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.command.Argument;
import ch.njol.skript.command.Commands;
import ch.njol.skript.config.Config;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.config.SimpleNode;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.effects.EffEvaluate;
import com.github.tukenuke.tuske.util.EffectSection;
import com.github.tukenuke.tuske.util.Evaluate;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;

import java.util.List;
import java.util.StringJoiner;

/**
 * The documentation is generated in {@link EffEvaluate}
 * @author Tuke_Nuke on 15/04/2017
 */
@NoDoc
public class EffEvaluateSection extends EffectSection {
	static {
		Registry.newEffect(EffEvaluateSection.class, "eval[uate] [logging [[the] error[s]] in %-objects%] [with safety]");
	}

	private Config currentScript;
	private List<Argument<?>> args;
	private Variable<?> result;
	private boolean withSafety = false;

	@Override
	public void execute(Event e) {
		if (hasSection()) {
			StringJoiner sj = new StringJoiner("\n");
			readSectionNode(sj, "", getSectionNode());
			if (sj.length() > 0) {
				Evaluate.getInstance().evaluate(sj.toString(), e, result, true, currentScript, args, this, withSafety);
			}
		}
	}

	@Override
	public String toString(Event event, boolean b) {
		return "evaluating";
	}

	@Override
	public boolean init(Expression<?>[] expr, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (!hasSection()) {
			Skript.error("You can't evaluate a empty code, put a code below or after this line, like:\n" +
					parseResult.expr + ": send \"Something\"\n" +
					"or\n" +
					parseResult.expr + ":\n" +
					"\tsend \"Something\"");
			return false;
		}
		if (expr[0] != null) {
			if (expr[0] instanceof Variable && ((Variable) expr[0]).isList())
				result = (Variable<?>) expr[0];
			else {
				Skript.error("The parameter needs to be a variable list.");
				return false;
			}
		}
		args = Commands.currentArguments;
		withSafety = parseResult.expr.contains("with safety");
		currentScript = ScriptLoader.currentScript;
		return true;
	}

	public void readSectionNode(StringJoiner sb, String indentation, SectionNode node) {
		if (node != null && sb != null && indentation != null)
			for (Node n : node) {
				if (n instanceof SectionNode) {
					sb.add(indentation + n.getKey() + ":");
					readSectionNode(sb, indentation + "\t", (SectionNode) n);
				} else if (n instanceof SimpleNode) {
					sb.add(indentation + n.getKey());
				}
			}
	}

}
