package io.github.apickledwalrus.skriptgui;

import java.io.IOException;

import ch.njol.skript.util.Version;
import io.github.apickledwalrus.skriptgui.gui.events.GUIEvents;
import io.github.apickledwalrus.skriptgui.gui.events.RecipeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import io.github.apickledwalrus.skriptgui.gui.GUIManager;

public class SkriptGUI extends JavaPlugin {

	private static SkriptGUI instance;
	private static GUIManager manager;

	@Override
	public void onEnable() {
		Plugin skript = getServer().getPluginManager().getPlugin("Skript");
		Version minimumSupportedVersion = new Version(2, 6, 3);
		if (skript == null) {
			// Skript doesn't exist within the server plugins folder
			getLogger().severe("Could not find Skript! Make sure you have it installed. Disabling...");
			getLogger().severe("skript-gui requires Skript " + minimumSupportedVersion + " or newer! Download Skript releases at https://github.com/SkriptLang/Skript/releases");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else if (!skript.isEnabled()) {
			// Skript is disabled on the server
			getLogger().severe("Skript failed to properly enable and is disabled on the server. Disabling...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else if (Skript.getVersion().isSmallerThan(minimumSupportedVersion)) {
			// Current Skript version is below minimum required version
			getLogger().severe("You're running an unsupported Skript version (" + Skript.getVersion() + ")! Disabling...");
			getLogger().severe("skript-gui requires Skript " + minimumSupportedVersion + " or newer! Download Skript releases at https://github.com/SkriptLang/Skript/releases");
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
			getLogger().severe("An error occurred while trying to load the addon's elements. The addon will be disabled.");
			getLogger().severe("Printing StackTrace:");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
		}

		// Register manager and events
		manager = new GUIManager();
		getServer().getPluginManager().registerEvents(new GUIEvents(), this);
		if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent")) {
			// We need to track this event (see https://github.com/APickledWalrus/skript-gui/issues/33)
			getServer().getPluginManager().registerEvents(new RecipeEvent(), this);
		}

	}

	public static SkriptGUI getInstance() {
		return instance;
	}

	public static GUIManager getGUIManager() {
		return manager;
	}

}
