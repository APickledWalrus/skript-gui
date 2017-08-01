package me.tuke.sktuke.documentation;

import me.tuke.sktuke.util.Util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Tuke_Nuke on 30/07/2017
 */
public class DefaultFile extends FileType {

	public DefaultFile() {
		super("sk");
	}

	@Override
	public void write(BufferedWriter writer, AddonInfo addon) throws IOException {
		StringJoiner joiner = new StringJoiner("\n");
		addSection(joiner, "Events", addon.getEvents());
		addSection(joiner, "Conditions", addon.getConditions());
		addSection(joiner, "Effects", addon.getEffects());
		addSection(joiner, "Expressions", addon.getExpressions());
		addSection(joiner, "Types", addon.getTypes());
		addSection(joiner, "Functions", addon.getFunctions());
		writer.write(joiner.toString());
	}
	private void addSection(StringJoiner joiner, String section, List<SyntaxInfo> list) {
		if (list.isEmpty())
			return;
		joiner.add(section + ":");
		for (SyntaxInfo info : list)
			addSyntax(joiner, info);

	}
	private void addSyntax(StringJoiner joiner, SyntaxInfo info) {
		joiner.add("\t" + info.name + ":");
		for (Map.Entry<String, Object> entry : info.toRawMap().entrySet())
			if (!entry.getKey().equals("Name")) //We already have the name above
				addEntry(joiner, entry.getKey(), entry.getValue());
	}
	private void addEntry(StringJoiner joiner, String name, Object value) {
		if (value == null)
			return;
		if (value instanceof String && !Util.notEmpty((String)value))
			return;
		if (value instanceof String[] && !Util.notEmpty((String[])value))
			return;
		if (value instanceof String[]) {
			joiner.add("\t\t" + name + ":");
			for (String line : (String[])value)
				joiner.add("\t\t\t" + line);
		} else
			joiner.add("\t\t" + name + ": " + value);
	}
}
