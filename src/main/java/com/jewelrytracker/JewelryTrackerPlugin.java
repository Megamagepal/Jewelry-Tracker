package com.jewelrytracker;

import com.google.inject.Provides;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import com.jewelrytracker.config.Slot;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.SpriteOverride;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.menus.WidgetMenuOption;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import com.jewelrytracker.*;
import net.runelite.client.plugins.interfacestyles.Skin;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import java.awt.image.BufferedImage;
import java.io.File;

import static net.runelite.api.Constants.CLIENT_DEFAULT_ZOOM;

@Slf4j
@PluginDescriptor(
	name = "Jewelry Tracker"
)
public class JewelryTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private JewelryTrackerConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private MenuManager menuManager;

	private static final WidgetMenuOption FIXED_EQUIPMENT_TAB_NONE = new WidgetMenuOption("NONE",
			"NONE", WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB);
	private static final WidgetMenuOption FIXED_EQUIPMENT_TAB_NECK = new WidgetMenuOption("NECK",
			"NECK", WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB);
	private static final WidgetMenuOption FIXED_EQUIPMENT_TAB_RING = new WidgetMenuOption("RING",
			"RING", WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB);
	private static final WidgetMenuOption FIXED_EQUIPMENT_TAB_BRACELET = new WidgetMenuOption("BRACELET",
			"BRACELET", WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB);

	private static final WidgetMenuOption RESIZABLE_INVENTORY_TAB_NONE = new WidgetMenuOption("NONE",
			"NONE", WidgetInfo.RESIZABLE_VIEWPORT_EQUIPMENT_TAB);
	private static final WidgetMenuOption RESIZABLE_INVENTORY_TAB_NECK = new WidgetMenuOption("NECK",
			"NECK", WidgetInfo.RESIZABLE_VIEWPORT_EQUIPMENT_TAB);
	private static final WidgetMenuOption RESIZABLE_INVENTORY_TAB_RING = new WidgetMenuOption("RING",
			"RING", WidgetInfo.RESIZABLE_VIEWPORT_EQUIPMENT_TAB);
	private static final WidgetMenuOption RESIZABLE_INVENTORY_TAB_BRACELET = new WidgetMenuOption("BRACELET",
			"BRACELET", WidgetInfo.RESIZABLE_VIEWPORT_EQUIPMENT_TAB);

	private static final WidgetMenuOption RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_NONE = new WidgetMenuOption("NONE",
			"NONE", WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_EQUIPMENT_ICON);
	private static final WidgetMenuOption RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_NECK = new WidgetMenuOption("NECK",
			"NECK", WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_EQUIPMENT_ICON);
	private static final WidgetMenuOption RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_RING = new WidgetMenuOption("RING",
			"RING", WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_EQUIPMENT_ICON);
	private static final WidgetMenuOption RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_BRACELET = new WidgetMenuOption("BRACELET",
			"BRACELET", WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_EQUIPMENT_ICON);

	@Override
	protected void startUp() throws Exception
	{
		menuManager.addManagedCustomMenu(FIXED_EQUIPMENT_TAB_NONE);
		menuManager.addManagedCustomMenu(FIXED_EQUIPMENT_TAB_NECK);
		menuManager.addManagedCustomMenu(FIXED_EQUIPMENT_TAB_RING);
		menuManager.addManagedCustomMenu(FIXED_EQUIPMENT_TAB_BRACELET);

		menuManager.addManagedCustomMenu(RESIZABLE_INVENTORY_TAB_NONE);
		menuManager.addManagedCustomMenu(RESIZABLE_INVENTORY_TAB_NECK);
		menuManager.addManagedCustomMenu(RESIZABLE_INVENTORY_TAB_RING);
		menuManager.addManagedCustomMenu(RESIZABLE_INVENTORY_TAB_BRACELET);

		menuManager.addManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_NONE);
		menuManager.addManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_NECK);
		menuManager.addManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_RING);
		menuManager.addManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_BRACELET);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		populateJewelery();
	}

	@Subscribe
	public void onMenuOpened(final MenuOpened event)
	{
		final MenuEntry firstEntry = event.getFirstEntry();

		if (firstEntry == null)
			return;

		final int widgetId = firstEntry.getParam1();

		if (widgetId == WidgetInfo.EQUIPMENT.getId())
		{
			int itemId = firstEntry.getIdentifier();

			if (itemId == -1)
				return;

			MenuEntry[] menuList = new MenuEntry[5];

			menuList[0] = event.getMenuEntries()[0];

			MenuEntry newMenu = new MenuEntry();
			newMenu.setOption("NONE");
			newMenu.setTarget("NONE");
			newMenu.setIdentifier(itemId);
			newMenu.setParam1(widgetId);
			newMenu.setType(MenuAction.RUNELITE.getId());
			menuList[1] = newMenu;
			MenuEntry newMenu2 = new MenuEntry();
			newMenu.setOption("NECK");
			newMenu.setTarget("NECK");
			newMenu.setIdentifier(itemId);
			newMenu.setParam1(widgetId);
			newMenu.setType(MenuAction.RUNELITE.getId());
			menuList[2] = newMenu;
			MenuEntry newMenu3 = new MenuEntry();
			newMenu.setOption("RING");
			newMenu.setTarget("RING");
			newMenu.setIdentifier(itemId);
			newMenu.setParam1(widgetId);
			newMenu.setType(MenuAction.RUNELITE.getId());
			menuList[3] = newMenu;
			MenuEntry newMenu4 = new MenuEntry();
			newMenu.setOption("BRACELET");
			newMenu.setTarget("BRACELET");
			newMenu.setIdentifier(itemId);
			newMenu.setParam1(widgetId);
			newMenu.setType(MenuAction.RUNELITE.getId());
			menuList[4] = newMenu;

			client.setMenuEntries(menuList);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(final MenuOptionClicked event)
	{
		if (event.getMenuAction() != MenuAction.RUNELITE)
			return;

		if (event.getMenuOption().equals("NONE"))
			configManager.setConfiguration("jewelrytracker","jewelrySlot","NONE");
		else if (event.getMenuOption().equals("NECK"))
			configManager.setConfiguration("jewelrytracker","jewelrySlot","NECK");
		else if (event.getMenuOption().equals("RING"))
			configManager.setConfiguration("jewelrytracker","jewelrySlot","RING");
		else if (event.getMenuOption().equals("BRACELET"))
			configManager.setConfiguration("jewelrytracker","jewelrySlot","BRACELET");
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() != client.getItemContainer(InventoryID.EQUIPMENT) || configManager.getConfig(JewelryTrackerConfig.class).Slot().equals(Slot.NONE))
			return;

		populateJewelery();

	}

	public void populateJewelery() {
		resetIcon();
		Item[] items = client.getItemContainer(InventoryID.EQUIPMENT).getItems();
		Item slot;

		int charges = 0;
		SpritePixels jewelrySprite = null;

		if(configManager.getConfig(JewelryTrackerConfig.class).Slot().equals(Slot.NECK)) {
			slot = items[EquipmentInventorySlot.AMULET.getSlotIdx()];

			switch (slot.getId()) {
				case ItemID.AMULET_OF_ETERNAL_GLORY:
					charges = -1;
					break;
				case ItemID.AMULET_OF_GLORY:
				case ItemID.AMULET_OF_GLORY_T:
				case ItemID.SKILLS_NECKLACE:
					charges = 0;
					break;
				case ItemID.AMULET_OF_GLORY1:
				case ItemID.AMULET_OF_GLORY_T1:
				case ItemID.GAMES_NECKLACE1:
				case ItemID.SKILLS_NECKLACE1:
				case ItemID.NECKLACE_OF_PASSAGE1:
				case ItemID.BURNING_AMULET1:
				case ItemID.DIGSITE_PENDANT_1:
					charges = 1;
					break;
				case ItemID.AMULET_OF_GLORY2:
				case ItemID.AMULET_OF_GLORY_T2:
				case ItemID.GAMES_NECKLACE2:
				case ItemID.SKILLS_NECKLACE2:
				case ItemID.NECKLACE_OF_PASSAGE2:
				case ItemID.BURNING_AMULET2:
				case ItemID.DIGSITE_PENDANT_2:
					charges = 2;
					break;
				case ItemID.AMULET_OF_GLORY3:
				case ItemID.AMULET_OF_GLORY_T3:
				case ItemID.GAMES_NECKLACE3:
				case ItemID.SKILLS_NECKLACE3:
				case ItemID.NECKLACE_OF_PASSAGE3:
				case ItemID.BURNING_AMULET3:
				case ItemID.DIGSITE_PENDANT_3:
					charges = 3;
					break;
				case ItemID.AMULET_OF_GLORY4:
				case ItemID.AMULET_OF_GLORY_T4:
				case ItemID.GAMES_NECKLACE4:
				case ItemID.SKILLS_NECKLACE4:
				case ItemID.NECKLACE_OF_PASSAGE4:
				case ItemID.BURNING_AMULET4:
				case ItemID.DIGSITE_PENDANT_4:
					charges = 4;
					break;
				case ItemID.AMULET_OF_GLORY5:
				case ItemID.AMULET_OF_GLORY_T5:
				case ItemID.GAMES_NECKLACE5:
				case ItemID.SKILLS_NECKLACE5:
				case ItemID.NECKLACE_OF_PASSAGE5:
				case ItemID.BURNING_AMULET5:
				case ItemID.DIGSITE_PENDANT_5:
					charges = 5;
					break;
				case ItemID.AMULET_OF_GLORY6:
				case ItemID.AMULET_OF_GLORY_T6:
				case ItemID.GAMES_NECKLACE6:
				case ItemID.SKILLS_NECKLACE6:
					charges = 6;
					break;
				case ItemID.GAMES_NECKLACE7:
					charges = 7;
					break;
				case ItemID.GAMES_NECKLACE8:
					charges = 8;
					break;
				default:
					resetIcon();
					return;
			}
			jewelrySprite = client.createItemSprite(slot.getId(), charges, 1, SpritePixels.DEFAULT_SHADOW_COLOR, 1, false, CLIENT_DEFAULT_ZOOM);
		}
		if(configManager.getConfig(JewelryTrackerConfig.class).Slot().equals(Slot.BRACELET)) {
			slot = items[EquipmentInventorySlot.GLOVES.getSlotIdx()];

			switch (slot.getId()) {
				case ItemID.COMBAT_BRACELET:
					charges = 0;
					break;
				case ItemID.COMBAT_BRACELET1:
					charges = 1;
					break;
				case ItemID.COMBAT_BRACELET2:
					charges = 2;
					break;
				case ItemID.COMBAT_BRACELET3:
					charges = 3;
					break;
				case ItemID.COMBAT_BRACELET4:
					charges = 4;
					break;
				case ItemID.COMBAT_BRACELET5:
					charges = 5;
					break;
				case ItemID.COMBAT_BRACELET6:
					charges = 6;
					break;
				default:
					resetIcon();
					return;
			}
			jewelrySprite = client.createItemSprite(slot.getId(), charges, 1, SpritePixels.DEFAULT_SHADOW_COLOR, 1, false, CLIENT_DEFAULT_ZOOM);
		}
		if(configManager.getConfig(JewelryTrackerConfig.class).Slot().equals(Slot.RING)) {
			slot = items[EquipmentInventorySlot.RING.getSlotIdx()];

			switch (slot.getId()) {
				case ItemID.RING_OF_WEALTH:
				case ItemID.RING_OF_WEALTH_I:
					charges = 0;
					break;
				case ItemID.RING_OF_WEALTH_1:
				case ItemID.RING_OF_WEALTH_I1:
				case ItemID.RING_OF_DUELING1:
				case ItemID.SLAYER_RING_1:
				case ItemID.RING_OF_RETURNING1:
					charges = 1;
					break;
				case ItemID.RING_OF_WEALTH_2:
				case ItemID.RING_OF_WEALTH_I2:
				case ItemID.RING_OF_DUELING2:
				case ItemID.SLAYER_RING_2:
				case ItemID.RING_OF_RETURNING2:
					charges = 2;
					break;
				case ItemID.RING_OF_WEALTH_3:
				case ItemID.RING_OF_WEALTH_I3:
				case ItemID.RING_OF_DUELING3:
				case ItemID.SLAYER_RING_3:
				case ItemID.RING_OF_RETURNING3:
					charges = 3;
					break;
				case ItemID.RING_OF_WEALTH_4:
				case ItemID.RING_OF_WEALTH_I4:
				case ItemID.RING_OF_DUELING4:
				case ItemID.SLAYER_RING_4:
				case ItemID.RING_OF_RETURNING4:
					charges = 4;
					break;
				case ItemID.RING_OF_WEALTH_5:
				case ItemID.RING_OF_WEALTH_I5:
				case ItemID.RING_OF_DUELING5:
				case ItemID.SLAYER_RING_5:
				case ItemID.RING_OF_RETURNING5:
					charges = 5;
					break;
				case ItemID.RING_OF_DUELING6:
				case ItemID.SLAYER_RING_6:
					charges = 6;
					break;
				case ItemID.RING_OF_DUELING7:
				case ItemID.SLAYER_RING_7:
					charges = 7;
					break;
				case ItemID.RING_OF_DUELING8:
				case ItemID.SLAYER_RING_8:
					charges = 8;
					break;
				case ItemID.SLAYER_RING_ETERNAL:
					charges = -1;
					break;
				default:
					resetIcon();
					return;
			}
			jewelrySprite = client.createItemSprite(slot.getId(), charges, 1, SpritePixels.DEFAULT_SHADOW_COLOR, 1, false, CLIENT_DEFAULT_ZOOM);
		}
		if(jewelrySprite != null) {
			client.getSpriteOverrides().put(SpriteID.TAB_EQUIPMENT, jewelrySprite);
		}
	}

	public void resetIcon() {
		BufferedImage equipTab = spriteManager.getSprite(SpriteID.TAB_EQUIPMENT, 0);
		SpritePixels temp = ImageUtil.getImageSpritePixels(equipTab, client);
		client.getSpriteOverrides().put(SpriteID.TAB_EQUIPMENT, temp);
		client.getWidgetSpriteCache().reset();
	}

	@Override
	protected void shutDown() throws Exception
	{
		resetIcon();

		menuManager.removeManagedCustomMenu(FIXED_EQUIPMENT_TAB_NONE);
		menuManager.removeManagedCustomMenu(FIXED_EQUIPMENT_TAB_NECK);
		menuManager.removeManagedCustomMenu(FIXED_EQUIPMENT_TAB_RING);
		menuManager.removeManagedCustomMenu(FIXED_EQUIPMENT_TAB_BRACELET);

		menuManager.removeManagedCustomMenu(RESIZABLE_INVENTORY_TAB_NONE);
		menuManager.removeManagedCustomMenu(RESIZABLE_INVENTORY_TAB_NECK);
		menuManager.removeManagedCustomMenu(RESIZABLE_INVENTORY_TAB_RING);
		menuManager.removeManagedCustomMenu(RESIZABLE_INVENTORY_TAB_BRACELET);

		menuManager.removeManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_NONE);
		menuManager.removeManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_NECK);
		menuManager.removeManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_RING);
		menuManager.removeManagedCustomMenu(RESIZABLE_BOTTOM_LINE_INVENTORY_TAB_BRACELET);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{

	}

	@Provides
	com.jewelrytracker.JewelryTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(com.jewelrytracker.JewelryTrackerConfig.class);
	}
}
