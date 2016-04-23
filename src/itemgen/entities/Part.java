package itemgen.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;



import com.elmokki.Drawing;
import com.elmokki.Generic;

import itemgen.ItemGen;
import itemgen.misc.Command;

public class Part extends Filter {

	public String slot = "";
	public int offsetx = 0;
	public int offsety = 0;
	public String sprite = "";
	public String mask = "";
	
	public Part(ItemGen itemGen) {
		super(itemGen);
	}
	
	public <E extends Entity> void handleOwnCommand(String str)
	{

		List<String> args = Generic.parseArgs(str);
		
		
		try
		{
		
		if(args.get(0).equals("#sprite"))
			this.sprite = args.get(1);
		else if(args.get(0).equals("#recolormask") || args.get(0).equals("#mask"))
			this.mask = args.get(1);
		else if(args.get(0).equals("#offsetx"))
			this.offsetx = Integer.parseInt(args.get(1));
		else if(args.get(0).equals("#offsety"))
			this.offsety = Integer.parseInt(args.get(1));
		else if(args.get(0).equals("#command") || args.get(0).equals("#define"))
			this.commands.add(Command.parseCommand(args.get(1)));
		else
			super.handleOwnCommand(str);
		}
		catch(IndexOutOfBoundsException e)
		{
			System.out.println("WARNING: " + str + " has insufficient arguments (" + this.name + ")");
		}
	}
	
	public void render(Graphics g, boolean useoffsets, int offsetx, int offsety, Color color, int extraX) throws IOException
	{				
		Part i = this;
		if(i == null || i.sprite == null || i.sprite.equals(""))
			return;
		
		int xoff = i.offsetx + offsetx + extraX;
		int yoff = i.offsety + offsety;
		if(!useoffsets)
		{
			xoff = extraX;
			yoff = 0;
		}
		String path = "./";
		BufferedImage image = null;
		
		if(i != null)
		{
			
			// Draw image
			try
			{
				image = ImageIO.read(new File(path, i.sprite));
			}
			catch(IOException e)
			{
				System.out.println("CRITICAL FAILURE, IMAGE FILE " + i.sprite + " CANNOT BE FOUND.");
				return;
			}
			g.drawImage(image, xoff, yoff, null);


			drawRecolorMask(g, this, color, xoff, yoff);
			
		}
	}
	
	
	private void drawRecolorMask(Graphics g, Part i, Color c, int x, int y) throws IOException
	{
		if(!i.mask.equals(""))
		{
			if(i.mask.equals("self"))
				i.mask = i.sprite;
			
			BufferedImage image;
			BufferedImageOp colorizeFilter;
			BufferedImage targetImage = null;
			try
			{
				image = ImageIO.read(new File("./", i.mask));
				
	
				if(!i.tags.contains("alternaterecolor"))
					colorizeFilter =  Drawing.createColorizeOp_alt(c);
				else
					colorizeFilter = Drawing.createColorizeOp(c);
				
				
				targetImage = colorizeFilter.filter(image, image);
			}
			catch(Exception e)
			{
				System.out.println("RECOLORMASK " + i.mask + " COULD NOT BE READ!");
			}
			
			g.drawImage(targetImage, x, y, null);
		}
	}
}
