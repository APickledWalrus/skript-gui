package me.tuke.sktuke.util;


import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Coal;
import org.bukkit.material.Dye;
import org.bukkit.material.Wool;


public class Translate{
		public static String getIDTranslate(Enchantment e){
			String r = "none";
			String n = e.getName();
			if (e != null){
				if (n.startsWith("DAMAGE_"))
					r =  e.getName().toLowerCase().replaceAll("_", ".");
				else if (n.startsWith("ARROW_") || n.startsWith("FROST_") || n.endsWith("WORKER")) 
					r = n.toLowerCase().split("_")[0] + WordUtils.capitalize(n.toLowerCase().split("_")[1]);
				else if (n.equals("DIG_SPEED"))
					r = "digging";
				else if (n.equals("FIRE_ASPECT"))
					r = "fire";
				else if (n.equals("LUCK"))
					r = "fishingSpeed";
				else if (n.equals("LURE"))
					r = "lootBonusFishing";
				else if (n.equals("LOOT_BONUS_BLOCKS"))
					r = "lootBonusDigger";
				else if (n.equals("LOOT_BONUS_MOBS"))
					r = "lootBonus";
				else if (n.startsWith("PROTECTION_"))
					r = "protect." + ((!n.endsWith("ENVIRONMENTAL")) ? (n.endsWith("EXPLOSIONS") ? "explosion" : n.toLowerCase().split("_")[1]) : "all");
				else if (n.equals("SILK_TOUCH"))
					r = "untouching";
				else if (n.endsWith("_STRIDER"))
					r = "waterWalker";
				else
					r = n.toLowerCase();
			}
			
			return "enchantment."+ r;
		}
		public static String getIDTranslate(EntityType e){
			String r = "generic";
			String n = e.name();
			if (e != null){
				switch (e){
				case MINECART_COMMAND: return "item.minecartCommandBlock.name";
				case MINECART_CHEST:
				case MINECART_FURNACE:
				case MINECART_HOPPER:
				case MINECART_TNT:
					return "item.minecart" + WordUtils.capitalize(n.toLowerCase().split("_")[1]) + ".name";
				case MINECART_MOB_SPAWNER: return "tile.mobSpawner.name";
				case ITEM_FRAME: return "item.frame.name";
				case ENDER_CRYSTAL: return "item.end_crystal.name";
				case WITHER_SKULL: return "item.skull.wither.name";
				case LEASH_HITCH: return "item.leash.name";
				case ENDER_PEARL: return "item.enderPearl.name";
				case FALLING_BLOCK: r = "FallingSand"; break;
				case HORSE: r =	"EntityHorse"; break;
				case DROPPED_ITEM: r = "Item"; break;
				case OCELOT: r = "Ozelot"; break;
				case MAGMA_CUBE: r = "LavaSlime"; break;
				case SNOWMAN: r = "SnowMan"; break;
				case IRON_GOLEM: r = "VillagerGolem"; break;
				case WITHER: r = "WitherBoss"; break;
				case EXPERIENCE_ORB: r = "XPOrb"; break;
					
				
				default: r = WordUtils.capitalize(n.toLowerCase().replaceAll("_", " ")).replaceAll(" ", "");
				}
			}
			return "entity." + r + ".name";
			
		}
		@SuppressWarnings("deprecation")
		public static String getIDTranslate (Block b){
			ItemStack i = new ItemStack(b.getType(), 1);
			i.setDurability(b.getData());
			if (i.getType().equals(Material.ANVIL))
				i.setDurability((short) ((i.getDurability() < 4) ? 0 : (i.getDurability() < 8) ?  1 : 2));
			return getIDTranslate(i);
		}
		public static String getIDTranslate(ItemStack i){
			Material m = i.getType();
			if (m.equals(Material.AIR) || m.equals(Material.PISTON_MOVING_PIECE) || m.equals(Material.PISTON_EXTENSION )|| m.equals(Material.DOUBLE_STEP) || m.equals(Material.WOOD_DOUBLE_STEP) || m.equals(Material.DOUBLE_STONE_SLAB2) || m.equals(Material.MELON_STEM))
				return null;
			String r = "none";
			String n = m.name();
			if (n.endsWith("_ITEM") || n.endsWith("_ON") || n.endsWith("_OFF") || n.startsWith("ITEM_") || n.endsWith("_INVERTED"))
				n = n.replaceFirst("_ITEM", "").replaceFirst("_ON", "").replaceFirst("_OFF", "").replaceFirst("ITEM_", "").replaceFirst("_INVERTED", "");
			switch (m){
			case CLAY_BRICK: r = "brick"; break;
			case CLAY_BALL: r = "clay"; break;
			case BURNING_FURNACE: r = "furnace"; break; 
			case COBBLESTONE_STAIRS: r = "stairsStone"; break;
			case GOLDEN_APPLE: r =  "appleGold"; break;
			case GLOWING_REDSTONE_ORE: r = "oreRedstone"; break;
			case SMOOTH_STAIRS: r = "stairsStoneBrickSmooth"; break;
			case SANDSTONE_STAIRS: r = "stairsSandStone"; break;
			case RED_SANDSTONE_STAIRS: r = "stairsRedSandStone"; break;
			case BREWING_STAND: 
				r = "brewingStand";
				m = Material.BREWING_STAND_ITEM; break;
			case STANDING_BANNER:
			case WALL_BANNER: m = Material.BANNER;
			case BANNER:
				r = "banner." + getColorFromID(i.getDurability()); break;
			case LEATHER_HELMET:
			case LEATHER_CHESTPLATE:
			case LEATHER_LEGGINGS:
			case LEATHER_BOOTS:
				r = n.split("_")[1].toLowerCase() + "Cloth"; break;
			case CHAINMAIL_HELMET:
			case CHAINMAIL_CHESTPLATE:
			case CHAINMAIL_LEGGINGS:
			case CHAINMAIL_BOOTS:
				r = n.split("_")[1].toLowerCase() + "Chain"; break;
			case WOOD_AXE:
			case STONE_AXE:
			case IRON_AXE:
			case GOLD_AXE:
			case DIAMOND_AXE:
				r = "hatchet"+ WordUtils.capitalize(n.split("_")[0].toLowerCase()); break;
			case BED_BLOCK:	r = "bed"; break;

			case ACACIA_DOOR:
			case BIRCH_DOOR:
			case DARK_OAK_DOOR:
			case JUNGLE_DOOR:
			case SPRUCE_DOOR: m = Material.valueOf(n+ "_ITEM");
			case COOKED_BEEF:
			case RAW_BEEF:
			case DIAMOND_BOOTS:
			case GOLD_BOOTS:
			case IRON_BOOTS: 
			case WATER_BUCKET:
			case GOLDEN_CARROT:
			case LAVA_BUCKET:
			case DIAMOND_CHESTPLATE:
			case GOLD_CHESTPLATE:
			case IRON_CHESTPLATE:
			case RAW_CHICKEN:
			case COOKED_CHICKEN:
			case IRON_DOOR:
			case DIAMOND_HELMET:
			case GOLD_HELMET:
			case IRON_HELMET:
			case DIAMOND_LEGGINGS:
			case GOLD_LEGGINGS:
			case IRON_LEGGINGS:
			case DIAMOND_HOE:
			case GOLD_HOE:
			case IRON_HOE:
			case STONE_HOE:
			case WOOD_HOE:
			case DIAMOND_PICKAXE:
			case GOLD_PICKAXE:
			case IRON_PICKAXE:
			case STONE_PICKAXE:
			case WOOD_PICKAXE:
			case DIAMOND_SWORD:
			case GOLD_SWORD:
			case IRON_SWORD:
			case STONE_SWORD:
			case WOOD_SWORD:
			case GOLD_INGOT:
			case IRON_INGOT:
			case HOPPER_MINECART:
			case COOKED_MUTTON:
			case BAKED_POTATO:
			case POISONOUS_POTATO:
			case DIAMOND_BLOCK:
			case EMERALD_BLOCK:
			case GOLD_BLOCK:
			case IRON_BLOCK:
			case LAPIS_BLOCK:
			case REDSTONE_BLOCK:
			case IRON_FENCE:
			case PACKED_ICE:
			case COAL_ORE:
			case DIAMOND_ORE:
			case EMERALD_ORE:
			case GOLD_ORE:
			case IRON_ORE:
			case LAPIS_ORE:
			case REDSTONE_ORE:
			case BRICK_STAIRS:
			case NETHER_BRICK_STAIRS:
			case QUARTZ_STAIRS:
			case WOOD_STAIRS:
			case COAL_BLOCK:
			case COOKED_RABBIT:
			case SPRUCE_DOOR_ITEM:
			case BIRCH_DOOR_ITEM:
			case JUNGLE_DOOR_ITEM:
			case ACACIA_DOOR_ITEM:
			case DARK_OAK_DOOR_ITEM:
				r = n.toLowerCase().split("_")[n.split("_").length -1] + WordUtils.capitalize(n.replaceAll(n.split("_")[n.split("_").length -1], "").toLowerCase().replaceAll("_", " ")).replaceAll(" ", ""); break;	

				
			case GOLD_RECORD:
			case GREEN_RECORD:
			case RECORD_3:
			case RECORD_4:
			case RECORD_5:
			case RECORD_6:
			case RECORD_7:
			case RECORD_8:
			case RECORD_9:
			case RECORD_10:
			case RECORD_11:
			case RECORD_12:
				r = "record"; break;
			case SPRUCE_WOOD_STAIRS:
			case BIRCH_WOOD_STAIRS:
			case JUNGLE_WOOD_STAIRS:
			case DARK_OAK_STAIRS:
			case ACACIA_STAIRS:
				r = "stairsWood" + WordUtils.capitalize(n.toLowerCase().replaceAll("_wood", "").replaceAll("_stairs", "").replaceAll("_", " ")).replaceAll(" ", ""); break;
			case STONE: r = "stone." + getStoneFromID(i.getDurability()); break;
			case MOSSY_COBBLESTONE: r = "stoneMoss"; break;
			case STEP: r = "stoneSlab." + getSlabFromID(i.getDurability()); break;
			case STONE_SLAB2: r = "stoneSlab2.red_sandstone"; break;
			case POTATO: r = "potatoes"; break;
			case STONE_PLATE:
			case WOOD_PLATE:
				r = "pressurePlate" + WordUtils.capitalize(n.split("_")[0].toLowerCase()); break;	
			case MONSTER_EGGS: r = "monsterStoneEgg." + getStoneFromID(i); break; 
			case NOTE_BLOCK: r = "musicBlock"; break;
			case PRISMARINE: r = "prismarine." + ((i.getDurability() == 1) ? "bricks" : (i.getDurability() == 2) ? "dark" : "rough"); break;
			
			case NETHER_WARTS: r = "netherStalk"; break;
			case LOG:
			case LOG_2:
			case LEAVES:
			case LEAVES_2: r = n.toLowerCase().replaceAll("_2", "") + "." + getWoodFromID(i); break; 
			case GLOWSTONE_DUST: r = "yellowDust"; break;
			case GLOWSTONE: r = "lightgem"; break;
			case JACK_O_LANTERN: r = "litpumpkin"; break;
			case RAILS: r = "rail"; break;
			case POWERED_RAIL: r = "goldenRail"; break;
			case DOUBLE_PLANT: r = "doublePlant." + getDoublePlantFromID(i.getDurability()); break; 
			case SOIL: r = "farmland"; break; 
			case YELLOW_FLOWER: r = "flower1.dandelion"; break;
			case RED_ROSE: r = "flower2." + getFlowerFromID(i.getDurability()); break;
			case ENDER_PORTAL_FRAME: r = "endPortalFrame";break;
			case ENDER_PORTAL: r = "endPortalFrame"; break;
			case TRAPPED_CHEST: r = "chestTrap"; break;
			case HARD_CLAY: r = "clayHardened"; break;
			case STAINED_CLAY: r = "clayHardenedStained." + getColorFromID(i.getDurability()); break; 
			case WOOL: r = "cloth." + getColorFromID(i.getDurability()); break; 
			case CARPET: r = "woolCarpet." + getColorFromID(i.getDurability()); break;
			case WOODEN_DOOR: r = "doorWood"; break;
			case RED_SANDSTONE: r = "redSandStone." + ((i.getDurability() == 1) ? "chiseled" : (i.getDurability() == 2) ? "smooth" : "default"); break;
			case COBBLESTONE: r = "stonebrick"; break;
			case SMOOTH_BRICK: r = "stonebricksmooth." + ((i.getDurability() == 1) ? "mossy" : (i.getDurability() == 2) ? "cracked" : (i.getDurability() == 3) ? "chiseled" : "default"); break;
			case LONG_GRASS: r = "tallgrass." + ((i.getDurability() == 1) ? "fern" : (i.getDurability() == 1) ? "grass" : "shrub"); break;
			case REDSTONE_WIRE:	r = "redstoneDust"; break; 
			case HUGE_MUSHROOM_1:
			case HUGE_MUSHROOM_2:
			case BROWN_MUSHROOM:
			case RED_MUSHROOM: r = "mushroom"; break;
			case REDSTONE_TORCH_ON:
			case REDSTONE_TORCH_OFF: r = "notGate"; break;
			case REDSTONE_LAMP_ON:
			case REDSTONE_LAMP_OFF:
				r = "redstoneLight"; break; 
			case WOOD_BUTTON:
			case STONE_BUTTON: 
				r = "button"; break;
			case FLOWER_POT: 
				r = "flowerPot";
				m = Material.FLOWER_POT_ITEM;
			case SUGAR_CANE_BLOCK:
			case SUGAR_CANE: 
				r = "reeds"; break;
			case NETHERRACK: r = "hellrock"; break;
			case SOUL_SAND: r = "hellsand"; break;
			case COBBLE_WALL: 
				r = "cobbleWall." + ((i.getDurability() == 0) ? "normal" : "mossy"); break;
			case SAND:
				r = (i.getDurability() > 0) ? "sand.red" : "sand"; break;
			case SANDSTONE: r = "sandStone." + ((i.getDurability() == 1) ? "chiseled" : (i.getDurability() == 2) ? "smooth" : "default"); break;
			case WOOD_STEP: r = "woodSlab." + ((i.getDurability() <4) ? getWoodFromID(i) : (i.getDurability() == 4) ? "acacia" : "big_oak" ); break;
			case WOOD:
			case SAPLING: r = n.toLowerCase() + "." + ((i.getDurability() <4) ? getWoodFromID(i) : (i.getDurability() == 4) ? "acacia" : "big_oak" ); break;
			case WALL_SIGN:
			case SIGN_POST: r = "sign"; break;
			case SLIME_BLOCK: r = "slime"; break;
			case SPONGE: r = "sponge." + ((i.getDurability() == 0) ? "dry" : "wet"); break;
			case STAINED_GLASS: r = "stainedGlass." + getColorFromID(i.getDurability()); break;
			case STAINED_GLASS_PANE: r = "thinStainedGlass." + getColorFromID(i.getDurability()); break;
			case TRIPWIRE: r = "tripWire"; break;
			case TRIPWIRE_HOOK: r = "tripWireSource"; break;
			case STATIONARY_LAVA:
			case STATIONARY_WATER: r = n.toLowerCase().replaceAll("stationary_", ""); break;
			case IRON_PLATE: r = "weightedPlate_heavy"; break;
			case GOLD_PLATE: r = "weightedPlate_light"; break;
			case ENDER_STONE: r = "whiteStone"; break;
			case COMMAND_MINECART:
			case COMMAND:
				r = n.toLowerCase().split("_")[n.split("_").length -1] + WordUtils.capitalize(n.replaceAll(n.split("_")[n.split("_").length -1], "").toLowerCase().replaceAll("_", " ")).replaceAll(" ", "") + "Block"; break;
			case DIAMOND_SPADE:
			case GOLD_SPADE:
			case IRON_SPADE:
			case STONE_SPADE:
			case WOOD_SPADE:
				r = "shovel"+ WordUtils.capitalize(n.split("_")[0].toLowerCase()); break;
			case DIRT: r = "dirt." + ((i.getDurability() == 0) ? "default" : (i.getDurability() == 1) ? "coarse" : "podzol"); break; 
			case MUTTON: r = "muttonRaw"; break;
			case IRON_BARDING: r = "horsearmormetal"; break;
			case GOLD_BARDING: 
			case DIAMOND_BARDING:
				r = "horsearmor" + n.split("_")[0].toLowerCase(); break;
			case PORK: n = "porkchop";
			case RABBIT:
				r = n.toLowerCase() + "Raw"; break;
			case GRILLED_PORK: r = "porkchopCooked"; break;
			case PUMPKIN_SEEDS:
			case MELON_SEEDS:
				r = n.toLowerCase().split("_")[1] +"_"+ n.toLowerCase().split("_")[0]; break;
			case STORAGE_MINECART: r = "minecartChest"; break;
			case POWERED_MINECART: r = "minecartFurnace"; break;
			case EXPLOSIVE_MINECART: r = "minecartTnt"; break;
			case MONSTER_EGG: r = "monsterPlacer"; break;
			case MUSHROOM_SOUP: r = "mushroomStew"; break;
			case NETHER_STALK: r = "netherStalkSeeds"; break;
			case NETHER_BRICK_ITEM: r = n.toLowerCase().replace("_", ""); break;
			case QUARTZ_ORE:
			case QUARTZ: r = "netherquartz"; break; 
			case IRON_DOOR_BLOCK: r = "doorIron"; break;
			case WOOD_DOOR:	r = "doorOak"; break;
			case FIREWORK_CHARGE: r = "fireworksCharge"; break;
			case FIREWORK:
			case CARROT_ITEM:
			case CARROT: 
				r = n.toLowerCase() + "s"; break;
			case CARROT_STICK: r = "carrotOnAStick"; break;
			case COAL: r = ((Coal)i.getData()).getType().name().toLowerCase();
			case INK_SACK: r = "dyePowder." + getDyeFromID(i.getDurability()); break;
			case WATCH: r = "clock"; break;
			case REDSTONE_COMPARATOR: 
			case REDSTONE_COMPARATOR_ON:
			case REDSTONE_COMPARATOR_OFF: m = Material.REDSTONE_COMPARATOR;
				r = n.split("_")[1].toLowerCase(); break; 
			case QUARTZ_BLOCK: r = "quartzBlock." + ((i.getDurability() == 1) ? "chiseled" : (i.getDurability() >= 2 && i.getDurability() <= 4) ? "lines" : "default"); break;
			case BOOK_AND_QUILL: r = "writingBook"; break;
			case DIODE_BLOCK_ON:
			case DIODE_BLOCK_OFF: m = Material.DIODE;
			case MELON_BLOCK:
			case CAKE_BLOCK:
			case SNOW_BLOCK: r = n.toLowerCase().replaceAll("_block", ""); break;
			case TRAP_DOOR:
			case WATER_LILY:
			case SNOW_BALL:
			case SLIME_BALL:
			case DEAD_BUSH: r = n.toLowerCase().replaceAll("_", ""); break;
			case MILK_BUCKET: r = "milk"; break;
			case SKULL: m = Material.SKULL_ITEM;
			case SKULL_ITEM:
				r = "skull." + getSkullFromID(i.getDurability()); break;
			case RAW_FISH: r = "fish."+ getFishFromID(i.getDurability()) +".raw"; break;
			case COOKED_FISH: r = (i.getDurability() == 0) ? "fish.cod.cooked" : "fish.salmon.cooked"; break;
			case ANVIL: r = "anvil." + getAnvilFromID(i.getDurability()); break;
			default: r = n.toLowerCase().split("_")[0]  + WordUtils.capitalize(n.replaceAll(n.split("_")[0], "").toLowerCase().replaceAll("_", " ")).replaceAll(" ", ""); break;
			
			}
			switch (n){
			case "END_CRYSTAL":
			case "TIPPED_ARROW":
			case "SPECTRAL_ARROW": 
			case "DRAGONS_BREATH": r = n.toLowerCase().replaceAll("s_", "_"); break;
			case "BEETROOT": r = n.toLowerCase() + "s"; break;
			case "COMMAND_REPEATING": 
			case "COMMAND_CHAIN":
				r = n.toLowerCase().split("_")[n.split("_").length -1] + WordUtils.capitalize(n.replaceAll(n.split("_")[n.split("_").length -1], "").toLowerCase().replaceAll("_", " ")).replaceAll(" ", "") + "Block"; break;
			case "BONE_BLOCK":
				r = n.toLowerCase().split("_")[n.split("_").length -1] + WordUtils.capitalize(n.replaceAll(n.split("_")[n.split("_").length -1], "").toLowerCase().replaceAll("_", " ")).replaceAll(" ", ""); break;	

			}
			
			return ((m.isBlock()) ? "tile." : "item.") + r + ".name";
		}
		private static String getSlabFromID(int i){
			if (i > 7)
				i -= 8;
			switch (i){
			case 1: return "sand";
			case 2: return "wood"; 
			case 3: return "cobble"; 
			case 4: return "brick"; 
			case 5: return "smoothStoneBrick";
			case 6: return "netherBrick"; 
			case 7: return "quartz";  
			default: return "stone";
			
			}
			
		}
		private static String getStoneFromID(ItemStack i){
			int dd = i.getType().equals(Material.MONSTER_EGGS) ? i.getDurability() - 2 : i.getDurability();
			switch (dd){
			case -2: return "stone";
			case -1: return "cobble";
			case 0: return "brick";
			case 1: return "mossybrick"; 
			case 2: return "crackedbrick"; 
			case 3: return "chiseledbrick"; 
			default: return "";
		
		}
		}
		private static String getStoneFromID(int i) {
			switch (i){
			case 1: return "granite";
			case 2: return "graniteSmooth"; 
			case 3: return "diorite"; 
			case 4: return "dioriteSmooth"; 
			case 5: return "andesite";
			case 6: return "andesiteSmooth";  
			default: return "stone";
			}
			
		}
		private static String getWoodFromID(ItemStack i){
			if (i.getType().equals(Material.LEAVES) || i.getType().equals(Material.LOG) || i.getType().equals(Material.SAPLING) || i.getType().equals(Material.WOOD) || i.getType().equals(Material.WOOD_STEP))
				switch (i.getDurability()){
				case 0: return "oak";
				case 1: return "spruce"; 
				case 2: return "birch"; 
				case 3: return "jungle"; 
				default: return "";
			
			} else
				return (i.getDurability() == 0) ? "acacia" : "big_oak";
			
		}
		private static String getAnvilFromID(int i) {
			/*if (isBlock)
				return (i <= 3) ? "intact" : (i <= 7) ? "slightlyDamaged" : "veryDamaged";
			*/
			return (i == 0) ? "intact" : (i == 1) ? "slightlyDamaged" : "veryDamaged";
			
			
		}
		
