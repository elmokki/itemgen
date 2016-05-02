package itemgen.entities;

import com.elmokki.Generic;

public class PathRequirement {
	public int p1 = -1;
	public int p2 = -1;
	public boolean freeorder = false;
	public int smax = 999;
	public int smin = 0;
	public double cost = 0;
	

	
	public PathRequirement(double cost, int p1, int p2, int smin, int smax, boolean freeorder)
	{
		this.p1 = p1;
		this.p2 = p2;
		this.smin = smin;
		this.smax = smax;
		this.freeorder = freeorder;
		this.cost = cost;
	}
	
	
	public PathRequirement getCopy()
	{
		return new PathRequirement(cost, p1, p2, smin, smax, freeorder);
	}
	
	public String toString()
	{
		String str = cost + " points of " + Generic.integerToShortPath(p1);
		if(p2 > -1)
		{
			str = str + Generic.integerToShortPath(p2);
			str = str + " (Secondary " + smin + "-" + smax + "), Free order: " + freeorder;
		}
		
		return str;
	}
}
