package io.github.apickledwalrus.skriptgui;

import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Version;
import io.github.apickledwalrus.skriptgui.elements.conditions.*;
import io.github.apickledwalrus.skriptgui.elements.effects.*;
import io.github.apickledwalrus.skriptgui.elements.expressions.*;
import io.github.apickledwalrus.skriptgui.elements.sections.*;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import io.github.apickledwalrus.skriptgui.gui.GUIEvents;
import io.github.apickledwalrus.skriptgui.types.GUIClassInfo;
import io.github.apickledwalrus.skriptgui.types.SlotTypeClassInfo;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import io.github.apickledwalrus.skriptgui.gui.GUIManager;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.lang.converter.Converters;
import org.skriptlang.skript.lang.experiment.Experiment;
import org.skriptlang.skript.lang.experiment.LifeCycle;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.function.Consumer;

public class SkriptGUI extends JavaPlugin implements AddonModule {

	private static SkriptGUI instance;
	private static GUIManager manager;

	public static final Experiment MENU_EXPERIMENT =
		Experiment.constant("menu guis", LifeCycle.EXPERIMENTAL, "menu guis");

	public static SkriptGUI getInstance() {
		if (instance == null) {
			throw new IllegalStateException("skript-gui has not yet enabled!");
		}
		return instance;
	}

	public static GUIManager getGUIManager() {
		if (manager == null) {
			throw new IllegalStateException("skript-gui has not yet enabled!");
		}
		return manager;
	}

	@Override
	public void onEnable() {
		Plugin skript = getServer().getPluginManager().getPlugin("Skript");
		Version minimumSupportedVersion = new Version(2, 14, 3);
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

		SkriptAddon addon = Skript.instance().registerAddon(SkriptGUI.class, "skript-gui");
		addon.localizer().setSourceDirectories("lang", null);
		addon.loadModules(this);

		// Register manager and events
		manager = new GUIManager();
		getServer().getPluginManager().registerEvents(new GUIEvents(), this);
	}

	@Override
	public void init(SkriptAddon addon) {
		Classes.registerClass(new GUIClassInfo(addon));
		Converters.registerConverter(GUI.class, Inventory.class, GUI::getInventory);
		Classes.registerClass(new SlotTypeClassInfo());
		Skript.experiments().register(Skript.getAddon(this), MENU_EXPERIMENT);
	}

	@Override
	public void load(SkriptAddon addon) {
		register(addon,
			CondHasGUI::register,
			CondIsLocked::register,
			EffCancelClose::register,
			EffLock::register,
			EffShowGUI::register,
			ExprGlobalGUIs::register,
			ExprGUI::register,
			ExprId::register,
			ExprGUIValues::register,
			ExprGUIWithId::register,
			ExprLastGUI::register,
			ExprLayout::register,
			ExprLockStatus::register,
			ExprMenuInventory::register,
			ExprNextSlot::register,
			ExprPaginatedList::register,
			ExprVirtualInventory::register,
			SecCreateGUI::register,
			SecMakeSlot::register,
			SecOpenClose::register,
			SecSlotChange::register
		);
	}

	@SafeVarargs
	private static void register(SkriptAddon addon, Consumer<SyntaxRegistry>... consumers) {
		SyntaxRegistry syntaxRegistry = addon.syntaxRegistry();
		for (Consumer<SyntaxRegistry> consumer : consumers) {
			consumer.accept(syntaxRegistry);
		}
	}

	@Override
	public String name() {
		return "skript-gui";
	}

}
