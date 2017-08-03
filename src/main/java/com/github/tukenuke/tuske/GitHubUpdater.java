package com.github.tukenuke.tuske;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;

/**
	 * This is a Auto Updater that use GitHub to get the informations.<br>
	 * &nbsp With that you can check the latest version and download from
	 * it. It won't do anything automatic, you have to use your own methods
	 * to know when check/download the latest version.<br>
	 * &nbsp I recommend to execute a delayed task to check for update if
	 * you use it in #onEnable(), as it will make the startup takes a
	 * little bit longer. And for update, after you checked if it 
	 * {@link #isLatestVersion()} and used the {@link #downloadLatest()},
	 * you can check if it {@link #hasDownloadReady(boolean)} and then
	 * use the {@link #updatePlugin()} to change the current jar file
	 * to the latest one.
	 * <p>
	 * If you want to use it in your plugin or have a suggestion for this, please contact me.
	 * @author Tuke_Nuke - github.com/Tuke-Nuke
	 */

public class GitHubUpdater {
	
	// The instance of your plugin
	private final JavaPlugin PLUGIN;
	// This the jar file of your plugin, it is required to get the right name of the file that can be modified.
	private final File PLUGIN_JAR;
	// The url path of your repository.
	private final String URL;
	// Download beta releases
	private boolean DOWNLOAD_BETA;
	// These values will be gotten when you use the method {@link: #checkForUpdate()}
	private String UPDATE_TITLE = null;
	private String UPDATE_VERSION = null;
	private String UPDATE_DOWNLOAD_URL = null;
	private String UPDATE_URL = null;

	/**
	 * The constructor of the GitHubUpdater
	 *
	 * @param instance - The instance of your plugin.
	 * @param pluginFile - The jar file of your plugin. You can get it with {@link org.bukkit.plugin.java.JavaPlugin#getFile()}
	 * @param gitHubUrl - The download link. It should be like '&ltGitHub user&gt/&ltrepo&gt
	 * @param acceptBetaReleases - True to allow to check/download pre releases.
	 */
	public GitHubUpdater(JavaPlugin instance, File pluginFile, String gitHubUrl, boolean acceptBetaReleases){
		if (instance == null)
			throw new IllegalStateException("The instance of your plugin can not be null.");
		if (pluginFile == null)
			throw new IllegalStateException("The jar file of your plugin can not be null. Get it with JavaPlugin#getFile()");
		if (gitHubUrl == null)
			throw new IllegalStateException("The repository can not be null. It should be like: <User>/<Repository>");
		PLUGIN = instance;
		PLUGIN_JAR = pluginFile;
		URL = gitHubUrl;
		DOWNLOAD_BETA = acceptBetaReleases;
	}
	public void acceptBetaReleases(boolean value) {
		DOWNLOAD_BETA = value;
	}
	/**
	 * Use it to check for latest update. It will get the latest version and download link.<p>
	 */
	public void checkForUpdate(final boolean showMessageError) throws IllegalArgumentException{
		Thread updater = new Thread(() -> {
			HttpURLConnection github = null;
			try {
				github = (HttpURLConnection) new URL("https://api.github.com/repos/" + URL + "/releases").openConnection();
				github.setRequestProperty("User-Agent", PLUGIN.getName());
				github.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				github.setRequestProperty("Connection", "keep-alive");
				github.setRequestProperty("Accept", "application/vnd.github.v3+json");
				BufferedReader br = new BufferedReader(new InputStreamReader(github.getInputStream()));
				JsonArray releases = (JsonArray) new JsonParser().parse(br); //Spigot does have Google Json's package, but in case it doesn't, them it won't check.
				br.close();
				UPDATE_DOWNLOAD_URL = null;
				UPDATE_TITLE = null;
				UPDATE_URL = null;
				UPDATE_VERSION = null;
				for (JsonElement release : releases) {
					if (release instanceof JsonObject) {
						if (UPDATE_VERSION == null && UPDATE_TITLE == null) { //It will get the first release
							if ((((JsonObject) release).get("prerelease").getAsBoolean()) && !DOWNLOAD_BETA)
								continue; //Found a beta release but the option is false
							UPDATE_VERSION = ((JsonObject) release).get("tag_name").getAsString();
							if (UPDATE_VERSION.startsWith("v"))
								UPDATE_VERSION = UPDATE_VERSION.substring(1);
							UPDATE_TITLE = ((JsonObject) release).get("name").getAsString();
							UPDATE_URL = ((JsonObject) release).get("html_url").getAsString();
							JsonArray assets = ((JsonObject) release).get("assets").getAsJsonArray();
							for (JsonElement asset : assets)
								if (asset instanceof JsonObject)
									UPDATE_DOWNLOAD_URL = ((JsonObject) asset).get("browser_download_url").getAsString();
							break;
						}
					}
				}
				return;

			} catch (MalformedURLException e) {
				if (showMessageError)
					PLUGIN.getLogger().severe("The updater couldn't get the link of the latest version.\n" + e.getMessage());
			} catch (Exception e) {
				if (showMessageError)
					PLUGIN.getLogger().warning("An unexpected error occurred when trying to check for latest update.");
			} finally {
				if (github != null)
					github.disconnect();
			}
			UPDATE_VERSION = null;
			UPDATE_TITLE = null;
		}, PLUGIN.getName() + "'s Updater");
		updater.run();
	}
	/**
	 * It will replace the current jar file of your plugin for a downloaded one.
	 * <p>
	 * <b>Note: It might crash your plugin, consider to use it in {@link JavaPlugin#onDisable()}</b>
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
		return UPDATE_URL;
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
		return UPDATE_VERSION;
	}
	/**
	 * Get the download URL of the latest version.
	 * @return The download URL
	 */
	public String getDownloadURL(){
		return UPDATE_DOWNLOAD_URL;
	}
	/**
	 * Check if the version of your plugin is equal to the latest version
	 * @return True if it is the latest version
	 */
	public boolean isLatestVersion(){
		String v1 = PLUGIN.getDescription().getVersion();
		String v2 = UPDATE_VERSION;
		if (v1.startsWith("v"))
			v1 = v1.substring(1);
		if (v2.startsWith("v"))
			v2 = v2.substring(1);
		return v1.equals(v2);
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
	 */
	public void downloadLatest(){
		Thread downloadThread = new Thread(() -> {
			HttpURLConnection download = null;
			try {
				download = (HttpURLConnection) new URL(UPDATE_DOWNLOAD_URL).openConnection();
				download.setRequestProperty("User-Agent", "Mozilla/5.0");
				File f = new File(PLUGIN.getDataFolder(), PLUGIN.getName() + ".jar");
				if (f.exists())
					f.delete();
				FileUtils.copyInputStreamToFile(download.getInputStream(), f);
			} catch (Exception e) {
			} finally {
				if (download != null)
					download.disconnect();
			}
		});
		downloadThread.start();

	}
}
