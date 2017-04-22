package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.Registry;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprDropsOfBlock extends SimpleExpression<ItemStack>{
	static {
		Registry.newSimple(ExprDropsOfBlock.class, "drops of %block% [(with|using) %-itemstack%]", "%block%'[s] drops [(with|using) %-itemstack%]");
	}
	
	private Expression<Block> b;
	private Expression<ItemStack> i;

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.b = (Expression<Block>) arg[0];
		this.i = (Expression<ItemStack>) arg[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "drops of " + this.b.toString(arg0, arg1);
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event e) {
		Block b = this.b.getSingle(e);
		if (b != null){
			if (i == null){
				return b.getDrops().toArray(new ItemStack[b.getDrops().size()]);
			}
			ItemStack i = this.i.getSingle(e);
			if (i != null)
				return b.getDrops(i).toArray(new ItemStack[b.getDrops(i).size()]);
		}
		return null;
	}
	

}
