package itemgen.misc;

import itemgen.Item;
import itemgen.entities.Effect;
import itemgen.entities.Filter;
import itemgen.entities.ItemTemplate;
import itemgen.entities.PathRequirement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;





import com.elmokki.Generic;








public class ChanceIncHandler {
	

	public ChanceIncHandler()
	{

	}
	

	
	public <T extends Filter> List<T> removeRelated(T thing, List<T> list)
	{
		if(thing.tags.contains("multitype"))
			return list;
		
		List<T> shit = new ArrayList<T>();
		
		list.remove(thing);
		for(String type : thing.types)
			for(T t : list)
				if(t.types.contains(type))
				{
					shit.add(t);
					
					continue;
				}
		
		list.removeAll(shit);
		
		return list;
	}
	
	public <T extends Filter> LinkedHashMap<T, Double> transformToHashMap(List<T> filters)
	{
		LinkedHashMap<T, Double> set = new LinkedHashMap<T, Double>();
		for(T t : filters)
		{


			set.put(t, t.basechance);
		}
		
		return set;
	}
	
	
	public <T extends Filter> LinkedHashMap<T, Double> handleChanceIncs(List<T> filters, Item i, List<Item> items)
	{

		LinkedHashMap<T, Double> set = new LinkedHashMap<T, Double>();
		for(T t : filters)
		{


			set.put(t, t.basechance);
		}
		
		handleIncs(set, i, items);


		
		
		List<T> redundantFilters = new ArrayList<T>();
		
		for(T f : set.keySet())
		{
			if(set.get(f) <= 0)
				redundantFilters.add(f);
		}
		
		for(Filter f : redundantFilters)
		{
			if(set.keySet().contains(f))
				set.remove(f);
		}

		
		

		return set;
	}
	

	
	public static boolean canAdd(Item u, Filter f)
	{


		
		// Forbid the same type
		for(Filter f2 : u.appliedFilters)
		{
			for(String s : f.types)
			{
				if(f2.types.contains(s))
				{
					
					return false;
				}
			}
		}  
		
		List<String> primaries = new ArrayList<String>();
		if(Generic.containsTag(f.tags, "primarycommand"))
		{
			for(String tag : f.tags)
			{
				List<String> args = Generic.parseArgs(tag);
				if(args.get(0).equals("primarycommand"))
				{
					primaries.add(args.get(1));
				}
			}
		}
		
		boolean ok = false;
		boolean primarycommandfail = false;
		for(Command c : u.getCommands())
		{

			boolean tempok = true;
			if(primaries.contains(c.command))
			{
				primarycommandfail = true;
				ok = false;
				break;
			}

			
			for(Command fc : f.commands)
			{

				if(c.command.equals(fc.command) && c.args.size() == 0 && fc.args.size() == 0)
				{
					tempok = false;
					break;
				}
			}
			
			if(tempok)
				ok = true;
		}
		

		

		
		return ok;
	}
	

	
	public static <E extends Filter> List<E> retrieveFilters(String lookfor, String defaultset, ResourceStorage<E> source, ItemTemplate p)
	{
		List<E> filters = new ArrayList<E>();
		
		boolean empty = true;
		
		
		if(p != null)
			for(String tag : p.tags)
			{
				if(tag.startsWith(lookfor))
				{
					empty = false;
					filters.addAll(source.get(tag.split(" ")[1]));
				}
			}
	
		

		
		if(empty)
			filters.addAll(source.get(defaultset));

		return filters;

	}
	

	
	
	
	private double applyMod(double value, String modifier)
	{
		
		if(modifier.startsWith("+"))
		{
			modifier = modifier.substring(1);
			value += Double.parseDouble(modifier);
		}
		else if(modifier.startsWith("*"))
		{
			modifier = modifier.substring(1);
			value *= Double.parseDouble(modifier);
		}
		else if(modifier.startsWith("/"))
		{
			modifier = modifier.substring(1);
			value /= Double.parseDouble(modifier);
		}
		else
		{

			value += Double.parseDouble(modifier);
		}
		return value;
	}
	
