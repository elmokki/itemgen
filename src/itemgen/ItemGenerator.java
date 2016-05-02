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
import itemgen.entities.PathRequirement;
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
	
	
	private void handleItems(Item item)
	{
		ItemTemplate t = item.template;
		
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
	}
	
	private Effect getEffect(List<Effect> effects, PathRequirement pr, Item item, int min, int max)
	{
		List<Effect> tefs = chandler.getPossibleAdditions(pr, effects, item.p1, item.p2, true);
		tefs = chandler.getRangeOfLevel(tefs, min, max);
		
		Effect eff = Effect.getRandom(r, chandler.handleCoolness(chandler.handleChanceIncs(tefs, item, magicitems), item));
		return eff;
	}
	
	private PathRequirement getPathRequirement(Effect eff, Item i)
	{
		List<PathRequirement> suitable = new ArrayList<PathRequirement>();
		for(PathRequirement pr : eff.magic_requirements)
		{
			boolean ok = true;
			
			
			if(pr.p1 > -1)
			{
				if(!Generic.matchingPaths(i.p1, pr.p1) && (i.appliedFilters.size() == 0 || pr.p2 > -1 || !Generic.matchingPaths(i.p2, pr.p1)))
					ok = false;
			}
			
			if(pr.p2 > -1)
			{
				if(!Generic.matchingPaths(i.p2, pr.p2))
					ok = false;
			}
			
			if(ok)
				suitable.add(pr);
		}
		
		
		if(suitable.size() > 1)
			return suitable.get(r.nextInt(suitable.size() - 1));
		else if(suitable.size() == 1)
			return suitable.get(0);
		else
		{
			return new PathRequirement(0, i.p1, -1, 0, 999, true);
		}
	}
	
	private PathRequirement combineRequirements(int level1, int level2, PathRequirement pr1, PathRequirement pr2)
	{
		
		if(pr2.p1 > 8 && Generic.matchingPaths(pr1.p1, pr2.p1))
			pr2.p1 = pr1.p1;
		if(pr2.p2 > 8 && Generic.matchingPaths(pr1.p2, pr2.p2))
			pr2.p2 = pr1.p1;
		
		int smin = pr1.smin;
		int smax = pr1.smax;
		int p1 = pr1.p1;
		int p2 = pr1.p2;
		
		
		double multi = 1 - (1 - (double)level2/(double)level1) / 2;
		double cost = 0;
		
		if(pr1.cost >= pr2.cost*multi)
		{
			cost = pr1.cost + 0.5*multi*pr2.cost;
		}
		else
		{
			cost = multi*pr2.cost + 0.5*pr1.cost;
		}
		
		if(pr1.p2 == -1)
		{
			p2 = pr2.p1;
		}
		
		
		PathRequirement pr_result = new PathRequirement(cost, p1, p2, smin, smax, true);
			
		return pr_result;
	}
	
	private void priceItem(Item i)
	{
		
		double cost = Math.round(i.pr.cost);
		int smin = i.pr.smin;
		int smax = i.pr.smax;
		
		if(i.p2 > -1 && i.pr.p2 > -1)
			smin = Math.max(1, smin);
		else
			smax = 0;
		
		
		int primcost = (int)Math.ceil((cost - smin) * (0.5 + (r.nextDouble() * 0.5))); 
		primcost = Math.max(1, primcost);
		
		int seccost = (int)Math.floor(cost - primcost);
		
		seccost = Math.min(seccost, smax);
		primcost = (int)(Math.ceil(cost) - seccost);
		
		i.lv1 = primcost;
		i.lv2 = seccost;
		
		if(i.lv2 == 0)
			i.p2 = -1;
		
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
		
		// First path
		item.p1 = path;
		
		// Secondary path
		if(r.nextDouble() < 10.15)
		{
			while(item.p2 == -1 || item.p2 == item.p1)
			{
				item.p2 = getPath();
			}
		}


		
		// If the item needs ingame armor and/or weapon, add them
		handleItems(item);
		
		// Magic effects

			Effect eff = getEffect(effects, null, item, level - 2, level + 2);
			
			
			if(eff != null)
			{
				item.addEffect(eff);
				effects.remove(eff);

				item.pr = getPathRequirement(eff, item).getCopy();
				if(item.pr.p1 > 8)
					item.pr.p1 = item.lv1;
				if(item.pr.p2 > 8)
					item.pr.p2 = item.lv2;
				
									
				
				double levelleft = level;
				levelleft -= eff.level;
				
				boolean fine = true;	
				
				// If the effect was a low level one, try reducing cost
				if(levelleft >= 2)
				{
					if(item.pr.cost >= (levelleft / 2) + 1 && (r.nextDouble() < 0.66 || item.pr.cost > level))
					{
						item.pr.cost -= levelleft / 2;
						fine = true;
					}
					else // If we won't reduce cost, we'll give an another effect
						fine = false;
				}
				
				// If the effect was a high level one, increase cost
				if(levelleft <= -1)
				{
					item.pr.cost -= levelleft / 2;
					fine = true;
				}
				
	
				if(item.pr.cost <= level - 2 && r.nextDouble() > 0.66)
					fine = false;
				
				//// Give an another effect	
				

				if(!fine)
				{
					
					eff = getEffect(effects, item.pr, item, level - 2, level - 2);
				
					// failsafe
					if(eff == null && item.p2 == -1)
					{
				
						while(item.p2 == -1 || item.p2 == item.p1)
						{
							item.p2 = getPath();
						}
						eff = getEffect(effects, item.pr, item, level - 2, level - 2);

					}
					
					// failsafe 2
					if(eff == null)
						eff = getEffect(effects, item.pr, item, level - 6, level - 2);


					if(eff != null)
					{
						item.addEffect(eff);
						effects.remove(eff);
						item.pr = this.combineRequirements(level, (int) eff.level, item.pr, getPathRequirement(eff, item).getCopy());

					}
					
				}
				
				
				this.priceItem(item);
				
			
			}
			else
			{
				// If this happens, no effect could be found for given slot-level combination. 
				// This is bad, but to be expected if the amount of content is low.
				//
				// It could also even be desireable at some point if, for example, we wanted
				// to avoid too many too similar items.
				
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
