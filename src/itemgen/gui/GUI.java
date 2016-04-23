package itemgen.gui;

import itemgen.ItemGen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.elmokki.Generic;


public class GUI extends JFrame implements ActionListener, ItemListener, ChangeListener 
{
	


	private static final long serialVersionUID = 1L;
	
	JTextPane textPane = new JTextPane();
    JProgressBar progress = new JProgressBar(0, 100);
    JButton startButton;
    JTabbedPane tabs = new JTabbedPane();
    
    JTextArea amount = new JTextArea("1");
    JTextArea modname = new JTextArea("Random");
    JTextArea seed = new JTextArea("Random");
    JCheckBox seedRandom = new JCheckBox("Random");
    JCheckBox modNameRandom = new JCheckBox("Random");
    JTextArea seeds = new JTextArea("1337, 715517, 80085");
    JCheckBox advDesc = new JCheckBox("Write advanced descriptions");
    JCheckBox basicDesc = new JCheckBox("Write basic descriptions");
    List<JCheckBox> optionChecks = new ArrayList<JCheckBox>();
    JCheckBox seedcheckbox = new JCheckBox("Use predefined nation seeds (separate by line change and/or comma)");
    
    JCheckBox hideOld = new JCheckBox("Hide non-booster vanilla items");
    JCheckBox hideBoosters = new JCheckBox("Hide booster vanilla items");

   
    JCheckBox noItems = new JCheckBox("Do not randomize spells");
    JCheckBox noSpells = new JCheckBox("Do not randomize items");

    JCheckBox dontChangeSiteSearchPath = new JCheckBox("Allow changing site search spell paths");
    JCheckBox dontChangeMagicBoostPath = new JCheckBox("Allow changing magic boost spell/item paths");

    
    JCheckBox hideVanillaNations = new JCheckBox("Hide vanilla nations");
    JSlider eraSlider = new JSlider(JSlider.HORIZONTAL, 1, 3, 2);

    
    JCheckBox nationals = new JCheckBox("Enable national spell path/level randomization");
    JCheckBox paths = new JCheckBox("Disable path type randomization");
    JCheckBox pathlevels = new JCheckBox("Disable path level randomization");
    JCheckBox levels = new JCheckBox("Disable research level randomization");
    JCheckBox schools = new JCheckBox("Disable spell school randomization");
    
