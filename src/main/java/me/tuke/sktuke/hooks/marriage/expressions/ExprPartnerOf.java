package me.tuke.sktuke.hooks.marriage.expressions;

import com.lenis0012.bukkit.marriage2.MPlayer;
import me.tuke.sktuke.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.lenis0012.bukkit.marriage2.MarriageAPI;

import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprPartnerOf extends SimplePropertyExpression <Player, OfflinePlayer>{
	static {
		Registry.newProperty(ExprPartnerOf.class, "partner", "player");
	}

	@Override
	public Class<OfflinePlayer> getReturnType() {
		return OfflinePlayer.class;
	}

	@Override
	@Nullable
	public OfflinePlayer convert(Player p) {
		MPlayer mp = MarriageAPI.getInstance().getMPlayer(p.getUniqueId());
		if (mp != null && mp.isMarried())
			return Bukkit.getOfflinePlayer(mp.getPartner().getUniqueId());
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "partner";
	}

}
