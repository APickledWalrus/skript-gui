package me.tuke.sktuke.util;

import javax.annotation.Nullable;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import me.tuke.sktuke.TuSKe;

public abstract class SimpleType<T>{
	
	private String variableName;
	private String name;
	private String pattern;
	private Class<T> clz;
	public SimpleType(Class<T> clz, String name){
		this(clz, name, name, ".+");
	}
	public SimpleType(Class<T> clz, String name, String pattern){
		this(clz, name, pattern, ".+");
	}
	public SimpleType(Class<T> clz, String name, String pattern, String variableName){
		this.clz = clz;
		this.name = name;
		this.pattern = pattern;
		this.variableName = variableName;
		register();
	}
	
	public abstract String toString(T arg0, int arg1);
	
	public abstract String toVariableNameString(T arg0);
	
	public T parse(String arg0, ParseContext arg1){
		return null;
	}
	public boolean canParse(ParseContext pc){
		return true;
	}
	
	private void register(){
		try {
			Classes.registerClass(new ClassInfo<T>(clz, name.toLowerCase().replaceAll("\\s+", "")).user(pattern).name(name).defaultExpression(new EventValueExpression<T>(clz)).parser(new Parser<T>() {

				@Override
				public String getVariableNamePattern() {
					return variableName;
				}

				@Override
				public boolean canParse(ParseContext context) {
					return SimpleType.this.canParse(context);
				}

				@Override
				@Nullable
				public T parse(String arg0, ParseContext arg1) {
					return SimpleType.this.parse(arg0, arg1);
				}

				@Override
				public String toString(T arg0, int arg1) {
					return SimpleType.this.toString(arg0, arg1);
				}

				@Override
				public String toVariableNameString(T arg0) {
					return SimpleType.this.toVariableNameString(arg0);
				}
			}));
		} catch (Exception e) {
			if (TuSKe.debug()) {
				TuSKe.debug("Couldn't register the type '" + name + "'. Due to: " + (e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "unknown"));
			}
		}
		
	}

}
