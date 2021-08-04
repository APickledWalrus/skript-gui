package io.github.apickledwalrus.skriptgui;

import java.io.IOException;

import ch.njol.skript.util.Version;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import io.github.apickledwalrus.skriptgui.gui.GUIManager;

public class SkriptGUI extends JavaPlugin {

	@SuppressWarnings("NotNullFieldNotInitialized")
	private static SkriptGUI instance;

	private static final GUIManager manager = new GUIManager();

	@Override
	public void onEnable() {
		Plugin skript = getServer().getPluginManager().getPlugin("Skript");
		if (skript == null || !skript.isEnabled()) {
			getLogger().severe("Could not find Skript! Make sure you have it installed and that it properly loaded. Disabling...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else if (!Skript.getVersion().isLargerThan(new Version(2, 5, 3))) { // Skript is not any version after 2.5.3 (aka 2.6)
			getLogger().severe("You are running an unsupported version of Skript. Please update to at least Skript 2.6-alpha1. Disabling...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		instance = this;

		SkriptAddon addon = Skript.registerAddon(this);
		try {
			addon.loadClasses("io.github.apickledwalrus.skriptgui.elements");
			addon.setLanguageFileDirectory("lang");
			new SkriptClasses(); // Register ClassInfos
			new SkriptConverters(); // Register Converters
		} catch (IOException e) {
			getLogger().severe("An error occured while trying to load the addon's elements. The addon will be disabled.");
			getLogger().severe("Printing StackTrace:");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	public static SkriptGUI getInstance() {
		return instance;
	}

	public static GUIManager getGUIManager() {
		return manager;
	}

}
