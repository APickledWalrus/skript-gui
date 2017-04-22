package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.Registry;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprDraggedItem extends SimpleExpression<ItemStack>{
	static {
		Registry.newSimple(ExprDraggedItem.class, "[event-][old(-| )]dragged(-| )item");
	}

	private boolean isOld;
	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		if (!ScriptLoader.isCurrentEvent(InventoryDragEvent.class)){
			Skript.error("The expression '" + arg3.expr + "' can only be used in Inventory Drag event.");
			return false;
		}
		isOld = arg3.expr.toLowerCase().contains("old");
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return null;
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event e) {
		if (isOld)
			return new ItemStack[]{((InventoryDragEvent)e).getOldCursor()};
		return new ItemStack[]{((InventoryDragEvent)e).getCursor()};
	}
	

	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		if (!isOld){
			ItemStack i = new ItemStack(Material.AIR);
			InventoryDragEvent ev = (InventoryDragEvent)e;
			if (delta == null && mode != ChangeMode.DELETE && mode != ChangeMode.RESET)
				return;
			else if (mode != ChangeMode.DELETE && mode != ChangeMode.RESET)
				i = (ItemStack)delta[0];
			switch (mode){
				case ADD: 
					if (i.isSimilar(ev.getCursor()))
						i.setAmount(ev.getCursor().getAmount() + i.getAmount());
					else
						return;
					break;
				case REMOVE:
					if (i.isSimilar(ev.getCursor()))
						i.setAmount(ev.getCursor().getAmount() - i.getAmount());
					else
						return;
					break;
				default:
					break;
			}
			ev.setCursor(i);
		}
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (isOld){
			Skript.error("Only the item after the event can is settable.");
			return null;
		}
		if (mode != ChangeMode.REMOVE_ALL)
			return CollectionUtils.array(ItemStack.class);
		return null;
		
	}

}
