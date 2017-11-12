package assignment5;
//required imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 * @author Yaswanth Reddy Vayalapti
 * ZID:Z1805553
 * This class creates a GUI where we can create multiple balls with different sizes and colors and animate.
 * Each ball has its own Thread.
 * This class has two inner classes one for storing Shapes and one for creating thread.
 */

public class AnimationGUI extends JFrame implements Serializable{

 
	
	public static ArrayList<ShapeCanvas> sha = new ArrayList<>();// Array List to store Objects.
	 static int glbref=0;
	JPanel editPanel = new JPanel(true);
	SpinnerNumberModel sizeControl = new SpinnerNumberModel(25, 5, 100, 5);
	JSpinner size = new JSpinner(sizeControl);     // JSpinner to increase or decrease size.
	JColorChooser color = new JColorChooser(Color.RED); // creating a color chooser and setting a default color.
	JPanel create = new JPanel(true);// Panel for ball creation
	JMenuBar bar = new JMenuBar();    //creating menu bar
	JMenuItem start = new JMenuItem("start"); //Menu Item to start animation
	private static Boolean stopFlag=true;   //flag to pause animation
	RunThread varThread;   //Object to Thread class
	ShapeCanvas tempCut;
	/*
	 * This Panel is where shapes are added .
	 * This Panel has paint component method which can be called when ever we need to repaint.
	 */
	JPanel centerPanel = new JPanel(true) {    
		
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			
			for (ShapeCanvas a : sha) {
				//accessing each object and drawing the shape.

				Ellipse2D dra = new Ellipse2D.Float(a.X, a.Y, a.dia, a.dia);
				g2d.setColor(a.c);
				g2d.draw(dra);
				if(sha.indexOf(a)==(glbref-1)) { //Accessing current ball
				g2d.fill(dra);   //Filling the current ball with color.
				}

			}

		}
		
	};
	JSlider horizantalSlider = new JSlider(); //Horizantal slider
	JSlider verticalSlider = new JSlider();  //Vertical slider
	

	AnimationGUI(String s) {

		super(s);
		setSize(400, 400);    //Setting size of frame

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setJMenuBar(bar);     //adding menu bar to frame
		bar.add(addFileMenu());   //adding file menu to menu bar
		bar.add(addEditMenu());    //adding edit menu to menu bar
		bar.add(addAnimation());   //adding animation menu to menu bar
		JPanel mainPanel = new JPanel(true);   //Panel for ball creation and control
		mainPanel.setLayout(new GridLayout(1, 2));  //setting grid layout for main panel.
		mainPanel.add(addCreationPanel()); //adding ball creation panel to main panel
		mainPanel.add(addSliderPanel());//  adding sliders panel to main panel
		add(mainPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		
	}
	/**
	 * Creates Menu items Read and Write and Exit 
	 * @return returns File Menu
	 */
	public JMenu addFileMenu() {
		JMenu file = new JMenu("File");
		JMenuItem read = new JMenuItem("Read Ball shapes from a File");
		JMenuItem write = new JMenuItem("Write Ball shapes to a File");
		/**
		 * Action Listener for Writing objects to a file.
		 */
		write.addActionListener(writetofile->{
			JFileChooser readChooser=new JFileChooser();   //Opens a file chooser
                int result = readChooser.showSaveDialog(null);
                if(result == JFileChooser.APPROVE_OPTION)
                {
                    try{
                        File inputfile = readChooser.getSelectedFile();
                        FileOutputStream opf = new FileOutputStream(inputfile);
                        ObjectOutputStream oop = new ObjectOutputStream(opf);
                        oop.writeObject(sha);  // Writing objects to selected file.
                        oop.close();
                        centerPanel.repaint();  //Calling Paint component with repaint.
                    }
                    catch(IOException io)
                    {                       
                    }
                }
            
        
		});
		/**
		 * Action Listener for reading contents from a file.
		 */
		read.addActionListener(readfrom->{
			JFileChooser readfromChooser=new JFileChooser(); //Opens a File Chooser
            int result = readfromChooser.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION)
            {
            	
                try{
                    File inputfile = readfromChooser.getSelectedFile();
                    FileInputStream opf = new FileInputStream(inputfile);
                    ObjectInputStream ois=new ObjectInputStream(opf);
                    
                    ArrayList<ShapeCanvas> list= (ArrayList<ShapeCanvas>)ois.readObject(); //ArrayList to read Objects
                    if(start.isEnabled()==false) {
                    	for(ShapeCanvas re:list) {
                    	varThread=new RunThread(re); //creating a thread for newly added balls.
                    	}
                    }
                    int actual=sha.size();
                    sha.clear();
                    
                    sha.addAll(list);
              
                    glbref=(glbref-actual)+list.size(); //Increasing global reference value to keep track of number of items entered.
                    System.out.println(glbref);
                    ois.close();  //closing input stream
                    centerPanel.repaint();
                }
                catch(IOException io)
                {  
                } catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		});
		
		JMenuItem exit = new JMenuItem("Exit");
		file.add(read);
		file.add(write);
		file.add(exit);
		exit.addActionListener(exi->{
			dispose();
		});
		return file;

	}
	/**
	 * This function creates edit menu items 
	 * @return returns edit menu
	 */

	public JMenu addEditMenu() {
		JMenu edit = new JMenu("Edit");   //adding edit menu item
		JMenuItem next = new JMenuItem("next");  //adding next menu item
		JMenuItem previous = new JMenuItem("previous");  //previous menu item
		JMenuItem cut = new JMenuItem("cut");  //adding cut menu item
		JMenuItem paste = new JMenuItem("paste");
		paste.setEnabled(false);
		edit.add(next);
		//action listener for next menu item
		next.addActionListener(nex->{
			if(glbref==sha.size()) {
				glbref=1;
			}
			else {
			glbref++;
			}
			

			ShapeCanvas speedControl=sha.get(glbref-1);
			horizantalSlider.setValue(Math.abs(speedControl.hspeed)); //setting speed to current value
			verticalSlider.setValue(Math.abs(speedControl.vspeed));  //setting vertical speed to certain value 
			centerPanel.repaint();
			
		});
		//action listener for previous menu item.
		edit.add(previous);
		previous.addActionListener(a->{
			if(glbref==1) {
				glbref=sha.size();
			}
			else {
			glbref--;
			}
			ShapeCanvas speedControl=sha.get(glbref-1);
			horizantalSlider.setValue(Math.abs(speedControl.hspeed));//setting speed to current value
			verticalSlider.setValue(Math.abs(speedControl.vspeed));  //setting vertical speed to certain value 
			centerPanel.repaint();
			
			
		});
		
		edit.add(cut);
		//action listener for cut menu item
		cut.addActionListener(cutbutton->{
			cut.setEnabled(false);
			paste.setEnabled(true);
			tempCut=sha.get(glbref-1);
			sha.remove(glbref-1);
			centerPanel.repaint();
		});
		
		edit.add(paste);
		//action listener for paste menu item.
		paste.addActionListener(pastebutton->{
			cut.setEnabled(true);
			paste.setEnabled(false);
			sha.add(glbref-1, tempCut);
			centerPanel.repaint();
		});
		

		return edit;
	}
	/**
	 * This method returns animation menu
	 * @return animation menu
	 */

	public JMenu addAnimation() {
		JMenu animation = new JMenu("Animation");
		
		JMenuItem stop = new JMenuItem("stop");
		animation.add(start);// adding start menu item
		animation.add(stop);  //adding stop menu  item
		stop.setEnabled(false);
		//action listener for stop menu item
		stop.addActionListener(l->{
			start.setEnabled(true);
			stop.setEnabled(false);
			stopFlag=false;   // setting flag to false
		});

		//Action Listener for start menu item.
		start.addActionListener(e -> {
			start.setEnabled(false);
			stop.setEnabled(true);
			stopFlag=true;
			
			for(ShapeCanvas s:sha) {   //Start separate thread for each object
				varThread=new RunThread(s);
			}
			
			
			});

		
	
		return animation;
	}
	/**
	 * Adding create Panel which contains Size spinner ,color chooser and adding new ball.
	 * 
	 * @return returns a panel with creation components.
	 */

	public JPanel addCreationPanel() {

		create.setLayout(new BoxLayout(create, BoxLayout.PAGE_AXIS));   //setting box layout for create Panel.
		TitledBorder border = new TitledBorder("Ball creation"); //Setting border for creation panel
		create.setBorder(border);
		JLabel text = new JLabel("Balls are introduced into the game at center point"); //
		create.add(text);
		JLabel sizeText=new JLabel("Size");  
		editPanel.add(sizeText); //adding spinner to panel

		editPanel.add(size);
		JButton colorButton = new JButton("select");  //adding color select button
		colorButton.setBackground(Color.RED);     //setting background for button
		editPanel.add(colorButton);

		//adding action listener for color select button
		colorButton.addActionListener(e -> {
			//creating a new frame
			JFrame colorFrame = new JFrame("Choose Color");

			colorFrame.add(color);
			JButton ok = new JButton("SELECT");//adding button to frame.
			colorFrame.add(ok, BorderLayout.SOUTH);
			colorFrame.setVisible(true);
			ok.addActionListener(a -> {
				colorFrame.dispose();
				colorButton.setBackground(color.getColor());
			});
			colorFrame.pack();

		});
		JButton addBall = new JButton("add new ball");
		editPanel.add(addBall);
		create.add(editPanel);
		//action listener for add Ball button
		addBall.addActionListener((e) -> {
			int actualWidth = centerPanel.getWidth();//panel width
			int actualHeight = centerPanel.getHeight();  //panel height
			String s = size.getValue().toString();
			Integer i = Integer.parseInt(s);
			float a = (float) i;
			int r = (int) a;
			// System.out.println(centerPanel.getWidth());
			int width = centerPanel.getWidth() / 2 - r / 2;  //X-co ordinate
			int height = centerPanel.getHeight() / 2 - r / 2;  //Y-Co ordinate
			int hsval=horizantalSlider.getValue();   //getting current  horizantal speed
			int vsval=verticalSlider.getValue();    //getting current vertical speed
			//Creating shape object
			ShapeCanvas nam = new ShapeCanvas(width, height, a, color.getColor(), actualWidth, actualHeight,hsval,vsval);

			sha.add(nam);// adding object to list
			glbref++;
			centerPanel.repaint();
			 centerPanel.add(nam);
			 if(start.isEnabled()==false) {  //if animation is on creating threads for new balls.
				 varThread=new RunThread(nam);
			 }


		});
		return create;

	}
	/**
	 * This method adds two sliders to control horizantal speed and vertical speed
	 * @return a panel with horizantal and vertical sliders
	 */

	public JPanel addSliderPanel() {
		JPanel sliderPanel = new JPanel(true);
		TitledBorder border = new TitledBorder("Current Ball Control");
		sliderPanel.setBorder(border);
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS)); // setting layout to panel
		JPanel horizantalPanel = new JPanel(true);
		JLabel horizantalLabel = new JLabel("Horizantal Speed:");
		
		horizantalSlider.setMinorTickSpacing(5);
		horizantalSlider.setPaintTicks(true);
		horizantalSlider.setPaintLabels(true);
		horizantalSlider.setSnapToTicks(true);
		horizantalPanel.add(horizantalLabel);
		horizantalPanel.add(horizantalSlider);
		//Change Listener for horizantal slider
		horizantalSlider.addChangeListener(e->{
			int l=sha.size();
			ShapeCanvas a=sha.get(glbref-1);
			a.hspeed=horizantalSlider.getValue(); // setting slider value to speed of object
			repaint();
			});
		//change listener for vertical slider
		verticalSlider.addChangeListener(e->{
			ShapeCanvas a=sha.get(glbref-1);
			a.vspeed=verticalSlider.getValue();
			repaint();
			});
		sliderPanel.add(horizantalPanel);
		JPanel verticalPanel = new JPanel(true);
		JLabel verticalLabel = new JLabel("Vertical Speed:");
		
		verticalSlider.setMinorTickSpacing(5);
		verticalSlider.setPaintTicks(true);
		verticalSlider.setPaintLabels(true);
		verticalSlider.setSnapToTicks(true);
		verticalPanel.add(verticalLabel);
		verticalPanel.add(verticalSlider);
		sliderPanel.add(verticalPanel);

		return sliderPanel;
	}

	/**
	 * Main function where a frame is created
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			AnimationGUI frame = new AnimationGUI("Bouncing Balls Animation");
			frame.setVisible(true);
			frame.pack();
		});

	}
	/**
	 * 
	 * @author Yaswanth Reddy Vayalpati
	 * ZID:Z1805553
	 *This inner class creates shape object 
	 */

	class ShapeCanvas extends JPanel implements Serializable {
		public int X, Y, wid, hei;  //variables for x co ordinate and Y coordinate , width, height
		float dia; // variable for size of the ball
		Color c;  //Variable for the color of the ball
		int hspeed,vspeed;   // variables for horizantal and vertical speeds.
		public int getX() {
			return X;
		}

		public void setX(int x) {
			X = x;
		}

		public int getY() {
			return Y;
		}

		public void setY(int y) {
			Y = y;
		}

		public float getDia() {
			return dia;
		}

		public void setDia(float dia) {
			this.dia = dia;
		}

		public Color getC() {
			return c;
		}

		public void setC(Color c) {
			this.c = c;
		}
		/**
		 * 
		 * @param a x co ordinate
		 * @param b y co ordinate
		 * @param c size of ball
		 * @param d color of the ball
		 * @param w width of the frame
		 * @param h  height of the frame
		 * @param hs horizantal speed
		 * @param vs vertical speed
		 */

		ShapeCanvas(int a, int b, float c, Color d, int w, int h,int hs,int vs) {
			this.X = a;
			this.Y = b;
			this.dia = c;
			this.c = d;
			this.wid = w;
			this.hei = h;
			this.hspeed=hs;
			this.vspeed=vs;
		}
		/**
		 * This method draws all the shapes in the Shapes Arraylist
		 */

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			for (ShapeCanvas a : sha) {
				Ellipse2D dra = new Ellipse2D.Float(a.X, a.Y, a.dia, a.dia);
				g2d.setColor(a.c);
				g2d.draw(dra);
				g2d.fill(dra);  

			}

		}

	}
	/**
	 * This class creates thread for each ball
	 * @author Yaswanth Reddy Vayalpti
	 *
	 */
	class RunThread extends Thread implements Serializable{
		ShapeCanvas tempObj;
		RunThread(ShapeCanvas b){
			tempObj=b;
			start();
		}
		/**
		 * run method moves the ball from one position to another
		 */
		@Override
		public void run() {
//			super.run();
			while (true) {
				if(stopFlag==true) {
					
					if ((tempObj.X < 0) || (tempObj.X > tempObj.wid)) { //If next positon is less than zero or greater than width
						tempObj.hspeed=-(tempObj.hspeed);
					}
					if ((tempObj.Y < 0) || (tempObj.Y > (tempObj.hei))) { //If next position is less than zero or greater than height
						tempObj.vspeed=-(tempObj.vspeed);
					}
					int tempX = tempObj.getX()+ tempObj.hspeed;
					int tempY = tempObj.getY() + tempObj.vspeed;
					tempObj.setX(tempX);
					tempObj.setY(tempY);
					

					try {
						Thread.sleep(40);   //putting thread to sleep
					} catch (InterruptedException ex) {
						System.out.println(ex.getMessage());
					}
				}
				repaint();
				}
		}
		
	}

}