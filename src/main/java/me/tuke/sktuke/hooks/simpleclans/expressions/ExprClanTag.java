package me.tuke.sktuke.hooks.simpleclans.expressions;

import javax.annotation.Nullable;

import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class ExprClanTag extends SimplePropertyExpression<Clan, String>{
	static {
		Registry.newProperty(ExprClanTag.class, "[clan] tag", "clan");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}
	@Override
	@Nullable
	public String convert(Clan c) {
	    if (c != null) {
	    	return c.getColorTag().replaceAll("&", "�");
	    }
	    return null;
	}

	@Override
	protected String getPropertyName() {
		return "tag";
	}
public void change(Event e, Object[] delta, Changer.ChangeMode mode){
    Object[] cc = getExpr().getArray(e);
	Clan c = SimpleClans.getInstance().getClanManager().getClan(cc[0].toString());
	String tag = ((String) delta[0]);
	if (mode == ChangeMode.SET){
		Helper.cleanTag(tag);
		if (c != null && Helper.cleanTag(tag).equals(c.getTag().toLowerCase()) || Helper.cleanTag(tag).toLowerCase().equals(c.getTag().toLowerCase())){
			c.changeClanTag(tag.replaceAll("&", "�"));
			SimpleClans.getInstance().getStorageManager().updateClan(c);
			
		}
	}
}
@SuppressWarnings("unchecked")
public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
	if (mode == ChangeMode.SET)
		return CollectionUtils.array(String.class);
	return null;
}
}

