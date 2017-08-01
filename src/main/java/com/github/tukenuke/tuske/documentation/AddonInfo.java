package com.github.tukenuke.tuske.documentation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Tuke_Nuke on 30/07/2017
 */
public class AddonInfo {

	private String name;

	public AddonInfo(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private List<SyntaxInfo> events = new ArrayList<>();
	private List<SyntaxInfo> conditions = new ArrayList<>();
	private List<SyntaxInfo> effects = new ArrayList<>();
	private List<SyntaxInfo> expressions = new ArrayList<>();
	private List<SyntaxInfo> types = new ArrayList<>();
	private List<SyntaxInfo> functions = new ArrayList<>();

	public List<SyntaxInfo> getEvents() {
		return events;
	}

	public List<SyntaxInfo> getConditions() {
		return conditions;
	}

	public List<SyntaxInfo> getEffects() {
		return effects;
	}

	public List<SyntaxInfo> getExpressions() {
		return expressions;
	}

	public List<SyntaxInfo> getTypes() {
		return types;
	}

	//Only Skript has this.
	public List<SyntaxInfo> getFunctions() {
		return functions;
	}

	public void sortLists() {
		Comparator<SyntaxInfo> c = Comparator.comparing(info -> info.name);
		events.sort(c);
		conditions.sort(c);
		effects.sort(c);
		expressions.sort(c);
		types.sort(c);
		functions.sort(c);
	}
}
