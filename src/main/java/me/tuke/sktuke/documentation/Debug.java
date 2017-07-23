package me.tuke.sktuke.documentation;

import com.google.common.html.HtmlEscapers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URLDecoder;

/**
 * @author Tuke_Nuke on 21/07/2017
 */

public class Debug {

	public static void main(String[] args) {
		System.out.println(HtmlEscapers.htmlEscaper().escape("<text></text> &l"));
	}
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Test {
		String value();
	}

}
