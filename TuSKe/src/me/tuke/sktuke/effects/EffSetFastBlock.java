package me.tuke.sktuke.effects;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.nms.NMS;

public class EffSetFastBlock extends Effect{

	private Expression<ItemStack> item;
	private Expression<Block> loc;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		loc = (Expression<Block>) arg[0];
		item = (Expression<ItemStack>) arg[1];
		
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "";
	}

	@Override
	protected void execute(final Event e) {
		final ItemStack i = item.getSingle(e);
		final NMS nms = TuSKe.getNMS();
		Bukkit.getScheduler().runTaskLater(TuSKe.getInstance(), new Runnable(){

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Set<Chunk> chunksToUpdate = new HashSet<>();
				for (Block l : loc.getArray(e)){
					nms.setFastBlock(l.getWorld(), l.getX(), l.getY(), l.getZ(), i.getTypeId(), i.getData().getData());
					if (!chunksToUpdate.contains(l.getChunk()))
						chunksToUpdate.add(l.getChunk());
				}
				for (Chunk c : chunksToUpdate)
					nms.updateChunk(c);				
			}}, 0L);
		
		
		
	}
}
