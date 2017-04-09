package me.tuke.sktuke.register;

import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.tuke.sktuke.events.customevent.AnvilCombineEvent;
import me.tuke.sktuke.events.customevent.AnvilRenameEvent;
import me.tuke.sktuke.events.customevent.InventoryMoveEvent;
import me.tuke.sktuke.manager.gui.GUIActionEvent;
import me.tuke.sktuke.util.EnumType;
import me.tuke.sktuke.util.ReflectionUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * @author Tuke_Nuke on 06/04/2017
 */
public class TuSKeEventValues {
	static {

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
		EventValues.registerEventValue(AnvilRenameEvent.class, Inventory.class,
				new Getter<Inventory, AnvilRenameEvent>() {
					@Override
					public Inventory get(AnvilRenameEvent event) {
						return event.getInventory();
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
						if (event.getInventory().getItem(2) != null && event.getInventory().getItem(2).hasItemMeta())
							return event.getInventory().getItem(2).getItemMeta().getDisplayName();
						return null;
					}
				}, 0);
		EventValues.registerEventValue(AnvilRenameEvent.class, Player.class,
				new Getter<Player, AnvilRenameEvent>() {
					@Override
					public Player get(AnvilRenameEvent event) {
						return event.getPlayer();
					}
				}, 0);
		if (ReflectionUtils.hasClass("org.bukkit.event.player.PlayerItemDamageEvent")){
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
		}

		if (ReflectionUtils.hasClass("org.bukkit.event.entity.SpawnerSpawnEvent")){
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
		EventValues.registerEventValue(PrepareItemCraftEvent.class, Player.class,
				new Getter<Player, PrepareItemCraftEvent>() {
					@Override
					public Player get(PrepareItemCraftEvent event) {
						return  event.getViewers().get(0) instanceof Player ? (Player) event.getViewers().get(0) : null;
					}
				}, 0);
		EventValues.registerEventValue(PrepareItemCraftEvent.class, ItemStack.class,
				new Getter<ItemStack, PrepareItemCraftEvent>() {
					@Override
					public ItemStack get(PrepareItemCraftEvent event) {
						return event.getRecipe().getResult();
					}
				}, 0);
		EventValues.registerEventValue(PrepareItemCraftEvent.class, Inventory.class,
				new Getter<Inventory, PrepareItemCraftEvent>() {
					@Override
					public Inventory get(PrepareItemCraftEvent event) {
						return event.getInventory();
					}
				}, 0);
		EventValues.registerEventValue(PrepareItemCraftEvent.class, Recipe.class,
				new Getter<Recipe, PrepareItemCraftEvent>() {
					@Override
					public Recipe get(PrepareItemCraftEvent event) {
						return  event.getRecipe();
					}
				}, 0);
		EventValues.registerEventValue(CraftItemEvent.class, Recipe.class,
				new Getter<Recipe, CraftItemEvent>() {
					@Override
					public Recipe get(CraftItemEvent event) {
						return  event.getRecipe();
					}
				}, 0);
	}
}
