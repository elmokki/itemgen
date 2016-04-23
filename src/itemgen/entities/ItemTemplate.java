package itemgen.entities;


import itemgen.ItemGen;
import itemgen.misc.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.elmokki.Generic;




public class ItemTemplate extends Filter {
	
	private LinkedHashMap<String, List<Part>> parts = new LinkedHashMap<String, List<Part>>();
	public List<Effect> effects = new ArrayList<Effect>();
	public List<String> types = new ArrayList<String>();
	public String renderorder = "";
	public List<Command> commands = new ArrayList<Command>();
	public String slot = "";
	public List<String> weapons = new ArrayList<String>();
	public List<String> armor = new ArrayList<String>();

	public ItemTemplate(ItemGen nationGen)
	{
		super(nationGen);
	}
	
	public Set<String> getListOfSlots()
	{
		return parts.keySet();
	}
	
	public List<Part> getItems(String slot)
	{
		if(this.parts.get(slot) == null)
			return null;
		
		List<Part> parts = new ArrayList<Part>();
		for(Part i : this.parts.get(slot))
			parts.add(i);
		
		return parts;
	}
	
	/**
	 * Loads item definitions from file.
	 * @param file
	 * @throws IOException
	 */
	private List<Part> loadItems(String file, int offsetx, int offsety, String slot) throws IOException
	{


		    List<Part> items = new ArrayList<Part>();
			items.addAll(Part.readFile(nationGen, file, Part.class));
			for(Part i : items)
			{
				try
				{
					i.slot = slot;
				}
				catch(NullPointerException e)
				{
					System.out.println("WARNING! File " + file + " produced a null item! Make sure there is a #new command for each item!");
				}
			}
			
			if(offsetx != 0 || offsety != 0)
			{
				for(Part i : items)
				{
					i.offsetx += offsetx;
					i.offsety += offsety;
				}
			}
	

			return items;
	}
	
	

	
	
	public String toString()
	{
		String str = this.types.toString();
		if(this.name != null && !this.name.equals(""))
			str = str + " (" + this.name + ")";
		
		return str;
	}
	
	public <E extends Entity> void handleOwnCommand(String str)
	{
		List<String> args = Generic.parseArgs(str);
		if(args.size() == 0)
			return;
		
		if(args.get(0).equals("#type"))
		{
			this.types.add(args.get(1));
		}
		else if(args.get(0).equals("#renderorder"))
		{
			this.renderorder = args.get(1);
		}
		else if(args.get(0).equals("#command"))
		{
			this.commands.add(Command.parseCommand(args.get(1)));
		}
		else if(args.get(0).equals("#weapon"))
		{
			this.weapons.add(args.get(1));
		}
		else if(args.get(0).equals("#armor"))
		{
			this.armor.add(args.get(1));
		}
		else if(args.get(0).equals("#slot"))
		{
			this.slot = args.get(1);
		}
		else if(args.get(0).equals("#effects"))
		{
			List<Effect> tef = nationGen.effects.get(args.get(1));
			if(tef != null)
				this.effects.addAll(tef);
			else
				System.out.println("Effect file " + args.get(1) + " was not found!");
		}
		else if(args.get(0).equals("#load"))
		{

			int offsetx = Generic.getNextArgument(args, "offsetx", 0);
			int offsety = Generic.getNextArgument(args, "offsety", 0);
			
			if(offsety == 0 && offsetx == 0 && args.size() >= 5)
			{
				offsetx = Integer.parseInt(args.get(3));
				offsety = Integer.parseInt(args.get(4));
			}
			
			if(!args.get(2).startsWith("."))
				args.set(2, "." + args.get(2));
			
	
			List<Part> set = null;
			try {
				set = loadItems(args.get(2), offsetx, offsety, args.get(1));
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error loading parts from " + args.get(2) + " because of " + e.getCause() + ": " + e.getMessage());
				return;
			}
			
	
			// Put itemset to it's place
			if(parts.get(args.get(1)) == null)
				this.parts.put(args.get(1), set);
			else
				parts.get(args.get(1)).addAll(set);
		
		}
		else
			super.handleOwnCommand(str);

	}
	
	
	
	
	
}
