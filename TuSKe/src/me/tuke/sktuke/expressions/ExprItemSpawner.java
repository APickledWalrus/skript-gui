package me.tuke.sktuke.expressions;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprItemSpawner extends SimpleExpression<ItemStack>{

	private Expression<ItemStack> item;
	private Expression<?> type;
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
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		item = (Expression<ItemStack>) arg[0];
		type = arg[1].getConvertedExpression(Object.class);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return item + " with spawner mob " + type;
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event e) {
		ItemStack item = this.item.getSingle(e).clone();
		String entityName = (String) type.getSingle(e);
		if (item != null && entityName != null && item.getType().equals(Material.MOB_SPAWNER)){
			
			BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
			CreatureSpawner spawner = (CreatureSpawner) meta.getBlockState();
			EntityType entityType;
			try {
				entityType = EntityType.valueOf(entityName.toUpperCase().replaceAll(" ", "_"));
			} catch (Exception exception){
				return null;
			}
			spawner.setSpawnedType(entityType);
			meta.setBlockState(spawner);
			item.setItemMeta(meta);
			return new ItemStack[]{item};
		}
		return null;
	}

}
