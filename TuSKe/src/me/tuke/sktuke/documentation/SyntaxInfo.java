package me.tuke.sktuke.documentation;

import java.util.ArrayList;
import java.util.List;

public class SyntaxInfo {
	
	private List<String> values = new ArrayList<>();
	
	public void addValue(String str){
		values.add(str);
	}
	
	public List<String> getValues(){
		return values;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (String str : values) //It will fix some syntax issues to make more friendly to see and some basic html stuffs from type's usage.
			sb.append(str.replaceAll("\\\\(\\(|\\))", "$1").replaceAll("\\d+¦", "").replaceAll("<.+?>(.+?)</.+?>", "$1").replaceAll("&gt;", ">").replaceAll("&lt;", "<") + "\n");
		return sb.toString();
	}
}
