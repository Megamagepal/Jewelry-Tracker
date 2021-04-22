package com.example;

import com.jewelrytracker.JewelryTrackerPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class JewelryTrackerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(JewelryTrackerPlugin.class);
		RuneLite.main(args);
	}
}