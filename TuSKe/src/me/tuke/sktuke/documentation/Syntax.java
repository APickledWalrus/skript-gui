package me.tuke.sktuke.documentation;
import java.util.ArrayList;
import java.util.List;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.util.StringUtils;

public class Syntax {
	
	private String name, since;
	private String[] desc, examples, syntaxes;
	private Class<?> returnType;
	private Expression<?> expr;
	private SyntaxType type;
	
	public Syntax(Expression<?> expr, SyntaxType type, String... syntaxes){
		name = expr.getClass().getSimpleName();
		if (expr.getClass().isAnnotationPresent(Name.class))
			name = ((Name)expr.getClass().getAnnotation(Name.class)).value();
		if (expr.getClass().isAnnotationPresent(Description.class))
			desc = ((Description)expr.getClass().getAnnotation(Description.class)).value();
		if (expr.getClass().isAnnotationPresent(Examples.class))
			examples = ((Examples)expr.getClass().getAnnotation(Examples.class)).value();
		if (expr.getClass().isAnnotationPresent(Since.class))
			since = ((Since)expr.getClass().getAnnotation(Since.class)).value();
		returnType = expr.getReturnType();
		this.expr = expr;
		this.syntaxes = syntaxes;
		this.type = type;
		
	}
	public Syntax(String name, String[] description, String[] syntaxes,String[] examples, String since, SyntaxType type){
		
	}
	public String getName(){
		return name;
	}
	public String getSince(){
		return since;
	}
	public String getChangers(){
		//return changers;
		if (expr != null){
			List<String> changers = new ArrayList<>();
			for (ChangeMode cm : ChangeMode.values())
				if (expr.acceptChange(cm) != null)
					changers.add(cm.name().toLowerCase());
			if (changers.size() == 0)
				return "none";
			else {
				return StringUtils.join(changers, ", ");
			}
		}
		return null;
	}
	public String[] getDescription(){
		return desc;
	}
	public String[] getExamples(){
		return examples;
	}
	public String[] getSyntaxes(){
		return syntaxes;
	}
	public Class<?> getReturnType(){
		return returnType;
	}
	public SyntaxType getType(){
		return type;
	}
}