		private static String getFlowerFromID(int i){
			switch (i){
			case 0: return "poppy";
			case 1: return "blueOrchid"; 
			case 2: return "allium"; 
			case 3: return "houstonia"; 
			case 4: return "tulipRed";
			case 5: return "tulipOrange";
			case 6: return "tulipWhite";
			case 7: return "tulipPink";
			default: return "oxeyeDaisy";
			
			}
		}
		private static String getDoublePlantFromID(int i){
			switch (i){
			case 0: return "sunflower";
			case 1: return "syringa"; 
			case 2: return "grass"; 
			case 3: return "fern"; 
			case 4: return "rose"; 
			default: return "peonia";
			
			}
			
		}
		private static String getColorFromID(int i){
			ItemStack item = new ItemStack(Material.WOOL, 1);
			item.setDurability((short) i);
			return ((Wool)item.getData()).getColor().equals(DyeColor.LIGHT_BLUE) ? "lightBlue" : ((Wool)item.getData()).getColor().name().toLowerCase();
		}
		private static String getDyeFromID(int i){
			ItemStack item = new ItemStack(Material.INK_SACK, 1);
			item.setDurability((short) i);
			return ((Dye)item.getData()).getColor().equals(DyeColor.LIGHT_BLUE) ? "lightBlue" : ((Dye)item.getData()).getColor().name().toLowerCase();
		}
		private static String getFishFromID(int i){
			switch (i){
			case 0: return "cod";
			case 1: return "salmon"; 
			case 2: return "clownfish"; 
			case 3: return "putterfish"; 
			default: return "fish";
			}
		}
		private static String getSkullFromID(int i){
			switch (i){
			case 0: return "skeleton";
			case 1: return "wither"; 
			case 2: return "zombie"; 
			case 3: return "char"; 
			case 4: return "creeper"; 
			default: return "dragon";
			
			}
		}
}
