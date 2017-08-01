package com.github.tukenuke.tuske.manager.customenchantment;

public class CEnchant {

	private int clevel;
	private CustomEnchantment cenchant;
	public CEnchant(CustomEnchantment ce, int level){
		cenchant = ce;
		clevel = level;
	}
	public int getLevel(){
		return clevel;
	}
	public CustomEnchantment getEnchant(){
		return cenchant;
	}
}
