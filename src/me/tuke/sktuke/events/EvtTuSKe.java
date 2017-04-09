package me.tuke.sktuke.events;

import me.tuke.sktuke.manager.gui.GUIActionEvent;
import me.tuke.sktuke.util.NewRegister;
import me.tuke.sktuke.util.ReflectionUtils;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class EvtTuSKe extends SkriptEvent{
	static {
		NewRegister.newEvent(EvtTuSKe.class, InventoryDragEvent.class, "Inventory drag", "inventory drag");
		NewRegister.newEvent(EvtTuSKe.class, GUIActionEvent.class, "GUI click", "gui (action|click)");
		if (ReflectionUtils.hasClass("org.bukkit.event.entity.SpawnerSpawnEvent"))
			NewRegister.newEvent(EvtTuSKe.class, SpawnerSpawnEvent.class, "Spawner spawn", "[mob] spawner spawn");
		if (ReflectionUtils.hasClass("org.bukkit.event.player.PlayerItemDamageEvent"))
			NewRegister.newEvent(EvtTuSKe.class, PlayerItemDamageEvent.class, "Item damage", "[player] item damage");
		if (ReflectionUtils.hasClass("org.bukkit.event.inventory.PrepareItemCraftEvent"))
			NewRegister.newEvent(EvtTuSKe.class, PrepareItemCraftEvent.class, "Item craft", "[tuske] prepare item craft");

	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "tuske event " + (arg0 != null ? arg0.getEventName() : "") ;
	}

	@Override
	public boolean check(Event arg0) {
		return true;
	}

	@Override
	public boolean init(Literal<?>[] arg0, int arg1, ParseResult arg2) {
		return true;
	}

}
