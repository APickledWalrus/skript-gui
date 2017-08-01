package me.tuke.sktuke.documentation;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Tuke_Nuke on 30/07/2017
 */
public class YamlFile extends FileType {

	public YamlFile() {
		super("yml");
	}

	@Override
	public void write(BufferedWriter writer, AddonInfo addon) throws IOException {
		YamlConfiguration yaml = new YamlConfiguration();
		addSection(yaml, "Events", addon.getEvents());
		addSection(yaml, "Conditions", addon.getConditions());
		addSection(yaml, "Effects", addon.getEffects());
		addSection(yaml, "Expressions", addon.getExpressions());
		addSection(yaml, "Types", addon.getTypes());
		addSection(yaml, "Functions", addon.getFunctions());
		writer.write(yaml.saveToString());
	}

	public void addSection(YamlConfiguration yaml, String section, List<SyntaxInfo> list) {
		for (SyntaxInfo info : list)
			for (Map.Entry<String, Object> entry : info.toRawMap().entrySet())
				if (!entry.getKey().equalsIgnoreCase("name")) {
					Object obj = entry.getValue();
					if (entry.getKey().equalsIgnoreCase("Patterns") && obj instanceof String)
						obj = ((String) obj).split("\n");
					yaml.set(section + "." + info.name + "." + entry.getKey(), obj);
				}

	}
}
