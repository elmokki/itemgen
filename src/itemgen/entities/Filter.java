package itemgen.entities;

import itemgen.ItemGen;
import itemgen.misc.Command;

import java.util.ArrayList;
import java.util.List;

import com.elmokki.Generic;



public class Filter extends Entity {
	public List<Command> commands = new ArrayList<Command>();
	public List<String> chanceincs = new ArrayList<String>();
	public List<String> types = new ArrayList<String>();
	public double level = 0;
	public double cost = 0;
	public Filter(ItemGen itemGen) {
		super(itemGen);
	}
	
	
		public List<Command> getCommands()
		{
			return this.commands;
		}
	
		public <E extends Entity> void handleOwnCommand(String str)
		{
	
			List<String> args = Generic.parseArgs(str);
			
			try
			{
			
			if(args.get(0).equals("#command") || args.get(0).equals("#define"))
			{
				this.commands.add(Command.parseCommand(args.get(1)));
			}
			else if(args.get(0).equals("#type") || args.get(0).equals("#category"))
				this.types.add(args.get(1));
			else if(args.get(0).equals("#chanceinc"))
			{
				args.remove(0);
				this.chanceincs.add(Generic.listToString(args));
			}
			else if(args.get(0).equals("#power") || args.get(0).equals("#coolness"))
			{
				if(this.level == 0)
				{
					this.level = Integer.parseInt(args.get(1));
					this.cost = 1;
				}
			}
			else if(args.get(0).equals("#level_cost"))
			{
				this.level = Double.parseDouble(args.get(1));
				this.cost = Double.parseDouble(args.get(2));
			}
			else
				super.handleOwnCommand(str);
			
			}
			catch(IndexOutOfBoundsException e)
			{
				System.out.println("WARNING: " + str + " has insufficient arguments (" + this.name + ")");
			}
		}

}
