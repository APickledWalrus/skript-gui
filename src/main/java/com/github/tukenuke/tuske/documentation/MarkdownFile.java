package com.github.tukenuke.tuske.documentation;

import com.github.tukenuke.tuske.util.Util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author Tuke_Nuke on 30/07/2017
 */
public class MarkdownFile extends FileType {

	public MarkdownFile() {
		super("md");
	}

	@Override
	public void write(BufferedWriter writer, AddonInfo addon) throws IOException {
		StringJoiner wiki = new StringJoiner("\n");
		StringJoiner summary = new StringJoiner("\n");
		summary.add("## Summary");
		addSection(wiki, summary, "Events", addon.getEvents());
		addSection(wiki, summary, "Conditions", addon.getConditions());
		addSection(wiki, summary, "Effects", addon.getEffects());
		addSection(wiki, summary, "Expressions", addon.getExpressions());
		addSection(wiki, summary, "Types", addon.getTypes());
		addSection(wiki, summary, "Functions", addon.getFunctions());
		summary.add("<br>\n");
		writer.write(summary.toString());
		writer.write(wiki.toString());
	}

	public void addSection(StringJoiner wiki, StringJoiner summary, String section, List<SyntaxInfo> list) {
		if (list.size() == 0)
			return;
		summary.add("  * [" + section + "](#" + section + ")");
		wiki.add("## " + section);
		wiki.add(" ");
		StringJoiner syntaxes = new StringJoiner("\n \n---\n \n");
		for (SyntaxInfo info : list) {
			StringJoiner syntax = new StringJoiner("\n");
			addSyntax(syntax, info, section);
			syntaxes.add(syntax.toString());
		}
		wiki.add(syntaxes.toString());
		wiki.add("<br>");
	}

	public void addSyntax(StringJoiner syntax, SyntaxInfo info, String type) {
		syntax.add("### " + info.name);
		if (Util.notEmpty(info.desc))
			for (String line : info.desc)
				syntax.add(line);
		else
			syntax.add("No description available yet.");
		syntax.add(" ");
		if (Util.notEmpty(info.examples)) {
			syntax.add("```java");
			for (String line : info.examples)
				syntax.add(line);
			syntax.add("```");
		} else
			syntax.add("**No examples available yet.**");
		if (type.equals("Types")) {
			syntax.add("<table>");
			syntax.add("  <tr>");
			syntax.add("    <th>Pattern</th>");
			syntax.add("    <td>" + info.patterns[0] + "<br>");
			for (int x = 1; x < info.patterns.length; x++)
				syntax.add("        " + info.patterns[x] + "<br>");
			syntax.add("    </td>");
			syntax.add("  </tr>");
			if (Util.notEmpty(info.usage)) {
				syntax.add("  <tr>");
				syntax.add("    <th>Usage</th>");
				syntax.add("    <td>" + info.usage[0] + "<br>");
				for (int x = 1; x < info.usage.length; x++)
					syntax.add("        " + info.usage[x] + "<br>");
				syntax.add("    </td>");
			}
			syntax.add("  </tr>");
			syntax.add("</table>");

		} else if (Util.notEmpty(info.patterns)) {
			syntax.add("<details><summary>Syntaxes</summary><p>");
			syntax.add(" ");
			syntax.add("```java");
			for (String line : info.patterns)
				syntax.add(line);
			syntax.add("```");
			syntax.add(" ");
			syntax.add("</p></details>");
		} else
			syntax.add("Hm... No syntax? That's really weird...");
		if (info.event_values != null) {
			syntax.add("<details><summary>Event values</summary><p>");
			syntax.add(" ");
			syntax.add("```java");
			for (String line : info.event_values)
				syntax.add(line);
			syntax.add("```");
			syntax.add(" ");
			syntax.add("</p></details>");
		}
		syntax.add("<p>\n</p>"); //Just a little space between
		syntax.add("<table>");
		syntax.add("  <th><div title=\"Since which version it was added.\">Since</div></th>");
		syntax.add("  <td>" + (info.since == null || info.since.isEmpty() ? "1.0" : info.since) + "</td>");
		if (Util.notEmpty(info.return_type)) {
			syntax.add("  <th><div title=\"What type it returns\">Return type</div</th>\n");
			syntax.add("  <td>" + info.return_type + "</td>");
		}
		if (Util.notEmpty(info.changers)) {
			syntax.add("  <th><div title=\"The possible modifiers that this expression accepts.\"><a href=\"http://bensku.github.io/Skript/effects.html#EffChange\">Changers</a></div></th>");
			syntax.add("  <td>" + Util.toFancyArray(info.changers) + "</td>");
		}
		if (info.cancellable != null) {
			syntax.add("  <th><div title=\"It means if you can cancel this event from happening or not.\"><a href =\"http://bensku.github.io/Skript/effects.html#EffCancelEvent\">Cancellable</a></div></th>");
			syntax.add("  <td>" + info.cancellable + "</td>");
		}
		syntax.add("</table>");
	}

	private void addTable(StringJoiner sj, String property, String... array) {
		if (array == null || array.length == 0)
			return;
		sj.add("  <tr>");
		sj.add("    <th>" + property + "</th>");
		sj.add("    <td>");
		for (String str : array) {
			if (property.equalsIgnoreCase("Patterns") || property.equalsIgnoreCase("Event values"))
				sj.add("    <code>" + str + "</code><br>");
			else
				sj.add("    " + str + "");
		}
		sj.add("    </td>");
		sj.add("  </tr>");
	}
}
