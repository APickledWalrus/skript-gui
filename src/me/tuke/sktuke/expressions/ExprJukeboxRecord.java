package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprJukeboxRecord extends SimplePropertyExpression<Block, ItemStack>{
	static {
		NewRegister.newProperty(ExprJukeboxRecord.class, "[jukebox] record", "block");
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	@Nullable
	public ItemStack convert(Block b) {
		return b != null ? new ItemStack(((Jukebox)b.getState()).getPlaying()) : null;
	}

	@Override
	protected String getPropertyName() {
		return "record item";
	}
	@Override
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		Block b = getExpr().getSingle(e);
		if (b != null && delta[0] != null){
			Jukebox jb = (Jukebox) b.getState();
			jb.setPlaying(((ItemStack)delta[0]).getType());
		}
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(ItemStack.class);
		return null;
		
	}

}
