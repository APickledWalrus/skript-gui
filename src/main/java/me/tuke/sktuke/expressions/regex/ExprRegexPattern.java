package me.tuke.sktuke.expressions.regex;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Tuke_Nuke on 12/04/2017
 */
@Name("Regex Pattern")
@Description("This expression returns some regex patterns that Skript or Bukkit uses most.")
@Examples({
		"player's uuid regex matches uuid pattern #It will be always true.",
		"\"{MyVariable}\" regex matches variable pattern",
		"set {_list::*} to regex split \"%{VariableList::*}%\" with list pattern"})
@Since("1.7.5")
public class ExprRegexPattern extends SimpleExpression<Pattern>{

	public static Map<String, Pattern> patterns = null; //It will be initialized only when this is used

	static {
		Registry.newMatchesEverything(ExprRegexPattern.class, "<.+> [regex] pattern");
	}
	private void init() {
		if (patterns == null) {
			patterns = new HashMap<>();
			patterns.put("uuid", Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
			patterns.put("variable", Pattern.compile("(?<=^|,)\\s*([^\",]*|\"([^\"]|\"\")*\")\\s*(,|$)"));
			patterns.put("list", SkriptParser.listSplitPattern);
		}
	}
	private Pattern result;

	@Override
	protected Pattern[] get(Event event) {
		return new Pattern[]{result};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Pattern> getReturnType() {
		return Pattern.class;
	}

	@Override
	public String toString(Event event, boolean b) {
		return null;
	}

	@Override
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		init();
		String name = parseResult.regexes.get(0).group(0).toLowerCase();
		if ((result = patterns.get(name)) != null)
			return true;
		Skript.error("Doesn't exist any regex pattern called '" + name + "'.");
		return false;
	}
}
