package me.tuke.sktuke.conditions;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondCanSpawn extends Condition{

	private Expression<?> loc;
	private Expression<EntityType> ent;
	private int isMonsters;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		if (arg1 == 1 || arg1 == 3){
			if (ScriptLoader.isCurrentEvent(CreatureSpawnEvent.class, ItemSpawnEvent.class, SpawnerSpawnEvent.class)){
				Skript.error("You can't use this condition inside a entity spawn event");
				return false;
			}
			ent = (Expression<EntityType>) arg[0];
		}
		loc = arg[(arg1 > 1 ? arg1 - 2 : arg1)];
		isMonsters = arg3.mark;
		setNegated(arg1 > 1);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "can spawn at " + loc;
	}

	@Override
	public boolean check(Event e) {
		if (loc.getSingle(e) == null || (isMonsters == 0 && ent.getSingle(e) == null ))
			return false;
		Location l;
		if (loc.getSingle(e) instanceof String)
			l = Bukkit.getWorld((String)loc.getSingle(e)).getSpawnLocation();
		else if (loc.getSingle(e) instanceof World)
			l = ((World)loc.getSingle(e)).getSpawnLocation();
		else
			l = (Location)loc.getSingle(e);
		boolean r = false;
		if (isMonsters > 0)
			r = (isMonsters == 1) ? l.getWorld().getAllowMonsters() : l.getWorld().getAllowAnimals();
		else {
			Entity en = ent.getSingle(e).data.spawn(l);
			r = en.isValid();
			if (en.isValid())
				en.remove();
		}
		if (isNegated())
			return !r;
		return r;
	}

}
