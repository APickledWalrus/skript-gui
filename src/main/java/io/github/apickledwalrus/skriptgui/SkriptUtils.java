package io.github.apickledwalrus.skriptgui;

import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SectionSkriptEvent;
import ch.njol.skript.lang.parser.ParserInstance;
import org.skriptlang.skript.lang.structure.Structure;

public final class SkriptUtils {

	private SkriptUtils() { }

	@SafeVarargs
	public static boolean isSection(ParserInstance parser, Class<? extends Section>... sections) {
		Structure current = parser.getCurrentStructure();
		return parser.isCurrentSection(sections)
				|| (current instanceof SectionSkriptEvent sectionSkriptEvent && sectionSkriptEvent.isSection(sections));
	}

}
