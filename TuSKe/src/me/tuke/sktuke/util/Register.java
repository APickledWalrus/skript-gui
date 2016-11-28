package me.tuke.sktuke.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.Landflag;
import com.jcdesimp.landlord.persistantData.LowOwnedLand;
import com.lenis0012.bukkit.marriage2.Gender;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import br.com.devpaulo.legendchat.api.events.PrivateMessageEvent;
import br.com.devpaulo.legendchat.channels.ChannelManager;
import br.com.devpaulo.legendchat.channels.types.Channel;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Comparator;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.Comparators;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.Experience;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Timespan;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.conditions.CondCanEat;
import me.tuke.sktuke.conditions.CondHasCustom;
import me.tuke.sktuke.conditions.CondHasGravity;
import me.tuke.sktuke.conditions.CondIsAgeable;
import me.tuke.sktuke.conditions.CondIsBlockType;
import me.tuke.sktuke.conditions.CondIsMobType;
import me.tuke.sktuke.conditions.CondIsTameable;
import me.tuke.sktuke.conditions.CondRegexMatch;
import me.tuke.sktuke.customenchantment.CEnchant;
import me.tuke.sktuke.customenchantment.CustomEnchantment;
import me.tuke.sktuke.customenchantment.EnchantManager;
import me.tuke.sktuke.customenchantment.EnchantConfig;
import me.tuke.sktuke.effects.EffCancelDrop;
import me.tuke.sktuke.effects.EffEvaluateFunction;
import me.tuke.sktuke.effects.EffExecutePermission;
import me.tuke.sktuke.effects.EffMakeDrop;
import me.tuke.sktuke.effects.EffPushBlock;
import me.tuke.sktuke.effects.EffRegisterEnchantment;
import me.tuke.sktuke.effects.EffRegisterPermission;
import me.tuke.sktuke.effects.EffRegisterRecipe;
import me.tuke.sktuke.effects.EffReloadEnchants;
import me.tuke.sktuke.effects.EffSaveData;
import me.tuke.sktuke.effects.EffUnregisterEnchantment;
import me.tuke.sktuke.events.EvtAnvilCombine;
import me.tuke.sktuke.events.EvtAnvilRename;
import me.tuke.sktuke.events.EvtInventoryDrag;
import me.tuke.sktuke.events.EvtInventoryMove;
import me.tuke.sktuke.events.EvtItemDamage;
import me.tuke.sktuke.events.EvtPlayerStartsMove;
import me.tuke.sktuke.events.EvtSpawnerSpawn;
import me.tuke.sktuke.events.customevent.AnvilCombineEvent;
import me.tuke.sktuke.events.customevent.AnvilRenameEvent;
import me.tuke.sktuke.events.customevent.DivorceEvent;
import me.tuke.sktuke.events.customevent.InventoryMoveEvent;
import me.tuke.sktuke.events.customevent.MarryEvent;
import me.tuke.sktuke.events.customevent.PlayerStartsMoveEvent;
import me.tuke.sktuke.events.customevent.PlayerStopsMoveEvent;
import me.tuke.sktuke.expressions.ExprAllCommand;
import me.tuke.sktuke.expressions.ExprAllRecipes;
import me.tuke.sktuke.expressions.ExprAlphabetOrder;
import me.tuke.sktuke.expressions.ExprAnvilItem;
import me.tuke.sktuke.expressions.ExprCatType;
import me.tuke.sktuke.expressions.ExprCommandInfo;
import me.tuke.sktuke.expressions.ExprDraggedItem;
import me.tuke.sktuke.expressions.ExprDropsOfBlock;
import me.tuke.sktuke.expressions.ExprEvaluateFunction;
import me.tuke.sktuke.expressions.ExprDraggedSlots;
import me.tuke.sktuke.expressions.ExprDroppedExp;
import me.tuke.sktuke.expressions.ExprExpOf;
import me.tuke.sktuke.expressions.ExprFirstLogin;
import me.tuke.sktuke.expressions.ExprHighiestBlock;
import me.tuke.sktuke.expressions.ExprHorseColor;
import me.tuke.sktuke.expressions.ExprHorseStyle;
import me.tuke.sktuke.expressions.ExprHorseVariant;
import me.tuke.sktuke.expressions.ExprInventoryMoveInv;
import me.tuke.sktuke.expressions.ExprInventoryMoveSlot;
import me.tuke.sktuke.expressions.ExprItemDamage;
import me.tuke.sktuke.expressions.ExprItemsOfRecipe;
import me.tuke.sktuke.expressions.ExprLanguage;
import me.tuke.sktuke.expressions.ExprLastColor;
import me.tuke.sktuke.expressions.ExprLastDamage;
import me.tuke.sktuke.expressions.ExprLastDamageCause;
import me.tuke.sktuke.expressions.ExprLastLogin;
import me.tuke.sktuke.expressions.ExprLeatherColor;
import me.tuke.sktuke.expressions.ExprListPaged;
import me.tuke.sktuke.expressions.ExprSplitCharacter;
import me.tuke.sktuke.expressions.ExprVirtualInv;
import me.tuke.sktuke.expressions.ExprLocalNameOf;
import me.tuke.sktuke.expressions.ExprMaxDurability;
import me.tuke.sktuke.expressions.ExprOfflineData;
import me.tuke.sktuke.expressions.ExprOnlineTime;
import me.tuke.sktuke.expressions.ExprPlayerVersion;
import me.tuke.sktuke.expressions.ExprRGBColor;
import me.tuke.sktuke.expressions.ExprRabbitType;
import me.tuke.sktuke.expressions.ExprRecipesOf;
import me.tuke.sktuke.expressions.ExprRegexReplace;
import me.tuke.sktuke.expressions.ExprRegexSplit;
import me.tuke.sktuke.expressions.ExprResultOfRecipe;
import me.tuke.sktuke.expressions.ExprServerOnlineTime;
import me.tuke.sktuke.expressions.customenchantments.ExprAcceptedItems;
import me.tuke.sktuke.expressions.customenchantments.ExprAllCustomEnchants;
import me.tuke.sktuke.expressions.customenchantments.ExprCEConflicts;
import me.tuke.sktuke.expressions.customenchantments.ExprEnabled;
import me.tuke.sktuke.expressions.customenchantments.ExprItemCustomEnchant;
import me.tuke.sktuke.expressions.customenchantments.ExprLevelOfCustomEnchant;
import me.tuke.sktuke.expressions.customenchantments.ExprLoreName;
import me.tuke.sktuke.expressions.customenchantments.ExprMaxLevel;
import me.tuke.sktuke.expressions.customenchantments.ExprRarity;
import me.tuke.sktuke.gui.CondHasGUI;
import me.tuke.sktuke.gui.EffFormatGUI;
import me.tuke.sktuke.gui.EffUnformatGUI;
import me.tuke.sktuke.gui.GUIActionEvent;
import me.tuke.sktuke.hooks.landlord.EffClaimLand;
import me.tuke.sktuke.hooks.landlord.EffUnclaimLand;
import me.tuke.sktuke.hooks.landlord.ExprLandClaimAt;
import me.tuke.sktuke.hooks.landlord.ExprLandClaimsOf;
import me.tuke.sktuke.hooks.landlord.ExprLandFriends;
import me.tuke.sktuke.hooks.landlord.ExprLandLocation;
import me.tuke.sktuke.hooks.landlord.ExprLandOwner;
import me.tuke.sktuke.hooks.landlord.ExprLandflag;
import me.tuke.sktuke.hooks.legendchat.CondCanSayChannel;
import me.tuke.sktuke.hooks.legendchat.CondMuted;
import me.tuke.sktuke.hooks.legendchat.EffMakeSay;
import me.tuke.sktuke.hooks.legendchat.EffMakeTell;
import me.tuke.sktuke.hooks.legendchat.EffMute;
import me.tuke.sktuke.hooks.legendchat.EffUnMute;
import me.tuke.sktuke.hooks.legendchat.EvtLCChat;
import me.tuke.sktuke.hooks.legendchat.EvtTellChat;
import me.tuke.sktuke.hooks.legendchat.ExprDefaultChannel;
import me.tuke.sktuke.hooks.legendchat.ExprHideState;
import me.tuke.sktuke.hooks.legendchat.ExprLegendchatChannel;
import me.tuke.sktuke.hooks.legendchat.ExprLegendchatMessage;
import me.tuke.sktuke.hooks.legendchat.ExprMuteLeftTime;
import me.tuke.sktuke.hooks.legendchat.ExprPlayerTags;
import me.tuke.sktuke.hooks.legendchat.ExprSpyState;
import me.tuke.sktuke.hooks.legendchat.ExprTagChat;
import me.tuke.sktuke.hooks.legendchat.ExprTellMessage;
import me.tuke.sktuke.hooks.legendchat.ExprTellReceiver;
import me.tuke.sktuke.hooks.marriage.CondMarried;
import me.tuke.sktuke.hooks.marriage.EffDivorce;
import me.tuke.sktuke.hooks.marriage.EffMarry;
import me.tuke.sktuke.hooks.marriage.EffSendMarry;
import me.tuke.sktuke.hooks.marriage.EvtDivorce;
import me.tuke.sktuke.hooks.marriage.EvtMarry;
import me.tuke.sktuke.hooks.marriage.ExprGenderOf;
import me.tuke.sktuke.hooks.marriage.ExprMarryHome;
import me.tuke.sktuke.hooks.marriage.ExprPartnerOf;
import me.tuke.sktuke.hooks.marriage.ExprThePartners;
import me.tuke.sktuke.hooks.simpleclans.CondClan;
import me.tuke.sktuke.hooks.simpleclans.CondLeader;
import me.tuke.sktuke.hooks.simpleclans.EffCreateClan;
import me.tuke.sktuke.hooks.simpleclans.EffDisbandClan;
import me.tuke.sktuke.hooks.simpleclans.EffInvitePlayerToClan;
import me.tuke.sktuke.hooks.simpleclans.EffPlacePlayerInClan;
import me.tuke.sktuke.hooks.simpleclans.EffRemoveFromClan;
import me.tuke.sktuke.hooks.simpleclans.EffVerifyClan;
import me.tuke.sktuke.hooks.simpleclans.EvtAllyClanAdd;
import me.tuke.sktuke.hooks.simpleclans.EvtAllyClanRemove;
import me.tuke.sktuke.hooks.simpleclans.EvtClanCreate;
import me.tuke.sktuke.hooks.simpleclans.EvtClanDisband;
import me.tuke.sktuke.hooks.simpleclans.EvtDemotePlayerClan;
import me.tuke.sktuke.hooks.simpleclans.EvtPromotePlayerClan;
import me.tuke.sktuke.hooks.simpleclans.EvtRivalClanAdd;
import me.tuke.sktuke.hooks.simpleclans.EvtRivalClanRemove;
import me.tuke.sktuke.hooks.simpleclans.ExprClanFromTag;
import me.tuke.sktuke.hooks.simpleclans.ExprClanMembers;
import me.tuke.sktuke.hooks.simpleclans.ExprClanTag;
import me.tuke.sktuke.hooks.simpleclans.ExprClanTwo;
import me.tuke.sktuke.hooks.simpleclans.ExprDeathsOfPlayer;
import me.tuke.sktuke.hooks.simpleclans.ExprKDRofPlayer;
import me.tuke.sktuke.hooks.simpleclans.ExprKillsOfClan;
import me.tuke.sktuke.hooks.simpleclans.ExprKillsOfPlayer;
import me.tuke.sktuke.hooks.simpleclans.ExprPlayerClan;
import me.tuke.sktuke.listeners.EnchantCheck;
import me.tuke.sktuke.listeners.InventoryCheck;
import me.tuke.sktuke.listeners.MarryCommand;
import me.tuke.sktuke.listeners.OnlineStatusCheck;
import me.tuke.sktuke.listeners.TagChat;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.events.AllyClanAddEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.AllyClanRemoveEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.DisbandClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerDemoteEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerPromoteEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.RivalClanAddEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.RivalClanRemoveEvent;

