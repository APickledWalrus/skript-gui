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
	 * This is a Auto Updater that use SkUnity Forums to get the informations.<br>
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
	 * @author Tuke_Nuke - {@link github.com/Tuke-Nuke}
	 */

public class SkUnityUpdater {
	
	// The instance of your plugin
	private JavaPlugin PLUGIN;
	// The thread's ID of your plugin. You can get it on URL of your thread. i.e. https://forums.skunity.com/t/<The title of your thread>/7397 < ID
	private int ID;
	private String CHANGE_LOG;
	// This the jar file of your plugin, it is required to get the right name of the file that can be modified.
	private File PLUGIN_JAR;
	/*
	 * This is a regex to get the download link, by default it will search the first link that contains "[Your plugin name].jar", but you can set it
	 * using the second constructor if you using something different as "Download it here" or "Click here to download".
	 * Note: only use the text that is a hyperlink, else it won't work.
	 */
	private String DOWNLOAD_REGEX;
	//These values will be gotten when you use the method {@link: #checkForUpdate()}
	private String URL = null;
	private String THREAD_TITLE = null;
	private String LATEST_VERSION = null;
	//A pattern to get the latest version based on thread's title. i.e. TuSKe [v1.0], TuSKe 1.0.
	private Pattern PATTERN_1 = Pattern.compile(".*(v(\\w+\\.?)+)(.*)");
	private Pattern PATTERN_2;

	/**
	 * The constructor of the SkUnityUpdater
	 *
	 * @param JavaPlugin - The instance of your plugin.
	 * @param File - The jar file of your plugin. You can get it with {@link org.bukkit.plugin.java.JavaPlugin#getFile()}
	 * @param int - The ID of the thread of your plugin in SkUnity Forums.
	 */
	
