package itemgen.entities;

import java.util.ArrayList;
import java.util.List;

import com.elmokki.Generic;

import itemgen.ItemGen;
import itemgen.misc.Command;

public class Effect extends Filter {

	public double[] magic = new double[8];
	public List<Command> itemCommands = new ArrayList<Command>();
	
	public Effect(ItemGen itemGen) {
		super(itemGen);
	}
	
	
	public <E extends Entity> void handleOwnCommand(String str)
	{

		List<String> args = Generic.parseArgs(str);
		
		try
		{
		
		if(args.get(0).equals("#element"))
		{
			int index = Generic.PathToInteger(args.get(1));
			magic[index] += Double.parseDouble(args.get(2));
		}
		else if(args.get(0).equals("#itemcommand"))
		{
			this.itemCommands.add(Command.parseCommand(args.get(1)));
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
