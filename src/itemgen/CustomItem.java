package itemgen;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.elmokki.Dom3DB;





public class CustomItem  {

	public LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
	public int id = 0;
	boolean armor = false;
	

	
	
	
	public CustomItem(ItemGen itemGen, boolean armor) {

		this.armor = armor;
		this.values.put("rcost", "0");

		if(!armor)
		{
			this.values.put("att", "0");
			this.values.put("def", "0");
			this.values.put("len", "0");
			this.values.put("dmg", "0");
		}
		else
		{
			this.values.put("def", "0");
			this.values.put("prot", "0");
			this.values.put("enc", "0");
		}
	}
	
	

	public static CustomItem fromDB(String id, boolean armor, ItemGen itemGen)
	{
		return fromDB(Integer.parseInt(id), armor, itemGen);
	}
	
	public static CustomItem fromDB(int id, boolean armor, ItemGen itemGen)
	{
		Dom3DB db;
		if(armor)
			db = itemGen.armordb;
		else
			db = itemGen.weapondb;
		
		

		CustomItem ci = new CustomItem(itemGen, armor);
		ci.armor = armor;
		
		ci.values.put("res", db.GetValue(id, "res"));
		

		if(armor)
		{
			ci.values.put("enc", db.GetValue(id, "enc"));
			ci.values.put("prot", db.GetValue(id, "prot"));
			ci.values.put("def", db.GetValue(id, "def"));
			ci.values.put("def", "" + (Integer.parseInt(ci.values.get("def")) + db.GetInteger(id, "par")));

		}
		else
		{
			ci.values.put("name", db.GetValue(id, "weapon_name"));
			if(db.GetInteger(id, "2h") == 1)
				ci.values.put("twohanded", "");
			
			ci.values.put("att", db.GetValue(id, "att"));
			ci.values.put("dmg", db.GetValue(id, "dmg"));
			
			if(!db.GetValue(id, "lgt").equals(""))
				ci.values.put("len", db.GetValue(id, "lgt"));
			if(!db.GetValue(id, "def").equals(""))
				ci.values.put("def", db.GetValue(id, "def"));
			if(!db.GetValue(id, "shots").equals(""))
				ci.values.put("ammo", db.GetValue(id, "shots"));
			if(!db.GetValue(id, "rng").equals(""))
				ci.values.put("range", db.GetValue(id, "rng"));
			if(db.GetInteger(id, "#att") > 0)
				ci.values.put("nratt", db.GetValue(id, "#att"));
			if(db.GetInteger(id, "nostr") == 1)
				ci.values.put("nostr", "");
			
			if(db.GetInteger(id, "dt_p") == 1)
				ci.values.put("pierce", "");
			if(db.GetInteger(id, "dt_s") == 1)
				ci.values.put("slash", "");
			if(db.GetInteger(id, "dt_b") == 1)
				ci.values.put("blunt", "");
			
			if(db.GetInteger(id, "charge") == 1)
				ci.values.put("charge", "");
			if(db.GetInteger(id, "flail") == 1)
				ci.values.put("flail", "");
			if(db.GetInteger(id, "unrepel") == 1)
				ci.values.put("unrepel", "");
			if(db.GetInteger(id, "norepel") == 1)
				ci.values.put("norepel", "");
			if(db.GetInteger(id, "bonus") == 1)
				ci.values.put("bonus", "");
			if(db.GetInteger(id, "ap") == 1)
				ci.values.put("armorpiercing", "");
			if(db.GetInteger(id, "an") == 1)
				ci.values.put("armornegating", "");
			
			if(db.GetInteger(id, "flyspr") > 0)
			{
				String str = db.GetValue(id, "flyspr");
				if(!db.GetValue(id, "animlength").equals(""))
					str = str + " " + db.GetValue(id, "animlength");
				else
					str = str + " 1";

				ci.values.put("flyspr", str);

			}
		
			
		}
		
		return ci;
	}
	
	public LinkedHashMap<String, String> getHashMap()
	{
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("id#", id + "");
		
		if(!armor)
		{
			map.put("#att", "1");
			map.put("shots", "0");
			map.put("rng", "0");
			map.put("att", "0");
			map.put("def", "0");
			map.put("lgt", "0");
			map.put("dmg", "0");
			map.put("2h", "0");
		}
		else
		{
			map.put("prot", "0");
			map.put("enc", "0");
			map.put("def", "0");
		}
		
		for(String str : values.keySet())
		{
			String arg = values.get(str);
			if(arg == null)
				continue;
			

			if(str.equals("secondaryeffectalways"))
			{
				str = "aeff#";
			}
			if(str.equals("secondaryeffect"))
			{
				str = "eff#";
			}
			
			if(str.equals("twohanded"))
			{
				str = "2h";
				arg = "1";
			}
			
			if(str.equals("charge"))
			{
				arg = "Charge";
			}
			
			if(str.equals("bonus"))
			{
				arg = "Bonus";
			}
		
			if(str.equals("dt_cap"))
			{
				arg = "Max dmg 1";
			}
			
			if(str.equals("magic"))
			{
				arg = "Magic";
				
			}
			
			if(str.equals("ammo"))
			{
				str = "shots";
			}
			
			if(str.equals("armorpiercing"))
			{
				str = "ap";
				arg = "ap";
			}
			
			if(str.equals("armornegating"))
			{
				str = "an";
				arg = "an";
			}
	
			if(str.equals("bonus"))
			{
				arg = "Bonus";
			}
			
			if(str.equals("range"))
			{
				str = "rng";
			}
			
			if(str.equals("len"))
			{
				str = "lgt";
			}
			
			if(str.equals("nratt"))
			{
				str = "#att";
			}
			
			if(str.equals("rcost"))
			{
				str = "res";
			}
			
			
			if(str.equals("name") && this.armor)
			{
				str = "armor_name";
				arg = arg.replaceAll("\"", "");
			}
			else if(str.equals("name") && !this.armor)
			{
				str = "weapon_name";	
				arg = arg.replaceAll("\"", "");
			}
			map.put(str, arg);
		}
		
		if(map.get("2h") == null)
			map.put("2h", "0");
		
		
		
		return map;
	}
	
	
	public void write(PrintWriter tw)
	{

		if(armor)
			tw.println("#newarmor " + id);
		else
			tw.println("#newweapon " + id);
		
		List<String> lines = new ArrayList<String>();
		
		for(String command : values.keySet())
		{
			if(command.equals("name"))
			{
				tw.println("#name " + values.get("name"));
			}
		}
		
		
		for(String command : values.keySet())
		{
			String arg = "";
			
			if(command.equals("name"))
				continue;
			
			if(values.get(command) != null)
			{
				arg = " " + values.get(command);
			}

			
			lines.add("#" + command + arg);
				
		}
			
		for(String str : lines)
		{			
			tw.println(str);
		}
		
		
		tw.println("#end");
		tw.println();
	}

}
