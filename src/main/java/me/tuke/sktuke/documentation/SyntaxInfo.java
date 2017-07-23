package me.tuke.sktuke.documentation;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.SyntaxElementInfo;
import ch.njol.skript.registrations.Classes;
import com.google.common.html.HtmlEscapers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SyntaxInfo {

	public String name;
	public String[] desc;
	public String[] examples;
	public String[] patterns;
	public String since;
	public String[] usage;
	public String[] events;
	public String[][] event_values;
	public SyntaxInfo() {

	}
	public SyntaxInfo(SyntaxElementInfo info) {
		Class<?> c = info.c.getClass();
		if (c.isAnnotationPresent(NoDoc.class))
			return;
		if (c.isAnnotationPresent(Name.class))
			name = c.getAnnotation(Name.class).value();
		if (name == null || name.isEmpty())
			name = c.getSimpleName();
		if (c.isAnnotationPresent(Description.class))
			desc = c.getAnnotation(Description.class).value();
		if (c.isAnnotationPresent(Examples.class))
			examples = c.getAnnotation(Examples.class).value();
		patterns = info.patterns;
		if (c.isAnnotationPresent(Since.class))
			since = c.getAnnotation(Since.class).value();
		if (c.isAnnotationPresent(Events.class))
			events = c.getAnnotation(Events.class).value();
	}
	public SyntaxInfo(SkriptEventInfo info, EventValuesGetter getter) {
		if (info.getDescription().equals(SkriptEventInfo.NO_DOC))
			return;
		name = info.getName();
		desc = info.getDescription();
		examples = info.getExamples();
		patterns = info.patterns;
		since = info.getSince();
		if (getter != null) {
			Class<?>[][] classes = getter.getEventValues(info.events);
			if (classes == null || classes.length == 0)
				return;
			for (int x = -1; x <= 1; x++) {
				event_values[x] = classes[x] != null ? new String[classes[x].length] : null;
				for (int y = 0; y < event_values.length; y++) {
					ClassInfo ci = Classes.getExactClassInfo(classes[x][y]);
					if (ci != null)
						event_values[x][y] =  ci.getCodeName();
				}
			}
		}
		events = null;
	}
	public SyntaxInfo(ClassInfo info) {
		if (info.getDocName().equals(ClassInfo.NO_DOC))
			return;
		name = info.getDocName();
		desc = info.getDescription();
		examples = info.getExamples();
		usage = info.getUsage();
		patterns = new String[info.getUserInputPatterns().length];
		int x = 0;
		for (Pattern p : info.getUserInputPatterns())
			patterns[x++] = p.pattern().replaceAll("\\((.+?)\\)\\?", "[$1]").replaceAll("(.)\\?", "[$1]");
	}

	private void fixPattern(String... syntaxes) {
		patterns = new String[syntaxes.length];
		for (int x = 0; x < syntaxes.length; x++)
			patterns[x] = syntaxes[x].replaceAll("\\\\(\\(|\\))", "$1").replaceAll("\\d+¦", "");
	}
	private void escapDescriptionHtml(String... description) {
		desc = new String[description.length];
		for (int x = 0; x < description.length; x++)
			desc[x] = description[x].replaceAll("<.+?>(.+?)</.+?>", "$1").replaceAll("&gt;", ">").replaceAll("&lt;", "<");

	}
}
