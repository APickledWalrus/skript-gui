package me.tuke.sktuke.register;

import javax.annotation.Nullable;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;

public class EnumType {

	public <T extends Enum<T>> EnumType(final Class<T> c, String code, String regexUser){
		Classes.registerClass(new ClassInfo<T>(c, code).user(regexUser).name(c.getSimpleName()).defaultExpression(new EventValueExpression<T>(c)).parser(new Parser<T>(){

			@Override
			@Nullable
			public T parse(String name, ParseContext arg1) {
				name = name.toUpperCase().replace(" ", "_");
				if (name.startsWith(c.getSimpleName().toUpperCase() + "."))
					name = name.split("\\.")[1];
				try {
					return Enum.valueOf(c, name);
				} catch(Exception e){}
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
				return c.getSimpleName().toLowerCase() + "\\..+";
			}
			
		}));

	}
	public static String toString(Enum<?> e){
		return e.name().toLowerCase().replaceAll("_", " ");
	}
}
