package me.tuke.sktuke;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import me.tuke.sktuke.listeners.OnlineStatusCheck;
import me.tuke.sktuke.manager.gui.v2.SkriptGUIEvent;
import me.tuke.sktuke.hooks.landlord.LandlordRegister;
import me.tuke.sktuke.hooks.legendchat.LegendchatRegister;
import me.tuke.sktuke.hooks.marriage.MarriageRegister;
import me.tuke.sktuke.hooks.simpleclans.SimpleClansRegister;
import me.tuke.sktuke.util.Evaluate;
import me.tuke.sktuke.util.Registry;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.tuke.sktuke.manager.customenchantment.CustomEnchantment;
import me.tuke.sktuke.manager.customenchantment.EnchantConfig;
import me.tuke.sktuke.manager.customenchantment.EnchantManager;
import me.tuke.sktuke.documentation.Documentation;
import me.tuke.sktuke.manager.gui.GUIManager;
import me.tuke.sktuke.nms.NMS;
import me.tuke.sktuke.nms.ReflectionNMS;
import me.tuke.sktuke.manager.recipe.RecipeManager;
import me.tuke.sktuke.util.ReflectionUtils;

public class TuSKe extends JavaPlugin {
	private static NMS nms;
	private static TuSKe plugin;
	private static GUIManager gui;
	private static RecipeManager recipes;
	private GitHubUpdater updater;

	public TuSKe() {
		if (plugin != null) //Unnecessary, just to look cool.
			throw new IllegalStateException("TuSKe can't have two instances.");
		plugin = this;
	}