    public static void main(String[] args) throws MalformedURLException
    {
    
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI g = new GUI();
                g.setVisible(true);
            }
        });
    }
    
    private void initGUI()
    {
    	optionChecks.add(advDesc);
    	optionChecks.add(basicDesc);
    	optionChecks.add(seedcheckbox);
    	optionChecks.add(hideVanillaNations);

    	seedRandom.setSelected(true);
    	modNameRandom.setSelected(true);
    	seed.setEnabled(false);
    	modname.setEnabled(false);
    	
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel options = new JPanel(new GridLayout(2,2));

        // Main
        tabs.addTab("Main", panel);
        
        startButton = new JButton("Start!");
        startButton.addActionListener(this);
        seedRandom.addItemListener(this);
        modNameRandom.addItemListener(this);
        advDesc.addItemListener(this);
        basicDesc.addItemListener(this);
        
        startButton.setPreferredSize(new Dimension(100, 50));


        textPane.setPreferredSize(new Dimension(600, 300));
        textPane.setBorder(BorderFactory.createLineBorder(Color.black));
        JScrollPane sp = new JScrollPane(textPane);
        
        
        progress.setPreferredSize(new Dimension(-1, 22));
        progress.setStringPainted(true);
        
        redirectSystemStreams();
        

        JPanel east = new JPanel(new GridLayout(20, 1));
        

        JPanel seedpanel = new JPanel(new GridLayout(1, 3));
        seedpanel.add(new JLabel("Seed"));
        seed.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        seedpanel.add(seed);
        seedpanel.add(seedRandom);

  

        
        east.add(seedpanel);
        east.add(new JLabel());
        east.add(hideOld);
        east.add(hideBoosters);
        east.add(new JLabel());
        east.add(new JLabel());
        east.add(new JLabel());
        east.add(new JLabel());
        east.add(new JLabel());
        east.add(new JLabel());
        east.add(new JLabel());
        east.add(new JLabel());
        east.add(new JLabel());
        east.add(new JLabel());
        east.add(new JLabel());
        east.add(new JLabel());
        east.add(new JLabel());

        
 
        east.add(startButton);
     
        
        
        panel.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
        //panel.add(progress, BorderLayout.SOUTH);
        panel.add(sp, BorderLayout.WEST);
        panel.add(east, BorderLayout.CENTER);
        
   
        
        
        
    
        
        
        add(tabs);

        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
		System.out.println("ItemGen version " + ItemGen.version + " (" + ItemGen.date + ")");
		System.out.println("--------------------------------------------");
    }
    
    boolean hasRun = false;
    public GUI() {
        setTitle("ItemGen GUI");
        this.setPreferredSize(new Dimension(1000, 425));
        this.setResizable(false);
        initGUI();



     }
    

    private void updateTextPane(final String text) {
    	  SwingUtilities.invokeLater(new Runnable() {
    	    public void run() {
    	      Document doc = textPane.getDocument();
    	      try {
    	        doc.insertString(doc.getLength(), text, null);
    	      } catch (BadLocationException e) {
    	        throw new RuntimeException(e);
    	      }
    	      textPane.setCaretPosition(doc.getLength() - 1);
    	    }
    	  });
    	}
    
    
    private List<Integer> parseSeeds()
    {
    	List<Integer> l = new ArrayList<Integer>();
    	String text = seeds.getText();
    	
    	String[] parts = text.split(",");
    	for(String str : parts)
    	{
    		String[] parts2 = str.split("\n");
    		for(String str2 : parts2)
    			if(Generic.isNumeric(str2.trim()))
    			{
    				l.add(Integer.parseInt(str2.trim()));
    			}
    	}
    	
    	return l;
    	
    	
    }
    private void redirectSystemStreams() {
    	  OutputStream out = new OutputStream() {
    	    @Override
    	    public void write(final int b) throws IOException {
    	      updateTextPane(String.valueOf((char) b));
    	    }
    	 
    	    @Override
    	    public void write(byte[] b, int off, int len) throws IOException {
    	      updateTextPane(new String(b, off, len));
    	    }
    	 
    	    @Override
    	    public void write(byte[] b) throws IOException {
    	      write(b, 0, b.length);
    	    }
    	  };
    	 
    	  System.setOut(new PrintStream(out, true));
    	  System.setErr(new PrintStream(out, true));
    	}


    private void process()
    {
    
    	
	    Thread thread = new Thread() {
	        public void run() {
	        	startButton.setEnabled(false);
	
	        	Random r = new Random();
	        	int s = r.nextInt();
	        	if(!seedRandom.isSelected())
	        		s = Integer.parseInt(seed.getText());

	     
	        

	        	ItemGen itemGen = new ItemGen();
	        	
	        	if(hideOld.isSelected())
	        		itemGen.settings += 1;
	        	if(hideBoosters.isSelected())
	        		itemGen.settings += 2;
	        	
	        	itemGen.generate(100, s);
	        	itemGen.write();
	        	
	        	
	        	
	        	startButton.setEnabled(true);
	   
	        }
	    };
	    thread.start();
	    hasRun = true;
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(startButton))
		{
			if(modname.getText().length() == 0)
			{
        		System.out.println("Please enter a mod name.");
				//modNameRandom.setSelected(true);
        		return;
			}
			
			if(!seedcheckbox.isSelected())
			{
				if(!(Generic.isNumeric(seed.getText()) || seed.getText().equals("Random")))
				{
					System.out.println("Please enter a numeric seed.");
					//seedRandom.setSelected(true);
					return;
				}

				if(!Generic.isNumeric(amount.getText()) || Integer.parseInt(amount.getText()) < 1)
				{
	        		System.out.println("Please enter a numeric nation amount.");
	        		return;
				}
		
		
			}
			else if(this.seedcheckbox.isSelected() && parseSeeds().size() == 0)
			{
				System.out.println("Please specify numeric seeds or disable predefined seeds.");
				return;
			}
			
			process();
		}
	}
	
		
	
	
	@Override
	public void itemStateChanged(ItemEvent e)
	{
	    Object source = e.getItemSelectable();
	    
	    
	    // Main screen settings
	    JTextArea target = null;
		if(source == this.modNameRandom)
		{
			target = this.modname;
		}
		else if(source == this.seedRandom)
		{
			target = this.seed;
		}
		
		if(target != null)
		{
	        if (e.getStateChange() == ItemEvent.DESELECTED) 
	        {
	            target.setEnabled(true);
	            if(target == this.seed)
	            {
	        		Random r = new Random();
	        		target.setText(r.nextInt() + "");
	            }
	        }
	        else if (e.getStateChange() == ItemEvent.SELECTED) 
	        {
	            target.setEnabled(false);
	            target.setText("Random");
	        }
		}
		
		
		
		// Options settings
		if(this.optionChecks.contains(source))
		{

			

			
			if(source == this.seedcheckbox)
			{
				this.seeds.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				this.seedRandom.setSelected(true);
				this.seedRandom.setEnabled(e.getStateChange() != ItemEvent.SELECTED);
				this.amount.setEnabled(e.getStateChange() != ItemEvent.SELECTED);
			
			}
	
		}
		
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		
		Object source = e.getSource();

		
	}
}
