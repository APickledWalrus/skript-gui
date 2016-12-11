package me.tuke.sktuke.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import me.tuke.sktuke.TuSKe;

public class LegendConfig {
	private File file;
	private YamlConfiguration y = new YamlConfiguration();
	
	public LegendConfig(TuSKe instance){
		file = new File(instance.getDataFolder(), "tags.yml");
	}
	
	public Map<String, String> getPlayerTags(OfflinePlayer p){
		Map<String, String> map = new HashMap<String, String>();
		if (file.exists()){
			
			if (p != null){
				if (hasOldData(p))
					convertToUUID(p);
				if (getTagFile().getConfigurationSection(p.getUniqueId().toString()) != null)
					for (String s : getTagFile().getConfigurationSection(p.getUniqueId().toString()).getKeys(false))
						map.put(s, getTagFile().getString(p.getUniqueId().toString() + "." + s));
			}
		}
		return map;	
		
	}
	public String getPlayerTag (OfflinePlayer p, String Tag){
		if (file.exists()){
			if (p != null && Tag != null){
				if (hasOldData(p))
					convertToUUID(p);
				if (getTagFile().contains(p.getUniqueId() + "." + Tag))
					return getTagFile().getString(p.getUniqueId() + "." + Tag);
			}
		}
		return null;
	}
	public void setPlayerTag(OfflinePlayer p, String Tag, String Value){
		if (!getFile().exists())
			createTagFile();
		if (getTagFile() != null && p != null && Tag != null && Value != null){
			getTagFile().set(p.getUniqueId().toString() + "." + Tag, Value);
			save();
		}
		
	}
	public YamlConfiguration getTagFile(){
		if (getFile().exists()){
			try {
				y.load(getFile());
			} catch (Exception e) {
			}
			return y;
		}
		return null;
	}
	public void clearTag(OfflinePlayer p, String Tag){
		if (getFile().exists()){
			if (hasOldData(p))
				convertToUUID(p);
			if (getTagFile() != null && p != null && Tag != null && getTagFile().isSet(p.getUniqueId()+"."+Tag)){
				if (getTagFile().getConfigurationSection(p.getUniqueId().toString()).getKeys(false).size() == 1)
					getTagFile().set(p.getUniqueId().toString(), null);
				else
					getTagFile().set(p.getUniqueId().toString() + "." + Tag, null);
				save();
			}
		}
		
	}
	public File getFile(){
		return file;
	}
	
	private boolean hasOldData(OfflinePlayer p){
		return getTagFile().getConfigurationSection(p.getName()) != null;
	}
	private void convertToUUID(OfflinePlayer p){
		if (hasOldData(p)){
			for (String s : getTagFile().getConfigurationSection(p.getName()).getKeys(false)){
				setPlayerTag(p, s, getTagFile().getString(p.getName() + "." + s));
			}
			getTagFile().set(p.getName(), null);
		}
	}
	
	public void createTagFile(){
		if (!getFile().exists())
			try {
				getFile().createNewFile();
				save();
			} catch (Exception e) {
		}
	}
	public void save(){

		if (getFile().exists())
			try {
				y.save(getFile());
			} catch (Exception e) {
			}
	}

}
