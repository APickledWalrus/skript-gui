package io.github.apickledwalrus.skriptgui.gui.events;

import ch.njol.skript.SkriptEventHandler;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Listener;

public class RecipeEvent implements Listener {

	public RecipeEvent() {
		// We still want this event to be processed by Skript
		SkriptEventHandler.listenCancelled.add(PlayerRecipeBookClickEvent.class);
	}

	public void onRecipeBookClick(PlayerRecipeBookClickEvent event) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(event.getPlayer().getOpenInventory().getTopInventory());
		if (gui != null) {
			event.setCancelled(true);
		}
	}

}
