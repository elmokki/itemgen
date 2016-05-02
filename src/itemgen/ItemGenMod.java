package itemgen;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class ItemGenMod {
	
	String[] slots = {"1-h wpn", "2-h wpn", "misc", "armor", "helm", "shield", "boots"};
	
	public List<Item> items = new ArrayList<Item>();
	
	public Random r;
	public int seed = -1;
	public ItemGen itemGen;
	
	
	public ItemGenMod(ItemGen itemGen, int seed)
	{
		this.seed = seed;
		r = new Random(seed);
		this.itemGen = itemGen;
	}

	int getItemsAtLevel(int level)
	{
		int itams = 0;
		for(int i = 0; i < 386; i++)
		{
			if(!itemGen.itemdb.GetValue(i, "name").equals("") && (itemGen.itemdb.GetInteger(i, "con") == level || level == -1))
			{
				itams++;

			}
		}
		return itams;
	}
	
	public void generate(int count)
	{
		

		
		ItemGenerator ig = new ItemGenerator(this);

		int itemcount = 0;
		int step = 0;
		
	
		for(int level = 0; level <= 8; level += 2)
		{
			int amount = (int) Math.round((double)count * (double)getItemsAtLevel(level) / (double)getItemsAtLevel(-1));
			
			
			for(int i = 0; i < amount; i++)
			{

				
				
				int path = ig.getPath();
				
				Item item = null;
				while(item == null)
				{
					item = ig.generateItem(level, path);
				}
				
				items.add(item);
				
				// Draw dots to show progress
				itemcount++;
				if(itemcount > count/10*step)
				{
					System.out.print(".");
					step++;
				}
				
				
				
				
	

				
			}
	
			
		}

	}
	
	
	public void draw(String dir)
	{
		new File("./mods/" + dir).mkdir();
		for(Item item : this.items)
			try {
				item.draw("./mods/" + dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	
	public void writeItems(PrintWriter tw)
	{
		for(Item item : this.items)
			item.writeItems(tw);
	}
	
	
	public void write(PrintWriter tw, String dir)
	{
		for(Item item : this.items)
		{
			tw.println();
			item.write(tw, dir);
		}
		
 

	}


	
	



	
}
