package itemgen.naming;

import java.util.Random;

import com.elmokki.Generic;

import itemgen.Item;
import itemgen.ItemGen;
import itemgen.ItemGenMod;
import itemgen.entities.Entity;
import itemgen.misc.ChanceIncHandler;

public class ItemNamer {
	
	ItemGen itemGen;
	private Random r;
	
	public ItemNamer(ItemGen itemGen)
	{
	
		this.itemGen = itemGen;
	}
	
	public void generateNames(ItemGenMod igm)
	{
		r = igm.r;
				
		ChanceIncHandler chandler = new ChanceIncHandler();
		for(Item i : igm.items)
		{
			
	
			i.name.type = Entity.getRandom(r, chandler.handleChanceIncs(itemGen.bases, i, null)).toPart();

			
			
			boolean prefix = r.nextDouble() < 0.75;
			boolean noun = r.nextDouble() < 0.75;
			
			if(i.level == 8)
			{
				prefix = r.nextBoolean();
			}
			
			
			NameFilter part;
			if(noun || !prefix)
				part = Entity.getRandom(r, chandler.handleChanceIncs(itemGen.nouns, i, null));
			else 
				part = Entity.getRandom(r, chandler.handleChanceIncs(itemGen.adjectives, i, null));
			
			
			
			
			if(prefix)
			{
				i.name.prefix = part.toPart();
			}
			else
			{
				part = Entity.getRandom(r, chandler.handleChanceIncs(itemGen.nouns, i, null));
				i.name.suffix = part.toPart();
	

			
				
				if(i.level >= 6 || r.nextDouble() < 0.05*i.level)
				{
					i.name.suffix = part.toPart();
					i.name.suffixprefix = Entity.getRandom(r, chandler.handleChanceIncs(itemGen.adjectives, i, null)).toPart();
	
				}
				
				double rand = r.nextDouble();
				if(rand < 0.4)
					i.name.definitesuffix = true;
				else if(rand < 0.8)
					i.name.pluralsuffix = true;
				else
				{
					i.name.definitesuffix = true;
					i.name.pluralsuffix = true;
				}
	
			}
			
		
			
			if(i.armor != null)
				i.armor.values.put("name", "\"" + i.name + "\"");
			if(i.weapon != null)
				i.weapon.values.put("name", "\"" + i.name + "\"");
			
			
			System.out.println(i.template.name + " - " + i.name + ": " + i.appliedFilters + " - level " + i.level + " - " + i.lv1 + Generic.integerToShortPath(i.p1) + " " + i.lv2 + Generic.integerToShortPath(i.p2));

		}
		
		
	}
}
