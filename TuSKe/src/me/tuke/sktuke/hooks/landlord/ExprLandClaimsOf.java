package me.tuke.sktuke.hooks.landlord;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.LowOwnedLand;
import com.jcdesimp.landlord.persistantData.OwnedLand;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprLandClaimsOf extends SimpleExpression<LowOwnedLand>{
	
	private Expression<Player> p;

	@Override
	public Class<? extends LowOwnedLand> getReturnType() {
		return LowOwnedLand.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.p = (Expression<Player>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "land claims of " + this.p;
	}

	@Override
	@Nullable
	protected LowOwnedLand[] get(Event e) {
		Player p = this.p.getSingle(e);
		List<LowOwnedLand> lands = new ArrayList<LowOwnedLand>();
		for (OwnedLand ol : Landlord.getInstance().getDatabase().find(OwnedLand.class).where().eq("ownerName", p.getUniqueId().toString()).findList())
			lands.add(ol.getLowLand());
		return lands.toArray(new LowOwnedLand[lands.size()]);
	}

}
