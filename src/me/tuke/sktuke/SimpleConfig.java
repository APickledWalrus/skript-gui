package me.tuke.sktuke;

import java.io.*;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;


public class SimpleConfig{

	private Object str = "Whatever";
	private JavaPlugin pl;
	private HashMap<String, String> map = new HashMap<>();
	public SimpleConfig(JavaPlugin plugin){
		pl = plugin;
	}
	public void loadDefault(){
		setDefault("use_metrics", true,
			"#Use metrics to send anonymous data about your server. The data that",
			"#is sent are:",
			"#",
			"#Players currently online (not max player count)",
			"#Version of the server (the same version you see in /version)",
			"#Version of this plugin",
			"#",
			"#If you don't agree with this, you can set it to false freely.",
			"#These values will be used only for statistic for this plugin.");
		setDefault("updater.check_for_new_update", true,
			"#It will check for new update everytime the server starts or",
			"#when someone use the command /tuske update check");
		setDefault("updater.auto_update", false,
			"#It will auto update the plugin. When there is a new version,",
			"#the plugin will download it and update when the server restarts.",
			"#Warning: I can't guarantee that the plugin is free of bugs that",
			"#can come in newest updates. I don't recommend to use in your main",
			"#server.",
			"#You can still download/update your plugin by command, see more in",
			"#/tuske update");
		setDefault("updater.download_pre_releases", (pl.getDescription().getVersion().contains("beta")),
			"#Download pre-releases.",
			"#Note: pre-releases versions shoudln't be used in your main server.",
			"#It's just to test new incomming features only!!");
		addComentsAbove("disable", 
			"#This option will be for future things of TuSKe.",
			"#It will be used when there is some features that isn't available",
			"#or uncompatible with your version. For now, it doesn't do nothing",
			"#but it tends to disable some crashing expression or lagging event",
			"#",
			"#true if you want to disable. (not implemented yet)");
		setDefault("disable.SomeExample", true);
		setDefault("debug_mode", false, 
			"#This option will be for future things of TuSKe.",
			"#It will just show some debug messages if needed.",
			"#So far, it doesn't do nothing, only prevent some testing debug messages",
			"#that can be accidentaly forgot inside the code.");
		setDefault("warn_unsafe_expressions", true,
			"#It will be used to warn about expressions that may have risk to your server,",
			"#The warn is sent when reloading a script and it won't prevent the expression to work.",
			"#An example is the expression 'random strings matching %regex%'.");
		setDefault("use_only_enum_names", false, 
			"#This is only needed in case of conflict with Skript or another addon",
			"#It will make some types, that is registered by TuSKe and if the type is a Enum,",
			"#to accept the form as '<Enum type>.<Enum name>'",
			"#For example, TuSKe register the type 'InventoryType' for the expression to create inventories",
			"#So, in case the value 'chest' is conflicting with something else, just enable it and",
			"#it will only accept if used like 'InventoryType.CHEST'",
			"#Example:",
			"#\topen virtual InventoryType.CHEST inventory with size 1 named \"Hi\" to player",
			"#Don't need to worry about it, is just in case.");
		addComentsAbove("documentation",
				"#A documentation that will be generated at 'plugins/TuSKe/documentation/'",
				"#for all addons");
		setDefault("documentation.enabled", true,
			"#Should documentation be generated?");
		
		//replace the config with the old values.
		String str = "use-metrics";
		if (pl.getConfig().isBoolean(str)){
			pl.getConfig().set(str.replaceAll("\\-", "_"), pl.getConfig().getBoolean(str));
			pl.getConfig().set(str, null);
		}
		for (String var : new String[]{"check-for-new-update", "auto-update"}){
			if (pl.getConfig().isBoolean(var)){
				pl.getConfig().set("updater." + var.replaceAll("\\-", "_"), pl.getConfig().getBoolean(var));
				pl.getConfig().set(var, null);
			}
			
		}
	}
	private boolean setDefault(String path, Object value, String... comments){
		if (!map.containsKey(path) ){
			if (comments.length > 0)
				addComentsAbove(path, comments);
			if (!pl.getConfig().isSet(path)){
				pl.getConfig().set(path, value);
				return true;
			}
		}
		return false;
		
	
	}
	private boolean addComentsAbove(String path, String... comments){
		if (!map.containsKey(path)){
			map.put(path, (map.size() > 0 ? "\n" : "")+ StringUtils.join(comments, "\n"));
			return true;
		}
		return false;
	}
	public void save(File file){
		try {			
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			String str = saveToString();
			bw.write(str);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String saveToString(){
		String toFile = pl.getConfig().saveToString();
		for (String key : map.keySet()){
			int last = key.split("\\.").length -1;
			String comment = map.get(key);
			String space = "";//updater:(.+)update:
			for (int x = 0; x < last; x++){
				space = space + "  ";
			}
			comment = comment.replaceAll("\n", "\n" + space) + "\n";
			String regex = keyToRegex(key);
			if (!key.equalsIgnoreCase(regex))
				toFile = toFile.replaceFirst("(?s)"+ regex, "$1$2" +  comment + space + "$3");
			else
				toFile = toFile.replaceFirst(key, comment + key);
		}
		map.clear();
		return toFile;
	}
	private String keyToRegex(String key){		
		return key.replaceAll("^((\\w+(\\s+|\\-)?)+)(\\.(.+\\.)?)((\\w+(\\s+|\\-)?)+)$", "($1:)(.+)($6:)"); //TODO fix that regex pattern
	}
}
