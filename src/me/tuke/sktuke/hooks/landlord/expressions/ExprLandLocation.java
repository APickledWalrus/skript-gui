package me.tuke.sktuke.hooks.landlord.expressions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.Chunk;
import org.bukkit.Location;

import javax.annotation.Nullable;

import com.jcdesimp.landlord.persistantData.LowOwnedLand;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprLandLocation extends SimplePropertyExpression<LowOwnedLand, Location>{
	static {
		NewRegister.newProperty(ExprLandLocation.class, "land[lord] location", "landclaim");
	}

	@Override
	public Class<? extends Location> getReturnType() {
		return Location.class;
	}

	@Override
	@Nullable
	public Location convert(LowOwnedLand ol) {
		if (ol != null){
			Chunk c = ol.getChunk();
			Location l = c.getWorld().getHighestBlockAt(c.getBlock(0, 0, 0).getLocation()).getLocation();
			l.setYaw(315F);
			l.setPitch(0L);
			l.add(0.5D, 0, 0.5D);
			return l;
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "land location";
	}

}