public class Register{

	public static int expr = 0;
	public static int evt = 0;
	public static int cond = 0;
	public static int eff = 0;

	public static LegendConfig config = null;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Integer[] load(TuSKe instance){
		if (!Skript.isAcceptRegistrations()){
			TuSKe.log("TuSKe can't be loaded when the server is already loaded.", Level.SEVERE);
		    Bukkit.getServer().getPluginManager().disablePlugin(TuSKe.getInstance());
			return null;
		}
		Skript.registerAddon(instance);
		EnchantConfig.loadEnchants();
		if (hasPlugin("Legendchat")){
				config = new LegendConfig(instance);
				Bukkit.getServer().getPluginManager().registerEvents(new TagChat(config), instance);
				Classes.registerClass(new ClassInfo<Channel>(Channel.class, "channel").user(new String[] { "channel" }).name("Channel").parser(new Parser<Channel>() {
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
					
					public boolean supportsOrdering() {
						return true;
					}

					@Override
					public Relation compare(Object arg0, Object arg1) {
						return Relation.get(arg0.equals(arg1));
					}
				});
				Skript.registerExpression(ExprLegendchatChannel.class, Channel.class, ExpressionType.SIMPLE, new String[] {"l[egend]c[hat] channel"});
				Skript.registerExpression(ExprLegendchatMessage.class, String.class, ExpressionType.SIMPLE, new String[] {"l[egend]c[hat] message"});
				Skript.registerExpression(ExprTellMessage.class, String.class, ExpressionType.SIMPLE, new String[] {"tell message"});
				Skript.registerExpression(ExprTellReceiver.class, CommandSender.class, ExpressionType.SIMPLE, new String[] {"[tell] receiver"});
				Skript.registerExpression(ExprTagChat.class, String.class, ExpressionType.SIMPLE, new String[]{"[chat] tag %string% of %player%", "%player%'[s] [chat] tag %string%"});
				Skript.registerExpression(ExprPlayerTags.class, String.class, ExpressionType.SIMPLE, new String[]{"[chat] tags of %player%","%player%'[s] [chat] tags"});
				Skript.registerExpression(ExprMuteLeftTime.class, Integer.class, ExpressionType.PROPERTY, new String[] {"mute (left|remaining) time of %player%", "%player%'[s] mute (left|remaining) time"});
				Skript.registerExpression(ExprDefaultChannel.class, Channel.class, ExpressionType.PROPERTY, new String[] {"default channel of %player%", "%player%'[s] default channel"});
				Skript.registerExpression(ExprSpyState.class, Boolean.class, ExpressionType.PROPERTY, new String[] {"spy state of %player%", "%player%'[s] spy state"});
				Skript.registerExpression(ExprHideState.class, Boolean.class, ExpressionType.PROPERTY, new String[] {"hide state of %player%", "%player%'[s] hide state"});
				Skript.registerCondition(CondMuted.class, "%player% is muted", "%player% is(n't| not) muted");
				Skript.registerCondition(CondCanSayChannel.class, "%player% can (see|say in) [channel] %channel%", "%player% can't (see|say in) [channel] %channel%");
				Skript.registerEffect(EffMakeTell.class, "[make] %player% [send] tell %string% to %player%");
				Skript.registerEffect(EffMute.class, "mute %player% [for %number% minute[s]]");
				Skript.registerEffect(EffUnMute.class, "unmute %player%");
				Skript.registerEffect(EffMakeSay.class, "make %player% say %string% in [channel] %channel%");
				Skript.registerEvent("Legendchat chat", EvtLCChat.class, ChatMessageEvent.class, new String[] { "l[egend]c[hat] chat" });
				EventValues.registerEventValue(ChatMessageEvent.class, Player.class,
						new Getter<Player, ChatMessageEvent>() {
							@Override
							public Player get(ChatMessageEvent event) {
								return event.getSender();
							}
						}, 0);
				Skript.registerEvent("Legendchat tell", EvtTellChat.class, PrivateMessageEvent.class, new String[] { "l[egend]c[hat] tell" });
				EventValues.registerEventValue(PrivateMessageEvent.class, CommandSender.class,
						new Getter<CommandSender, PrivateMessageEvent>() {
							@Override
							public CommandSender get(PrivateMessageEvent event) {
								return (CommandSender) event.getSender();
							}
						}, 0);
				
				expr+= 4;
				evt+= 2;
				cond+= 2;
				eff+= 4;
		}

		if (hasPlugin("Marriage")){
				Classes.registerClass(new ClassInfo<Gender>(Gender.class, "gender").user("gender").name("Gender").defaultExpression(new EventValueExpression(Gender.class)).parser(new Parser<Gender>() {
	
					@Override
					public String getVariableNamePattern() {
						return "gender";
					}
	
					@Nullable
					public Gender parse(String s, ParseContext cc) {
						try {
							return Gender.valueOf(s.toUpperCase());
						} catch (IllegalArgumentException e) {
							return null;
						}
					}
	
					@Override
					public String toString(Gender g, int i) {
						return g.toString().toLowerCase();
					}
	
					@Override
					public String toVariableNameString(Gender g) {
						return ".+";
					}
				
				}));
				Bukkit.getServer().getPluginManager().registerEvents(new MarryCommand(), TuSKe.getInstance());
				Skript.registerExpression(ExprPartnerOf.class, OfflinePlayer.class, ExpressionType.PROPERTY, new String[] {"partner of %player%", "%player%'[s] partner"});
				Skript.registerExpression(ExprGenderOf.class, Gender.class, ExpressionType.PROPERTY, new String[] {"gender of %player%", "%player%'[s] gender"});
				Skript.registerExpression(ExprMarryHome.class, Location.class, ExpressionType.PROPERTY, new String[] {"marry home of %player%", "%player%'[s] marry home"});
				Skript.registerCondition(CondMarried.class, "%player% is married", "%player% is(n't| not) married");
				Skript.registerEffect(EffMarry.class, "marry %player% with %player%");
				Skript.registerEffect(EffSendMarry.class, "[make] %player% invite %player% to marry", "send invite of marry from %player% to %player%");
				Skript.registerEffect(EffDivorce.class, "divorce %player%", "make %player% divorce");
				Skript.registerEvent("Marry Event", EvtMarry.class, MarryEvent.class, new String [] {"[player] marry"});
				Skript.registerExpression(ExprThePartners.class, Player.class, ExpressionType.SIMPLE,"[event-](partner-(one|two)|priest)");
				Skript.registerEvent("Divorce Event", EvtDivorce.class, DivorceEvent.class, new String [] {"[player] divorce"});
				EventValues.registerEventValue(DivorceEvent.class, Player.class,
						new Getter<Player, DivorceEvent>() {
							@Override
							public Player get(DivorceEvent event) {
								return event.getPlayer();
							}
						}, 0);
				expr+= 3;
				cond++;
				eff+= 3;
				evt+= 2;

		}
		if (hasPlugin("Landlord") && Skript.classExists("com.jcdesimp.landlord.persistantData.LowOwnedLand")){
			Classes.registerClass(new ClassInfo<LowOwnedLand>(LowOwnedLand.class, "landclaim").user(new String[]{"landclaim"}).name("Land Claim").defaultExpression(new EventValueExpression(LowOwnedLand.class)).parser(new Parser<LowOwnedLand>(){

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
			Classes.registerClass(new ClassInfo<Landflag>(Landflag.class, "landflag").user(new String[]{"landflag"}).name("Land Flag").defaultExpression(new EventValueExpression(Landflag.class)).parser(new Parser<Landflag>(){

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
			Skript.registerExpression(ExprLandClaimAt.class, LowOwnedLand.class, ExpressionType.SIMPLE, "land[lord] claim at %location/chunk%");
			Skript.registerExpression(ExprLandOwner.class, OfflinePlayer.class, ExpressionType.PROPERTY, new String[] {"land[lord] owner of %landclaim%", "%landclaim%'[s] land[lord] owner"});
			Skript.registerExpression(ExprLandflag.class, Boolean.class, ExpressionType.SIMPLE, "landflag %landflag% of %landclaim% for (1圯veryone|2圩riends)");
			Skript.registerExpression(ExprLandFriends.class, OfflinePlayer.class, ExpressionType.SIMPLE, "land[lord] friends of %landclaim%", "%landclaim%'[s] land[lord] friends");
			Skript.registerExpression(ExprLandLocation.class, Location.class, ExpressionType.PROPERTY, new String[] {"land[lord] location of %landclaim%", "%landclaim%'[s] land[lord] location"});
			Skript.registerExpression(ExprLandClaimsOf.class, LowOwnedLand.class, ExpressionType.SIMPLE, "land[lord] claims of %player%", "%player%'[s] land[lord] claims");
			Skript.registerEffect(EffClaimLand.class, "claim land[lord] at %location/chunk% for %player%");
			Skript.registerEffect(EffUnclaimLand.class, "unclaim land[lord] at %location/chunk%");
			eff+=2;
			expr+=6;
		}
		if (hasPlugin("SimpleClans") || hasPlugin("SimpleClansLegacy")){
			try{
				
				Skript.registerEvent("Clan create", EvtClanCreate.class, CreateClanEvent.class, new String[] { "clan create" });
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
				Skript.registerEvent("Clan disband", EvtClanDisband.class, DisbandClanEvent.class, new String[] { "clan disband" });
				EventValues.registerEventValue(DisbandClanEvent.class, Clan.class,
						new Getter<Clan, DisbandClanEvent>() {
							@Override
							public Clan get(DisbandClanEvent event) {
								return event.getClan();
							}
						}, 0); 
				Skript.registerEvent("Ally clan add", EvtAllyClanAdd.class, AllyClanAddEvent.class, new String [] { "ally clan add" });
				EventValues.registerEventValue(AllyClanAddEvent.class, Clan.class,
						new Getter<Clan, AllyClanAddEvent>() {
							@Override
							public Clan get(AllyClanAddEvent event) {
								return event.getClanFirst();
							}
						}, 0);
				Skript.registerEvent("Ally clan remove", EvtAllyClanRemove.class, AllyClanRemoveEvent.class, new String[] { "ally clan remove" });
				EventValues.registerEventValue(AllyClanRemoveEvent.class, Clan.class,
						new Getter<Clan, AllyClanRemoveEvent>() {
							@Override
							public Clan get(AllyClanRemoveEvent event) {
								return event.getClanFirst();
							}
						}, 0);
				Skript.registerEvent("Rival clan add", EvtRivalClanAdd.class, RivalClanAddEvent.class, new String[] { "rival clan add" });
				EventValues.registerEventValue(RivalClanAddEvent.class, Clan.class,
						new Getter<Clan, RivalClanAddEvent>() {
							@Override
							public Clan get(RivalClanAddEvent event) {
								return event.getClanFirst();
							}
						}, 0);
				Skript.registerEvent("Rival clan remove", EvtRivalClanRemove.class, RivalClanRemoveEvent.class, new String[] { "rival clan remove" });
				EventValues.registerEventValue(RivalClanRemoveEvent.class, Clan.class,
						new Getter<Clan, RivalClanRemoveEvent>() {
							@Override
							public Clan get(RivalClanRemoveEvent event) {
								return event.getClanFirst();
							}
						}, 0);
				Skript.registerEvent("Clan promote player", EvtPromotePlayerClan.class, PlayerPromoteEvent.class, new String[] { "[clan] promote player" });
				EventValues.registerEventValue(PlayerPromoteEvent.class, Player.class,
						new Getter<Player, PlayerPromoteEvent>() {
							@Override
							public Player get(PlayerPromoteEvent event) {
								return event.getClanPlayer().toPlayer();
							}
						}, 0);
				Skript.registerEvent("Clan demote player", EvtDemotePlayerClan.class, PlayerDemoteEvent.class, new String[] { "[clan] demote player" });
				EventValues.registerEventValue(PlayerDemoteEvent.class, Player.class,
						new Getter<Player, PlayerDemoteEvent>() {
							@Override
							public Player get(PlayerDemoteEvent event) {
								return event.getClanPlayer().toPlayer();
							}
						}, 0);	
				Classes.registerClass(new ClassInfo<Clan>(Clan.class, "clan").user(new String[] { "clan" }).name("clan").defaultExpression(new EventValueExpression(Clan.class)).parser(new Parser<Clan>() {
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
				
				Skript.registerExpression(ExprPlayerClan.class, Clan.class, ExpressionType.PROPERTY, new String[] {"clan of %player%", "%player%'[s] clan"});
				Skript.registerExpression(ExprClanTag.class, String.class, ExpressionType.PROPERTY, new String[] {"[clan] tag of %clan%", "%clan%'[s] [clan] tag"});
				Skript.registerExpression(ExprKDRofPlayer.class, Number.class, ExpressionType.PROPERTY, new String [] { "clan K[ill ]D[eath ]R[atio] of %player%", "%player%'[s] K[ills ]D[eaths ]R[atio]"});
				Skript.registerExpression(ExprClanFromTag.class, Clan.class, ExpressionType.SIMPLE,"clan from tag %string%");
				Skript.registerExpression(ExprClanMembers.class, OfflinePlayer.class, ExpressionType.SIMPLE, "clan members of %clan%", "%clan%'[s] clan members");
				Skript.registerExpression(ExprKillsOfPlayer.class, Number.class, ExpressionType.SIMPLE, "clan (1字ival|2好eutral|3圭ivilian) kills of %player%", "%player%'[s] clan (1字ival|2好eutral|3圭ivilian) kills");
				Skript.registerExpression(ExprKillsOfClan.class, Number.class, ExpressionType.SIMPLE, "clan total ((1字ival|2好eutral|3圭ivilian) kills|4匠[ill ]D[eath ]R[atio]|5圬eaths) of %clan%", "%clan%'[s] clan total ((1字ival|2好eutral|3圭ivilian) kills|4匠[ill ]D[eath ]R[atio]|5圬eaths)");
				Skript.registerExpression(ExprDeathsOfPlayer.class, Number.class, ExpressionType.PROPERTY, new String[] {"clan deaths of %player%", "%player%'[s] clan deaths"});
				Skript.registerExpression(ExprClanTwo.class, Clan.class, ExpressionType.SIMPLE, "[event-]clan-two");
				Skript.registerEffect(EffCreateClan.class, "create [a] [new] clan named %string% with tag %string% (to|for) %player%");
				Skript.registerEffect(EffRemoveFromClan.class, "(remove|kick) %player% from his clan", "[make] %player% resign from his clan");
				Skript.registerEffect(EffPlacePlayerInClan.class, "(add|place) %player% (to|in) [clan] %clan%", "[make] %player% join to %clan%");
				Skript.registerEffect(EffInvitePlayerToClan.class,"[make] %player% [a] invite %player% to his clan", "send invite of clan from %player% to %player%");
				Skript.registerEffect(EffDisbandClan.class, "disband [clan] %clan%");
				Skript.registerEffect(EffVerifyClan.class, "verify [clan] %clan%");
				Skript.registerCondition(CondLeader.class, "%player% is leader of his clan", "%player% is(n't| not) leader of his clan");
				Skript.registerCondition(CondClan.class, "%player% (has|have) [a] clan", "%player% (hasn't|doesn't have) [a] clan");
				expr+= 8;
				evt+= 8;
				cond+= 2;
				eff+=6;
			} catch (NoClassDefFoundError e){
				TuSKe.log("Couldn't hook with SimpleClans. Make sure you have the lastest verssion of plugin and " + TuSKe.getInstance().getName() + ".", Level.WARNING);
			}
			
		}
		if (hasPlugin("ProtocolSupport") || hasPlugin("ViaVersion")){
				Skript.registerExpression(ExprPlayerVersion.class, String.class, ExpressionType.PROPERTY, "(mc|minecraft) version of %player%", "%player%'[s] (mc|minecraft) version");
				expr++;
		}
	
		if (TuSKe.hasSupport()){
			expr +=1;
			eff+=2;
			Skript.registerExpression(ExprOfflineData.class, Player.class, ExpressionType.PROPERTY, "[the] player data of %offlineplayer%", "%offlineplayer%'[s] player data");
			Skript.registerEffect(EffSaveData.class, "save [player] data of %player%");
			Skript.registerEffect(EffMakeDrop.class, "(make|force) %player% drop[s] %itemstack% [from his inventory]");
		} else
			TuSKe.log("The version of your server it isn't supported for some expressions: " + Bukkit.getServer().getClass().getPackage().getName().split(".v")[1], Level.WARNING);
		
		
		Classes.registerClass(new ClassInfo<Recipe>(Recipe.class, "recipe").user(new String[]{"recipe"}).name("Recipe").defaultExpression(new EventValueExpression(Recipe.class)).parser(new Parser<Recipe>(){

			@Override
			@Nullable
			public Recipe parse(String s, ParseContext arg1) {
				return null;
			}

			@Override
			public String toString(Recipe r, int arg1) {
				TuSKe.log(r.toString());
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
		if (Classes.getExactClassInfo(InventoryType.class) == null){
			Classes.registerClass(new ClassInfo<InventoryType>(InventoryType.class, "inventorytype").user(new String[]{"inventorytype"}).name("Inventory Type").defaultExpression(new EventValueExpression(InventoryType.class)).parser(new Parser<InventoryType>(){

				@Override
				@Nullable
				public InventoryType parse(String s, ParseContext arg1) {
					try {
						return InventoryType.valueOf(s.toUpperCase().replaceAll(" ", "_"));
					} catch(Exception e){}
					return null;
				}

				@Override
				public String toString(InventoryType ct, int arg1) {
					return ct.name().toLowerCase().replace("_", " ");
				}

				@Override
				public String toVariableNameString(InventoryType ct) {
					return ct.name();
				}
				
				@Override
				public String getVariableNamePattern() {
					return ".+";
				}
				
			}));
			
		} else {
		}
		if (Classes.getExactClassInfo(ClickType.class) == null){
			Classes.registerClass(new ClassInfo<ClickType>(ClickType.class, "clickaction").user(new String[]{"click ?(action)?"}).name("Click Action").defaultExpression(new EventValueExpression(ClickType.class)).parser(new Parser<ClickType>(){

				@Override
				@Nullable
				public ClickType parse(String s, ParseContext arg1) {
					try {
						return ClickType.valueOf(s.toUpperCase().replaceAll(" ", "_"));
					} catch(Exception e){}
					return null;
				}

				@Override
				public String toString(ClickType ct, int arg1) {
					return ct.name().toLowerCase().replace("_", " ");
				}

				@Override
				public String toVariableNameString(ClickType ct) {
					return ct.name();
				}
				
				@Override
				public String getVariableNamePattern() {
					return ".+";
				}
				
			}));
			
		} 
		Classes.registerClass(new ClassInfo<CEnchant>(CEnchant.class, "customenchantment").user(new String[]{"custom ?enchantment"}).name("Custom Enchantment").defaultExpression(new EventValueExpression(CEnchant.class)).parser(new Parser<CEnchant>(){

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
		Skript.registerExpression(ExprExpOf.class, Integer.class, ExpressionType.PROPERTY, new String [] {"[total] [e]xp of %player%", "%player%'[s] [total] [e]xp"});
		Skript.registerExpression(ExprLastLogin.class, Date.class, ExpressionType.PROPERTY, new String[] {"last login of %player%", "%player%'[s] last login"});
		Skript.registerExpression(ExprFirstLogin.class, Date.class, ExpressionType.PROPERTY, new String[] {"first login of %player%", "%player%'[s] first login"});
		Skript.registerExpression(ExprOnlineTime.class, Timespan.class, ExpressionType.PROPERTY, new String[] {"online time of %player%", "%player%'[s] online time"});
		Skript.registerExpression(ExprLastDamage.class, Number.class, ExpressionType.PROPERTY, new String[] {"last damage of %livingentity%", "%livingentity%'[s] last damage"});
		Skript.registerExpression(ExprLastDamageCause.class, DamageCause.class, ExpressionType.PROPERTY, new String[] {"last damage cause of %livingentity%", "%livingentity%'[s] last damage cause"});
		Skript.registerExpression(ExprHorseStyle.class, String.class, ExpressionType.PROPERTY, new String [] {"horse style of %entity%", "%entity%'[s] horse style"});
		Skript.registerExpression(ExprHorseColor.class, String.class, ExpressionType.PROPERTY, new String [] {"horse color of %entity%", "%entity%'[s] horse color"});
		Skript.registerExpression(ExprHorseVariant.class, String.class, ExpressionType.PROPERTY, new String [] {"horse variant of %entity%", "%entity%'[s] horse variant"});
		Skript.registerExpression(ExprRabbitType.class, String.class, ExpressionType.PROPERTY, new String [] {"rabbit type of %entity%", "%entity%'[s] rabbit type"});
		Skript.registerExpression(ExprCatType.class, String.class, ExpressionType.PROPERTY, new String [] {"(cat|ocelot) type of %entity%", "%entity%'[s] (cat|ocelot) type"});
		Skript.registerExpression(ExprDropsOfBlock.class, ItemStack.class, ExpressionType.SIMPLE, "drops of %block% [(with|using) %-itemstack%]", "%block%'[s] drops [(with|using) %-itemstack%]");
		Skript.registerExpression(ExprListPaged.class, Object.class, ExpressionType.SIMPLE, "page %number% of %objects% with %number% lines");
		Skript.registerExpression(ExprRecipesOf.class, Recipe.class, ExpressionType.SIMPLE, "recipes of %itemstack%", "%itemstack%'[s] recipes");
		Skript.registerExpression(ExprItemsOfRecipe.class, ItemStack.class, ExpressionType.SIMPLE, "ingredients of %recipe%", "%recipe%'[s] ingredients");
		Skript.registerExpression(ExprResultOfRecipe.class, ItemStack.class, ExpressionType.PROPERTY, "result item of %itemstacks/recipe%", "%itemstacks/recipe%'[s] result item");
		Skript.registerExpression(ExprAlphabetOrder.class, Object.class, ExpressionType.SIMPLE, "alphabetical order of %objects%");
		Skript.registerExpression(ExprHighiestBlock.class, Block.class, ExpressionType.SIMPLE, "highest block at %location%");
		//1.1
		Skript.registerExpression(ExprMaxDurability.class, Integer.class, ExpressionType.PROPERTY, "[the] max durability of %itemstack%", "%itemstack%'[s] max durability");
		//
		// 1.5
		Skript.registerExpression(ExprLevelOfCustomEnchant.class, Number.class, ExpressionType.SIMPLE, new String[] {"level of [custom enchantment] %customenchantment% of %itemstack%"});
		Skript.registerExpression(ExprAllCustomEnchants.class, CEnchant.class, ExpressionType.SIMPLE, new String[]{"[all] custom enchantments of %itemstack%", "%itemstack%'[s] [all] custom enchantments"});
		Skript.registerExpression(ExprItemCustomEnchant.class, ItemStack.class, ExpressionType.SIMPLE, new String[]{"%itemstack% with custom enchantment[s] %customenchantments%"});
		Skript.registerEffect(EffReloadEnchants.class, "reload [all] [custom] enchantments");
		Skript.registerCondition(CondHasCustom.class, "%itemstack% has [a] custom enchantment [%-customenchantment%]", "%itemstack% has(n't| not) [a] custom enchantment [%-customenchantment%]");
		//
		// 1.5.3
		Skript.registerEffect(EffRegisterEnchantment.class, "(register|create) [a] [new] [custom] enchantment with id [name] %string%");
		Skript.registerEffect(EffUnregisterEnchantment.class, "unregister [the] [custom] enchantment %customenchantment%");
		Skript.registerExpression(ExprMaxLevel.class, Number.class, ExpressionType.PROPERTY, "max level of %customenchantment%", "%customenchantment%'[s] max level");
		Skript.registerExpression(ExprRarity.class, Number.class, ExpressionType.PROPERTY, "rarity of %customenchantment%", "%customenchantment%'[s] rarity");
		Skript.registerExpression(ExprLoreName.class, String.class, ExpressionType.PROPERTY, "[lore] name of %customenchantment%", "%customenchantment%'[s] [lore] name");
		Skript.registerExpression(ExprAcceptedItems.class, String.class, ExpressionType.SIMPLE, "accepted items for %customenchantment%");
		Skript.registerExpression(ExprCEConflicts.class, CEnchant.class, ExpressionType.SIMPLE, "conflicts for %customenchantment%");
		Skript.registerExpression(ExprEnabled.class, Boolean.class, ExpressionType.PROPERTY, "enabled for %customenchantment%");
		Skript.registerExpression(ExprLeatherColor.class, Integer.class, ExpressionType.SIMPLE, "[leather] (0字ed|1夙reen|2在lue) colo[u]r of %-itemstacks/colors%", "%-itemstacks/colors%'[s] [leather] (0字ed|1夙reen|2在lue) color");
		Skript.registerExpression(ExprItemDamage.class, Integer.class, ExpressionType.SIMPLE, "item damage");
		//Skript.registerCondition(CondIsMoving.class, "%player% is( (moving|walking)|(n't| not) stopped)", "%player% is((n't| not) (moving|walking)| stopped)");
		//
		//1.5.4
		Skript.registerExpression(ExprRGBColor.class, Integer.class, ExpressionType.SIMPLE, "[the] R[ed, ]G[reen and ]B[blue] [colo[u]r[s]] of %-itemstacks/colors%", "%-itemstacks/colors%'[s] R[ed, ]G[reen and ]B[blue] [colo[u]r[s]]");
		
		//
		//1.5.7
		Skript.registerExpression(ExprServerOnlineTime.class, Timespan.class, ExpressionType.SIMPLE, "[the] online time of server", "server'[s] online time");
		/*if (getServer().getPluginManager().getPlugin("Umbaska") != null){
			Skript.registerExpression(ExprDelayOfSpawner.class, Timespan.class, ExpressionType.PROPERTY, "spawner delay of %block%", "%block%'[s] spawner delay");
			Skript.registerExpression(ExprSpawnerType.class, EntityType.class, ExpressionType.PROPERTY, new String[] {"spawner entity of %block%", "%block%'[s] spawner entity"});
		}
		*/
		//1.5.9
		Skript.registerExpression(ExprLanguage.class, String.class, ExpressionType.PROPERTY, "[the] (locale|language) of %player%", "%player%'[s] (locale|language)");
		
		
		Skript.registerExpression(ExprLocalNameOf.class, String.class, ExpressionType.SIMPLE, "[the] [json] client id of %object%" , "%object%'[s] [json] client id");
		Skript.registerExpression(ExprInventoryMoveInv.class, Inventory.class, ExpressionType.SIMPLE, "[event-]inventory-(one|two)");
		Skript.registerExpression(ExprInventoryMoveSlot.class, Integer.class, ExpressionType.SIMPLE, "[event-]slot-(one|two)");
		//
		//1.6
		Skript.registerCondition(CondRegexMatch.class, "%string% [regex] matches %string%", "%string% [regex] does(n't| not) match %string%");
		//
		//1.6.2
		
		String cr = "string/" + Classes.getExactClassInfo(ClickType.class).getCodeName();
		//if (Skript.getVersion().toString().contains("2.2-dev"))
		//	cr = "string";
		Skript.registerEffect(EffFormatGUI.class, new String[]{
				"(format|create|make) [a] gui slot %numbers% of %players% with %itemstack% [to [do] nothing]",
				"(format|create|make) [a] gui slot %numbers% of %players% with %itemstack% to (1圭lose|2她pen %-inventoy%) [(using|with) %-" + cr + "% [(button|click|action)]]",
				"(format|create|make) [a] gui slot %numbers% of %players% with %itemstack% to [(1圭lose|2她pen %-inventoy%) then] (run|exe[cute]) %commandsender% command %string% [(using|with) perm[ission] %-string%] [[(,| and) ](using|with) %-" + cr + "% [(button|click|action)]] [[(,| and) ](using|with) cursor [item] %-itemstack%]",
				"(format|create|make) [a] gui slot %numbers% of %players% with %itemstack% to [(1圭lose|2她pen %-inventoy%) then] (run|exe[cute]) function <(.+)>\\([%-objects%[, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%]]\\) [[(,| and) ](using|with) %-" + cr + "% [(button|click|action)]] [[(,| and) ](using|with) cursor [item] %-itemstack%]",
				"(format|create|make) [a] gui slot %numbers% of %players% with %itemstack% to (run|exe[cute]) [gui [click]] event"});
		
		Skript.registerEffect(EffUnformatGUI.class, "(unformat|remove|clear|reset) [the] gui slot %numbers% of %players%", "(unformat|remove|clear|reset) [all] [the] gui slots of %players%");
		Skript.registerEffect(EffEvaluateFunction.class, "evaluate function %strings% [with %-objects%[, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%]]");
		Skript.registerExpression(ExprEvaluateFunction.class, Object.class, ExpressionType.SIMPLE, "result of function %string% [with %-objects%[, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%][, %-objects%]]");
		Skript.registerCondition(CondHasGUI.class, new String[]{"%player% has [a] gui", "slot %number% of %player% is a gui","%player% does(n't| not) have [a] gui", "slot %number% of %player% is(n't| not) [a] gui"});
		//
		// 1.6.6
		Skript.registerExpression(ExprDraggedSlots.class, Integer.class, ExpressionType.SIMPLE, "[event-]dragged(-| )slots");
		Skript.registerExpression(ExprDraggedItem.class, ItemStack.class, ExpressionType.SIMPLE, "[event-][old(-| )]dragged(-| )item");
		Skript.registerExpression(ExprDroppedExp.class, Experience.class, ExpressionType.SIMPLE, "[the] dropped [e]xp[erience] [orb[s]]");
		//Skript.registerCondition(CondCanSpawn.class , "(1妃onster|2地nimal)[s] can spawn (at|in) %location/world/string%", "%entitytype% can spawn (at|in) %location/world/string%", "(1妃onster|2地nimal)[s] can('t| not) spawn (at|in) %location/world/string%", "%entitytype% can('t| not) spawn (at|in) %location/world/string%");
		//
		// 1.6.8
		Skript.registerCondition(CondIsMobType.class, "%livingentities% (is|are) [a] (0多ostile|1好eutral|2如assive) [mob]", "%livingentities% (is|are)(n't| not) [a] (0多ostile|1好eutral|2如assive) [mob]");
		//Skript.registerExpression(ExprPlayerGUI.class, Player.class, ExpressionType.SIMPLE, "GUI-player");
		//Skript.registerEffect(EffFormatGroupGUI.class, "(format|create|make) [a] gui inventory with [group] [id] %string% to %player%");
		Skript.registerExpression(ExprAllRecipes.class, Recipe.class, ExpressionType.SIMPLE, "[all] [registred] recipes");
		Skript.registerCondition(CondIsAgeable.class, "%entities% ((is|are) ageable|can grow up)", "%entities% ((is|are)(n't| not) ageable|can(n't| not) grow up)");
		Skript.registerCondition(CondIsTameable.class, "%entities% (is|are) tameable", "%entities% (is|are)(n't| not) tameable");
		Skript.registerExpression(ExprSplitCharacter.class, String.class, ExpressionType.SIMPLE, "split %string% (with|by|using) %number% [char[aracter][s]]", "%string% [split] (with|by|using) %number% [char[aracter][s]]");
		Skript.registerExpression(ExprLastColor.class, String.class, ExpressionType.PROPERTY, "[the] last color of %string%", "%string%'[s] last color");
		
		//1.6.9.3
		Skript.registerExpression(ExprVirtualInv.class, Inventory.class, ExpressionType.SIMPLE, "virtual %inventorytype% inventory [with size %-number%] [(named|with (name|title)) %-string%]");
		Skript.registerEffect(EffRegisterPermission.class, "(register|create) master permission %string%");
		Skript.registerExpression(ExprCommandInfo.class, String.class, ExpressionType.SIMPLE, 	"description of command %string%", "command %string%'[s] description",
																								"main [command] of command %string%", "command %string%'[s] main [command]",
																								"permission of command %string%", "command %string%'[s] permission",
																								"permission message of command %string%", "command %string%'[s] permission message",
																								"plugin [owner] of command %string%", "command %string%'[s] plugin [owner]",
																								"usage of command %string%", "command %string%'[s] usage",
																								"file [location] of command %string%", "command %string%'[s] file location");
		Skript.registerExpression(ExprAllCommand.class, String.class, ExpressionType.SIMPLE, "[all] commands", "aliases of command %string%", "command %string%'[s] aliases");
		Skript.registerEffect(EffExecutePermission.class, "[execute] [the] command %strings% by %players% with perm[ission] %string%", "[execute] [the] %players% command %strings% with perm[ission] %string%", "(let|make) %players% execute [[the] command] %strings% with perm[ission] %string%");
		//Skript.registerExpression(ExprItemSpawner.class, ItemStack.class, ExpressionType.SIMPLE, "%itemstack% with spawner type %string%");
		//
		//1.7
		Skript.registerExpression(ExprRegexSplit.class, String.class, ExpressionType.SIMPLE, "regex split %string% (with|using) [pattern] %string%");
		Skript.registerExpression(ExprRegexReplace.class, String.class, ExpressionType.SIMPLE, "regex replace [all] [pattern] %string% with [group[s]] %string% in %string%");
		//
		//1.7.1
		Skript.registerEffect(EffRegisterRecipe.class, 
				"(create|register) [new] [custom] shaped recipe with (return|result) %itemstack% using [ingredients] %itemstacks%",
				"(create|register) [new] [custom] shapeless recipe with (return|result) %itemstack% using [ingredients] %itemstacks%",
				"(create|register) [new] [custom] furnace recipe with (return|result) %itemstack% using [source] %itemstack% [[and] with experience %-number%]");
		//
		Skript.registerCondition(CondIsBlockType.class, "%itemstack% is [a] (solid|transparent|flammable|occluding) block", "%itemstack% is(n't| not) [a] (solid|transparent|flammable|occluding) block");
		Skript.registerCondition(CondCanEat.class, "%itemstack% is edible", "%itemstack% is(n't| not) edible");
		Skript.registerCondition(CondHasGravity.class, "%itemstack% has gravity", "%itemstack% has(n't| not) gravity");
		Skript.registerEffect(EffCancelDrop.class, "cancel [the] drops [of (inventory|[e]xp[periences])]");
		Skript.registerEffect(EffPushBlock.class, "move %block% to %direction%");
		Skript.registerEvent("Anvil conbine", EvtAnvilCombine.class, AnvilCombineEvent.class, new String[] { "anvil [item] (combine|merge)" });
		Skript.registerEvent("Anvil rename", EvtAnvilRename.class, AnvilRenameEvent.class, new String[] { "anvil [item] rename" });
		Skript.registerEvent("Item damage", EvtItemDamage.class, PlayerItemDamageEvent.class, new String[] {"[player] item damage"});
		Skript.registerEvent("Player starts move", EvtPlayerStartsMove.class, PlayerStartsMoveEvent.class, "player start[s] (mov(e|ing)|walk[ing])");
		//Skript.registerEvent("Player stops move", EvtPlayerStopsMove.class, PlayerStopsMoveEvent.class, "player stop[s] (mov(e|ing)|walk[ing])");
		Skript.registerEvent("Inventory drag", EvtInventoryDrag.class, InventoryDragEvent.class, "inventory drag");
		Skript.registerEvent("GUI click", SimpleEvent.class, GUIActionEvent.class, "gui (action|click)");
		Skript.registerExpression(ExprAnvilItem.class, ItemStack.class, ExpressionType.SIMPLE, "[event-]item-(one|two|result|three)");

		Bukkit.getServer().getPluginManager().registerEvents(new InventoryCheck(instance), instance);
		Bukkit.getServer().getPluginManager().registerEvents(new OnlineStatusCheck(instance), instance);
		Bukkit.getServer().getPluginManager().registerEvents(new EnchantCheck(instance), instance);
		//Bukkit.getServer().getPluginManager().registerEvents(new PlayerMovesCheck(instance),instance);
		
		
		for (Player p: Bukkit.getOnlinePlayers()){
			OnlineStatusCheck.setTime(p, System.currentTimeMillis());
		}
		if (Skript.classExists("org.bukkit.event.entity.SpawnerSpawnEvent")){
			Skript.registerEvent("Spawner spawn", EvtSpawnerSpawn.class, SpawnerSpawnEvent.class, "spawner spawn");
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
			evt++;
		}			
		Skript.registerExpression(ExprInventoryMoveInv.class, Inventory.class, ExpressionType.SIMPLE, "[event-]inventory-(one|two)");
		Skript.registerExpression(ExprInventoryMoveSlot.class, Integer.class, ExpressionType.SIMPLE, "[event-]slot-(one|two)");
		Skript.registerEvent("Inventory move", EvtInventoryMove.class, InventoryMoveEvent.class, "inventory move");
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

		if (!Skript.getVersion().toString().contains("2.2-dev"))
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
						return event.getClickEvent().getClick().name().toLowerCase().replaceAll("_", " ");
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
						return event.getType().name().toLowerCase().replace("_", " ");
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
		

		expr+= 40;
		eff+= 10;
		evt+= 7;
		cond+= 14;
		return new Integer[]{evt, cond, expr, eff, CustomEnchantment.getEnchantments().size()};
	}
	public static boolean hasPlugin(String plugin){
		return Bukkit.getServer().getPluginManager().getPlugin(plugin) != null;
	}
	
	public void registerProperty(Class<? extends SimplePropertyExpression<?, ?>> c){
		
	}
}
