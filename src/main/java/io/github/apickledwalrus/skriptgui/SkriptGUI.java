package io.github.apickledwalrus.skriptgui;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import io.github.apickledwalrus.skriptgui.gui.GUIManager;

public class SkriptGUI extends JavaPlugin {

	private static SkriptGUI instance;
	private static SkriptAddon addonInstance;

	private static final GUIManager manager = new GUIManager();

	@Override
	public void onEnable() {

		instance = this;

		addonInstance = Skript.registerAddon(this);
		try {
			addonInstance.loadClasses("io.github.apickledwalrus.skriptgui.elements");
		} catch (IOException e) {
			getLogger().severe("An error occured while trying to load the addon's elements. The addon will be disabled.");
			getLogger().severe("Printing StackTrace:");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
		}

		Classes.registerClass(new ClassInfo<>(GUI.class, "guiinventory")
				.user("gui inventor(y|ies)?")
				.name("GUI")
				.description("Represents a skript-gui GUI")
				.examples("See the GUI creation section.")
				.since("1.0")
				.parser(new Parser<GUI>() {

					@Override
					public boolean canParse(ParseContext ctx) {
						return false;
					}

					@Override
					public String toString(GUI gui, int flags) {
						return gui.getInventory().getType().getDefaultTitle().toLowerCase()
								+ " gui named " + gui.getName() 
								+ " with " + gui.getInventory().getSize() / 9 + " rows"
								+ " and shape " + gui.getRawShape();
					}

					@Override
					public String toVariableNameString(GUI gui) {
						return toString(gui, 0);
					}

					@Override
					public String getVariableNamePattern() {
						return ".+";
					}

				})
		);

	}

	public static SkriptGUI getInstance() {
		if (instance == null)
			throw new IllegalStateException("The plugin's instance was requested, but it is null.");
		return instance;
	}

	public static SkriptAddon getAddonInstance() {
		if (addonInstance == null)
			throw new IllegalStateException("The plugin's addon instance was requested, but it is null.");
		return addonInstance;
	}

	public static GUIManager getGUIManager() {
		return manager;
	}

}
