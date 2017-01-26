package me.tuke.sktuke.documentation;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;

public class Documentation {
	
	private Set<Syntax> syntaxes = new HashSet<>();
	private JavaPlugin plugin;
	private boolean enabled = false;
	public Documentation(JavaPlugin instance, boolean enabled){
		plugin = instance;
		this.enabled = enabled;
	}
	public void addSyntax(Syntax syntax){
		if (enabled)
			syntaxes.add(syntax);
	}
	public void generateDocs(){
		if (enabled)
			return;
		File file = new File(plugin.getDataFolder(), "documentation.yml");
		if (!file.exists())
			file.mkdirs();
		YamlConfiguration yml = new YamlConfiguration();
		try {
			yml.load(file);
			for (Syntax syntax : syntaxes){
				String mainPath = syntax.getType().name().toLowerCase() + "." + syntax.getName();
				yml.set(mainPath + ".Syntaxes", Arrays.asList(syntax.getSyntaxes()));
				if (syntax.getChangers() != null){
					yml.set(mainPath + ".Accepted changers", syntax.getChangers());
					
				}
				if (syntax.getReturnType() != null){
					ClassInfo<?> ci = Classes.getExactClassInfo(syntax.getReturnType());
					yml.set(mainPath + ".Return type", ci.getCodeName());
				}
				
			}
			yml.save(file);
		} catch (Exception e) {
			plugin.getLogger().severe("A error occured while creating a documentation: " + e.getMessage());
			e.printStackTrace();
		}
	}
	public boolean isEnabled(){
		return enabled;
	}
	

}
