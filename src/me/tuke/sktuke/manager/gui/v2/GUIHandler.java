package me.tuke.sktuke.manager.gui.v2;

import java.util.WeakHashMap;

/**
 * @author Tuke_Nuke on 02/03/2017
 */
public class GUIHandler {
	private static GUIHandler ourInstance = new GUIHandler();

	public static GUIHandler getInstance() {
		return ourInstance;
	}

	private GUIHandler() {
	}

	public WeakHashMap<String, GUIInventory> list = new WeakHashMap<>();

	public GUIInventory lastCreated = null;
	public GUIInventory getGUI(String id){
		return id != null && list.containsKey(id) ? list.get(id) : null;
		/*String key = p.getUniqueId().toString();
		if (list.containsKey(key)) {
			return list.get(key);
		} else {
			GUIInventory gui = new GUIInventory(p.getOpenInventory().getTopInventory()).shapeDefault();
			gui.setListener(new SkriptGUIEvent(gui));
			list.put(key, gui);
			return gui;
		}*/
	}
}
