package io.github.apickledwalrus.skriptgui.gui.events;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RecipeEvent implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onRecipeBookClick(PlayerRecipeBookClickEvent event) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(event.getPlayer().getOpenInventory().getTopInventory());
		if (gui != null) {
			event.setCancelled(true);
		}
	}

}
