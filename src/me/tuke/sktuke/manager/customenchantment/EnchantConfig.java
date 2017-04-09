package me.tuke.sktuke.manager.customenchantment;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import me.tuke.sktuke.TuSKe;

public class EnchantConfig {
	//static Plugin p = Bukkit.getServer().getPluginManager().getPlugin("TuSKe");
	public static File file = new File(TuSKe.getInstance().getDataFolder(), "enchantments.yml");
	public static YamlConfiguration y = new YamlConfiguration();
	public static Pattern p = Pattern.compile("(\\s+)?(.+)(\\s+)?$");
	//-----------------DEFAULT-----------------
	public static final int MAX_LEVEL = 3999;
	public static final int DEFAULT_NUMBER = 3;
	public static final int MIN_NUMBER = 1;
	public static final int MAX_RARITY = 5;
	public static final boolean ENABLED = false;
	public static final boolean ENABLED_ON_ANVIL = true;
	public static final AcceptedItems ACCEPTED_ITEMS = AcceptedItems.ALL;
	//------------------------------------------

	public static void loadEnchants(){
		if (!file.exists())
			createNewFile();
		else if (file.exists() && loadFile() && y.isConfigurationSection("Enchantments")){ 
			
			try {
				first: for (String enchants: y.getConfigurationSection("Enchantments").getKeys(false)){
					ConfigurationSection cs = y.getConfigurationSection("Enchantments." + enchants);
					if (cs.getString("Name") != null){
						if (CustomEnchantment.getByID(enchants) != null || CustomEnchantment.getByName(cs.getString("Name")) != null){
							TuSKe.log("Couldn't register the enchantment '" + enchants + "' because it has a repeated name or lore name.", Level.WARNING);
							continue first;
						}
						int max = DEFAULT_NUMBER;
						int rarity = max;
						boolean enabled = ENABLED;
						boolean enabledOnAnvil = ENABLED_ON_ANVIL;
						
						if (cs.isSet("MaxLevel") && (!cs.isInt("MaxLevel") || (cs.getInt("MaxLevel") <= 0 || cs.getInt("MaxLevel") > MAX_LEVEL))){
							TuSKe.log("The value of 'MaxLevel' from enchantment '" + enchants + "' is not between 1 and "+ 20 +" or is not a valid value. Using the default value (" + DEFAULT_NUMBER + ").", Level.WARNING);
							cs.set("MaxLevel", DEFAULT_NUMBER);
						} else
							max = cs.getInt("MaxLevel");
						if (cs.isSet("Rarity") && (!cs.isInt("Rarity") || (cs.getInt("Rarity") <= 0 || cs.getInt("Rarity") > 5))){
							TuSKe.log("The value of 'Rarity' from enchantment '" + enchants + "' is not between 1 and 5 or is not a valid value. Using the default value (" + DEFAULT_NUMBER + ").", Level.WARNING);
							cs.set("Rarity", DEFAULT_NUMBER);
						} else
							rarity = cs.getInt("Rarity");
						if (cs.isSet("AcceptedItems")){
							if (cs.getString("AcceptedItems").contains(",") || cs.getString("AcceptedItems").contains(" and ")){
								List<String> list = Arrays.asList(getArray(cs.getString("AcceptedItems")));
								for (int x = 0; x < list.size(); x++){
									if (!AcceptedItems.isValue(list.get(x).toUpperCase())){
										list.remove(x);
										if (list.size() < 1) {
											cs.set("AcceptedItems", WordUtils.capitalize(ACCEPTED_ITEMS.name().toLowerCase()));
											TuSKe.log("There aren't any valid values for 'AcceptedItems'. Using the default value ("+ WordUtils.capitalize(ACCEPTED_ITEMS.name().toLowerCase()) +")", Level.WARNING);
										} else
											TuSKe.log("'" + list.get(x) + "' is not a valid value for 'AcceptedItems'. Ignoring it...", Level.WARNING);
									}
								
								}
									
							}
							else if (!AcceptedItems.isValue(cs.getString("AcceptedItems").toUpperCase().replaceAll(" ", ""))){
								TuSKe.log("The value of 'AcceptedItems' from enchantment '" + enchants + "' is not a valid value. Using the default value (" + WordUtils.capitalize(ACCEPTED_ITEMS.name().toLowerCase()) + ").");							
								cs.set("AcceptedItems", null);
							}
							
						}
						if (cs.isSet("Enabled") && !cs.isBoolean("Enabled"))
							cs.set("Enabled", null);
						else if (cs.isBoolean("Enabled"))
							enabled = cs.getBoolean("Enabled");
						if (cs.isSet("Enabled") && !cs.isBoolean("Enabled"))
							cs.set("EnabledOnAnvil", null);
						else if (cs.isBoolean("Enabled"))
							enabledOnAnvil = cs.getBoolean("Enabled");
						CustomEnchantment.registerNewEnchantment(
							enchants, 
							cs.getString("Name"), 
							max, 
							rarity, 
							AcceptedItems.getArrayList(getArray(((cs.isSet("AcceptedItems")) ? cs.getString("AcceptedItems") : ""))), 
							enabled, 
							enabledOnAnvil,
							getArray((cs.isSet("Conflicts") ? cs.getString("Conflicts") : "")));
						
					}
					else
						TuSKe.log("The enchantment '" + enchants + "' doesn't have a correct name. Skipping it.", Level.WARNING);
				}

				CustomEnchantment.stopRegistration();
				if (!y.isInt("Config.MaxEnchantmentsPerItem")){
					if (y.isInt("Config.MaxEnchantsPerItem"))
						y.set("Config.MaxEnchantsPerItem", null);
					else
						TuSKe.log("Wrong value for 'MaxEnchantPerItem'. Using the default value (4).", Level.WARNING);
					y.set("Config.MaxEnchantmentsPerItem", 4);
					save();
				}
				if (!y.isBoolean("Config.CompatibilityMode")){
					y.set("Config.CompatibilityMode", false);
					TuSKe.log("Wrong value for 'CompatibilityMode'. Using the default value (false).", Level.WARNING);
				}
				if (!y.isInt("Config.GlobalRarity")){
					y.set("Config.GlobalRarity", 5);
					save();
					//Bukkit.getLogger().warning("[TuSKe] Wrong value for 'GlobalRarity'. Using the default value (5).");
				} else if (y.getInt("Config.GlobalRarity") < 1 || y.getInt("Config.GlobalRarity") > 5) {
					y.set("Config.GlobalRarity", 5);
					TuSKe.log("Wrong value for 'GlobalRarity', only accepts integers between 1 and 5. Using the default value (5).", Level.WARNING);
					
				}
					
			} catch (Exception e){
				TuSKe.log("A error has occured when trying to load the enchantments file. Checks if hasn't any wrong values. \n" + e, Level.SEVERE);
			}
		}
	}
	public static boolean loadFile(){
		if (file.exists()){
			try {
				y.load(file);
				return true;
			} catch (Exception e) {
				TuSKe.log("Couldn't load the enchantments file, probably there are wrong values. But if you think everything is right, ask for support using this message and the enchantment file. \n\n" + e.getMessage(), Level.SEVERE);
			}
		}
		return false;
	}
	public static void createNewFile(){
		if (!file.exists())
			try {
				TuSKe.getInstance().saveResource("enchantments.yml", false);
			} catch(Exception e){
				TuSKe.log("Error occurred when creating a new enchantment file: \n" + e.getMessage(), Level.SEVERE);
				
			}
	}
	
	public static void save() {
		if (file.exists())
			try {
				y.save(file);
			} catch(Exception e){
				TuSKe.log("Error occurred when saving enchantment file: \n" + e.getMessage(), Level.SEVERE);
				
			}
	}
	public static int getGlobalRarity(){
		return y.getInt("Config.GlobalRarity");
	}
	public static void reload(){
		file = new File(TuSKe.getInstance().getDataFolder(), "enchantments.yml");
		CustomEnchantment.clear();
		CustomEnchantment.acceptRegistration(true);
		if (loadFile()){
			loadEnchants();
		}
	}
	public static String[] getArray(String str){
		String[] res = str.replaceAll(" and ", ",").split(",");
		for (int x = 0; x < res.length; x++){
			Matcher m = p.matcher(res[x]);
			if (m.find())
				res[x] = m.group(2);
		}
		return res;
	}
}
