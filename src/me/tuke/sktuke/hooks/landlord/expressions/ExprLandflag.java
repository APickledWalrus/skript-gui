package me.tuke.sktuke.hooks.landlord.expressions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.Landflag;
import com.jcdesimp.landlord.persistantData.LowOwnedLand;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprLandflag extends SimpleExpression<Boolean>{
	static {
		NewRegister.newSimple(ExprLandflag.class, "landflag %landflag% of %landclaim% for (1¦everyone|2¦friends)");
	}

	private Expression<LowOwnedLand> ol;
	private Expression<Landflag> lf;
	private boolean isFriend = false;
	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.ol = (Expression<LowOwnedLand>) arg[1];
		this.lf = (Expression<Landflag>) arg[0];
		if (arg3.mark == 2)
			this.isFriend = true;
		
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "landflag " + this.lf + " of " +this.ol;
	}

	@Override
	@Nullable
	protected Boolean[] get(Event e) {
		LowOwnedLand ol = this.ol.getSingle(e);
		Landflag lf = this.lf.getSingle(e);
		if (ol != null && lf != null){ 
			if (isFriend)
				return new Boolean[] {LowOwnedLand.stringToBool(ol.getLandPerms(false)[1][lf.getPermSlot()])};
			else
				return new Boolean[] {ol.canEveryone(lf)};
		}
		return new Boolean[] {false};
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		LowOwnedLand ol = this.ol.getSingle(e);
		Landflag lf = this.lf.getSingle(e);
		if (ol != null && lf != null){
			String bo = "1";
			if ((Boolean) delta[0] == false)
				bo = "0";
			int isf = 0;
			if (isFriend)
				isf++;
			String[][] perms = ol.getLandPerms(false);
			perms[isf][lf.getPermSlot()] = bo;
			ol.setPermissions(ol.permsToString(perms));
			Landlord.getInstance().getDatabase().save(ol);
		}
		
		
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Boolean.class);
		return null;
	}

}
