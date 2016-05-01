package itemgen.entities;

import java.util.ArrayList;
import java.util.List;

import com.elmokki.Generic;

import itemgen.ItemGen;
import itemgen.misc.Command;

public class Effect extends Filter {

	public List<PathRequirement> magic_requirements = new ArrayList<PathRequirement>();
	public List<Command> itemCommands = new ArrayList<Command>();
	
	public Effect(ItemGen itemGen) {
		super(itemGen);
	}
	
	
	public <E extends Entity> void handleOwnCommand(String str)
	{

		List<String> args = Generic.parseArgs(str);
		
		try
		{
		
		if(args.get(0).equals("#path_requirement"))
		{
			args.remove(0);

			double cost = Double.parseDouble(args.get(0));
			int p1 = Generic.PathToInteger(args.get(1));
			int p2 = -1;
			if(args.size() > 2)
			{
					if(Generic.PathToInteger(args.get(2)) > -1)
					{
						p2 = Generic.PathToInteger(args.get(2));
					}
			}	
			
			int smin = 0;
			int smax = 999;
			if(args.contains("smin"))
			{
				int index = args.indexOf("smin");
				if(args.size() > index + 2)
				{
					smin = Integer.parseInt(args.get(index + 1));
				}
			}
			if(args.contains("smax"))
			{
				int index = args.indexOf("smax");
				if(args.size() > index + 2)
				{
					smax = Integer.parseInt(args.get(index + 1));
				}
			}	
			
			boolean freeorder = false;
			if(args.contains("freeorder"))
				freeorder = true;
			magic_requirements.add(new PathRequirement(cost, p1, p2, smin, smax, freeorder));
			
		}
		else if(args.get(0).equals("#itemcommand"))
		{
			this.itemCommands.add(Command.parseCommand(args.get(1)));
		}
		else
			super.handleOwnCommand(str);
		
		}
		catch(Exception e)
		{
			System.out.println("WARNING: " + str + " has insufficient arguments (" + this.name + ")");
		}
	}
}
