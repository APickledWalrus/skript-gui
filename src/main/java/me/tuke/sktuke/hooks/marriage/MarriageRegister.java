package me.tuke.sktuke.hooks.marriage;

import ch.njol.skript.SkriptAddon;
import com.lenis0012.bukkit.marriage2.Gender;
import me.tuke.sktuke.util.EnumType;

/**
 * @author Tuke_Nuke on 10/04/2017
 */
public class MarriageRegister {
	public MarriageRegister(SkriptAddon tuske) {
		types();
		try {
			tuske.loadClasses(this.getClass().getPackage().getName(), "conditions", "effects", "expressions");
		} catch (Exception e) {

		}
	}

	private static void types(){
		new EnumType(Gender.class, "gender", "gender");
	}
}
