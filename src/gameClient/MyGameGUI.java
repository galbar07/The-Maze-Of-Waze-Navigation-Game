package gameClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.json.JSONException;
import org.json.JSONObject;
import Server.Game_Server;
import Server.game_service;
import algorithms.robot_algo;
import dataStructure.*;
import utils.Point3D;
import utils.StdDraw;
/**
 * This class draw to the user the graph and the movement of the fruit and robots on the graph
 * @author Gal bar Eden Reuveni
 *
 */

public class MyGameGUI extends JFrame implements ActionListener, MouseListener,Runnable {

	private static final long serialVersionUID = 1L;
	private KML_Logger _kml;
	DGraph gg ;
	game_service game;
	static int i=0;
	static int mc=0;
	Thread t ;
	double max_x;
	double min_x;
	double max_y;
	double min_y;
	Timer time;
	int id;

/**
 * default constructor of MyGameGUI
 * @param id the player's id
 * @throws JSONException
 * @throws IOException
 */
	public MyGameGUI() throws JSONException, IOException {
		JFrame Show = new JFrame();
		String inputString = JOptionPane.showInputDialog(null, "Please Enter your id number ");
	    this.id = Integer.parseInt(inputString);
	    initGUI();
		this.setVisible(true);
		BufferedImage image = ImageIO.read(new File("images/waze_maze.png"));
		this.getGraphics().drawImage(image, 90, 100, 400,400, null);
		Game_Server.login(id);
		}
	
	/**
	 * returns this kml object 
	 * @return
	 */
	public KML_Logger getKml() {
		return _kml;
	}
	/**
	 * 
	 * sets kml object to the one it got from the game
	 * @param _kml
	 */
	private void setKml(KML_Logger _kml) {
		this._kml = _kml;
	}

	/**
	 * Setting the scale of the stdraw
	 * @param mode
	 * @throws JSONException
	 * @throws IOException
	 */
	private void set_scale(int mode) throws JSONException, IOException {
		
		StdDraw.setCanvasSize(800,800);

		this.max_x = Double.MIN_VALUE;
		this.max_y = Double.MIN_VALUE;
		this.min_x = Double.MAX_VALUE;
		this.min_y = Double.MAX_VALUE;
	
		gg.init(this.game.getGraph());
		Collection<node_data>search = gg.getV();
		for (node_data v : search) {
			max_x = Math.max(max_x, v.getLocation().x());
			max_y = Math.max(max_y, v.getLocation().y());
			min_y = Math.min(min_y, v.getLocation().y());
			min_x = Math.min(min_x, v.getLocation().x());

		}
		StdDraw.setXscale(min_x-0.002,max_x+0.002);
		StdDraw.setYscale(min_y-0.002,max_y+0.002);

		paint(mode);
	}
	
	/**
	 * In here you choose if you want to use manual mode or auto mode and by that you can start playing the game
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		switch(str)
		{
		case "Automatic":
			try {
				try {
					try {
						paintAuto();
					} catch (InterruptedException e1) {e1.printStackTrace();}
				} catch (IOException e1) {e1.printStackTrace();}		
			} catch (JSONException e1) {e1.printStackTrace();}
			
			break;
			
		    case "Manual":
			try {
				try {
				try {
					paintManual();
				} catch (InterruptedException e1) {e1.printStackTrace();}
				} catch (IOException e1) {e1.printStackTrace();}		
			} catch (JSONException e1) {e1.printStackTrace();}
			break;
			
		    case "Your statistics in all stages":{
		    	Your_statistics();
		    	break;
		    }
		    
		    case "Your rank compared to all students":{
					Your_rank();
					break;
		    }
		    		
		}
	}
	/**
	 * player's rank in each one of the 11 required levels, compared to the entire class 
	 */
	private void Your_rank() {
		SimpleDB.getStats(id);
		SimpleDB.getRank(id);
		StringBuilder ans = new StringBuilder("\n");
		for(int i=0;i<SimpleDB.rankInClass[0].length;i++)
			ans.append("On level "+SimpleDB.rankInClass[0][i]+" your rank in class is: "+SimpleDB.rankInClass[1][i]+"\n");
		JOptionPane.showMessageDialog(null, "Your rank compared to the entire class- \n"+ans+"" ,"Your rank compared to all students", JOptionPane.CLOSED_OPTION);
				
	}

