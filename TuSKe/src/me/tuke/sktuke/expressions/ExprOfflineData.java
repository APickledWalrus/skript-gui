package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import me.tuke.sktuke.TuSKe;

public class ExprOfflineData extends SimplePropertyExpression<OfflinePlayer, Player>{
	static {
		NewRegister.newProperty(ExprOfflineData.class, "player data", "offlineplayer");
	}

	@Override
	public Class<? extends Player> getReturnType() {
		return Player.class;
	}

	@Override
	@Nullable
	public Player convert(OfflinePlayer p) {
		if (!p.isOnline()){
			Player player = TuSKe.getNMS().getToPlayer(p);
			if ( player != null){
				return player;
			}
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "player data";
	}

}
