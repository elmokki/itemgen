package itemgen.naming;

import itemgen.ItemGen;
import itemgen.entities.Entity;
import itemgen.entities.Filter;

import java.util.List;

import com.elmokki.Generic;



public class NameFilter extends Filter {

	
	public int rank = -1;
	
	
	public NamePart toPart()
	{
		NamePart np = new NamePart(this.name);
		np.tags.addAll(this.tags);
		return np;
	}
	
	public NameFilter(ItemGen itemGen) {
		super(itemGen);
	}

	
	public <E extends Entity> void handleOwnCommand(String str)
	{

		List<String> args = Generic.parseArgs(str);
		
		try
		{
		
		if(args.get(0).equals("#rank"))
			this.rank = Integer.parseInt(args.get(1));
		else
			super.handleOwnCommand(str);
		
		}
		catch(IndexOutOfBoundsException e)
		{
			System.out.println("WARNING: " + str + " has insufficient arguments (" + this.name + ")");
		}
	}
}
