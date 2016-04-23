package itemgen.gui;

import itemgen.ItemGen;

public class ConsoleGUI {
	public static void main(String[] args)
	{
		ItemGen g = new ItemGen();
		g.generate(50, 435653968);
		g.write();
	}
}
