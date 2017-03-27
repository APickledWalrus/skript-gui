package me.tuke.sktuke.documentation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.SyntaxElementInfo;
import ch.njol.skript.registrations.Classes;

public class Documentation {
	
	private Map<String, Map<String, List<SyntaxInfo>>> docs = new HashMap<>(); // It will contains Addon's name, and 4 lists containing events, conditions, effects and expressions.
	private JavaPlugin instance;
	public Documentation(JavaPlugin plugin){
		instance = plugin;
	}

	public void load(){
		Bukkit.getScheduler().runTaskLaterAsynchronously(instance, new Runnable(){

			@Override
			public void run() {
				
				// Get all stuffs and organize them to write in the file
				instance.getLogger().info("Generating documentation of Skript & Addons.");
				Map<String, List<SyntaxInfo>> type = new HashMap<>(); 
				type.put("Types", new ArrayList<SyntaxInfo>()); //It will create a file only for the types, since it isn't possible to find which addon registered it.
				docs.put("Types", type);
				Map<String, String> addons = new HashMap<>(); // It is to store the addon's package and addon's name. Like "me.tuke.sktuke" and "TuSKe".
				for (SkriptAddon addon : Skript.getAddons()){
					addons.put(addon.plugin.getClass().getPackage().getName(), addon.getName());
				}
				addons.put(Skript.class.getPackage().getName(), "Skript");
				for (String name : addons.values()){
					Map<String, List<SyntaxInfo>> syntaxes = new HashMap<>();
					syntaxes.put("Events", new ArrayList<SyntaxInfo>());
					syntaxes.put("Conditions", new ArrayList<SyntaxInfo>());
					syntaxes.put("Effects", new ArrayList<SyntaxInfo>());
					syntaxes.put("Expressions", new ArrayList<SyntaxInfo>());
					docs.put(name, syntaxes);
				}
				for (SkriptEventInfo<?> event : Skript.getEvents())
					addEventSyntaxInfo(addons, event);
				for (SyntaxElementInfo<?> condition : Skript.getConditions())
					addSyntaxInfo(addons, "Conditions", condition.c, condition.patterns);
				for (SyntaxElementInfo<?> effect : Skript.getEffects())
					addSyntaxInfo(addons, "Effects", effect.c, effect.patterns);
				
				Iterator<ExpressionInfo<?, ?>> exprs = Skript.getExpressions();
				while (exprs.hasNext()){
					ExpressionInfo<?, ?> expr = exprs.next();
					addSyntaxInfo(addons, "Expressions", expr.c, expr.patterns);
				}
				for (ClassInfo<?> ci : Classes.getClassInfos())
					addTypeSyntaxInfo(ci);
				
				//Mapping the syntax done, now writting in a file
				
				File dir = new File(instance.getDataFolder(),"documentation/");
				
				if (!dir.exists())
					dir.mkdirs();
				for (Entry<String, Map<String, List<SyntaxInfo>>> entry : docs.entrySet()){
					try {
						File addonFile = new File(dir, entry.getKey() + ".sk");
						if (!addonFile.exists()) 
							addonFile.createNewFile();
						BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(addonFile), "UTF-8"));
						String toWrite = "";
						if (entry.getKey().equals("Types"))
							toWrite += "#All Skript and Addons types.\n"
									 + "#Each type has an pattern of how it should\n"
									 + "#Be used in command arguments or 'parsed as %type%'.\n"
									 + "#Also, some values contains its default values,\n"
									 + "#but they may have different values when using in scripts.\n\n";
						for (Entry<String, List<SyntaxInfo>> entry2 : entry.getValue().entrySet()){
							if (entry2.getValue().size() == 0)
								continue;
							toWrite += entry2.getKey() + ":\n";
							for (SyntaxInfo s : entry2.getValue())
								toWrite += s.toString();
						}
						bf.write(toWrite);
						bf.flush();
						bf.close();
					} catch (Exception e){
						instance.getLogger().severe("A error occurred while creating documentation of '" + entry.getKey() + "':");
						e.printStackTrace();
						return;
					}
				}
				instance.getLogger().info("Documentation was generated successfully.");
			}}, 100L);
	}
	public void addSyntaxInfo(Map<String, String> addons, String type, Class<?> c, String... syntaxes){
		String addonOwner = null;
		String pack = c.getPackage().getName();
		for (Entry<String, String> entry : addons.entrySet()){
			if (pack.startsWith(entry.getKey())){
				addonOwner = entry.getValue();
				break;
			}
		}
		if (addonOwner == null)
			addonOwner = "Skript";
		docs.get(addonOwner).get(type).add(getSyntaxInfo(c, syntaxes));
		
		
	}
	private void addEventSyntaxInfo(Map<String, String> addons, SkriptEventInfo<?> sei){
		String addonOwner = null;
		String pack = sei.c.getPackage().getName();
		for (Entry<String, String> entry : addons.entrySet()){
			if (pack.startsWith(entry.getKey())){
				addonOwner = entry.getValue();
				break;
			}				
		}
		if (addonOwner == null)
			addonOwner = "Skript";
		SyntaxInfo s = new SyntaxInfo();
		s.addValue("\t" + sei.name + ":");
		s.getValues().addAll(fromArray("Patterns", sei.patterns));
		docs.get(addonOwner).get("Events").add(s);
	}
	private void addTypeSyntaxInfo(ClassInfo<?> ci){
		SyntaxInfo s = new SyntaxInfo();
		s.addValue(" "); //Just to make a empty space before types
		if (ci.getDocName() != null){
			if (ci.getDocName().isEmpty())
				return; //It can hide some not needed types, as "byte" or "inventory holder"
			s.addValue("\t" + ci.getDocName().toLowerCase() + ":"  + (ci.getUserInputPatterns() != null && ci.getUserInputPatterns().length == 1 ? " " + ci.getUserInputPatterns()[0].pattern().replaceAll("\\((.+?)\\)\\?", "[$1]").replaceAll("(.)\\?", "[$1]"): ""));
		}else if (!ci.getCodeName().isEmpty())
			s.addValue("\t" + ci.getCodeName() + ":" + (ci.getUserInputPatterns() != null && ci.getUserInputPatterns().length == 1 ? " " + ci.getUserInputPatterns()[0].pattern().replaceAll("\\((.+?)\\)\\?", "[$1]").replaceAll("(.)\\?", "[$1]"): ""));
		if (ci.getUserInputPatterns() != null && ci.getUserInputPatterns().length > 1){
			String indentation = ci.getC().isEnum() ? "\t" : "";
			if (indentation.equals("\t"))
				s.addValue("\t\tPatterns:");
			for (Pattern p : ci.getUserInputPatterns())
				s.addValue(indentation + "\t\t" +p.pattern().replaceAll("\\((.+?)\\)\\?", "[$1]").replaceAll("(.)\\?", "[$1]"));
		} if (ci.getC().isEnum()){
			if (ci.getUsage() == null || ci.getUsage().length == 0){
				List<String> values = new ArrayList<String>();
				for (Object e : ci.getC().getEnumConstants()){
					values.add(e.toString().replaceAll("_", " ").toLowerCase());
				}
				s.addValue("\t\tValues: " + arrayToString(values.toArray(new String[values.size()])));
			} else
				s.addValue("\t\tValues: " + arrayToString(ci.getUsage()));
		}
		docs.get("Types").get("Types").add(s);
	}
	
	private SyntaxInfo getSyntaxInfo(Class<?> c, String... syntaxes){
		List<String> list = new ArrayList<String>();
		if (c.isAnnotationPresent(Name.class))
			list.add("\t" + c.getAnnotation(Name.class).value() + ":");
		else
			list.add("\t" + c.getSimpleName() + ":");
		if (syntaxes.length > 0)
			list.addAll(fromArray(null, syntaxes));
		SyntaxInfo s = new SyntaxInfo();
		s.getValues().addAll(list);
		return s;
	}
	private List<String> fromArray(String value, String... values){
		List<String> result = new ArrayList<>();
		if (value != null)
			result.add("\t\t" + value + ":");
		for (int x = 0; x < values.length; x++)
			result.add((value != null? "\t" : "") +"\t\t" + values[x]);
		return result;
		
	}
	
	private String arrayToString(String... values){
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < values.length; x++){
			if (x > 0){
				if (x == values.length - 1)
					sb.append(" and ");
				else
					sb.append(", ");
			}
			sb.append(values[x]);
		}
			
		return sb.toString();
		
	}
}