	public SkUnityUpdater(JavaPlugin instance, File pluginFile, int threadID){
		this(instance, pluginFile, threadID, instance.getName() + ".jar");
		
	}
	/**
	 * The constructor of the SkUnityUpdater
	 *
	 * @param - JavaPlugin - The instance of your plugin.
	 * @param - File - The jar file of your plugin. You can get it with {@link org.bukkit.plugin.java.JavaPlugin#getFile()}
	 * @param - int - The ID of the thread of your plugin in SkUnity Forums.
	 * @param - String - The hyperlink in your thread to download the last update. It can be something as "[Plugin's name].jar" or "Download here"
	 */
	public SkUnityUpdater(JavaPlugin instance, File pluginFile, int threadID, String downloadRegex){
		PLUGIN_JAR = pluginFile;
		this.PLUGIN = instance;
		ID = threadID;
		PATTERN_2 = Pattern.compile(".*"+ instance.getName() + "\\s+((\\w+\\.?)+)(.*)");	
		this.DOWNLOAD_REGEX = downloadRegex;
	}
	/**
	 * Use it to check for latest update. It will get the latest version and download link.<p>
	 * <b>Note: It will throw {@link java.lang.IllegalArgumentException} if the download link is from
	 * {@link spigotmc.org}
	 */
	public boolean checkForUpdate(boolean showMessageError) throws IllegalArgumentException{
		try {
			HttpURLConnection forum = (HttpURLConnection) new URL("https://forums.skunity.com/t/" + ID + "/1").openConnection();
			forum.setRequestProperty("User-Agent", "Mozilla/5.0");
			String page = IOUtils.toString(forum.getInputStream(), "UTF-8");
			if (page.contains("<title>")){
				THREAD_TITLE = page.split("<title>")[1].split(" - Addons - skUnity Forums")[0];
				Matcher m = PATTERN_1.matcher(THREAD_TITLE);
				if (!m.find(1))
					m = PATTERN_2.matcher(THREAD_TITLE);
				if (m.find(1)){
					LATEST_VERSION = m.group(1);
					if (LATEST_VERSION.toLowerCase().startsWith("v"))
						LATEST_VERSION = LATEST_VERSION.substring(1);
				}
			}
			if (page.contains("Changelog:"))
				CHANGE_LOG = page.split("<strong>Changelog:</strong> <a href=\"")[1].split("\">#")[0];
			if (page.contains("\">" + DOWNLOAD_REGEX) && page.contains("Download:</strong> <a class=\"attachment\" href=\"")){
				URL = page.split("Download:</strong> <a class=\"attachment\" href=\"")[1].split("\">" + DOWNLOAD_REGEX)[0];
				//String i = page.split("\">" + DOWNLOAD_REGEX)[0];
				//URL = i.split("href=\"")[i.split("href=\"").length - 1];
				//URL = page.split("href=\"")[1].split("\"")[0];
				if(URL.startsWith("/uploads/default/original/"))
					URL = "https://forums.skunity.com" + URL;
				if(!(URL.startsWith("http") || URL.startsWith("www")))
					URL = "https://" + URL;
			}
			//BufferedReader reader = new BufferedReader(new InputStreamReader(forum.getInputStream()));
			//String result = null;
			//FileUtils.readFileToString(forum.getInputStream(), "UTF-8");
			//PLUGIN.getLogger().info(IOUtils.toString(forum.getInputStream()));
			/*while ((result = reader.readLine()) != null){
				if (result.contains("<title>") && (result.matches(PATTERN_1.pattern()) || result.matches(PATTERN_2.pattern()))){
					Matcher m = PATTERN_1.matcher(result);
					if (!m.find(1))
						m = PATTERN_2.matcher(result);
					if (m.find(1)){
						LATEST_VERSION = m.group(1);
						if (LATEST_VERSION.toLowerCase().startsWith("v"))
							LATEST_VERSION = LATEST_VERSION.substring(1);
						THREAD_TITLE = result.split("<title>")[1].split(" - Addons - skUnity Forums")[0];
					}
					if (URL != null){
						reader.close();
						return;
					}
				}
				if (URL == null && result.contains(DOWNLOAD_REGEX) && result.contains("<a class=\"attachment\"")){
					URL = result.split("href=\"")[1].split("\"")[0];
					if(URL.startsWith("/uploads/default/original/"))
						URL = "https://forums.skunity.com" + URL;
					if(!(URL.startsWith("http") || URL.startsWith("www")))
						URL = "https://" + URL;
					if (LATEST_VERSION != null){
						reader.close();
						return;
					}
					if(URL.contains("spigotmc.org/resources")){
						reader.close();
						throw new IllegalArgumentException("The download link can't be from Spigot");
					}
				}
			}*/
			//reader.close();
			return true;
		} catch (MalformedURLException e) {
			if (showMessageError)
				PLUGIN.getLogger().severe("The updater couldn't get the link of the latest version.\n" + e.getMessage());
		} catch (IOException e) {
			if (showMessageError)
				PLUGIN.getLogger().warning("An unexpected error occurred when trying to check for latest update. Maybe SkUnity is down?");
		}
		return false;
	}
	/**
	 * Use it only if you want to download from a external site. i.e. YourPluginSite.com/download/latest
	 * @param URL - The site
	 */
	public void useDirectURL(String URL){
		this.URL = URL;
	}
	/**
	 * It will make a backup of current version and save in /plugins/[Plugin's folder]/[Plugin's name]-backup.jar
	 */
	public void backupCurrent(){
		File jarTo = new File(PLUGIN.getDataFolder(), PLUGIN.getName() + "-backup.jar");
		File jarOld = new File("plugins" + File.separator, PLUGIN_JAR.getName());
		try {
			if (jarTo.exists())
				jarTo.delete();
			jarOld.renameTo(new File(jarOld.getPath().replace(".jar", "-backup.jar")));
			jarOld = new File("plugins" + File.separator, PLUGIN_JAR.getName().replaceAll(".jar", "-backup.jar"));
			FileUtils.copyFileToDirectory(jarOld, new File(PLUGIN.getDataFolder() + File.separator));
		} catch (IOException e) {
			PLUGIN.getLogger().warning("An unexpected error occurred when trying to make a backup of current version.\n" + e.getMessage());
		}
		
	}
	/**
	 * It will replace the current jar file of your plugin for a downloaded one.
	 * @param backupCurrentVersion - Should it {@link #backupCurrent()} version?
	 * <p>
	 * <b>Note: It might crash your plugin, consider to use it in {@link #onDisable()}</b>
	 */
	public void updatePlugin(boolean backupCurrentVersion){
		if (hasDownloadReady(true)){
			File jarNew = new File(PLUGIN.getDataFolder(), PLUGIN.getName() + ".jar");
			File jarOld = new File("plugins" + File.separator, PLUGIN_JAR.getName());
			try {
				if (backupCurrentVersion)
					backupCurrent();
				jarOld.delete();
				FileUtils.copyFileToDirectory(jarNew, new File("plugins" + File.separator));
				jarNew.delete();

			} catch (IOException e) {
				PLUGIN.getLogger().severe("An unexpected error occurred when trying to update the plugin.\n" + e.getMessage());
			}
		}
	}
	/**
	 * Get the latest version from SkUnity.
	 * @return The latest version
	 */
	public String getLatestVersion(){
		return LATEST_VERSION;
	}
	/**
	 * Get the title of the thread of your plugin. i.e. [Addon] [YourPlugin] v1.0 - Description of your plugin
	 * @return The title of the thread
	 */
	public String getThreadTitle(){
		return THREAD_TITLE;
	}
	/**
	 * Get the URL of the thread of your plugin.
	 * @return https://forums.skunity.com/t/ + threadID
	 */
	
	public String getThreadURL(){
		return "https://forums.skunity.com/t/" + ID;
	}
	public String getChangeLogURL(){
		return CHANGE_LOG;
	}
	/**
	 * Get the download URL of the latest version.
	 * @return The download URL
	 */
	public String getDownloadURL(){
		return URL;
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
			HttpURLConnection download = (HttpURLConnection) new URL(URL).openConnection();
			download.setRequestProperty("User-Agent", "Mozilla/5.0");
			File f = new File(PLUGIN.getDataFolder(), PLUGIN.getName() + ".jar");
			if (f.exists())
				f.delete();
			FileUtils.copyInputStreamToFile(download.getInputStream(), f);
			return true;
		} catch (Exception e) {
			//PLUGIN.getLogger().info("An unexpected error occurred when trying to download the latest version.\n" + e.getMessage());
		}
		return false;
	}
}
