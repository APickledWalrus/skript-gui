package me.tuke.sktuke.util;

import javax.annotation.Nullable;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.EnumUtils;
import me.tuke.sktuke.TuSKe;
import org.bukkit.Bukkit;

import java.util.StringJoiner;

@SuppressWarnings("unchecked")
public class EnumType extends ClassInfo{

	public <T extends Enum<T>> EnumType(final Class<T> c, String code, String regexUser) {
		this(c, code, regexUser, code);
	}
	public <T extends Enum<T>> EnumType(final Class<T> c, String code, String regexUser, String languageNode) {
		super(c, code);
		EnumUtils<T> enumUtils = new EnumUtils<>(c, languageNode);
		String names = enumUtils.getAllNames();
		if (names != null && !names.isEmpty())
			usage(enumUtils.getAllNames());
		else
			usage(getAllNames(c));
		Parser<T> parser = getParser(c, enumUtils);
		try {
			Classes.registerClass(user(regexUser)
					.defaultExpression(new EventValueExpression<>(c))
					.parser(parser));
		} catch (Exception e) {
			//if (!TuSKe.getInstance().getConfig().isSet("cancel_override_type." + code)) {
			//	ReflectionUtils.setField(ClassInfo.class, Classes.getExactClassInfo(c), "parser", parser);
			//} else
				TuSKe.debug("Couldn't register the type '" + code + "'. Due to: " + (e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "unknown"));
		}

	}
	private <T extends Enum<T>> Parser<T> getParser(Class<T> c, EnumUtils<T> enumUtils) {
		if (!TuSKe.getInstance().getConfig().getBoolean("use_only_enum_names")){//It will parse the enum types as '<Enum name>' and ''<Enum type>.<Enum name>'
			return new Parser<T>() {
				@Override
				@Nullable
				public T parse(String name, ParseContext arg1) {
					name = fromString(name);
					if (name.startsWith(c.getSimpleName().toUpperCase() + "."))
						name = name.split("\\.")[1];
					T result = enumUtils.parse(name);
					if (result != null)
						return result;
					try {
						return Enum.valueOf(c, name);
					} catch(Exception e) {

					}
					return null;
				}

				@Override
				public String toString(T e, int arg1) {
					return EnumType.toString(e);
				}

				@Override
				public String toVariableNameString(T e) {
					return c.getSimpleName() + "." +e.name();
				}

				@Override
				public String getVariableNamePattern() {
					return "(" + c.getSimpleName().toLowerCase() + "\\.)?.+";
				}
			};
		} else {
			return new Parser<T>() {
				@Override
				@Nullable
				public T parse(String name, ParseContext arg1) {
					name = fromString(name);
					if (name.startsWith(c.getSimpleName().toUpperCase() + ".")) {
						name = name.split("\\.")[1];
						T result = enumUtils.parse(name);
						if (result != null)
							return result;
						try {
							return Enum.valueOf(c, name);
						} catch (Exception e) {

						}
					}
					return null;
				}

				@Override
				public String toString(T e, int arg1) {
					return EnumType.toString(e);
				}

				@Override
				public String toVariableNameString(T e) {
					return c.getSimpleName() + "." +e.name();
				}

				@Override
				public String getVariableNamePattern() {
					return "(" + c.getSimpleName().toLowerCase() + "\\.)?.+";
				}
			};
		}
	}

	public static String getAllNames(Class<? extends Enum> enumClz) {
		StringJoiner sj = new StringJoiner(", ");
		for (Enum e : enumClz.getEnumConstants()) {
			sj.add(toString(e));
		}
		return sj.toString();
	}
	public static String toString(Enum<?> e){
		return e.name().toLowerCase().replaceAll("_", " ");
	}
	public static String fromString(String str){
		return str.toUpperCase().replaceAll("\\s+", "_");
	}
}
