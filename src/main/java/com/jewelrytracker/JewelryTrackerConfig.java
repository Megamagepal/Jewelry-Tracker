package com.jewelrytracker;

import com.jewelrytracker.config.Slot;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.gpu.config.ColorBlindMode;

@ConfigGroup("jewelrytracker")
public interface JewelryTrackerConfig extends Config
{
	@ConfigItem(
			keyName = "jewelrySlot",
			name = "Jewelry Slot",
			description = "Choose which slot to keep track of",
			position = 1
	)
	default Slot Slot()
	{
		return Slot.NONE;
	}
}
