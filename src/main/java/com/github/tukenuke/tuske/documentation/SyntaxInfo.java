package com.github.tukenuke.tuske.documentation;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.SyntaxElementInfo;
import ch.njol.skript.lang.function.JavaFunction;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.registrations.Classes;
import com.github.tukenuke.tuske.util.ReflectionUtils;
import com.github.tukenuke.tuske.util.Util;
import org.bukkit.event.Cancellable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Tuke_Nuke on 30/07/2017
 */
public class SyntaxInfo {

	public String id, name, since, return_type, dependency;
	public String[] desc, examples, patterns, usage, changers, event_values;
	public Boolean cancellable; //It needs to be null for non event syntax

	public SyntaxInfo(SyntaxElementInfo info) {
		Class<?> c = info.c;
		if (c.isAnnotationPresent(NoDoc.class))
			return;
		if (c.isAnnotationPresent(Name.class))
			name = c.getAnnotation(Name.class).value();
		if (name == null || name.isEmpty())
			name = c.getSimpleName();
		id = c.getSimpleName();
		if (c.isAnnotationPresent(Description.class))
			desc = escapeHtml(c.getAnnotation(Description.class).value());
		if (c.isAnnotationPresent(Examples.class))
			examples = c.getAnnotation(Examples.class).value();
		patterns = fixPattern(info.patterns);
		if (c.isAnnotationPresent(Since.class))
			since = c.getAnnotation(Since.class).value();
		if (c.isAnnotationPresent(Dependency.class))
			dependency = c.getAnnotation(Dependency.class).value();
	}
	public SyntaxInfo(ExpressionInfo info, Class[] classes) {
		//
		this(info);
		ClassInfo ci = Classes.getSuperClassInfo(info.returnType);
		if (ci != null)
			return_type = ci.getDocName() == null || ci.getDocName().isEmpty() ? ci.getCodeName() : ci.getDocName();
		else
			return_type = "Object";
		List<String> array = new ArrayList<>();
		Expression expr = (Expression) ReflectionUtils.newInstance(info.c);
		try {
			for (Changer.ChangeMode mode : Changer.ChangeMode.values()) {
				if (Changer.ChangerUtils.acceptsChange(expr, mode, classes))
					array.add(mode.name().toLowerCase().replace('_', ' '));
			}
			if (array.size() == 0)
				array.add("none");
		} catch (Exception e) {
			array.add("unknown");
		}
		changers = array.toArray(new String[array.size()]);
	}
	public SyntaxInfo(SkriptEventInfo info, EventValuesGetter getter) {
		if (info.getDescription() != null && info.getDescription().equals(SkriptEventInfo.NO_DOC))
			return;
		name = info.getName();
        id = info.getId();
		desc = escapeHtml(info.getDescription());
		examples = info.getExamples();
		patterns = fixPattern(info.patterns);
		if (patterns != null && name.startsWith("On ")) {
			for (int x = 0; x < patterns.length; x++)
				patterns[x] = "[on] " +  patterns[x];
		}
		for (Class c : info.events)
			if (Cancellable.class.isAssignableFrom(c)) {
				cancellable = Boolean.TRUE;
			} else {
				cancellable = Boolean.FALSE;
				break;
			}
		since = info.getSince();
		if (getter != null) {
			Class<?>[][] classes = getter.getEventValues(info.events);
			if (classes == null || classes.length == 0)
				return;
			String[] time = new String[]{"past event-", "event-", "future event-"};
			List<String> times = new ArrayList<>();
			for (int x = 0; x < classes.length; x++)
				for (int y = 0; y < classes[x].length; y++) {
					ClassInfo ci = Classes.getSuperClassInfo(classes[x][y]);
					if (ci != null)
						times.add(time[x] + ci.getCodeName());
				}
			event_values = times.toArray(new String[times.size()]);
			if (event_values.length == 0)
				event_values = new String[]{"none"};
		}
	}
	public SyntaxInfo(ClassInfo info) {
		if (info.getDocName() != null && info.getDocName().equals(ClassInfo.NO_DOC))
			return;
		name = info.getDocName();
        id = info.getC().getSimpleName();
		desc = escapeHtml(info.getDescription());
		examples = info.getExamples();
		usage = escapeHtml(info.getUsage());
		since = info.getSince();
		if (info.getUserInputPatterns() != null) {
			patterns = new String[info.getUserInputPatterns().length];
			int x = 0;
			for (Pattern p : info.getUserInputPatterns())
				patterns[x++] = p.pattern().replaceAll("\\((.+?)\\)\\?", "[$1]").replaceAll("(.)\\?", "[$1]");
		}
	}

	public SyntaxInfo(JavaFunction info) {
		name = info.getName();
		id = info.getClass().getSimpleName();
		desc = escapeHtml(info.getDescription());
		examples = info.getExamples();
		StringBuilder sb = new StringBuilder();
		sb.append(info.getName()).append("(");
		if (info.getParameters() != null) {
			int index = 0;
			for (Parameter p : info.getParameters()) {
				if (index++ != 0) //Skip the first parameter
					sb.append(", ");
				sb.append(p);
			}
		}
		sb.append(")");
		patterns = new String[]{sb.toString()};
		since = info.getSince();
		return_type = info.getReturnType().getDocName() == null || info.getReturnType().getDocName().isEmpty() ?
				info.getReturnType().getCodeName() : info.getReturnType().getDocName();
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<>();
        addProperty(map, "ID", id);
		addProperty(map, "Name", name);
		addArray(map, "Description", desc);
		addArray(map, "Examples", examples);
		addProperty(map, "Usage", usage);
		addArray(map, "Since", since);
		addProperty(map, "Return type", return_type);
		addArray(map, "Changers", changers);
		addArray(map, "Patterns", patterns);
		addArray(map, "Event values", event_values);
		if (cancellable != null)
			map.put("Cancellable", cancellable);
		return map;
	}
	private void addProperty(Map<String, Object> map, String property, String... value) {
		if (!Util.notEmpty(value))
			return;
		StringBuilder sb = new StringBuilder();
		for (String str : value) {
			if (str != null && !str.isEmpty()) {
				if (sb.length() > 0)
					sb.append("\n");
				sb.append(str);
			}
		}
		if (sb.length() > 0)
			map.put(property, sb.toString());
	}
	private void addArray(Map<String, Object> map, String property, String... array) {
		if (!Util.notEmpty(array))
			return;
		map.put(property, array);
	}

	private String[] fixPattern(String... syntaxes) {
		if (Util.notEmpty(syntaxes)) {
			syntaxes = syntaxes.clone();
			for (int x = 0; x < syntaxes.length; x++)
				syntaxes[x] = syntaxes[x].replaceAll("\\\\([()])", "$1").replaceAll("\\d+¦", "");
		}
		return syntaxes;
	}
	private String[] escapeHtml(String... description) {
		if (Util.notEmpty(description)) {
			description = description.clone();
			for (int x = 0; x < description.length; x++)
				description[x] = description[x].replaceAll("<.+?>(.+?)</.+?>", "$1");
		}
		return description;
	}
}
