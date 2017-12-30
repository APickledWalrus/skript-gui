package com.github.tukenuke.tuske.expressions.regex;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprRegex extends SimpleExpression<String> {

    static {
        Registry.newSimple(ExprRegex.class, "/<.+>/");
    }

    private String regex;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
        regex = arg3.regexes.get(0).group();
        return true;
    }

    @Override
    public String toString(@Nullable Event arg0, boolean arg1) {
        return "/" + regex + "/";
    }

    @Override
    @Nullable
    protected String[] get(Event e) {
        return new String[] {
                regex
        };
    }

}