	/**
	 * player's score in all stages he had done.
	 */
	private void Your_statistics() {
		
		
			SimpleDB.getStats(id);
			StringBuilder ans = new StringBuilder("\n");
			for(int i=0;i<=SimpleDB.maxUserLevel;i++)
				ans.append("On level "+i+" your best score is: "+SimpleDB.bestScores[i]+"\n");
			if (SimpleDB.maxUserLevel!=23)
			JOptionPane.showMessageDialog(null, "You have played "+SimpleDB.gamesPlayed+ " games.\n"+
					"Next stage you can play: "+SimpleDB.maxUserLevel+ "\n"+
					"Your best scores- \n"+ans+"\n"
					,"Your statistics in all stages", JOptionPane.CLOSED_OPTION);
			else
				JOptionPane.showMessageDialog(null, "You've played "+SimpleDB.gamesPlayed+ " games"+
						" and passed all "+SimpleDB.maxUserLevel+" levels.\nTime to start again and break your own records!\n"+"\n"+
						"Your best scores-"+ans+"\n"
						,"Your statistics in all stages", JOptionPane.CLOSED_OPTION);
	}

	/**
	 * Here is the logic of the paint in manual mode first you draw the graph and locate the robots 
	 * based on the user choice ,and then we let the user to decide to which fruit he wants to go
	 * @throws JSONException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void paintManual() throws JSONException, IOException, InterruptedException {
        String inputString = JOptionPane.showInputDialog(null, "INPUT LEVEL");
        int input = Integer.parseInt(inputString);
        if(input>23 ||input<0)throw new RuntimeException("No such level.\nPlease  pick a stage between 0 to 23");
    	game_service game = Game_Server.getServer(input);

    	String g = game.getGraph();
    	DGraph gg = new DGraph();
		gg.init(g);
		setKml(new KML_Logger(input, gg));
		this.game = game;
		this.gg = gg;
		set_scale(0);
		robot_algo rob = new robot_algo(game);
		game_manager alg = new game_manager(rob);
		alg.locate_robots_manual();
		game.startGame();
		t = new Thread(this);
		t.start();
		
		int delay = 50; //milliseconds
		
		ActionListener taskPerformer = new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		    	  try {
					alg.move_robots_manual();
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
		    	  if(!game.isRunning()) {
		    		 try {
						try {
							gameover();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					} 
		    	  }
		      }
		  };
		time =new Timer(delay, taskPerformer);
		time.start();
	}
	/**
	 *In here we draw the graph and locate the robots based on where is the most valuabale fruit 
	 *after that we activate our algoritham to always find the most expensive fruit and gp there using the
	 *best way to get there
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void paintAuto() throws JSONException, IOException, InterruptedException {
		    String inputString = JOptionPane.showInputDialog(null, "INPUT LEVEL BETWEEN 0-23");
	        int input = Integer.parseInt(inputString);
	        if(input>23 ||input<0)throw new RuntimeException("No such level.\nPlease  pick a stage between 0 to 23");
	    	game_service game = Game_Server.getServer(input);
	    	String g = game.getGraph();
	    	DGraph gg = new DGraph();
			gg.init(g);
			this.game = game;
			this.gg = gg;
			setKml(new KML_Logger(input, gg));
			robot_algo robot = new robot_algo(this.game);
			game_manager manage = new game_manager(robot);
			manage.locate_robots_auto();
			set_scale(1);
			game.startGame();
			t = new Thread(this);
			t.start();
			int delay;
			switch(input) {	//set sleep for each one of the stages, in milliseconds

				case 0:
					delay = 150;
					break;
				case 1:
					delay = 103;
					break;
				case 3:
					delay = 110;
					break;
				case 5:
					delay = 120;
					break;
				case 9:
					delay = 101;
					break;
				case 11:
					delay = 102;
					break;
				case 13:
					delay = 103;
					break;
				case 16:
					delay = 110;
					break;
				case 19:
					delay = 103;
					break;
				case 20:
					delay = 101;
					break;
				case 23:
					delay = 51;
					break;
				default:
					delay=1;
					break;
					//set sleep for every other stage, in milliseconds
			
			}
			
			ActionListener taskPerformer = new ActionListener() {
			      public void actionPerformed(ActionEvent evt) {
			    	  try {
						manage.move_robots_Auto();
			    		
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
			    	  if(!game.isRunning()) {
			    		 try {
						 try {
							gameover();
							if(dialogKML() == 0) {
								getKml().KMLtoFile();
								String content = new String(Files.readAllBytes(Paths.get("data\\"+input+".kml")), "UTF-8");
								game.sendKML(content);
								System.out.println("KML file has been succesfully sent to the DB");
							}
							
							} catch (InterruptedException e) {e.printStackTrace();}
						    } catch (IOException e) {e.printStackTrace();}
			    	  }
			      }
			  };
			time =new Timer(delay, taskPerformer);
			time.start();
			
	}
	/**
	 * we let the user decide if he wants to save a kml file of his session
	 * @return
	 */
	public int dialogKML(){
		try {
	        Object[] options = {"Yes", "No"};
	        int x = JOptionPane.showOptionDialog(null, "Would you like to save this game as a KML file?\n"
	        		+ ""
	        		+ "",
	                "",
	                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

	     
			  if(x == -1)
				  return 1;
			  
			  return x;
	      
		}catch(Exception err) {
			return 1;
		}
	}
	/**
	 * When the game is over we wants to show the user that the game is over
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void gameover() throws IOException, InterruptedException {
		time.stop();
		t.join();
		BufferedImage image = ImageIO.read(new File("images/gameover.png"));
		this.getGraphics().drawImage(image, 90, 100, 400,400, null);
		JFrame Show = new JFrame();
		StdDraw.setCanvasSize(1,1);
		JOptionPane.showMessageDialog(Show,"Game Over you scored :" + this.game.toString() );
		SimpleDB.printLog();
		
	}
	
	/**
	 * This is a thread that keeps draw the graph while the game is running
	 */
	@Override
	public void run() {
	int ind=0;
	long dt=120;
	int jj = 0;
	int id = 999;
	Game_Server.login(id);
	while(this.game.isRunning()) {
		try {
			paint_random();
			List<String> stat = game.getRobots();
			for(int i=0;i<stat.size();i++) {
				System.out.println(jj+") "+stat.get(i));
			}
			ind++;
			jj++;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	String res = game.toString();
}
	
		
	/**
	 * Init the jframe 
	 * @throws JSONException
	 * @throws IOException
	 */

	private void initGUI() throws JSONException, IOException  
	{	
		this.setSize(600, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("Play");
		menuBar.add(menu);
		this.setMenuBar(menuBar);
		Menu menu2 = new Menu("Game Statistics");
		menuBar.add(menu2);
		this.setMenuBar(menuBar);
		MenuItem item1 = new MenuItem("Automatic");
		item1.addActionListener(this);
		MenuItem item2 = new MenuItem("Manual");
		item2.addActionListener(this);
		MenuItem item4 = new MenuItem("Your statistics in all stages");
		item4.addActionListener(this);
		MenuItem item5 = new MenuItem("Your rank compared to all students");
		item5.addActionListener(this);
		menu.add(item1);
		menu.add(item2);
		menu2.add(item4);
		menu2.add(item5);
		this.addMouseListener(this);
	}	
	
	/**
	 * Draw the graph while the game is running
	 * @param mode
	 * @throws JSONException
	 */
	
	public void paint(int mode) throws JSONException
	//add text in case two edges go to the same direction
	{

		StdDraw.enableDoubleBuffering();
		Font font = new Font("Arial", Font.BOLD, 15);
		StdDraw.setPenRadius(0.02);
		Collection<node_data> Paint_node = gg.getV();
		for (node_data v : Paint_node) {
			StdDraw.setPenColor(Color.black);
			StdDraw.point(v.getLocation().x(), v.getLocation().y());
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setFont(font);
			StdDraw.text(v.getLocation().x(), v.getLocation().y()+0.00020, Integer.toString(v.getKey()));
		}
		StdDraw.setPenRadius(0.005);
		for (node_data v : Paint_node) {
			Collection<edge_data> Paint_edges = gg.getE(v.getKey());
			if(Paint_edges==null)
				break;
			for(edge_data E: Paint_edges) {
				Point3D p1 = pointreturn(E.getDest());
				Point3D p2 = pointreturn(E.getSrc());
				if(p1!=null && p2!=null) {
					StdDraw.setPenRadius(0.005);
					StdDraw.setPenColor(Color.RED);
					StdDraw.line(p1.x(), p1.y(),p2.x(), p2.y());
					Point3D T = new Point3D(((p1.x()+p2.x())/2),((p1.y()+p2.y())/2));
					StdDraw.setPenRadius(0.5);
					StdDraw.setPenColor(Color.BLACK);
					String no = String.format("%.1f", E.getWeight());
					StdDraw.text(((T.x()+p1.x())/2),((T.y()+p1.y())/2), no);
					StdDraw.setPenColor(Color.CYAN);
					StdDraw.setPenRadius(0.009);
					Point3D p4 = new Point3D((p1.x()+p2.x())/2,(p1.y()+p2.y())/2);


					for(int i=0;i<2;i++) {
						Point3D p5 = new Point3D((p4.x()+p1.x())/2,(p4.y()+p1.y())/2);
						p4 = new Point3D(p5);
					}
					StdDraw.point(p4.x(),p4.y());					

				}				
			}
		
		}
		paint_fruit();
			if(mode==1){
		paint_robots();
		}
			StdDraw.show();
			
	}
	/**
	 * Draws the robot on the graph
	 * @throws JSONException
	 */
	private void paint_robots() throws JSONException {
		List<String> robots = game.getRobots();
		Iterator<String> r_iter=robots.iterator(); 
		JSONObject line2 ;
	    int count=0;
	    JSONObject line = new JSONObject(this.game.toString());
	    JSONObject ttt1 = line.getJSONObject("GameServer");
		int rs = ttt1.getInt("robots");
	    while(count<rs) {
			line2 = new JSONObject(r_iter.next().replaceAll("\\s+",""));
			JSONObject ttt = line2.getJSONObject("Robot");
			String[] posOfRobots = ttt.getString("pos").split(",");
			Point3D p_robot = new Point3D(Double.parseDouble(posOfRobots[0]),Double.parseDouble(posOfRobots[1])); 
			StdDraw.picture(p_robot.x(),p_robot.y(),"images/robot.png",0.0007,0.0007);//change 
			this.getKml().Placemark(2, p_robot.x(),p_robot.y(), this.getKml().currentTime());
			count++;
	    }
		
	}
	/**
	 * Draw the fruit on the graph
	 * @throws JSONException
	 */
	private void paint_fruit() throws JSONException {
		Iterator<String> f_iter = game.getFruits().iterator();
		JSONObject line2 ;
		while(f_iter.hasNext()) {
			
			line2 = new JSONObject(f_iter.next().replaceAll("\\s+",""));
			JSONObject ttt = line2.getJSONObject("Fruit");
			double rid = ttt.getDouble("value");
			int type = ttt.getInt("type");
			String p[] = ttt.getString("pos").split(",");
			Point3D p_fruit = new Point3D(Double.parseDouble(p[0]),Double.parseDouble(p[1])); 
			if(type==1) {
			StdDraw.picture(p_fruit.x(),p_fruit.y(),"images/apple.png",0.0007,0.0007);//change
			this.getKml().Placemark(0, p_fruit.x(), p_fruit.y(), this.getKml().currentTime());
			}
			else{
				StdDraw.picture(p_fruit.x(),p_fruit.y(),"images/banana.png",0.0007,0.0007);
				this.getKml().Placemark(1, p_fruit.x(), p_fruit.y(), this.getKml().currentTime());
			}
		}
	}

	/**
	 * Return the point that associate with that key
	 * @param key
	 * @return
	 */
	private Point3D pointreturn(int key) {
		Collection<node_data> Paint_node = gg.getV();
		for (node_data v : Paint_node) {
			if(v.getKey() == key) {
				return v.getLocation();
			}
		}
		return null;
	}
	/**
	 * draw the graph
	 * @throws JSONException
	 */
	private void paint_random() throws JSONException {
		
		StdDraw.enableDoubleBuffering();
		Font font = new Font("Arial", Font.BOLD, 15);
		StdDraw.setPenRadius(0.02);
		Collection<node_data> Paint_node = gg.getV();
		for (node_data v : Paint_node) {
			StdDraw.setPenColor(Color.black);
			StdDraw.point(v.getLocation().x(), v.getLocation().y());
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setFont(font);
			StdDraw.text(v.getLocation().x(), v.getLocation().y()+0.00020, Integer.toString(v.getKey()));
		}
		
		StdDraw.setPenRadius(0.005);
		StdDraw.setPenColor(Color.RED);
		StdDraw.text(this.max_x-0.001, this.max_y, "Time remaining :" + this.game.timeToEnd()/1000);//need to find the formula
		for (node_data v : Paint_node) {
			Collection<edge_data> Paint_edges = gg.getE(v.getKey());
			if(Paint_edges==null)
				break;
			for(edge_data E: Paint_edges) {
				Point3D p1 = pointreturn(E.getDest());
				Point3D p2 = pointreturn(E.getSrc());
				if(p1!=null && p2!=null) {
					StdDraw.setPenRadius(0.005);
					StdDraw.setPenColor(Color.RED);
					StdDraw.line(p1.x(), p1.y(),p2.x(), p2.y());
					Point3D T = new Point3D(((p1.x()+p2.x())/2),((p1.y()+p2.y())/2));
					StdDraw.setPenRadius(0.5);
					StdDraw.setPenColor(Color.BLACK);
					 String no = String.format("%.1f", E.getWeight());
					StdDraw.text(((T.x()+p1.x())/2),((T.y()+p1.y())/2), no);
					StdDraw.setPenColor(Color.CYAN);
					StdDraw.setPenRadius(0.009);
					Point3D p4 = new Point3D((p1.x()+p2.x())/2,(p1.y()+p2.y())/2);

					for(int i=0;i<2;i++) {
						Point3D p5 = new Point3D((p4.x()+p1.x())/2,(p4.y()+p1.y())/2);
						p4 = new Point3D(p5);
					}
					StdDraw.point(p4.x(),p4.y());
				}
			}
		
		}
		paint_fruit();
		paint_robots();
		StdDraw.pause(150);
		
		StdDraw.show();
		StdDraw.clear();
		
		
		
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}
	
	}

