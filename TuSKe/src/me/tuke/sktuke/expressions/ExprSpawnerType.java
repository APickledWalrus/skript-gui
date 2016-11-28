package me.tuke.sktuke.expressions;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

public class ExprSpawnerType extends SimplePropertyExpression<Block, EntityType>{

	@Override
	public Class<? extends EntityType> getReturnType() {
		return EntityType.class;
	}

	@Override
	@Nullable
	public EntityType convert(Block b) {
		return (b != null && b.getState() instanceof CreatureSpawner) ? EntityType.parse(((CreatureSpawner)b.getState()).getSpawnedType().toString().replace("_", " ")): null;
	}

	@Override
	protected String getPropertyName() {
		return "spawner type";
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		Bukkit.broadcastMessage("Oi");
		Block b = getExpr().getSingle(e);
		Bukkit.broadcastMessage("Boolean: " + (b != null) + " && " + (b.getState() instanceof CreatureSpawner) + " && " + (delta != null));
		if (b != null && b.getState() instanceof CreatureSpawner && delta != null){
			//CreatureSpawner cs = (CreatureSpawner)b.getState();
			//EntityType et = (EntityType)delta[0];
		}
	}

	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(EntityType.class);
		return null;
		
	}

}
