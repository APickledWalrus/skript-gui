package me.tuke.sktuke.hooks.landlord;

import ch.njol.skript.SkriptAddon;
import ch.njol.skript.lang.ParseContext;
import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.Landflag;
import com.jcdesimp.landlord.persistantData.LowOwnedLand;
import me.tuke.sktuke.util.SimpleType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Leandro Pereira (aka Tuke_Nuke)
 */
public class LandlordRegister {

	public LandlordRegister(SkriptAddon tuske) {
		types();
		try {
			tuske.loadClasses(this.getClass().getPackage().getName(), "effects", "expressions");
		} catch (Exception e) {

		}
	}

	private void types() {
		new SimpleType<LowOwnedLand>(LowOwnedLand.class, "landclaim", "land ?claim(s)?"){
			@Override
			@Nullable
			public LowOwnedLand parse(String s, ParseContext arg1) {
				return null;
			}

			@Override
			public String toString(LowOwnedLand ol, int arg1) {
				return String.valueOf(ol.getId());
			}

			@Override
			public String toVariableNameString(LowOwnedLand ol) {
				return "ownedland:" + ol.getId();
			}};
		final Map<String, Landflag> fixedFlags = new HashMap<>();
		for (Map.Entry<String, Landflag> entry: Landlord.getInstance().getFlagManager().getRegisteredFlags().entrySet()){
			fixedFlags.put(entry.getKey().toLowerCase(), entry.getValue());
		}

		new SimpleType<Landflag>(Landflag.class, "landflag", "land ?flags(s)?"){
			@Override
			@Nullable
			public Landflag parse(String s, ParseContext arg1) {
				if (fixedFlags.containsKey((s = s.toLowerCase())))
					return fixedFlags.get(s);
				return null;
			}

			@Override
			public String toString(Landflag lf, int arg1) {
				return lf.getDisplayName().toLowerCase();
			}

			@Override
			public String toVariableNameString(Landflag lf) {
				return "ownedland:" + lf.getDisplayName().toLowerCase();

			}};

	}
}
