package itemgen;

import itemgen.entities.Effect;
import itemgen.entities.ItemTemplate;
import itemgen.entities.Part;
import itemgen.misc.Command;
import itemgen.naming.Name;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.imageio.ImageIO;








import com.elmokki.Drawing;
import com.elmokki.Generic;

public class Item {
	
	public int id = 0;
	public LinkedHashMap<String, Part> slotmap = new LinkedHashMap<String, Part>();
	public ItemTemplate template;
	public Color color = Color.white;
	public List<Effect> appliedFilters = new ArrayList<Effect>();
	public int level = 0;
	public double[] baseMagic = new double[8];
	public int p1 = 0;
	public int lv1 = 0;
	public int p2 = -1;
	public int lv2 = 0;
	public Name name = new Name();
	public int type = 1;
	public String descr = "";
	public CustomItem armor = null;
	public CustomItem weapon = null;
	
	
	public Item(ItemTemplate t)
	{
		this.template = t;
		
	}
	
	private int convertSlot()
	{
		String[] slots = {"1-h wpn", "2-h wpn", "misc", "armor", "helm", "shield", "boots"};
		int[] types = {1, 2, 8, 5, 6, 4, 7};
		
		for(int i = 0; i < slots.length; i++)
		{
			if(slots[i].equals(template.slot))
				return(types[i]);
		}
		
		System.out.println("NO SUITABLE TYPE FOUND FOR SLOT " + template.slot + "!");
		return(1);
	}
	
	private void updateCustomItem(CustomItem ci)
	{
		for(Effect eff : this.appliedFilters)
		{
			for(Command c : eff.itemCommands)
			{
				String old = null;
				if(ci.values.get(c.command.substring(1)) != null)
				{
					old = ci.values.get(c.command.substring(1));
				}
				
				if(old == null || !Generic.isNumeric(old))
				{
					ci.values.put(c.command.substring(1), Generic.listToString(c.args));
				}
				else if(Generic.isNumeric(old))
				{
					ci.values.put(c.command.substring(1), (Integer.parseInt(old) + Integer.parseInt(c.args.get(0)) + ""));
				}
			}
		}
	}
	
	public void writeItems(PrintWriter tw)
	{
		if(armor != null)
		{
			tw.println();
			updateCustomItem(armor);
			armor.write(tw);
		}
		if(weapon != null)
		{
			tw.println();
			updateCustomItem(weapon);
			weapon.write(tw);
		}
	}
	
