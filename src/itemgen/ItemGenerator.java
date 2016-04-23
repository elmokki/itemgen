package itemgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.elmokki.Drawing;
import com.elmokki.Generic;

import itemgen.entities.Effect;
import itemgen.entities.Entity;
import itemgen.entities.ItemTemplate;
import itemgen.entities.Part;
import itemgen.misc.ChanceIncHandler;

public class ItemGenerator {
	private int id = 500;
	private Random r;
	private ItemGenMod igm;
	private ChanceIncHandler chandler;
	private List<ItemTemplate> templates;
	public int[] points = {2, 3, 4, 5, 7};
	List<Item> magicitems = new ArrayList<Item>();

	public ItemGenerator(ItemGenMod igm)
	{
		this.r = igm.r;
		this.igm = igm;
		this.chandler = new ChanceIncHandler();
		
		templates = new ArrayList<ItemTemplate>();
		for(String key : igm.itemGen.items.keySet()) 
			templates.addAll(igm.itemGen.items.get(key));
	}

	int getPath()
	{
		// TODO: Proper item type selection
		return r.nextInt(8);
	}
	
	private List<ItemTemplate> getTemplatesOfType(String type, List<ItemTemplate> templates)
	{
		List<ItemTemplate> temps = new ArrayList<ItemTemplate>();
		
		for(ItemTemplate t : templates)
			if(t.slot.equals(type))
				temps.add(t);
		
		return temps;
	}
	
	
	private int[] getPathDistribution(int level)
	{
		int[] paths = new int[8];
		for(int i = 0; i < 386; i++)
		{
			if(!igm.itemGen.itemdb.GetValue(i, "name").equals("") && (igm.itemGen.itemdb.GetInteger(i, "con") == level || level == -1))
			{
				paths[Generic.shortPathToInteger(igm.itemGen.itemdb.GetValue(i, "p1"))] += 1;


			}
		}
		return paths;
	}
	
	private double getItemTypeShareAtLevel(int level, String type)
	{
		double itams = igm.getItemsAtLevel(level);
		double correctitams = 0;
		for(int i = 0; i < 386; i++)
		{
			if(!igm.itemGen.itemdb.GetValue(i, "name").equals("") && (igm.itemGen.itemdb.GetInteger(i, "con") == level || level == -1))
			{
				if(igm.itemGen.itemdb.GetValue(i, "type").equals(type))
					correctitams++;
			}
		}
		return (correctitams / itams);
	}
	
	
	private String getRandomItemType(int level)
	{
		double rand = r.nextDouble();
		double at = 0;
		for(String str : igm.slots)
		{
			if(getItemTypeShareAtLevel(level, str) > 0)
			{
				at += getItemTypeShareAtLevel(level, str);
				if(at >= rand)
					return str;
			}
		}
		
		System.out.println("Error randomizing an item type for level " + level);
		return "FAIL";
	}
	
	
	public Item generateItem(int level, int path)
	{

		Item temp = new Item(null);
		temp.level = level;
		
		// Select the item template
		// Takes into account vanilla item type distribution already
		ItemTemplate t = null;
		int spins = 0;
		do
		{
			String place = getRandomItemType(level);
			List<ItemTemplate> temps = getTemplatesOfType(place, templates);
			t = Entity.getRandom(r, chandler.handleChanceIncs(temps, temp, magicitems));
			spins++;
		} 
		while(t == null || spins > 50);
		
		if(spins > 50)
			System.out.println("ERROR RANDOMIZING AN ITEM FOR LEVEL " + level);
		
		// Fetch possible effects
		List<Effect> effects = new ArrayList<Effect>();
		effects.addAll(t.effects);

		// Basic item stats
		Item item = new Item(t);
		item.level = level;
		item.id = id++;
		
		// Determine path
		item.baseMagic[path] += level + 2;
		
		// Entirely random color for now
		item.color = Drawing.getColor(r);
		
		item.p1 = path;
		

		
		// If the item needs ingame armor and/or weapon, add them
		// TODO: Weighing?
		if(t.armor.size() > 0)
		{
			item.armor = CustomItem.fromDB(t.armor.get(r.nextInt(t.armor.size())), true, igm.itemGen);
			item.armor.id = igm.itemGen.idHandler.nextArmorId();
		}
		if(t.weapons.size() > 0)
		{
			item.weapon = CustomItem.fromDB(t.weapons.get(r.nextInt(t.weapons.size())), false, igm.itemGen);
			item.weapon.id = igm.itemGen.idHandler.nextWeaponId();
		}
		
		// Magic effects
		

			List<Effect> tefs = chandler.getPossibleAdditions(effects, item);
			tefs = chandler.getRangeOfLevel(tefs, level - 2, level + 2);
			Effect eff = Effect.getRandom(r, chandler.handleCoolness(chandler.handleChanceIncs(tefs, item, magicitems), item));
			if(eff != null)
			{
				item.addEffect(eff);
				effects.remove(eff);

				double pathlevel = eff.cost;
				double levelleft = level;
				
				levelleft-= eff.level;
				
				boolean fine = true;		
				// If the effect was a low level one, try reducing cost
				if(levelleft >= 2)
				{
					if(pathlevel >= 3 && r.nextDouble() > 0.25)
					{
						pathlevel -= levelleft;
						fine = true;
					}
					else
						fine = false;
				}
				// If the effect was a high level one
				if(levelleft <= -1)
				{
					pathlevel += -levelleft;
					fine = true;
				}
				
					
				if(!fine)
				{
					tefs = chandler.getPossibleAdditions(effects, item);
					tefs = chandler.getRangeOfLevel(tefs, level - 2, level - 2);
					eff = Effect.getRandom(r, chandler.handleCoolness(chandler.handleChanceIncs(tefs, item, magicitems), item));
					
					if(eff != null)
					{
						pathlevel += eff.cost;
						item.addEffect(eff);
						effects.remove(eff);
					}
				}
				
				item.lv1 = (int) Math.max(1, Math.round(pathlevel));
				item.level = level;
				System.out.println("-> " + item.p1 + ": " + item.lv1 + " - " + item.level + " / " + item.appliedFilters);
			}
			else
			{
				return null;
			}
		
		
		
		
		// Get looks for items
		// TODO: Ideally no two items would be identical (or extremely nearly so) in looks, but we need more item parts for that
		// TODO: ...or even better: Disable identical looks check when there are too many items being generated
		// TODO: Make colors more suitable for the elements
		for(String slot : t.getListOfSlots())
		{
			Part p = Entity.getRandom(r, chandler.handleCoolness(chandler.handleChanceIncs(t.getItems(slot), item, magicitems), item));
			item.setSlot(slot, p);
		}
		
		return item;
	}
}
