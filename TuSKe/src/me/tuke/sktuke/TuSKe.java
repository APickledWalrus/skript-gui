package me.tuke.sktuke;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.util.StringUtils;
import me.tuke.sktuke.customenchantment.CustomEnchantment;
import me.tuke.sktuke.customenchantment.EnchantConfig;
import me.tuke.sktuke.customenchantment.EnchantManager;
import me.tuke.sktuke.gui.GUIManager;
import me.tuke.sktuke.nms.NMS;
import me.tuke.sktuke.recipe.RecipeManager;
import me.tuke.sktuke.register.Register;
import me.tuke.sktuke.util.LegendConfig;

public class TuSKe extends JavaPlugin {
	private static NMS nms;
	private static TuSKe plugin;
	private static long time = System.currentTimeMillis();
	private static GUIManager gui;
	private static RecipeManager recipes;
	private GitHubUpdater updater;
	private static boolean hasSupport = hasNMS();
	private static Register reg;
	
	@Override
	public void onEnable() {
		plugin = this;
		reg = new Register(this);
		if (reg.hasPlugin("Skript")){
			loadConfig();
			gui = new GUIManager();
			Integer[] result = reg.load();//array of amount of registered things
			if (result == null)
				return;//in case it started while skript is already loaded.
			updater = new GitHubUpdater(this, this.getFile(), "https://github.com/Tuke-Nuke/TuSKe/releases");
			recipes = new RecipeManager();
			if (getConfig().getBoolean("use_metrics"))
				try {
					Metrics metrics = new Metrics(this);
					metrics.start();
					log("Enabling Metrics... Done!");
				} catch (IOException e) {
					log("A error occured when trying to start the Metrics.");
				}
			double d = result[4]/1000;
			log("Loaded sucessfully a total of " + result[0] + " events, " + result[1] + " conditions, " + result[2] + " expressions and "+ result[3] + " effects in " +d+ " seconds. Enjoy ^-^");
			if (getConfig().getBoolean("updater.check_for_new_update"))
				checkUpdate();
				
		} else {
			log("Error 404 - Skript not found.", Level.SEVERE);
		    getServer().getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		gui.clearAll();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		if(getConfig().getBoolean("updater.check_for_new_update") && getConfig().getBoolean("updater.auto_update") && updater.hasDownloadReady(true)){
			updater.updatePlugin();
		}
		
		
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] arg){
		if (cmd.getName().equalsIgnoreCase("tuske")){
			if (arg.length > 0 && arg[0].equalsIgnoreCase("update")){
				if (arg.length > 1 && arg[1].equalsIgnoreCase("download")){
					if (updater.hasDownloadReady(false) && getConfig().getBoolean("updater.auto_update"))
						sender.sendMessage("§e[§cTuSKe§e] §3Already have a downloaded file ready to be updated.");
					else if (!updater.isLatestVersion()){
						sender.sendMessage("§e[§cTuSKe§e] §3Downloading the latest version...");
						if (!updater.downloadLatest())
							sender.sendMessage("§3A error occured when trying to download latest version. Maybe SkUnity is down?");
						else
							sender.sendMessage("§3The latest version was been dowloaded to TuSKe's folder.");
					} else
						sender.sendMessage("§e[§cTuSKe§e] §3The plugin is already running the latest version!");
				} else if (arg.length > 1 && arg[1].equalsIgnoreCase("plugin")){
					if (!getConfig().getBoolean("updater.check_for_new_update"))
						sender.sendMessage("§e[§cTuSKe§e] §3The option'");
					if (!updater.isLatestVersion() || updater.hasDownloadReady(true)){
						if (!updater.hasDownloadReady(false))
							if (!updater.downloadLatest()){
								sender.sendMessage("§e[§cTuSKe§e] §3A error occured when trying to download latest version. Maybe SkUnity is down?");
								return true;
							}
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
							//if (false)
							//	sender.sendMessage("§e[§cTuSKe§e] §3A error occured when trying to check for latest version. Maybe GitHub is down?");
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
						"§4/§c" + label + " " + arg[0] + " download §e> §3Download the lateast update.",
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
							"§4/§c" + label + " " + arg[0] + " list §e> §3Shows a list of registred items.",
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
		if (new File(this.getDataFolder(), "config.yml").exists())
			sc.save();		
	}
	private void sendDownloadRaw(CommandSender s){
		if (s instanceof Player)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw "+ s.getName() +" [{\"text\":\"\\u00a73Click \"},{\"text\":\"\\u00a7chere\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"\\u00a73Link to\n\\u00a77Git\u00a78Hub\"},\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://"+updater.getDownloadURL()+"\"}},{\"text\":\" \\u00a73to \\u00a73see \\u00a73what's \\u00a73new, \\u00a73click \"}, {\"text\":\"\\u00a7chere\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"\\u00a73Link to download\"},\"clickEvent\":{\"action\":\"open_url\",\"value\":\""+updater.getDownloadURL()+"\"}},{\"text\":\" \\u00a73to \\u00a73download \\u00a73or \\u00a73use \\u00a73the \\u00a73command \"},{\"text\":\"\\u00a7c/tuske \\u00a7cupdate \\u00a7cdownload\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/tuske update download\"}},{\"text\":\" \\u00a73to \\u00a73download \\u00a73directly \\u00a73to \\u00a73TuSKe's \\u00a73folder. \\u00a73And \\u00a73you \\u00a73can \\u00a73use \"},{\"text\":\"\\u00a7c/tuske \\u00a7cupdate \\u00a7cplugin\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/tuske update plugin\"}}]");
	}
	public static RecipeManager getRecipeManager(){
		return recipes;
	}
	
	public static GUIManager getGUIManager(){
		return gui;
	}
	public static LegendConfig getLegendConfig(){
		return reg.config;
	}
	public static void log(String msg){
		log(msg, Level.INFO);
	}
	public static void log(String msg, Level lvl){
		plugin.getLogger().log(lvl, msg);
	}
	public static void debug(Object... objects){
		if (!plugin.getConfig().getBoolean("debug_mode"))
			return;
		log("[Debug] " + StringUtils.join(objects, " || "));
	}
	public static boolean hasSupport(){
		return hasSupport;
	}
	private static boolean hasNMS(){
	
		try {
			String rversion = Bukkit.getServer().getClass().getPackage().getName().split(".v")[1];
			Class<?> classs = Class.forName("me.tuke.sktuke.nms.M_" + rversion);
            if (NMS.class.isAssignableFrom(classs)) { 
            	nms = (NMS) classs.getConstructor().newInstance(); 
            	return true;
            }
		} catch (final Exception e){
		}
		return false;
		
	}
	public static TuSKe getInstance(){
		return plugin;
	}
	public static NMS getNMS(){
		
		return nms;
	}
	public static boolean isSpigot(){
		try {
			final Class<Player> clazz = Player.class;
			if (clazz.getMethod("spigot") != null){
				return true;
			}
			
		} catch (Exception e){}
		return false;
	}
	
	/*public boolean isPixelmon(){
		return Bukkit.getServerName().equalsIgnoreCase("LendaryCraft");
	}*/
	private void checkUpdate(){
		log("Checking for latest update...");
		Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable(){
			
			@Override
			public void run() {
				updater.checkForUpdate(true);
				if (updater.getLatestVersion() != null)
					if (!updater.isLatestVersion()){
						if (getConfig().getBoolean("updater.auto_update")){
							if (updater.downloadLatest())
								log("Downloaded the latest version. The plugin will be updated when the server restarts.");
							else
								log("A error occurred while checking for new updates.\n" + updater.getLastException().getMessage());
						} else{
							log("New update available: v" + updater.getLatestVersion());
							log("Check what's new in: " + updater.getDownloadURL());
							log("You can download and update it with /tuske update.");
						}
					} else
						log("No new update was found!");
			}}, 10L);
	}
	public static Long getTime(){
		return time;
	}
}