	public Double applyModifier(double value, String modifier)
	{
		String[] mod = modifier.split(" or ");
		double[] results = new double[mod.length];
		
		if(mod.length == 0)
			return value;
		
		boolean max = true;
		if(modifier.startsWith("max("))
			modifier = modifier.substring(4, modifier.length() - 2);
		if(modifier.startsWith("min("))
		{
			modifier = modifier.substring(4, modifier.length() - 2);
			max = false;
		}
		
		

		double biggest = applyMod(value, mod[0]);;
		for(int i = 0; i < results.length; i++)
		{
			results[i] = applyMod(value, mod[i]);
			
			
			if(results[i] > biggest && max)
				biggest = results[i];
			else if(results[i] < biggest && !max)
				biggest = results[i];
		}
		
		return biggest;
			
	}
	
	
	public <T extends Filter> List<T> getRangeOfLevel(List<T> list, int min, int max)
	{
		List<T> newlist = new ArrayList<T>();
		for(T t : list)
			if(t.level >= min && t.level <= max)
				newlist.add(t);
		
		return newlist;
	}
	

	
	

	/**
	 * Returns effects with suitable paths for the item
	 * @param list
	 * @param item
	 * @return
	 */
	public <T extends Effect> List<T> getPossibleAdditions(PathRequirement cpr, List<T> list, int path1, int path2, boolean needboth)
	{
		List<T> newlist = new ArrayList<T>();
		

		
		for(T t : list)
		{
			if(t.magic_requirements.size() == 0)
				newlist.add(t);
			else
			{
				for(PathRequirement pr : t.magic_requirements)
				{
					boolean p1 = false;
					boolean p2 = false;
					
					// Single path requirements
					if(pr.p2 == -1)
					{
						// Path 1 
						// Either same as requirement, requirement is "any" or both sorcery/elemental
						if(Generic.matchingPaths(path1, pr.p1))
						{
							newlist.add(t);
							continue;
						}
						
						
						// Path 2
						// More tricky!
						if(Generic.matchingPaths(path2, pr.p1))
						{
							// Prevents getting secondary path stuff for first thing
							if(cpr != null)
							{
								if(cpr.cost - cpr.smin > pr.cost + cpr.smin)
								{
									newlist.add(t);
									continue;
								}
							}
						}
					}
					
					// Double path requirements
					else if(path1 > -1 && path2 > -1)
					{
						
						// Check primary path
						if(Generic.matchingPaths(path1, pr.p1))
						{

							// Check secondary path
							if(Generic.matchingPaths(path2, pr.p2))
							{
								newlist.add(t);
								continue;
							}

						}
					}
					
					
					if(pr.p1 == path1 || pr.p1 == path2)
						p1 = true;
					if(pr.p2 == path2 || pr.p2 == path1)
						p2 = true;
					
					if(p1 && (p2 || pr.p2 == -1 || !needboth))
					{
						newlist.add(t);
						break;
					}
				}
			}
		}
		
		return newlist;
	}
	
	
	
	
	
	
	public <T extends Filter> LinkedHashMap<T, Double> handleCoolness(LinkedHashMap<T, Double> filters, Item item)
	{
		for(T p : filters.keySet())
		{
			
			double dif = Math.abs(item.level - p.level);
			if(dif >= 1)
			{	
				double multi = 1 / (dif);
				filters.put(p, applyModifier(p.basechance, "*" + multi));
			}
			else
			{
				filters.put(p, p.basechance);

			}
		}
		
		return filters;
	}
	
	

	
	
	
	private <T extends Filter> void handleIncs(LinkedHashMap<T, Double> filters, Item item, List<Item> items)
	{



		
		// Do chanceincs!
		for(T f : filters.keySet())
		{

			
			for(String str : f.chanceincs)
			{
				
				List<String> args = Generic.parseArgs(str);
				
				
				
				if(item != null)
				{	
					
					// Magic paths
					if(args.get(0).equals("path1"))
					{
						boolean reverse = false;
						if(args.get(1).equals("not"))
						{
							args.remove(1);
							reverse = true;
						}
						
						boolean fine = false;
						if(Generic.PathToInteger(args.get(1)) == item.p1);
						{
							if(Double.parseDouble(args.get(args.size() - 2)) >= item.lv1)
								fine = true;
							else
								fine = true;
					
						}
						
						if(fine != reverse)
						{
							filters.put(f, applyModifier(f.basechance, args.get(args.size() - 1)));

						}
	
					}
					else if(args.get(0).equals("path2"))
					{
						boolean reverse = false;
						if(args.get(1).equals("not"))
						{
							reverse = true;
							args.remove(1);
						}
						
						boolean fine = false;
						if(Generic.PathToInteger(args.get(1)) == item.p2);
						{
							if(Double.parseDouble(args.get(args.size() - 2)) >= item.lv2)
								fine = true;
							else
								fine = true;
					
						}
						
						if(fine != reverse)
						{
							filters.put(f, applyModifier(f.basechance, args.get(args.size() - 1)));

						}
	
					}
					else if(args.get(0).equals("paths"))
					{
						boolean reverse = false;
						if(args.get(1).equals("not"))
						{
							reverse = true;
							args.remove(1);
						}
						boolean fine = false;
						if(Generic.matchingPaths(item.p1, Generic.PathToInteger(args.get(1))));
						{
							if(Generic.matchingPaths(item.p2, Generic.PathToInteger(args.get(2))));
								fine = true;
					
					
						}
						
						if(fine != reverse)
						{
							filters.put(f, applyModifier(f.basechance, args.get(args.size() - 1)));

						}
	
					}
					else if(args.get(0).equals("tag"))
					{
						boolean reverse = false;
						if(args.get(1).equals("not"))
						{
							reverse = true;
							args.remove(1);
						}
						
						boolean ok = false;
						for(Effect e : item.appliedFilters)
						{
							if(e.tags.contains(args.get(1)))
								ok = true;
						}
						if(item.template.tags.contains(args.get(1)))
							ok = true;
						
						if(ok != reverse)
						{
							filters.put(f, applyModifier(f.basechance, args.get(args.size() - 1)));
						}
						
	
					}
					
					else if(args.get(0).equals("level"))
					{

						boolean below = false;
						if(args.contains("below"))
							below = true;
						
						if(!below && item.level >= Integer.parseInt(args.get(args.size() - 2)))
							filters.put(f, applyModifier(f.basechance, args.get(args.size() - 1)));
						else if(below && item.level < Integer.parseInt(args.get(args.size() - 2)))
							filters.put(f, applyModifier(f.basechance, args.get(args.size() - 1)));

	
					}
					else if(args.get(0).equals("type"))
					{

		
						if(item.template.types.contains(args.get(1)))
						{
	
							filters.put(f, applyModifier(f.basechance, args.get(args.size() - 1)));
						}
						
	
					}
					else if(args.get(0).equals("slot"))
					{

		
						if(item.template.slot.equals(args.get(1)))
						{
	
							filters.put(f, applyModifier(f.basechance, args.get(args.size() - 1)));
						}
						
	
					}
					
				}
			}

		}
		
		

		
	
		

	}

	
	protected boolean alreadyHasType(List<? extends Filter> filters, List<String> types)
	{
		if(types.size() == 0)
			return false;
		
		for(Filter f : filters)
			for(String type : types)
				if(f.types.contains(type) && !f.tags.contains("freetype"))
					return true;
		
		return false;
	}
	
	protected boolean alreadyHasType(List<? extends Filter> filters, String type)
	{
		if(type.equals(""))
			return false;
		
		for(Filter f : filters)
			if(f.types.contains(type) && !f.tags.contains("freetype"))
				return true;
		
		return false;
	}
	

		

		

	
}
