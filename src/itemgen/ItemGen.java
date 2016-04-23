package itemgen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;





import com.elmokki.Dom3DB;
import com.elmokki.Generic;

import itemgen.entities.Effect;
import itemgen.entities.Filter;
import itemgen.entities.ItemTemplate;

import itemgen.misc.ResourceStorage;
import itemgen.naming.ItemNamer;
import itemgen.naming.NameFilter;

public class ItemGen {
	
	public static String version = "0.0.1";
	public static String date = "12th of July 2014";
	
	public int settings = 0;
	public ResourceStorage<ItemTemplate> items = new ResourceStorage<ItemTemplate>(ItemTemplate.class, this);
	public ResourceStorage<Effect> effects = new ResourceStorage<Effect>(Effect.class, this);
	

	public Dom3DB weapondb;
	public Dom3DB armordb;
	public Dom3DB itemdb;
	

	public List<Filter> elements = new ArrayList<Filter>();
	
	public List<NameFilter> nouns = new ArrayList<NameFilter>();
	public List<NameFilter> adjectives = new ArrayList<NameFilter>();
	public List<NameFilter> bases = new ArrayList<NameFilter>();

	
	private Random r = new Random();
	int seed;
	public IdHandler idHandler;
	
	public ItemGen()
	{
		try {
			
			System.out.print("Loading Edi's Dom4DB...");
			weapondb =  new Dom3DB("weapon.csv");
			armordb =  new Dom3DB("armor.csv");
			itemdb =  new Dom3DB("items.csv");
			System.out.println(" Done!");

			System.out.print("Loading definitions...");
			effects.loadFolder("./data/effects/");
			items.loadFolder("./data/items/");
			
			nouns = NameFilter.readFile(this, "./data/names/nouns.txt", NameFilter.class);
			adjectives = NameFilter.readFile(this, "./data/names/adjectives.txt", NameFilter.class);
			bases = NameFilter.readFile(this, "./data/names/basenames.txt", NameFilter.class);

			elements = Filter.readFile(this, "./data/elements.txt", Filter.class);
			System.out.println(" Done!");

			idHandler = new IdHandler();
			idHandler.loadFile("forbidden_ids.txt");
	        

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
	
	private ItemGenMod mod;
	public void generate(int amount, int seed)
	{
		this.seed = r.nextInt();
		System.out.print("Generating " + amount + " items with seed " + seed);
		mod = new ItemGenMod(this, seed);
		mod.generate(amount);
		System.out.println(" Done!");

		System.out.print("Naming items...");

		ItemNamer itemNamer = new ItemNamer(this);
		itemNamer.generateNames(mod);
		System.out.println(" Done!");

	}
	
	public void write()
	{
		String dir = "itemgen_" + mod.seed + "/";

		System.out.print("Drawing sprites...");
		mod.draw(dir);
		System.out.println(" Done!");

		System.out.print("Writing mod...");

		FileWriter fstream = null;
		try {
			fstream = new FileWriter("./mods/itemgen_" + mod.seed + ".dm");
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter tw = new PrintWriter(fstream);
		
		tw.println("#modname \"ItemGen, seed " + mod.seed + "\"");
		tw.println("#description \"Randomly generated mod that generates new magic items!\"");
		tw.println("#version 1.00");
		tw.println("#domversion 4.00");
		tw.println();

		tw.println("-- Weapon/Armor");
		tw.println();

		mod.writeItems(tw);

		tw.println("-- Items");
		tw.println();
		
		mod.write(tw, dir);
		
		// Hiding old stuff
		
		System.out.println(" Done!");

		
		if(Generic.containsBitmask(settings, 1) || Generic.containsBitmask(settings, 2))
		{
			System.out.print("Hiding vanilla items sprites...");
			hideOld(!Generic.containsBitmask(settings, 2), !Generic.containsBitmask(settings, 1), tw);
			System.out.println(" Done!");

		}
		tw.flush();
		tw.close();
		
		
		
		System.out.println("All done!");

	}
	
	public void hideOld(boolean leaveboosters, boolean leavenonboosters, PrintWriter tw)
	{
		tw.println();
		tw.println("Hiding vanilla items");
		tw.println("------------");

		for(int i = 0; i < 386; i++)
		{
			
			boolean booster = false;
			for(int j = 0; j < 9; j++)
			{
				String p = Generic.integerToShortPath(j);
				if(this.itemdb.GetInteger(i, p) > 0)
					booster = true;
				
			}
			
		
			if(!itemdb.GetValue(i, "name").equals("") && !(booster && leaveboosters) && !(!booster && leavenonboosters))
			{
				tw.println();
				tw.println("#selectitem " + i);
				tw.println("#constlevel 12");
				tw.println("#end");
			}
		}
	}
	
	

	
}

