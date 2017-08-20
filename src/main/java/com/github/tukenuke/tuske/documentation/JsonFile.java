package com.github.tukenuke.tuske.documentation;

import com.google.gson.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Tuke_Nuke on 30/07/2017
 */
public class JsonFile extends FileType {

	private Gson gson;
	public JsonFile(boolean raw) {
		super("json");
		GsonBuilder gson = new GsonBuilder().disableHtmlEscaping();
		if (!raw)
			gson.enableComplexMapKeySerialization().setPrettyPrinting();
		this.gson = gson.create();
	}

	@Override
	public void write(BufferedWriter writer, AddonInfo addon) throws IOException {
		JsonObject json = new JsonObject();
		addSection(json, "events", addon.getEvents());
		addSection(json, "conditions", addon.getConditions());
		addSection(json, "effects", addon.getEffects());
		addSection(json, "expressions", addon.getExpressions());
		addSection(json, "types", addon.getTypes());
		addSection(json, "functions", addon.getFunctions());
		gson.toJson(json, writer);
	}

	public void addSection(JsonObject json, String property, List<SyntaxInfo> list) {
		JsonArray array = new JsonArray();
		for (SyntaxInfo syntax : list) {
			JsonObject jsonSyntax = getJsonSyntax(syntax);
			if (jsonSyntax.has("patterns"))
				array.add(getJsonSyntax(syntax));
		}
		if (array.size() > 0)
			json.add(property, array);
	}
	public JsonObject getJsonSyntax(SyntaxInfo info) {
		JsonObject syntax = new JsonObject();
		for (Map.Entry<String, Object> entry : info.toMap().entrySet()) {
			String property = entry.getKey().toLowerCase().replace('_', ' ');
			if (entry.getValue() instanceof String)
				syntax.addProperty(property, (String) entry.getValue());
			else if (entry.getValue() instanceof Boolean)
				syntax.addProperty(property, (Boolean) entry.getValue());
			else {
				JsonArray json = new JsonArray();
				for (String str : (String[]) entry.getValue())
					json.add(new JsonPrimitive(str));
				syntax.add(property, json);
			}
		}
		/*addProperty(syntax, "name", info.name);
		addProperty(syntax, "description", info.desc);
		addProperty(syntax, "examples", info.examples);
		addProperty(syntax, "patterns", info.patterns);
		addProperty(syntax, "usage", info.usage);
		addProperty(syntax, "since", info.since);
		addProperty(syntax, "return_type", info.return_type);
		addArray(syntax, "changers", info.changers);
		addArray(syntax, "events", info.events);
		addArray(syntax, "event_values", info.event_values);*/
		return syntax;
	}
}
