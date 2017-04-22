package me.tuke.sktuke.expressions;

import me.tuke.sktuke.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprPlayerVersion extends SimplePropertyExpression<Player, String>{
	private static final boolean isViaversion = Bukkit.getServer().getPluginManager().isPluginEnabled("ViaVersion");
	private static final boolean isProtocolSupport = Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolSupport");

	static {
		if (isViaversion || isProtocolSupport)
			Registry.newProperty(ExprPlayerVersion.class, "(mc|minecraft) version", "player");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(Player p) {
		if (isViaversion){
			int i = us.myles.ViaVersion.api.ViaVersion.getInstance().getPlayerVersion(p);
			return us.myles.ViaVersion.api.protocol.ProtocolVersion.getProtocol(i).getName().replace(".x", "");
		} else if (isProtocolSupport)
			return protocolsupport.api.ProtocolSupportAPI.getProtocolVersion(p).getName();
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "minecraft version";
	}

}
