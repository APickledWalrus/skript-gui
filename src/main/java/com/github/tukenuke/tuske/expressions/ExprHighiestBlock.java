package com.github.tukenuke.tuske.expressions;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprHighiestBlock extends SimpleExpression<Block>{
	static {
		Registry.newSimple(ExprHighiestBlock.class, "highest block at %location%");
	}

	private Expression<Location> l;

	@Override
	public Class<? extends Block> getReturnType() {
		return Block.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.l = (Expression<Location>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "highiest block at " + this.l;
	}

	@Override
	@Nullable
	protected Block[] get(Event e) {
		Location l = this.l.getSingle(e);
		if (l != null)
			return new Block[] {l.getWorld().getHighestBlockAt(l).getRelative(BlockFace.DOWN)};
		return null;
	}

}
