package me.tuke.sktuke.register;

import java.util.*;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.*;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.Landflag;
import com.jcdesimp.landlord.persistantData.LowOwnedLand;
import com.lenis0012.bukkit.marriage2.Gender;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.api.events.*;
import br.com.devpaulo.legendchat.channels.ChannelManager;
import br.com.devpaulo.legendchat.channels.types.Channel;
import ch.njol.skript.*;
import ch.njol.skript.classes.*;
import ch.njol.skript.classes.Comparator;
import ch.njol.skript.expressions.base.*;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.util.*;
import ch.njol.skript.registrations.*;
import ch.njol.skript.util.*;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.conditions.*;
import me.tuke.sktuke.customenchantment.*;
import me.tuke.sktuke.effects.*;
import me.tuke.sktuke.events.*;
import me.tuke.sktuke.events.customevent.*;
import me.tuke.sktuke.expressions.*;
import me.tuke.sktuke.expressions.customenchantments.*;
import me.tuke.sktuke.gui.*;
import me.tuke.sktuke.hooks.landlord.*;
import me.tuke.sktuke.hooks.legendchat.*;
import me.tuke.sktuke.hooks.marriage.*;
import me.tuke.sktuke.hooks.simpleclans.*;
import me.tuke.sktuke.listeners.*;
import me.tuke.sktuke.util.LegendConfig;
import me.tuke.sktuke.util.Regex;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.events.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Register{

	public static int expr = 0;
	public static int evt = 0;
	public static int cond = 0;
	public static int eff = 0;

	public LegendConfig config = null;
	private TuSKe instance;
	
	public Register(TuSKe instance){
		this.instance = instance;
	}
	public Integer[] load(){
		if (!Skript.isAcceptRegistrations()){//To prevent to load the when the server is already loaded.
			TuSKe.log("TuSKe can't be loaded when the server is already loaded.", Level.SEVERE);
		    Bukkit.getServer().getPluginManager().disablePlugin(instance);
			return null;
		}
		long start = System.currentTimeMillis();
		Skript.registerAddon(instance);
		EnchantConfig.loadEnchants();
		ArrayList<Boolean> boo = new ArrayList<>(); //excluding the skript dependencie since isn't needed on registration
		List<String> depends = instance.getDescription().getSoftDepend();
		for (int x = 1; x < depends.size(); x++){
			if (x == 1 || x == 5){
				boo.add(hasPlugin(depends.get(++x)) || hasPlugin(depends.get(x))); // SimpleClas and SimpleClansLegacy are the same plugin, just with different names (idk why).
			}else{
				boo.add(hasPlugin(depends.get(x))); // Just to get the right 
			}
		}
		Boolean[] booleans = boo.toArray(new Boolean[boo.size()]);
		if (booleans[4])
			booleans[4] = Skript.classExists("com.jcdesimp.landlord.persistantData.LowOwnedLand");
		registerEvents(booleans);
		registerClassInfos(booleans); //It needs to be before the expressions (I added before effect and conditions too just to future updates)
		//cause some expressions get some class info.
		registerConditions(booleans);
		registerEffects(booleans);
		registerExpressions(booleans);
		registerEventValues(booleans);
		//Skript.registerCondition(CondIsMoving.class, "%player% is( (moving|walking)|(n't| not) stopped)", "%player% is((n't| not) (moving|walking)| stopped)");
		//Skript.registerCondition(CondCanSpawn.class , "(1妃onster|2地nimal)[s] can spawn (at|in) %location/world/string%", "%entitytype% can spawn (at|in) %location/world/string%", "(1妃onster|2地nimal)[s] can('t| not) spawn (at|in) %location/world/string%", "%entitytype% can('t| not) spawn (at|in) %location/world/string%");
		/*Classes.registerClass(new ClassInfo<CEnchant>(CEnchant.class, "customenchantment").user(new String[]{"custom ?enchantment"}).name("Custom Enchantment").defaultExpression(new EventValueExpression(CEnchant.class)).parser(new Parser<CEnchant>(){

			@Override
			@Nullable
			public CEnchant parse(String s, ParseContext arg1) {
				int l = 0;
				if (s.matches(".*\\s{1,}\\d{1,}$")){
					l = Integer.valueOf(s.split(" ")[s.split(" ").length-1]);
					s = s.replace(" " + l,"");
				}
				return (EnchantManager.isCustomByID(s)) ? new CEnchant(CustomEnchantment.getByID(s), l) : null;
			}

			@Override
			public String toString(CEnchant ce, int arg1) {
				return ce.getEnchant().getId();
			}

			@Override
			public String toVariableNameString(CEnchant ce) {
				return "ce:" + ce.getEnchant().getId();
			}
			
			@Override
			public String getVariableNamePattern() {
				return ".+";
			}
			
		}));*/
		//
		//
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryCheck(instance), instance);
		Bukkit.getServer().getPluginManager().registerEvents(new OnlineStatusCheck(instance), instance);
		Bukkit.getServer().getPluginManager().registerEvents(new EnchantCheck(instance), instance);
		//Bukkit.getServer().getPluginManager().registerEvents(new PlayerMovesCheck(instance),instance);
		if (instance.getConfig().isSet("CompatibilityMode") && instance.getConfig().isBoolean("CompatibilityMode"))
			for (Player p: Bukkit.getOnlinePlayers()){
				OnlineStatusCheck.setTime(p, System.currentTimeMillis());
			}
		return new Integer[]{evt, cond, expr, eff, (int) (System.currentTimeMillis() - start)}; //Enchantments will be loaded after the server started.
	}
	public boolean hasPlugin(String plugin){
		return Bukkit.getServer().getPluginManager().getPlugin(plugin) != null;
	}
	public void registerEvents(Boolean... boo){
		if (boo[0]){ //SimpleClans
			newEvent(EvtClanCreate.class, CreateClanEvent.class, 1,"Clan create", "clan create");
			newEvent(EvtClanDisband.class, DisbandClanEvent.class, 1, "Clan disband", "clan disband" );
			newEvent(EvtAllyClanAdd.class, AllyClanAddEvent.class, 1, "Ally clan add", "ally clan add" );
			newEvent(EvtAllyClanRemove.class, AllyClanRemoveEvent.class, 1, "Ally clan remove", "ally clan remove");
			newEvent(EvtRivalClanAdd.class, RivalClanAddEvent.class, 1,"Rival clan add", "rival clan add");
			newEvent(EvtRivalClanRemove.class, RivalClanRemoveEvent.class, 1,"Rival clan remove",  "rival clan remove");
			newEvent(EvtPromotePlayerClan.class, PlayerPromoteEvent.class, 1, "Clan promote player", "[clan] promote player");
			newEvent(EvtDemotePlayerClan.class, PlayerDemoteEvent.class, 1, "Clan demote player", "[clan] demote player");
		} 
		if (boo[1]){
			config = new LegendConfig(instance);
			Bukkit.getServer().getPluginManager().registerEvents(new TagChat(config), instance);
			newEvent(EvtLCChat.class, ChatMessageEvent.class, 1, "Legendchat chat", "l[egend]c[hat] chat" );
			newEvent(EvtTellChat.class, PrivateMessageEvent.class, 1, "Legendchat tell", "l[egend]c[hat] tell");
		}
		//General

		newEvent(EvtAnvilCombine.class, AnvilCombineEvent.class, 1, "Anvil conbine", "anvil [item] (combine|merge)");
		newEvent(EvtAnvilRename.class, AnvilRenameEvent.class, 1, "Anvil rename", "anvil [item] rename");
		newEvent(EvtItemDamage.class, PlayerItemDamageEvent.class, 1, "Item damage", "[player] item damage");
		newEvent(EvtInventoryMove.class, InventoryMoveEvent.class, 1, "Inventory move", "inventory move");
		newEvent(EvtInventoryDrag.class, InventoryDragEvent.class, 1, "Inventory drag", "inventory drag");
		newEvent(SimpleEvent.class, GUIActionEvent.class, 1, "GUI click", "gui (action|click)");
		//Skript.registerEvent("Player starts move", EvtPlayerStartsMove.class, PlayerStartsMoveEvent.class, "player start[s] (mov(e|ing)|walk[ing])");
		//Skript.registerEvent("Player stops move", EvtPlayerStopsMove.class, PlayerStopsMoveEvent.class, "player stop[s] (mov(e|ing)|walk[ing])");
		if (Skript.classExists("org.bukkit.event.entity.SpawnerSpawnEvent")){
			newEvent(EvtSpawnerSpawn.class, SpawnerSpawnEvent.class, 1, "Spawner spawn", "spawner spawn");
		}
	}
	public void registerEventValues(Boolean... boo){
		if (boo[0]){
			EventValues.registerEventValue(CreateClanEvent.class, Player.class,
					new Getter<Player, CreateClanEvent>() {
						@Override
						public Player get(CreateClanEvent event) {
							return event.getClan().getLeaders().get(0).toPlayer();
						}
					}, 0);
			EventValues.registerEventValue(CreateClanEvent.class, Clan.class,
					new Getter<Clan, CreateClanEvent>() {
						@Override
						public Clan get(CreateClanEvent event) {
							return event.getClan();
						}
					}, 0);
			EventValues.registerEventValue(DisbandClanEvent.class, Clan.class,
					new Getter<Clan, DisbandClanEvent>() {
						@Override
						public Clan get(DisbandClanEvent event) {
							return event.getClan();
						}
					}, 0); 
			EventValues.registerEventValue(AllyClanAddEvent.class, Clan.class,
					new Getter<Clan, AllyClanAddEvent>() {
						@Override
						public Clan get(AllyClanAddEvent event) {
							return event.getClanFirst();
						}
					}, 0);;
			EventValues.registerEventValue(AllyClanRemoveEvent.class, Clan.class,
					new Getter<Clan, AllyClanRemoveEvent>() {
						@Override
						public Clan get(AllyClanRemoveEvent event) {
							return event.getClanFirst();
						}
					}, 0);
			EventValues.registerEventValue(RivalClanAddEvent.class, Clan.class,
					new Getter<Clan, RivalClanAddEvent>() {
						@Override
						public Clan get(RivalClanAddEvent event) {
							return event.getClanFirst();
						}
					}, 0);
			EventValues.registerEventValue(RivalClanRemoveEvent.class, Clan.class,
					new Getter<Clan, RivalClanRemoveEvent>() {
						@Override
						public Clan get(RivalClanRemoveEvent event) {
							return event.getClanFirst();
						}
					}, 0);
			EventValues.registerEventValue(PlayerPromoteEvent.class, Player.class,
					new Getter<Player, PlayerPromoteEvent>() {
						@Override
						public Player get(PlayerPromoteEvent event) {
							return event.getClanPlayer().toPlayer();
						}
					}, 0);
			EventValues.registerEventValue(PlayerDemoteEvent.class, Player.class,
					new Getter<Player, PlayerDemoteEvent>() {
						@Override
						public Player get(PlayerDemoteEvent event) {
							return event.getClanPlayer().toPlayer();
						}
					}, 0);	
			
		}
		if (boo[1]){
			EventValues.registerEventValue(ChatMessageEvent.class, Player.class,
					new Getter<Player, ChatMessageEvent>() {
						@Override
						public Player get(ChatMessageEvent event) {
							return event.getSender();
						}
					}, 0);
			EventValues.registerEventValue(PrivateMessageEvent.class, CommandSender.class,
					new Getter<CommandSender, PrivateMessageEvent>() {
						@Override
						public CommandSender get(PrivateMessageEvent event) {
							return (CommandSender) event.getSender();
						}
					}, 0);
		}
		//General
		EventValues.registerEventValue(InventoryMoveEvent.class, Player.class, 
				new Getter<Player, InventoryMoveEvent>(){
					@Override
					public Player get(InventoryMoveEvent event) {
						return event.getPlayer();
					}
				}, 0);
		EventValues.registerEventValue(InventoryMoveEvent.class, ItemStack.class, 
				new Getter<ItemStack, InventoryMoveEvent>(){
					@Override
					public ItemStack get(InventoryMoveEvent event) {
						return event.getItem();
					}
				}, 0);
		EventValues.registerEventValue(InventoryMoveEvent.class, String.class, 
				new Getter<String, InventoryMoveEvent>(){
					@Override
					public String get(InventoryMoveEvent event) {
						return event.getClickType();
					}
				}, 0);
		EventValues.registerEventValue(GUIActionEvent.class, Player.class,
				new Getter<Player, GUIActionEvent>() {
					@Override
					public Player get(GUIActionEvent event) {
						return (Player) event.getClickEvent().getWhoClicked();
					}
				}, 0);	
		EventValues.registerEventValue(GUIActionEvent.class, Inventory.class,
				new Getter<Inventory, GUIActionEvent>() {
					@Override
					public Inventory get(GUIActionEvent event) {
						return event.getClickEvent().getInventory();
					}
				}, 0);
		EventValues.registerEventValue(GUIActionEvent.class, Integer.class,
				new Getter<Integer, GUIActionEvent>() {
					@Override
					public Integer get(GUIActionEvent event) {
						return event.getClickEvent().getSlot();
					}
				}, 0);
		EventValues.registerEventValue(GUIActionEvent.class, ItemStack.class,
				new Getter<ItemStack, GUIActionEvent>() {
					@Override
					public ItemStack get(GUIActionEvent event) {
						return event.getClickEvent().getCursor();
					}
				}, 0);

		EventValues.registerEventValue(GUIActionEvent.class, ClickType.class,
				new Getter<ClickType, GUIActionEvent>() {
					@Override
					public ClickType get(GUIActionEvent event) {
						return event.getClickEvent().getClick();
					}
				}, 0);
		EventValues.registerEventValue(GUIActionEvent.class, String.class,
				new Getter<String, GUIActionEvent>() {
					@Override
					public String get(GUIActionEvent event) {
						return EnumType.toString(event.getClickEvent().getClick());
					}
				}, 0);
		EventValues.registerEventValue(InventoryDragEvent.class, Inventory.class,
				new Getter<Inventory, InventoryDragEvent>() {
					@Override
					public Inventory get(InventoryDragEvent event) {
						return event.getInventory();
					}
				}, 0);
		EventValues.registerEventValue(InventoryDragEvent.class, Player.class,
				new Getter<Player, InventoryDragEvent>() {
					@Override
					public Player get(InventoryDragEvent event) {
						return (Player) event.getWhoClicked();
					}
				}, 0);
		EventValues.registerEventValue(InventoryDragEvent.class, ItemStack.class,
				new Getter<ItemStack, InventoryDragEvent>() {
					@Override
					public ItemStack get(InventoryDragEvent event) {
						return event.getOldCursor();
					}
				}, 0);

		EventValues.registerEventValue(InventoryDragEvent.class, String.class,
				new Getter<String, InventoryDragEvent>() {
					@Override
					public String get(InventoryDragEvent event) {
						return EnumType.toString(event.getType());
					}
				}, 0);
		
		EventValues.registerEventValue(AnvilCombineEvent.class, Inventory.class,
				new Getter<Inventory, AnvilCombineEvent>() {
					@Override
					public Inventory get(AnvilCombineEvent event) {
						return event.getInventory();
					}
				}, 0);
		EventValues.registerEventValue(AnvilCombineEvent.class, Player.class,
				new Getter<Player, AnvilCombineEvent>() {
					@Override
					public Player get(AnvilCombineEvent event) {
						return event.getPlayer();
					}
				}, 0);
		EventValues.registerEventValue(AnvilRenameEvent.class, ItemStack.class,
				new Getter<ItemStack, AnvilRenameEvent>() {
					@Override
					public ItemStack get(AnvilRenameEvent event) {
						return event.getInventory().getItem(0);
					}
				}, 0);
		EventValues.registerEventValue(AnvilRenameEvent.class, String.class,
				new Getter<String, AnvilRenameEvent>() {
					@Override
					public String get(AnvilRenameEvent event) {
						return event.getInventory().getItem(2).getItemMeta().getDisplayName();
					}
				}, 0);
		EventValues.registerEventValue(AnvilRenameEvent.class, Player.class,
				new Getter<Player, AnvilRenameEvent>() {
					@Override
					public Player get(AnvilRenameEvent event) {
						return event.getPlayer();
					}
				}, 0);
		EventValues.registerEventValue(PlayerItemDamageEvent.class, Player.class,
				new Getter<Player, PlayerItemDamageEvent>() {
					@Override
					public Player get(PlayerItemDamageEvent event) {
						return event.getPlayer();
					}
				}, 0);
		EventValues.registerEventValue(PlayerItemDamageEvent.class, ItemStack.class,
				new Getter<ItemStack, PlayerItemDamageEvent>() {
					@Override
					public ItemStack get(PlayerItemDamageEvent event) {
						return event.getItem();
					}
				}, 0);
		EventValues.registerEventValue(PlayerStartsMoveEvent.class, Player.class,
				new Getter<Player, PlayerStartsMoveEvent>() {
					@Override
					public Player get(PlayerStartsMoveEvent event) {
						return event.getPlayer();
					}
				}, 0);
		EventValues.registerEventValue(PlayerStartsMoveEvent.class, Location.class,
				new Getter<Location, PlayerStartsMoveEvent>() {
					@Override
					public Location get(PlayerStartsMoveEvent event) {
						return event.getStartLocation();
					}
				}, 0);
		EventValues.registerEventValue(PlayerStopsMoveEvent.class, Player.class,
				new Getter<Player, PlayerStopsMoveEvent>() {
					@Override
					public Player get(PlayerStopsMoveEvent event) {
						return event.getPlayer();
					}
				}, 0);
		EventValues.registerEventValue(PlayerStopsMoveEvent.class, Player.class,
				new Getter<Player, PlayerStopsMoveEvent>() {
					@Override
					public Player get(PlayerStopsMoveEvent event) {
						return event.getPlayer();
					}
				}, 0);

		if (Skript.classExists("org.bukkit.event.entity.SpawnerSpawnEvent")){
			EventValues.registerEventValue(SpawnerSpawnEvent.class, Block.class,
					new Getter<Block, SpawnerSpawnEvent>() {
						@Override
						public Block get(SpawnerSpawnEvent event) {
							return event.getSpawner().getBlock();
						}
					}, 0);
			EventValues.registerEventValue(SpawnerSpawnEvent.class, Entity.class,
					new Getter<Entity, SpawnerSpawnEvent>() {
						@Override
						public Entity get(SpawnerSpawnEvent event) {
							return event.getEntity();
						}
					}, 0);
		}	
		
	}
	public void registerConditions(Boolean... boo){
		if (boo[0]){
			newCondition(CondLeader.class, 1, "%player% is leader of his clan", "%player% is(n't| not) leader of his clan");
			newCondition(CondClan.class, 1, "%player% (has|have) [a] clan", "%player% (hasn't|doesn't have) [a] clan");
		}
		if (boo[1]){
			newCondition(CondMuted.class, 1, "%player% is muted", "%player% is(n't| not) muted");
			newCondition(CondCanSayChannel.class, 1, "%player% can (see|say in) [channel] %channel%", "%player% can't (see|say in) [channel] %channel%");
		}
		if (boo[2]){
			newCondition(CondMarried.class, 1, "%player% is married", "%player% is(n't| not) married");
		}
		//General
		newCondition(CondIsBlockType.class, 4, "%itemstack% is [a] (solid|transparent|flammable|occluding) block", "%itemstack% is(n't| not) [a] (solid|transparent|flammable|occluding) block");
		newCondition(CondCanEat.class, 1, "%itemstack% is edible", "%itemstack% is(n't| not) edible");
		newCondition(CondHasGravity.class, 1, "%itemstack% has gravity", "%itemstack% has(n't| not) gravity");
		//1.5
		newCondition(CondHasCustom.class, 1,"%itemstack% has [a] custom enchantment [%-customenchantment%]", "%itemstack% has(n't| not) [a] custom enchantment [%-customenchantment%]");
		//1.6.2
		newCondition(CondRegexMatch.class, 1, "%string% [regex] matches %string%", "%string% [regex] does(n't| not) match %string%");
		newCondition(CondHasGUI.class, 1, "%player% has [a] gui", "slot %number% of %player% is a gui","%player% does(n't| not) have [a] gui", "slot %number% of %player% is(n't| not) [a] gui");
		//1.6.8
		newCondition(CondIsMobType.class, 1,"%livingentities% (is|are) [a] (0多ostile|1好eutral|2如assive) [mob]", "%livingentities% (is|are)(n't| not) [a] (0多ostile|1好eutral|2如assive) [mob]");
		newCondition(CondIsAgeable.class, 1, "%entities% ((is|are) ageable|can grow up)", "%entities% ((is|are)(n't| not) ageable|can(n't| not) grow up)");
		newCondition(CondIsTameable.class, 1, "%entities% (is|are) tameable", "%entities% (is|are)(n't| not) tameable");
		
		
	}
	public void registerEffects(Boolean... boo){
		if (boo[0]){
			newEffect(EffCreateClan.class, 1, "create [a] [new] clan named %string% with tag %string% (to|for) %player%");
			newEffect(EffRemoveFromClan.class, 1, "(remove|kick) %player% from his clan", "[make] %player% resign from his clan");
			newEffect(EffPlacePlayerInClan.class, 1, "(add|place) %player% (to|in) [clan] %clan%", "[make] %player% join to %clan%");
			newEffect(EffInvitePlayerToClan.class, 1, "[make] %player% [a] invite %player% to his clan", "send invite of clan from %player% to %player%");
			newEffect(EffDisbandClan.class, 1, "disband [clan] %clan%");
			newEffect(EffVerifyClan.class, 1, "verify [clan] %clan%");
		}
		if (boo[1]){
			newEffect(EffMakeTell.class, 1, "[make] %player% [send] tell %string% to %player%");
			newEffect(EffMute.class, 1, "mute %player% [for %number% minute[s]]");
			newEffect(EffUnMute.class, 1, "unmute %player%");
			newEffect(EffMakeSay.class, 1, "make %player% say %string% in [channel] %channel%");
			
		}
		if (boo[2]){
			newEffect(EffMarry.class, 1, "marry %player% with %player%");
			newEffect(EffSendMarry.class, 1, "[make] %player% invite %player% to marry", "send invite of marry from %player% to %player%");
			newEffect(EffDivorce.class, 1, "divorce %player%", "make %player% divorce");
			
		}
		if (boo[4]){
			newEffect(EffClaimLand.class, 1, "claim land[lord] at %location/chunk% for %player%");
			newEffect(EffUnclaimLand.class, 1, "unclaim land[lord] at %location/chunk%");
		}
		//General
		if (TuSKe.hasSupport()){
			newEffect(EffSaveData.class, 1, "save [player] data of %player%");
			newEffect(EffMakeDrop.class, 1, "(make|force) %player% drop[s] %itemstack% [from his inventory]");
		} else
			TuSKe.log("The version of your server it isn't supported for some expressions: " + Bukkit.getServer().getClass().getPackage().getName().split(".v")[1], Level.WARNING);
		newEffect(EffCancelDrop.class, 1, "cancel [the] drops [of (inventory|[e]xp[periences])]");
		newEffect(EffPushBlock.class, 1, "move %block% to %direction%");
		// 1.5.3
		newEffect(EffRegisterEnchantment.class, 1, "(register|create) [a] [new] [custom] enchantment with id [name] %string%");
		newEffect(EffUnregisterEnchantment.class, 1, "unregister [the] [custom] enchantment %customenchantment%");
		//1.6.2		
		String cr = "string/" + Classes.getExactClassInfo(ClickType.class).getCodeName();
		newEffect(EffFormatGUI.class, 1,
			"(format|create|make) [a] gui slot %numbers% of %players% with %itemstack% [to [do] nothing]",
			"(format|create|make) [a] gui slot %numbers% of %players% with %itemstack% to (1圭lose|2她pen %-inventoy%) [(using|with) %-" + cr + "% [(button|click|action)]]",
			"(format|create|make) [a] gui slot %numbers% of %players% with %itemstack% to [(1圭lose|2她pen %-inventoy%) then] (run|exe[cute]) %commandsender% command %string% [(using|with) perm[ission] %-string%][[(,| and)] (using|with) %-" + cr + "% [(button|click|action)]][[(,| and)] (using|with) cursor [item] %-itemstack%]",
			"(format|create|make) [a] gui slot %numbers% of %players% with %itemstack% to [(1圭lose|2她pen %-inventoy%) then] (run|exe[cute]) function <(.+)>\\([%-objects%[, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%]]\\)[[(,| and)] (using|with) %-" + cr + "% [(button|click|action)]][[(,| and)] (using|with) cursor [item] %-itemstack%]",
			"(format|create|make) [a] gui slot %numbers% of %players% with %itemstack% to (run|exe[cute]) [gui [click]] event");
		newEffect(EffUnformatGUI.class, 1, "(unformat|remove|clear|reset) [the] gui slot %numbers% of %players%", "(unformat|remove|clear|reset) [all] [the] gui slots of %players%");
		newEffect(EffEvaluateFunction.class, 1, "evaluate function %strings% [with %-objects%[, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%]]");
		//1.6.9
		newEffect(EffRegisterPermission.class, 1,"(register|create) master permission %string%");
		newEffect(EffExecutePermission.class, 1, "[execute] [the] command %strings% by %players% with perm[ission] %string%", "[execute] [the] %players% command %strings% with perm[ission] %string%", "(let|make) %players% execute [[the] command] %strings% with perm[ission] %string%");
		//1.7.1
		Skript.registerEffect(EffRegisterRecipe.class, 
				"(create|register) [new] [custom] shaped recipe with (return|result) %itemstack% using [ingredients] %itemstacks% [with shape %strings%]",
				"(create|register) [new] [custom] shapeless recipe with (return|result) %itemstack% using [ingredients] %itemstacks%",
				"(create|register) [new] [custom] furnace recipe with (return|result) %itemstack% using [source] %itemstack% [[and] with experience %-number%]");
	}
	public void registerExpressions(Boolean... boo){
		if (boo[0]){
			newPropertyExpression(ExprPlayerClan.class, 1, "clan", "player");
			newPropertyExpression(ExprClanTag.class, 1, "[clan] tag", "clan");
			newPropertyExpression(ExprKDRofPlayer.class, 1, "clan K[ill ]D[eath ]R[atio]", "player");
			newPropertyExpression(ExprClanMembers.class, 1, "clan members", "clan");
			newPropertyExpression(ExprKillsOfPlayer.class, 1, "clan (1字ival|2好eutral|3圭ivilian) kills", "clan");
			newPropertyExpression(ExprDeathsOfPlayer.class, 1, "clan deaths", "player");
			newSimpleExpression(ExprClanFromTag.class, 1, "clan from tag %string%");
			newSimpleExpression(ExprClanTwo.class, 0, "[event-]clan-two");
		}
		if (boo[1]){
			newSimpleExpression(ExprLegendchatChannel.class, 0, "l[legend]c[hat] channel");
			newSimpleExpression(ExprLegendchatChannel.class, 0, "l[egend]c[hat] channel");
			newSimpleExpression(ExprLegendchatMessage.class, 0, "l[egend]c[hat] message");
			newSimpleExpression(ExprTellMessage.class, 0, "tell message");
			newSimpleExpression(ExprTellReceiver.class, 0, "[tell] receiver");
			newPropertyExpression(ExprTagChat.class, 1, "[chat] tag %string%", "player");
			newPropertyExpression(ExprPlayerTags.class, 1, "[chat] tags", "player");
			newPropertyExpression(ExprMuteLeftTime.class, 1, "mute (left|remaining) time", "player");
			newPropertyExpression(ExprDefaultChannel.class, 1, "default channel", "player");
			newPropertyExpression(ExprSpyState.class, 1, "spy state", "player");
			newPropertyExpression(ExprHideState.class, 1, "hide state", "player");
		}
		if (boo[2]){
			newPropertyExpression(ExprPartnerOf.class, 1, "partner", "player");
			newPropertyExpression(ExprGenderOf.class, 1, "gender", "player");
			newPropertyExpression(ExprMarryHome.class, 1, "marry home", "player");
		}
		if(boo[3]){
			newPropertyExpression(ExprPlayerVersion.class, 1, "(mc|minecraft) version", "player");			
		}
		if (boo[4]){
			newPropertyExpression(ExprLandOwner.class, 1, "land[lord] owner", "landclaim");
			newPropertyExpression(ExprLandFriends.class, 1, "land[lord] friends", "landclaim");
			newPropertyExpression(ExprLandLocation.class, 1, "land[lord] location", "landclaim");
			newPropertyExpression(ExprLandClaimsOf.class, 1, "land[lord] claims", "player");
			newSimpleExpression(ExprLandflag.class, 1, "landflag %landflag% of %landclaim% for (1圯veryone|2圩riends)");
			newSimpleExpression(ExprLandClaimAt.class, 1, "land[lord] claim at %location/chunk%");
		}
		//General stuffs
		if (TuSKe.hasSupport()){
			newPropertyExpression(ExprOfflineData.class, 1, "player data", "offlineplayer");
			newPropertyExpression(ExprExpOf.class, 1, "[total] [e]xp", "player");
			newPropertyExpression(ExprLastLogin.class, 1, "last login", "player");
			newPropertyExpression(ExprFirstLogin.class, 1, "first login", "player");
			newPropertyExpression(ExprOnlineTime.class, 1,"online time", "player");
			newPropertyExpression(ExprLastDamage.class, 1, "last damage", "livingentity");
			newPropertyExpression(ExprLastDamageCause.class, 1, "last damage cause", "livingentity");
			newPropertyExpression(ExprHorseStyle.class, 1, "horse style", "entity");
			newPropertyExpression(ExprHorseColor.class, 1, "horse color", "entity");
			newPropertyExpression(ExprHorseVariant.class, 1, "horse variant", "entity");
			newPropertyExpression(ExprRabbitType.class, 1, "rabbit type", "entity");
			newPropertyExpression(ExprCatType.class, 1, "(cat|ocelot) type", "entity");
			newPropertyExpression(ExprRecipesOf.class, 1, "[all] recipes", "itemstack");
			newPropertyExpression(ExprItemsOfRecipe.class, 1, "[all] ingredients", "recipe");
			newPropertyExpression(ExprResultOfRecipe.class, 1, "result item", "itemstacks/recipe");
			newSimpleExpression(ExprAlphabetOrder.class, 1, "alphabetical order of %objects%");
			newSimpleExpression(ExprHighiestBlock.class, 1, "highest block at %location%");
			newSimpleExpression(ExprAnvilItem.class, 0, "[event-]item-(one|two|result|three)");
			newSimpleExpression(ExprInventoryMoveInv.class, 0, "[event-]inventory-(one|two)");
			newSimpleExpression(ExprInventoryMoveSlot.class, 0, "[event-]slot-(one|two)");
			newSimpleExpression(ExprDropsOfBlock.class, 1, "drops of %block% [(with|using) %-itemstack%]", "%block%'[s] drops [(with|using) %-itemstack%]");
			newSimpleExpression(ExprListPaged.class, 1, "page %number% of %objects% with %number% lines");
			//1.1
			newPropertyExpression(ExprMaxDurability.class, 1, "max durability", "itemstack");
			//1.5
			newPropertyExpression(ExprAllCustomEnchants.class, 1, "[all] custom enchantments", "itemstack");
			newSimpleExpression(ExprLevelOfCustomEnchant.class, 1, "level of [custom enchantment] %customenchantment% of %itemstack%");
			newSimpleExpression(ExprItemCustomEnchant.class, 1, "%itemstack% with custom enchantment[s] %customenchantments%");
			//1.5.3
			newPropertyExpression(ExprMaxLevel.class, 1, "max level", "customenchantment");
			newPropertyExpression(ExprRarity.class, 1, "rarity", "customenchantment");
			newPropertyExpression(ExprLoreName.class, 1, "[lore] name", "customenchantment");
			newPropertyExpression(ExprLeatherColor.class, 1, "[leather] (0字ed|1夙reen|2在lue) colo[u]r", "-itemstacks/colors");
			newSimpleExpression(ExprEnabled.class, 1, "enabled for %customenchantment%");
			newSimpleExpression(ExprItemDamage.class, 0, "item damage");
			newSimpleExpression(ExprAcceptedItems.class, 1, "accepted items for %customenchantment%");
			newSimpleExpression(ExprCEConflicts.class, 1, "conflicts for %customenchantment%");
			//1.5.4
			newPropertyExpression(ExprRGBColor.class, 1, "R[ed, ]G[reen and ]B[blue] [colo[u]r[s]]", "-itemstacks/colors");
			//1.5.7
			newSimpleExpression(ExprServerOnlineTime.class, 1, "[the] online time of server", "server'[s] online time");
			//1.5.9
			newPropertyExpression(ExprLanguage.class, 1, "(locale|language)", "player");
			newPropertyExpression(ExprLocalNameOf.class, 1, "[json] client id" , "object");
			newSimpleExpression(ExprInventoryMoveInv.class, 0, "[event-]inventory-(one|two)");
			newSimpleExpression(ExprInventoryMoveSlot.class, 0, "[event-]slot-(one|two)");
			newSimpleExpression(ExprEvaluateFunction.class, 1, "result of function %string% [with %-objects%[, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%]]");
			// 1.6.6
			newSimpleExpression(ExprDraggedSlots.class, 0, "[event-]dragged(-| )slots");
			newSimpleExpression(ExprDraggedItem.class, 0, "[event-][old(-| )]dragged(-| )item");
			newSimpleExpression(ExprDroppedExp.class, 1, "[the] dropped [e]xp[erience] [orb[s]]");
			//1.6.8
			newSimpleExpression(ExprSplitCharacter.class, 1, "split %string% (with|by|using) %number% [char[aracter][s]]", "%string% [split] (with|by|using) %number% [char[aracter][s]]");
			newPropertyExpression(ExprLastColor.class, 1, "last color", "string");
			newSimpleExpression(ExprAllRecipes.class, 1, "[all] [registred] recipes");
			//1.6.9
			newSimpleExpression(ExprVirtualInv.class, 1, "virtual %inventorytype% inventory [with size %-number%] [(named|with (name|title)) %-string%]");
			newSimpleExpression(ExprCommandInfo.class, 7, 
				"[the] description of command %string%", "command %string%'[s] description",
				"[the] main [command] of command %string%", "command %string%'[s] main [command]",
				"[the] permission of command %string%", "command %string%'[s] permission",
				"[the] permission message of command %string%", "command %string%'[s] permission message",
				"[the] plugin [owner] of command %string%", "command %string%'[s] plugin [owner]",
				"[the] usage of command %string%", "command %string%'[s] usage",
				"[the] file [location] of command %string%", "command %string%'[s] file location");
			newSimpleExpression(ExprAllCommand.class, 2, 
				"[all] commands",
				"[the] aliases of command %string%", "command %string%'[s] aliases");
			//1.7
			newSimpleExpression(ExprRegexSplit.class, 1, "regex split %string% (with|using) [pattern] %string%");
			newSimpleExpression(ExprRegexReplace.class, 1, "regex replace [all] [pattern] %string% with [group[s]] %string% in %string%");
			//1.7.1
			newSimpleExpression(ExprUUIDOfflinePlayer.class, 1, "offline player from [uuid] %string%");
			newSimpleExpression(ExprParseRegexError.class, 1, "[last] regex [parser] error");
			
			
		}
		
	}
	public void registerClassInfos(Boolean... boo){
		if (boo[0]){	
			Classes.registerClass(new ClassInfo<Clan>(Clan.class, "clan").user("clan").name("clan").defaultExpression(new EventValueExpression(Clan.class)).parser(new Parser<Clan>() {
				@Override
			    @Nullable
			    public Clan parse(String s, ParseContext context) {
						return null;
			    }
			    @Override
			    public String toString(Clan c, int flags) {
			    	return c.getName().toString();
			    }
			   @Override
			   public String toVariableNameString(Clan c) {
			    	return c.toString().toLowerCase();
			   	}
			    @Override
			    public String getVariableNamePattern() {
			        return ".+"; 
			    }
			}));			
		}
		if (boo[1]){
			Classes.registerClass(new ClassInfo<Channel>(Channel.class, "channel").user("channel").name("Channel").parser(new Parser<Channel>() {
				@Override
			    @Nullable
			    public Channel parse(String s, ParseContext context) {
					ChannelManager cm = Legendchat.getChannelManager();
					if (cm.existsChannel(s.toLowerCase()))
						return cm.getChannelByName(s.toLowerCase());
					return null;
			    }
			    @Override
			    public String toString(Channel c, int flags) {
			    	return c.getName().toLowerCase();
			    }
			   @Override
			   public String toVariableNameString(Channel c) {
			    	return c.toString().toLowerCase();
			   	}
			    @Override
			    public String getVariableNamePattern() {
			        return ".+"; 
			    }
			}));
			Comparators.registerComparator(Channel.class, Channel.class, new Comparator(){

				@Override
				public Relation compare(Object arg0, Object arg1) {
					return Relation.get(arg0.equals(arg1));
				}

				@Override
				public boolean supportsOrdering() {
					return true;
				}
			});
			
		}
		if (boo[2]){
			new EnumType(Gender.class, "gender", "gender");
		}
		if (boo[4]){
			Classes.registerClass(new ClassInfo<LowOwnedLand>(LowOwnedLand.class, "landclaim").user("land ?claim").name("Land Claim").defaultExpression(new EventValueExpression(LowOwnedLand.class)).parser(new Parser<LowOwnedLand>(){

				@Override
				public String getVariableNamePattern() {
					return ".+";
				}

				@Override
				@Nullable
				public LowOwnedLand parse(String s, ParseContext arg1) {
					return null;
				}

				@Override
				public String toString(LowOwnedLand ol, int arg1) {
					return String.valueOf(ol.getId());
				}

				@Override
				public String toVariableNameString(LowOwnedLand ol) {
					return "ownedland:" + ol.getId();
				}
				
			}));
			final Map<String, String> fixflags = new HashMap<String, String>();
			for (String key : Landlord.getInstance().getFlagManager().getRegisteredFlags().keySet())
				fixflags.put(Landlord.getInstance().getFlagManager().getRegisteredFlags().get(key).getDisplayName().toUpperCase(), key);
			Classes.registerClass(new ClassInfo<Landflag>(Landflag.class, "landflag").user("land ?flag").name("Land Flag").defaultExpression(new EventValueExpression(Landflag.class)).parser(new Parser<Landflag>(){

				@Override
				public String getVariableNamePattern() {
					return ".+";
				}

				
				@Override
				@Nullable
				public Landflag parse(String s, ParseContext arg1) {
					if (fixflags.containsKey(s.toUpperCase()))
						return Landlord.getInstance().getFlagManager().getRegisteredFlags().get(fixflags.get(s.toUpperCase()));
					return null;
				}

				@Override
				public String toString(Landflag lf, int arg1) {
					return lf.getDisplayName().toLowerCase();
				}

				@Override
				public String toVariableNameString(Landflag lf) {
					return "ownedland:" + lf.getDisplayName().toLowerCase();
				}
				
			}));
		}
		//Genral types
		if (Classes.getExactClassInfo(Recipe.class) == null){
			Classes.registerClass(new ClassInfo<Recipe>(Recipe.class, "recipe").user("recipe").name("Recipe").defaultExpression(new EventValueExpression(Recipe.class)).parser(new Parser<Recipe>(){
	
				@Override
				@Nullable
				public Recipe parse(String s, ParseContext arg1) {
					return null;
				}
	
				@Override
				public String toString(Recipe r, int arg1) {
					if (r instanceof ShapelessRecipe)
						return "shapeless recipe";
					else if (r instanceof ShapedRecipe)
						return "shaped recipe";
					else if (r instanceof FurnaceRecipe)
						return "furnace recipe";
					return null;
				}
	
				@Override
				public String toVariableNameString(Recipe r) {
					
					if (r instanceof ShapelessRecipe)
						return "shapelessrecipe:" + r.toString().split("@")[1];
					else if (r instanceof ShapedRecipe)
						return "shapedrecipe:" + r.toString().split("@")[1];
					else if (r instanceof FurnaceRecipe)
						return "furnacerecipe:" + r.toString().split("@")[1];
					return null;
				}
				
				@Override
				public String getVariableNamePattern() {
					return ".+";
				}
				
			}));
		}
		if (Classes.getExactClassInfo(InventoryType.class) == null){
			new EnumType(InventoryType.class, "inventorytype", "inventory ?type");
		}
		if (Classes.getExactClassInfo(ClickType.class) == null){
			new EnumType(ClickType.class, "clicktype", "click ?(action|type)?");					
		} 
		Classes.registerClass(new ClassInfo<Regex>(Regex.class, "regex").user("reg(ular )?ex(pression)?").name("Regular expression").defaultExpression(new EventValueExpression(Regex.class)).parser(new Parser<Regex>(){

			@Override
			@Nullable
			public Regex parse(String s, ParseContext arg1) {			
				if (arg1 == ParseContext.COMMAND){
					Regex reg = new Regex(s);
					if (reg.isPatternParsed())
						return reg;
				}
				return null;
			}
			@Override
			public boolean canParse(ParseContext pc){
				return pc == ParseContext.COMMAND;
			}

			@Override
			public String toString(Regex reg, int arg1) {
				return reg.getRegex();
			}

			@Override
			public String toVariableNameString(Regex reg) {
				return reg.getRegex();
			}
			
			@Override
			public String getVariableNamePattern() {
				return ".+";
			}
			
		}));
		Classes.registerClass(new ClassInfo<CEnchant>(CEnchant.class, "customenchantment").user("custom ?enchantment").name("Custom Enchantment").defaultExpression(new EventValueExpression(CEnchant.class)).parser(new Parser<CEnchant>(){

			@Override
			@Nullable
			public CEnchant parse(String s, ParseContext arg1) {
				int l = 0;
				if (s.matches(".*\\s{1,}\\d{1,}$")){
					l = Integer.valueOf(s.split(" ")[s.split(" ").length-1]);
					s = s.replace(" " + l,"");
				}
				return (EnchantManager.isCustomByID(s)) ? new CEnchant(CustomEnchantment.getByID(s), l) : null;
			}

			@Override
			public String toString(CEnchant ce, int arg1) {
				return ce.getEnchant().getId();
			}

			@Override
			public String toVariableNameString(CEnchant ce) {
				return "ce:" + ce.getEnchant().getId();
			}
			
			@Override
			public String getVariableNamePattern() {
				return ".+";
			}
			
		}));
	}

	public <E extends Expression<T>, T> void newPropertyExpression(Class<E> c, int amount, String property, String from){
		if (instance.getConfig().isSet("disable." + c.getSimpleName()) && instance.getConfig().getBoolean("disable." + c.getSimpleName()))
			return;
		Class<T> ret;
		try {
			ret = (Class<T>) c.newInstance().getReturnType();
		} catch (Exception e) {
			TuSKe.log("Couldn't register the expression '" + property + " of " + from + "'. Error message: " + e.getMessage(), Level.WARNING);
			return;
		}
		expr += amount;
		Skript.registerExpression(c, ret, ExpressionType.PROPERTY, "[the] " + property + " of %" + from + "%", "%" + from + "%'[s] " + property);
		
	}
	public <E extends Expression<T>, T> void newSimpleExpression(Class<E> c, int amount, String... syntax){
		if (instance.getConfig().isSet("disable." + c.getSimpleName()) && instance.getConfig().getBoolean("disable." + c.getSimpleName()))
			return;
		Class<T> ret;
		try {
			ret = (Class<T>) c.newInstance().getReturnType();
		} catch (Exception e) {
			TuSKe.log("Couldn't register the expression '" + syntax[0] + "'. Error message: " + e.getMessage(), Level.WARNING);
			return;
		}
		expr += amount;
		Skript.registerExpression(c, ret, ExpressionType.SIMPLE, syntax);
		
	}
	public <E extends Condition> void newCondition(Class<E> c, int amount, String... syntax){
		if (instance.getConfig().isSet("disable." + c.getSimpleName()) && instance.getConfig().getBoolean("disable." + c.getSimpleName()))
			return;
		cond+= amount;
		Skript.registerCondition(c, syntax);
	}
	public <E extends Effect> void newEffect(Class<E> c, int amount, String... syntax){
		if (instance.getConfig().isSet("disable." + c.getSimpleName()) && instance.getConfig().getBoolean("disable." + c.getSimpleName()))
			return;
		eff+= amount;
		Skript.registerEffect(c, syntax);
	}
	public <E extends SkriptEvent> void newEvent(Class<E> c, Class<? extends Event> event, int amount, String name, String... syntax){
		if (instance.getConfig().isSet("disable." + event.getSimpleName()) && instance.getConfig().getBoolean("disable." + event.getSimpleName()))
			return;
		evt+= amount;
		Skript.registerEvent(name, c, event, syntax);
	}
}
