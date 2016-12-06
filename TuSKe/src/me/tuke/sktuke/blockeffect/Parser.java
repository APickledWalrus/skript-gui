package me.tuke.sktuke.blockeffect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import me.tuke.sktuke.TuSKe;

public class Parser {
	private BlockPosition bp = new BlockPosition(0D, 0D, 0D, 0, (byte) 1);
	int loopTimes = 0;
	String loop, loopMatch;
	public boolean parser(String str){
		TuSKe.debug(1, str);
		String match = null;
		if (str.matches("^\\((.+)\\)(\\d+).*")){
			int bracket = getLastBracket(str);
			match = str.substring(1, bracket);
			Integer i = Integer.valueOf(str.replaceFirst("\\(" + fixBracket(match) + "\\)(\\d+).*", "$1"));
			parseLoop(i, match, (match = fixBracket("(" + match + ")" +i)));
		} else if (str.matches("^(b|d|x|y|z)\\d+.*")){
			parseBlock(str);
			match = "(b|d|x|y|z)\\d+";
		} else if (str.matches("^w\\d+(t|s|m)?.*")) {
			match = "w\\d+(t|s|m)?";
			Long t = Long.valueOf(str.replaceFirst("w(\\d+)(t|s|m)?.*", "$1"));
			if (str.matches("w(\\d+)s.*"))
				t*=20;
			else if (str.matches("w(\\d+)m.*"))
				t*=1200;
			final String str1 = str;
			final String match1 = match;
			//TuSKe.debug("LoopOrNot1: "+ loopTimes, loop, loopMatch);
			Bukkit.getScheduler().runTaskLaterAsynchronously(TuSKe.getInstance(), new Runnable(){

				@Override
				public void run() {
					parser(str1, match1);
					//TuSKe.debug("LoopOrNot: "+ loopTimes, loop, loopMatch);
					if (loopTimes > 0)
						parseLoop(loopTimes, loop, loopMatch);
					
				}}, t);
			return false;
		}
		if (match != null){
			return parser(str, match);
		
		}
		return true;
	}
	private void parseBlock(String str){

	if (str.matches("^b\\d+.*")){
		Integer i = Integer.valueOf(str.replaceFirst("(b|d|x|y|z)(\\d+).*", "$2"));
		bp.id = i;		
	} else if (str.matches("^d\\d+.*")){
		Integer i =Integer.valueOf(str.replaceFirst("(b|d|x|y|z)(\\d+).*", "$2"));
		bp.data = i.byteValue();
	} else if (str.matches("^x\\d+.*")){
		Double i = Double.valueOf(str.replaceFirst("(b|d|x|y|z)(\\d+).*", "$2"));
		bp.x = i;
	} else if (str.matches("^y\\d+.*")){
		Double i = Double.valueOf(str.replaceFirst("(b|d|x|y|z)(\\d+).*", "$2"));
		bp.y = i;
	} else if (str.matches("^z\\d+.*")){
		Double i = Double.valueOf(str.replaceFirst("(b|d|x|y|z)(\\d+).*", "$2"));
		bp.z = i;
	}
	
		
	}
	private boolean parser(String str, String match){
		str = str.replaceFirst(match, "");
		//TuSKe.debug("AA "+ str, match);
		if (!str.equalsIgnoreCase(""))
			return parser(str);
		return false;
		
	}
	private void parseLoop(int times, String str, String match){
		//TuSKe.debug("Debug time: " + times, str, match);
		if (times-- > 0){
			//TuSKe.debug("Loop: " +times);
			loopTimes = times;
			loop = str;
			loopMatch = match;
			if (parser(str))
				parseLoop(times, str, match);
		}else {
			loopTimes = 0;
			loop = null;
			loopMatch = null;
		}
	
		
	}
	@SuppressWarnings("unchecked")
	public <T> T getMatch(String str, String pattern, int group){
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		if (m.find(group)){
			return (T) m.group(group);
			//if (clz.equals(Double.class)){
			//	return (T) Double.valueOf(m.group(group));
			//}
		}
		return null;
		
	}
	private String fixBracket(String str){
		return str.replaceAll("(\\(|\\))", "\\\\$1");
	}
	
	public int getLastBracket(String str){
		int foundOpen = 0;
		int foundClose = 0;
		for (int x = 0; x < str.length(); x++){
			char c = str.charAt(x);
			if (c == '(')
				foundOpen++;
			if (c == ')'){
				foundClose++;
				if (foundClose == foundOpen)
					return x;
			}
		}
		return -1;
	}
	

}
