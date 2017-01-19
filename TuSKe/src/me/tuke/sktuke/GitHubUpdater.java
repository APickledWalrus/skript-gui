package me.tuke.sktuke;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.plugin.java.JavaPlugin;
	/** 
	 * This is a Auto Updater that use GitHub to get the informations.<br>
	 * &nbsp With that you can check the latest version and download from
	 * it. It won't do anything automatic, you have to use your own methods
	 * to know when check/download the latest version.<br>
	 * &nbsp I recommend to execute a delayed task to check for update if
	 * you use it in #onEnable(), as it will make the startup takes a
	 * little bit longer. And for update, after you checked if it 
	 * {@link #isLatest()} and used the {@link #downloadLatest()}, 
	 * you can check if it {@link #hasDownloadReady(boolean)} and then
	 * use the {@link #updatePlugin()} to change the current jar file
	 * to the latest one.
	 * <p>
	 * If you want to use it in your plugin or have a suggestion for this, please contact me.
	 * @author Tuke_Nuke - {@link http://github.com/Tuke-Nuke}
	 */

public class GitHubUpdater {
	
	// The instance of your plugin
	private JavaPlugin PLUGIN;
	// This the jar file of your plugin, it is required to get the right name of the file that can be modified.
	private File PLUGIN_JAR;
	//These values will be gotten when you use the method {@link: #checkForUpdate()}
	private String URL = null;
	private String UPDATE_TITLE = null;
	private String LATEST_VERSION = null;
	private Exception lastException = null;

	/**
	 * The constructor of the GitHubUpdater
	 *
	 * @param JavaPlugin - The instance of your plugin.
	 * @param File - The jar file of your plugin. You can get it with {@link org.bukkit.plugin.java.JavaPlugin#getFile()}
	 * @param String - The download link
	 */
	
	public GitHubUpdater(JavaPlugin instance, File pluginFile, String gitHubUrl){
		PLUGIN = instance;
		PLUGIN_JAR = pluginFile;
		URL = gitHubUrl;
		
		
	}
	/**
	 * Use it to check for latest update. It will get the latest version and download link.<p>
	 */
	public void checkForUpdate(final boolean showMessageError) throws IllegalArgumentException{
		Thread updater = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					HttpURLConnection github = (HttpURLConnection) new URL(URL).openConnection();
					github.setRequestProperty("User-Agent", "Mozilla/5.0");
					String page = IOUtils.toString(github.getInputStream(), "UTF-8");
					Pattern p = Pattern.compile("<a href=\"/Tuke-Nuke/TuSKe/releases/tag/(.+)\">(.+)</a>");
					Matcher m = p.matcher(page);
					if (m.find()){
						LATEST_VERSION = m.group(1);
						UPDATE_TITLE = m.group(2);
					}
					return;
					
				} catch (MalformedURLException e) {
					lastException = e;
					if (showMessageError)
						PLUGIN.getLogger().severe("The updater couldn't get the link of the latest version.\n" + e.getMessage());
				} catch (IOException e) {
					lastException = e;
					if (showMessageError)
						PLUGIN.getLogger().warning("An unexpected error occurred when trying to check for latest update. Maybe GitHub is down?");
				}	
				LATEST_VERSION = null;
				UPDATE_TITLE = null;
			}}, "TuSKe's Updater");
		updater.run();
	}
	/**
	 * It will replace the current jar file of your plugin for a downloaded one.
	 * @param backupCurrentVersion - Should it {@link #backupCurrent()} version?
	 * <p>
	 * <b>Note: It might crash your plugin, consider to use it in {@link #onDisable()}</b>
	 */
	public void updatePlugin(){
		if (hasDownloadReady(true)){
			File jarNew = new File(PLUGIN.getDataFolder(), PLUGIN.getName() + ".jar");
			File jarOld = new File("plugins" + File.separator, PLUGIN_JAR.getName());
			try {
				jarOld.delete();
				FileUtils.copyFileToDirectory(jarNew, new File("plugins" + File.separator));
				jarNew.delete();

			} catch (IOException e) {
				lastException = e;
				PLUGIN.getLogger().severe("An unexpected error occurred when trying to update the plugin.\n" + e.getMessage());
			}
		}
	}
	/**
	 * Get the url where to check for update. In this case, if the option 'updater.download_pre_releases' from config is false,
	 * it will concat with "/latest" to only get stable versions
	 * @return The URL
	 */
	public String getURL(){
		return URL + (PLUGIN.getConfig().getBoolean("updater.download_pre_releases") ? "/latest" : "");
	}
	/**
	 * It returns the title of the update, it is from GitHub and it can be something like
	 * "New things and fixed other things", "Added this and that", for example
	 * @return The title of update
	 */
	
	public String getUpdateTitle(){
		return UPDATE_TITLE;
	}
	/**
	 * Get the latest version.
	 * @return The latest version
	 */
	public String getLatestVersion(){
		return LATEST_VERSION;
	}
	/**
	 * Get the download URL of the latest version.
	 * @return The download URL
	 */
	public String getDownloadURL(){
		return URL + "/tag/" + LATEST_VERSION;
	}
	/**
	 * Check if the version of your plugin is equal to the latest version
	 * @return True if it is the latest version
	 */
	public boolean isLatestVersion(){
		return PLUGIN.getDescription().getVersion().equals(LATEST_VERSION);
	}
	/**
	 * It will check if it has {@link #downloadLatest()} in plugin' folder.
	 * @param ignoreVersion - True if you want to ignore if the plugin is already updated
	 * @return True if there is a jar file in plugin's folder
	 */
	public boolean hasDownloadReady(boolean ignoreVersion){
		return new File(PLUGIN.getDataFolder(), PLUGIN.getName() + ".jar").exists() && (ignoreVersion || !isLatestVersion());
	}
	/**
	 * It will download the latest version of plugin and save in plugin's folder.
	 * @return True if the download was successful
	 */
	public boolean downloadLatest(){
		try {
			HttpURLConnection download = (HttpURLConnection) new URL(URL + "/download/" + LATEST_VERSION + "/" + PLUGIN.getName() + ".jar").openConnection();
			download.setRequestProperty("User-Agent", "Mozilla/5.0");
			File f = new File(PLUGIN.getDataFolder(), PLUGIN.getName() + ".jar");
			if (f.exists())
				f.delete();
			FileUtils.copyInputStreamToFile(download.getInputStream(), f);
			return true;
		} catch (Exception e) {
			lastException = e;
		}
		return false;
	}
	public Exception getLastException(){
		return lastException;
	}
}