	@Override
	public void onEnable() {
		// --------- Safe check if everything is ok to load ---------
		Boolean hasSkript = hasPlugin("Skript");
		if (!hasSkript || !Skript.isAcceptRegistrations()) {
			if (!hasSkript)
				log("Error 404 - Skript not found.", Level.SEVERE);
			else
				log("TuSKe can't be loaded when the server is already loaded.", Level.SEVERE);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		// ----------------------------------------------------------
		// ------------------ Initiate some stuffs ------------------
		loadConfig();
		EnchantConfig.loadEnchants();
		updater = new GitHubUpdater(this, getFile(), "Tuke-Nuke/TuSKe", getConfig().getBoolean("updater.download_pre_releases"));
		// ----------------------------------------------------------
		// ------------------------ Listener ------------------------
		// TODO temporary: make them auto-enable
		Bukkit.getServer().getPluginManager().registerEvents(new OnlineStatusCheck(this), this);
		// ----------------------------------------------------------
		// ------- Some stuffs like Metrics, docs and updater -------
		if (getConfig().getBoolean("use_metrics")) {
			new Metrics(this);
			log("Enabling Metrics... Done!");
		}
		if (getConfig().getBoolean("updater.check_for_new_update")) {
			checkUpdate();
			log("Check for updates enabled. It will check in some seconds.");
		}
		if (getConfig().getBoolean("documentation.enabled")) {
			log("Documentation enabled. Some files containing all syntax of all addons will be generated.");
			new Documentation(this).load();
		}
		// ----------------------------------------------------------
		// ---------------- Some thanks for donators ----------------
		log(" ");
		log(" A special thanks for donators:");
		log(" @X0Freak - 46$");
		log(" ");
		// ----------------------------------------------------------
		// ------------- Start to register all syntaxes -------------
		SkriptAddon tuske = Skript.registerAddon(this).setLanguageFileDirectory("lang");
		try {
			//                 It will return as "me.tuske.sktuke"
			tuske.loadClasses(getClass().getPackage().getName(), "register", "events", "conditions", "effects", "sections", "expressions");
			//TODO remove all dependencies and make them separated?
			if (hasPlugin("SimpleClans") || hasPlugin("SimpleClansLegacy")) // It is the same plugin, but with different names. I don't know why
				new SimpleClansRegister(tuske);
			if (hasPlugin("Legendchat"))
				new LegendchatRegister(tuske);
			if (hasPlugin("Marriage"))
				new MarriageRegister(tuske);
			if (ReflectionUtils.hasClass("com.jcdesimp.landlord.persistantData.LowOwnedLand")) //TODO Landlord provides support for an older version of API. Update needed
				new LandlordRegister(tuske);
			info("Loaded %d events, %d conditions, %d effects, %d expressions and %d types. Have fun!", Registry.getResults());

		} catch (Exception e) {
			info("Error while registering stuffs. Please, report it at %s", getDescription().getWebsite() + "/issues" );
			e.printStackTrace();
		}
		// ----------------------------------------------------------
	}

	@Override
	public void onDisable() {
		SkriptGUIEvent.getInstance().unregisterAll();
		if (gui != null)
			gui.clearAll();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		if(updater != null && getConfig().getBoolean("updater.check_for_new_update") && getConfig().getBoolean("updater.auto_update") && updater.hasDownloadReady(true)){
			updater.updatePlugin();
		}
	}
	public static TuSKe getInstance(){
		return plugin;
	}
	public static boolean hasPlugin(String str) {
		return plugin.getServer().getPluginManager().isPluginEnabled(str);
	}

	public void info(String msg, Object... values) {
		log(String.format(msg, values), Level.INFO);
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] arg){//TODO Remake this
		if (cmd.getName().equalsIgnoreCase("tuske")){
			if (arg.length > 0 && arg[0].equalsIgnoreCase("update")){
				if (arg.length > 1 && arg[1].equalsIgnoreCase("download")){
					if (updater.hasDownloadReady(false) && getConfig().getBoolean("updater.auto_update"))
						sender.sendMessage("§e[§cTuSKe§e] §3Already have a downloaded file ready to be updated.");
					else if (!updater.isLatestVersion()){
						sender.sendMessage("§e[§cTuSKe§e] §3Downloading the latest version...");
						updater.downloadLatest();
						sender.sendMessage("§3The latest version was been dowloaded to TuSKe's folder.");
					} else
						sender.sendMessage("§e[§cTuSKe§e] §3The plugin is already running the latest version!");
				} else if (arg.length > 1 && arg[1].equalsIgnoreCase("plugin")){
					if (!getConfig().getBoolean("updater.check_for_new_update"))
						sender.sendMessage("§e[§cTuSKe§e] §3The option 'check_for_new_update', in config file, needs to be true to check for updates.");
					else if (!updater.isLatestVersion() || updater.hasDownloadReady(true)){
						if (!updater.hasDownloadReady(false))
							updater.downloadLatest();
						getConfig().set("updater.auto_update", true);
						sender.sendMessage("§e[§cTuSKe§e] §3The plugin will update when the server restarts.");
					} else
						sender.sendMessage("§e[§cTuSKe§e] §3The plugin is already running the latest version!");
				} else if (arg.length > 1 && arg[1].equalsIgnoreCase("check")){
					sender.sendMessage("§e[§cTuSKe§e] §3Checking for update...");
					updater.checkForUpdate(true);
					Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable(){

						@Override
						public void run() {
							if (!updater.isLatestVersion()){
								sender.sendMessage("§e[§cTuSKe§e] §3New update available: §cv" + updater.getLatestVersion());
								if (sender instanceof Player)
									sendDownloadRaw(sender);
								else
									sender.sendMessage(new String[]{
										"§3Check what's new: §c" + updater.getDownloadURL(),
										"§3You can download and update it with §c/tuske update§3."
									});
							} else
								sender.sendMessage("§e[§cTuSKe§e] §3You are running the latest version: §cv" + updater.getLatestVersion());
							
						}}, 1L);
				} else {
					sender.sendMessage(new String[]{
						"§e[§cTuSKe§e] §3Main commands of §c"+ arg[0]+"§3:",
						"§4/§c" + label + " " + arg[0] + " check §e> §3Check for latest update.",
						"§4/§c" + label + " " + arg[0] + " download §e> §3Download the latest update.",
						"§4/§c" + label + " " + arg[0] + " plugin §e> §3Update the plugin after the server restarts.",
					});
					
				}
			} else if (arg.length > 0 && arg[0].equalsIgnoreCase("reload")){	
				if (arg.length > 1 && arg[1].equalsIgnoreCase("config")){
					reloadConfig();
					loadConfig();
					sender.sendMessage("§e[§cTuSKe§e] §3Config reloaded!");
				}
				else if (arg.length > 1 && arg[1].equalsIgnoreCase("enchantments")){
					EnchantConfig.reload();
					if (CustomEnchantment.getEnchantments().size() == 0)
						sender.sendMessage("§e[§cTuSKe§e] §3No enchantments were loaded. :(");
					else
						sender.sendMessage("§e[§cTuSKe§e] §3A total of §c" + CustomEnchantment.getEnchantments().size() + "§3custom enchantments were loaded succesfully.");
				} else {
					sender.sendMessage(new String[]{
						"§e[§cTuSKe§e] §3Main commands of §c"+ arg[0]+"§3:",
						"§4/§c" + label + " " + arg[0] + " config §e> §3Reload the config.",
						"§4/§c" + label + " " + arg[0] + " enchantments §e> §3Reload the enchantments' file.",
					});
					
				}	

			} else if (arg.length > 0 && arg[0].matches("ench(antment)?")){
				if (arg.length > 1 && arg[1].equalsIgnoreCase("list")){
					sender.sendMessage(new String[]{"§e[§cTuSKe§e] §3All registred enchantments:", "      §eName       §c-§e ML §c-§e R §c-§e Enabled?"});
					
					for (CustomEnchantment c : CustomEnchantment.getEnchantments()){
						sender.sendMessage("§c" + left(c.getId(), 15) + " §4-§c  " + c.getMaxLevel() + "  §4-§c " + c.getRarity() + " §4- " + (c.isEnabledOnAnvil() ? "§a" : "§c") + (c.isEnabledOnTable()));
					}
					
				} else if (arg.length > 1 && arg[1].equalsIgnoreCase("toggle")){
					String ench = getEnchantment(arg, 2);
					if (arg.length > 2 && EnchantManager.isCustomByID(ench)){
						CustomEnchantment ce = CustomEnchantment.getByID(ench);
						ce.setEnabledOnTable(!ce.isEnabledOnTable());
						sender.sendMessage("§e[§cTuSKe§e] §3The enchantment §c" + ce.getId() + "§3 was " + (ce.isEnabledOnTable() ? "§aenabled" : "§cdisabled") + "!");
					} else if (arg.length > 2 && !EnchantManager.isCustomByID(ench))
						sender.sendMessage("§e[§cTuSKe§e] §3There isn't any registred enchantment with ID §c" + ench + "§3.");
					else
						sender.sendMessage(new String[]{
								"§e[§cTuSKe§e] §3Use this command to enable/disable a enchantment.",
								"§4/§c" + label + " "+arg[0]+" toggle §4<§cID§4> §e> §3Enable/disable a enchantment.",
							});
				} else if (arg.length > 1 && arg[1].equalsIgnoreCase("give")){			
					if (sender instanceof Player){
						String ench = getEnchantment(arg, 2);
						if (arg.length > 2 && EnchantManager.isCustomByID(ench)){			
							Integer lvl = (arg.length > 3 && isInteger(arg[arg.length - 1])) ? Integer.valueOf(arg[arg.length - 1]) : 1;
							CustomEnchantment ce = CustomEnchantment.getByID(ench);
							Player p = (Player)sender;
							ItemStack i = p.getInventory().getItem(p.getInventory().getHeldItemSlot());
							if (i != null && !i.getType().equals(Material.AIR)){
								if (ce.isCompatible(i)){
									if(!EnchantManager.addToItem(p.getInventory().getItem(p.getInventory().getHeldItemSlot()), ce, lvl, true)){
										sender.sendMessage("§e[§cTuSKe§e] §3The enchantment §c" + ce.getId() + "§3 couldn't be added to your held item.");
									} else
										sender.sendMessage("§e[§cTuSKe§e] §3The enchantment §c" + ce.getId() + "§3 was added to your held item.");
								} else
									sender.sendMessage("§e[§cTuSKe§e] §3The enchantment §c" + ce.getId() + "§3 doesn't accept this item.");
							} else
								sender.sendMessage("§e[§cTuSKe§e] §3You have to hold a item first.");
						} else if (arg.length > 2 && !EnchantManager.isCustomByID(ench)){	
							sender.sendMessage("§e[§cTuSKe§e] §3There isn't any registred enchantment with ID §c" + ench + "§3.");
						} else
							sender.sendMessage(new String[]{
									"§e[§cTuSKe§e] §3Use this command to enchant your held item.",
									"§4/§c" + label + " " + arg[0] + " give §4<§cID§4> §c[§4<§cLevel§4>§c] §e> §3Add a enchantment to your held item.",
								});
					} else
						sender.sendMessage("§e[§cTuSKe§e] §3This command is only for players.");
				} else
					sender.sendMessage(new String[]{
							"§e[§cTuSKe§e] §3Main commands of §c"+ arg[0]+"§3:",
							"§4/§c" + label + " " + arg[0] + " list §e> §3Shows a list of registered enchantment.",
							"§4/§c" + label + " " + arg[0] + " toggle §e> §3Enable/disable a enchantment.",
							"§4/§c" + label + " " + arg[0] + " give §e> §3Add a enchantment to your held item.",
						});
			} else {
				sender.sendMessage(new String[]{
					"§e[§cTuSKe§e] §3Main commands:",
					"§4/§c" + label + " reload §e> §3Reload config/enchantments.",
					"§4/§c" + label + " update §e> §3Check for latest update.",
					"§4/§c" + label + " ench §e> §3Manage the enchantments.",
				});
				
			}
		}
		return true;
		
	}
	private String getEnchantment(String[] str, int id){
		StringBuilder sb = new StringBuilder();
		sb.append("");
		for (int x = id; x < str.length; x++){
			if (!(x == str.length - 1 && isInteger(str[x]))){
				sb.append(str[x]);
				if (x < str.length - 2)
					sb.append(" ");
			}
		}
		if (sb.toString().equals("") && str.length > id)
			sb.append(str[id]);
		return sb.toString();
	}
	private boolean isInteger(String arg){
		return arg.matches("\\d+");
	}
	private String left(String s, int d){
		StringBuilder sb = new StringBuilder(d);
		sb.append(s);
		while (sb.length() < d)
			sb.append(" ");
		return sb.toString();
	}
	
	private void loadConfig(){
		SimpleConfig sc = new SimpleConfig(this);
		sc.loadDefault();
		File f = new File(this.getDataFolder(), "config.yml");
		if (!f.exists())
			try {
				if (!getDataFolder().exists())
					getDataFolder().mkdirs();
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		Evaluate.getInstance().parseConfig(getConfig());
		new Thread(() ->sc.save(f)).start();
	}
	private void sendDownloadRaw(CommandSender s){
		if (s instanceof Player)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw "+ s.getName() +" [{\"text\":\"\\u00a73Click \"},{\"text\":\"\\u00a7chere\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"\\u00a73Link to\n\\u00a77Git\u00a78Hub\"},\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://"+updater.getDownloadURL()+"\"}},{\"text\":\" \\u00a73to \\u00a73see \\u00a73what's \\u00a73new, \\u00a73click \"}, {\"text\":\"\\u00a7chere\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"\\u00a73Link to download\"},\"clickEvent\":{\"action\":\"open_url\",\"value\":\""+updater.getDownloadURL()+"\"}},{\"text\":\" \\u00a73to \\u00a73download \\u00a73or \\u00a73use \\u00a73the \\u00a73command \"},{\"text\":\"\\u00a7c/tuske \\u00a7cupdate \\u00a7cdownload\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/tuske update download\"}},{\"text\":\" \\u00a73to \\u00a73download \\u00a73directly \\u00a73to \\u00a73TuSKe's \\u00a73folder. \\u00a73And \\u00a73you \\u00a73can \\u00a73use \"},{\"text\":\"\\u00a7c/tuske \\u00a7cupdate \\u00a7cplugin\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/tuske update plugin\"}}]");
	}
	public static RecipeManager getRecipeManager(){
		if (recipes == null)
			recipes = new RecipeManager();
		return recipes;
	}
	
	public static GUIManager getGUIManager(){
		if (gui == null)
			 gui = new GUIManager(getInstance());
	    return gui;
	}
	public static void log(String msg){
	    log(msg, Level.INFO);
	}
	public static void log(String msg, Level lvl){
	    plugin.getLogger().log(lvl, msg);
	}
	public static void log(Level lvl, String... msgs){
		for (String msg : msgs)
			log(msg, lvl);
	}
	public static boolean debug(){
		return plugin.getConfig().getBoolean("debug_mode");
	}
	public static void debug(Object... objects){
		if (!debug())
			return;
		StackTraceElement caller = new Exception().getStackTrace()[1];
		log(String.format("[Debug] [%s, line %s] %s", caller.getFileName(), caller.getLineNumber(), StringUtils.join(objects, " || ")));
	}
	public static NMS getNMS(){		
		if (nms == null){
			nms = (NMS) ReflectionUtils.newInstance(ReflectionUtils.getClass("me.tuke.sktuke.nms.M_" + ReflectionUtils.packageVersion));
			if (nms == null) {// Didn't find any interface avaliable for that version.
                nms = new ReflectionNMS(); //An default NMS class using reflection, in case it couldn't find it.
                log("Couldn't find support for the Bukkit version '" +ReflectionUtils.packageVersion+ "'. Some expressions, such as \"player data of %offline player%\", may or may not work fine, so it's better to ask the developer about it." , Level.WARNING);
            }
		}
		return nms;
	}

	public static boolean isSpigot(){
		return ReflectionUtils.hasMethod(Player.class, "spigot");
	}
	private void checkUpdate(){
		Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
			log("Checking for latest update...");
			updater.checkForUpdate(true);
			if (updater.getLatestVersion() != null)
				if (!updater.isLatestVersion()){
					if (getConfig().getBoolean("updater.auto_update")){
						updater.downloadLatest();
						log("Downloaded the latest version. The plugin will be updated when the server restarts.");
					} else{
						log("New update available: v" + updater.getLatestVersion());
						log("Check what's new in: " + updater.getURL());
						log("You can download and update it with /tuske update.");
					}
				} else
					log("No new update was found!");
		}, 10L);
	}
}