package com.github.tukenuke.tuske.documentation;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.JavaFunction;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.SkriptLogger;
import com.github.tukenuke.tuske.util.EffectSection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.SyntaxElementInfo;
import ch.njol.skript.registrations.Classes;

/**
 * @author Tuke_Nuke on 30/07/2017
 */
public class Documentation implements Runnable{

	private Map<String, AddonInfo> addons = new HashMap<>(); //Key: Main addon class's package.
	private JavaPlugin instance;
	private FileType fileType;
	public Documentation(JavaPlugin plugin, FileType file){
		instance = plugin;
		this.fileType = file;
	}

	public void load(){
		Bukkit.getScheduler().runTaskLaterAsynchronously(instance, this, 100L);
	}
	@Override
	public void run() {
		//Make sure to not run when Skript is registering yet
		if (Skript.isAcceptRegistrations())
			return;
		//First, let's map all addons' package
		instance.getLogger().info("Generating documentation of Skript & Addons.");
		addons.put(Skript.class.getPackage().getName(), new AddonInfo("Skript"));
		for (SkriptAddon addon : Skript.getAddons())
			addons.put(addon.plugin.getClass().getPackage().getName(), new AddonInfo(addon.getName()));
		//Now, let's organize all syntaxes
		EventValuesGetter getter = new EventValuesGetter();
		for (SkriptEventInfo info : Skript.getEvents())
			addSyntax(getAddon(info.c).getEvents(), new SyntaxInfo(info, getter));
		for (SyntaxElementInfo info : Skript.getConditions()) {
			if (EffectSection.class.isAssignableFrom(info.c)) //Separate effect sections to effects instead of conditions
				addSyntax(getAddon(info.c).getEffects(), new SyntaxInfo(info));
			else
				addSyntax(getAddon(info.c).getConditions(), new SyntaxInfo(info));
		}
		for (SyntaxElementInfo info : Skript.getEffects())
			addSyntax(getAddon(info.c).getEffects(), new SyntaxInfo(info));
		Class[] types = new Class[Classes.getClassInfos().size()];
		int x = 0;
		for (ClassInfo info : Classes.getClassInfos())
			types[x++] = info.getC();
		//A LogHandler for expressions since it catch the changers, which can throw errors in console
		//such as "Expression X can only be used in event Y"
		ParseLogHandler log = SkriptLogger.startParseLogHandler();
		Skript.getExpressions().forEachRemaining(info -> addSyntax(getAddon(info.c).getExpressions(), new SyntaxInfo(info, types)));
		log.clear();
		log.stop();

		for (ClassInfo info : Classes.getClassInfos())
			addSyntax(getAddon(info).getTypes(), new SyntaxInfo(info));
		for (JavaFunction info : Functions.getJavaFunctions()) //Only Skript use this...
			addSyntax(getAddon(info.getClass()).getFunctions(), new SyntaxInfo(info));
		//Before, lets delete old files...
		File docsDir = new File(instance.getDataFolder(), "documentation/");
		docsDir.delete();
		docsDir.mkdirs();
		//Done, now let's write them all into files
		for (AddonInfo addon : addons.values()) {
			addon.sortLists();
			File file = new File(docsDir, addon.getName() + "." + fileType.getExtension());
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				try {
					file.createNewFile();
				} catch (IOException io) {

				}
			}
			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
				fileType.write(writer, addon);
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
		instance.getLogger().info("Documentation was generated successfully.");
	}

	private void addSyntax(List<SyntaxInfo> list, SyntaxInfo syntax) {
		if (syntax.name == null || syntax.name.isEmpty())
			return;
		if (syntax.patterns == null || syntax.patterns.length == 0)
			return;
		list.add(syntax);
	}

	public AddonInfo getAddon(ClassInfo info) {
		AddonInfo addon;
		if (info.getParser() != null)
			addon = getAddon(info.getParser().getClass());
		else if (info.getSerializer() != null)
			addon = getAddon(info.getSerializer().getClass());
		else if (info.getChanger() != null)
			addon = getAddon(info.getChanger().getClass());
		else
			addon = getAddon(info.getClass());
		return addon;
	}

	public AddonInfo getAddon(Class<?> c) {
		String name = c.getPackage().getName();
		for (Entry<String, AddonInfo> entry : addons.entrySet())
			if (name.startsWith(entry.getKey()))
				return entry.getValue();
		//If null, it means that an addon wasn't registered or has wrong package format, so let's put in Skript.
		return addons.get(Skript.class.getPackage().getName());
	}
}