	public void write(PrintWriter tw, String moddir)
	{
		this.type = convertSlot();
		
		if(this.id == 0)
		{
			System.out.println("ITEM WITHOUT PROPER ID! ABORT! ABORT!");
			return;
		}
		if(this.lv1 == 0)
		{
			System.out.println("ITEM WITHOUT PROPER PRIMARY PATH! ABORT! ABORT!");
			return;
		}
		
		/*
		try {
			this.draw(moddir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		tw.println("#selectitem " + id);
		tw.println("#spr \"" + moddir + "item_" + id + ".tga\"");
		tw.println("#name \"" + name + "\"");
		tw.println("#type " + type);
		tw.println("#constlevel " + level);
		tw.println("#mainpath " + p1);
		tw.println("#mainlevel " + lv1);
		if(lv2 > 0)
		{
			tw.println("#secondarypath " + p2);
			tw.println("#secondarylevel " + lv2);
		}
		if(armor != null)
			tw.println("#armor \"" + name.toString(this) + "\"");
		if(weapon != null)
			tw.println("#weapon " + weapon.id);
		
		for(Command c : this.getCommands())
		{
			tw.println(c);
		}
		
		tw.println("#descr \"" + descr + "\"");

		tw.println("#end");
	
	}
	
	
	public double[] getMagic()
	{
		double[] magic = baseMagic;
		for(Effect e : appliedFilters)
		{
			for(int i = 0; i < 8; i++)
				magic[i] += e.magic[i];
		}
		
		return magic;
	}
	
	
	public void addEffect(Effect f)
	{
		this.appliedFilters.add(f);
	}
	
	public List<Command> getCommands()
	{
		List<Command> coms = new ArrayList<Command>();
		
		coms.addAll(this.template.commands);
		
		for(Effect e : appliedFilters)
			for(Command c : e.commands)
				handleCommand(coms, c);
		
		return coms;
	}
	
	protected void handleCommand(List<Command> commands, Command c)
	{


		// List of commands that may appear more than once per unit
		List<String> uniques = new ArrayList<String>();

		
		

		c = new Command(c.command, c.args);
		Command old = null;
		for(Command cmd : commands)
		{
			if(cmd.command.equals(c.command))
				old = cmd;
		}
		
		


		if(old != null && !uniques.contains(c.command))
		{
	
			for(int i = 0; i < c.args.size(); i++)
			{
			
				String arg = c.args.get(i);
				String oldarg = old.args.get(i);
				if(arg.startsWith("+") || arg.startsWith("-"))
				{
					if(arg.startsWith("+"))
						arg = arg.substring(1);
					

					try
					{
		
						
						oldarg = "" + (Integer.parseInt(oldarg) + Integer.parseInt(arg));
						old.args.set(i, oldarg);
					}
					catch(NumberFormatException e)
					{
						System.out.println("FATAL ERROR: Argument parsing " + oldarg + " + " + arg + " on " + c.command + " caused crash.");
					}
					continue;
			
				}
				else if(arg.startsWith("*"))
				{
						
					arg = arg.substring(1);
					try
					{
			

						oldarg = "" + (int)(Integer.parseInt(oldarg) * Double.parseDouble(arg));
						old.args.set(i, oldarg);
					}
					catch(Exception e)
					{
						System.out.println("FATAL ERROR: Argument parsing " + oldarg + " * " + arg + " on " + c.command + " caused crash.");
					}
					continue;
				}
				else
				{
					if(!uniques.contains(c.command))
					{
						oldarg = arg;
						old.args.set(i, oldarg);
						continue;
	
					}
					else
					{
						commands.add(c);
						continue;
					}
				}
			}
		}
		else
		{

			
			for(int i = 0; i < c.args.size(); i++)
			{
				if(c.args.get(i).startsWith("+"))
					c.args.set(i, c.args.get(i).substring(1));
				else if(c.args.get(i).startsWith("*"))
					c.args.set(i, 0 + "");

			}
			commands.add(c);
		}
	
		

	}
	
	public void draw(String spritedir) throws IOException
	{
		Drawing.writeTGA(this.render(0), "" + spritedir + "item_" + this.id + ".tga");
	}
	
	
	public BufferedImage render() throws IOException
	{
		return render(0);
	}
	
	public BufferedImage render(int offsetX) throws IOException
	{
		
		Item u = this;

		// Get width and height;
		
		String renderorder = template.renderorder;
		if(renderorder.equals(""))
		{
			for(String str : slotmap.keySet())
				renderorder = renderorder + str + " ";
			
			renderorder = renderorder.trim();
		}
	
		BufferedImage base;
		String baseslot = renderorder.split(" ")[0];
		try
		{ 
			base = ImageIO.read(new File("./", u.slotmap.get(baseslot).sprite));
		}
		catch(Exception e)
		{
			System.out.println("Error initializing sprite for " + template.name + " / " + template.slot + " / " + template.types + " from part slot " + baseslot + ".");
			base = new BufferedImage(64, 64, BufferedImage.TYPE_3BYTE_BGR);
		}
	
		// create the new image, canvas size is the max of both image sizes
		int w = base.getWidth();
		int h = base.getHeight();
		
		

		
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, w, h);
		

		

		for(String s : renderorder.split(" "))
		{


			renderPart(g, getSlot(s), true, offsetX);

		}
		
			
		// Save as new image
		return combined; 
	}
	
	public Part getSlot(String s)
	{
		return slotmap.get(s);

	}
	
	public void setSlot(String slotname, Part newitem)
	{
		
	
	
		slotmap.put(slotname, newitem);
		if(newitem == null)
			return;


		
		//handleDependency(slotname);
		

		
	}
	
	private void renderPart(Graphics g, Part i, boolean useoffset, int extraX) throws IOException
	{
		if(i == null)
			return;
		
		
		
		int offsetx = 0;
		int offsety = 0;
		if(!useoffset)
			i.render(g, false, 0, 0, this.color, extraX);
		else
			i.render(g, true, offsetx, offsety, this.color, extraX);
	}
}
