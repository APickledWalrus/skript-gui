package com.github.tukenuke.tuske.effects;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffPushBlock extends Effect{
	static {
		Registry.newEffect(EffPushBlock.class, "move %block% to %direction%");
	}

	private Expression<Block> b;
	private BlockFace bf;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.b = (Expression<Block>) arg[0];
		String d = arg3.expr.toLowerCase();
		if (d.contains("north"))
			bf = BlockFace.NORTH;
		else if (d.contains("east"))
			bf = BlockFace.EAST;
		else if (d.contains("south"))
			bf = BlockFace.SOUTH;
		else if (d.contains("west"))
			bf = BlockFace.WEST;
		else if (d.contains("down"))
			bf = BlockFace.DOWN;
		else
			bf = BlockFace.UP;
			
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "push " + this.b + " upwards";
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(Event e) {
		if (this.b.getSingle(e) != null){
			Block b = this.b.getSingle(e);
			Block b2 = b.getRelative(bf);
			if (b.getType().isSolid() && (b2.getType().equals(Material.AIR) || !b2.getType().isSolid()) && !b2.getType().equals(Material.CHEST) && !b2.getType().equals(Material.TRAPPED_CHEST) && !b2.getType().equals(Material.FURNACE)){
				b2.setTypeIdAndData(b.getTypeId(), b.getData(), false);
				b.setType(Material.AIR);
			}
			
		}
		
	}

}
