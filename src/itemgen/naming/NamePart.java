package itemgen.naming;

import itemgen.Item;
import itemgen.misc.Command;

import java.util.ArrayList;
import java.util.List;




import com.elmokki.Generic;


public class NamePart {
	public String text = "";
	public boolean weak = false;
	public List<String> elements = new ArrayList<String>();
	public int minimumelements = 0;
	public List<String> tags = new ArrayList<String>();
	
	
	public String toString(Item u)
	{
		if(u == null)
			return this.toString();
		
		String str = this.text;
		
		if(Generic.containsTag(tags, "commandvariant"))
		{
			for(String tag : this.tags)
			{
				List<String> args = Generic.parseArgs(tag);
				if(args.get(0).equals("commandvariant"))
				{
					String command = args.get(1);
					for(Command c : u.getCommands())
						if(c.command.equals(command))
						{
							boolean ok = true;
							
							if(args.contains("negative") && Integer.parseInt(c.args.get(0)) >= 0)
								ok = false;
							if(args.contains("positive") && Integer.parseInt(c.args.get(0)) <= 0)
								ok = false;
							
							if(ok)
							{
								str = args.get(args.size() - 1);
								break;
							}
						}
				}
			}
		}


		return str;
	}
	
	public String toString()
	{
		return this.text;
	}
	
	public NamePart(String str)
	{
		this.text = str;
	}
	
	public NamePart()
	{

	}
	
	public NamePart getCopy()
	{
		NamePart part = new NamePart();
		part.text = this.text;
		part.weak = this.weak;
		part.elements.addAll(this.elements);
		part.minimumelements = this.minimumelements;
		return part;
	}
	
	public static NamePart fromLine(String line)
	{
		if(line.startsWith("-") || line.equals(""))
			return null;
		
		NamePart part = new NamePart();
		List<String> args = Generic.parseArgs(line);
		

		part.text = args.get(0);
		if(args.size() > 1)
		{
			part.minimumelements = Integer.parseInt(args.get(1));
		}
		// If part isn't longer it's minimum elements must be 0, as elements will be defined later
		if(args.size() < 3)
		{
			part.minimumelements = 0;
			return part;
		}
		
		args.remove(0);
		args.remove(0);
		
		for(String str : args)
		{
			if(str.equals("weak"))
				part.weak = true;
			else if(Generic.PathToInteger(str) != -1)
				part.elements.add(str);
			else
				part.tags.add(str);
		}
		
		return part;
	}
	
}
