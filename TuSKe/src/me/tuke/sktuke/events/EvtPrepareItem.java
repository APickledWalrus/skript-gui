package me.tuke.sktuke.events;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

public class EvtPrepareItem extends SkriptEvent{

	private Literal<ItemStack> result;
	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "item prepare craft";
	}

	@Override
	public boolean check(Event e) {
		PrepareItemCraftEvent craft = (PrepareItemCraftEvent)e;
		if (craft.getInventory().getResult().getType().equals(Material.AIR))
			return false;
		if (result != null && !result.getSingle(e).equals(craft.getRecipe().getResult()))
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?>[] arg, int arg1, ParseResult arg2) {
		result = (Literal<ItemStack>) arg[0];
		return false;
	}

}
