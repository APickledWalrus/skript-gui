package com.github.tukenuke.tuske.expressions.gui;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.util.EnumUtils;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.util.EnumType;
import com.github.tukenuke.tuske.util.InventoryUtils;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprVirtualInv extends SimpleExpression<Inventory>{
	private static Parser<InventoryType> parser = EnumType.getParser(InventoryType.class, null);
	static {
		Registry.newSimple(ExprVirtualInv.class,
				"virtual <.+?> [inventory] [with size %-number%] [(named|with (name|title)) %-string%]",
				"virtual <.+?> [inventory] [with %-number% row[s]] [(named|with (name|title)) %-string%]",
				"virtual <.+?> [inventory] [(named|with (name|title)) %-string%] with size %-number%",
				"virtual <.+?> [inventory] [(named|with (name|title)) %-string%] with %-number% row[s]");
	}

	private Expression<InventoryType> it;
	private Expression<Number> size;
	private Expression<String> name;
	
	@Override
	public Class<? extends Inventory> getReturnType() {
		return Inventory.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		if (arg3.regexes != null && arg3.regexes.size() > 0) {// No value found, so let's try the workaround with regex
			String stringType = arg3.regexes.get(0).group(0);
			InventoryType type = parser.parse(stringType, ParseContext.COMMAND);
			if (type != null)
				it = new SimpleLiteral<>(type, false);
			else {
				Skript.error("There is no inventory type called '" + stringType + "'. Check TuSKe documentation to search about it.");
				return false;
			}
		}
		if (arg1 > 1) {
			name = (Expression<String>) arg[0];
			size = (Expression<Number>) arg[1];
		} else {
			size = (Expression<Number>) arg[0];
			name = (Expression<String>) arg[1];
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "virtual inventory";
	}

	@Override
	@Nullable
	protected Inventory[] get(Event e) {
		InventoryType type;
		if (it != null && (type = it.getSingle(e)) != null){
			Integer size = this.size != null ? this.size.getSingle(e).intValue() : null;
			String name = this.name != null ? this.name.getSingle(e) : null;
			return new Inventory[]{InventoryUtils.newInventory(type, size, name)};
		}
		return null;
	}
}
