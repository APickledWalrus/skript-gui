package me.tuke.sktuke.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
@Name("Alphabetical Order")
@Description("Will return a list of {{types|Objects|objects}} organized in alphabetic order.")
@Examples({
		"loop alphabetical order of all players:",
		"\tsend \"%loop-object%\""})
@Since("1.0")
public class ExprAlphabetOrder extends SimpleExpression<Object>{
	static {
		NewRegister.newCombined(ExprAlphabetOrder.class, "alphabetical order of %objects%");
	}
	
	private Expression<Object> o;
	@Override
	public Class<? extends Object> getReturnType() {
		return Object.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.o = (Expression<Object>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return null;
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		
		if (this.o.getArray(e) != null){
			Object[] ao = (Object[])this.o.getArray(e);
			for (int x = 1; x < ao.length; x++){
				for (int y = ao.length-1; y >= x; y--){
					if (ao[y-1].toString().compareTo(ao[y].toString()) > 0){
						Object object = ao[y-1];
						ao[y-1] = ao[y];
						ao[y] = object;
						
					}
				}
			}
			return ao; 
		}
		return null;
	}

}
