package me.tuke.sktuke.expressions;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nullable;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.events.customevent.AnvilCombineEvent;
import me.tuke.sktuke.events.customevent.AnvilRenameEvent;

public class ExprAnvilItem extends SimpleExpression<ItemStack>{

	private int i;
	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		
		if (arg3.expr.toLowerCase().contains("item-one")){
			if (!ScriptLoader.isCurrentEvent(AnvilCombineEvent.class)){
				Skript.error("'" + arg3.expr + "' can't be used out of event 'On Anvil combine'.", ErrorQuality.SEMANTIC_ERROR);
				return false;
			}
			this.i = 0;
		}
		else if (arg3.expr.toLowerCase().contains("item-two")){
			if (!ScriptLoader.isCurrentEvent(AnvilCombineEvent.class)){
				Skript.error("'" + arg3.expr + "' can't be used out of event 'On Anvil combine'.", ErrorQuality.SEMANTIC_ERROR);
				return false;
			}
			this.i = 1;
		}
		else if (!ScriptLoader.isCurrentEvent(AnvilCombineEvent.class, AnvilRenameEvent.class)){
				Skript.error("'" + arg3.expr + "' can only be used in 'On Anvil combine' or 'On Anvil rename'.", ErrorQuality.SEMANTIC_ERROR);
				return false;	
		} else
			this.i = 2;
			
			
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "event-item-one";
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event e) {
		if (e instanceof AnvilCombineEvent)
			return new ItemStack[] { ((AnvilCombineEvent)e).getInventory().getItem(this.i)};
		else if (e instanceof AnvilRenameEvent)
			return new ItemStack[] { ((AnvilRenameEvent)e).getInventory().getItem(this.i)};
		return null;
	}

}
