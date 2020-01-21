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


	public MyGameGUI() throws JSONException, IOException {
		JFrame Show = new JFrame();
		JOptionPane.showMessageDialog(Show,"Loading ..." );	
		initGUI();
		this.setVisible(true);
		BufferedImage image = ImageIO.read(new File("images/waze_maze.png"));
		this.getGraphics().drawImage(image, 90, 100, 400,400, null);
		Game_Server.login(302313184);
		SimpleDB.printLog();
		
		}
	
	/**
	 * Return this kml object 
	 * @return
	 */
	public KML_Logger getKml() {
		return _kml;
	}
	/**
	 * let the game mannage the kml file by the points the he visit
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
			
		    case "Test Boaz":{
		    	AutomaticCheck();
		    	
		    }
		}
	}
	//Automatic check for boaz test
	private void AutomaticCheck() {
		
		
		
		
		
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
        if(input>23 ||input<0)throw new RuntimeException("No such level exsist");
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
					// TODO Auto-generated catch block
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
	        if(input>23 ||input<0)throw new RuntimeException("No such level exsist");
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
			int delay = 135; //milliseconds
			
			ActionListener taskPerformer = new ActionListener() {
			      public void actionPerformed(ActionEvent evt) {
			    	  try {
						manage.move_robots_Auto();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			    	  if(!game.isRunning()) {
			    		 try {
						 try {
							gameover();
							if(dialogKML() == 0) {
								getKml().KMLtoFile();
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
	        int x = JOptionPane.showOptionDialog(null, "Do you want to save this game as KML file?\n"
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
		//moveRobots(game, gg);
		try {
			paint_random();
			List<String> stat = game.getRobots();
			for(int i=0;i<stat.size();i++) {
				System.out.println(jj+") "+stat.get(i));
			}
			ind++;
		//	Thread.sleep(dt);
			jj++;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	String res = game.toString();
	String remark = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><kml xmlns=\"http://earth.google.com/kml/2.2\"><Document><name>Game Scenario 0</name><Style id=\"Robot-0\"><IconStyle><Icon><href>https://www.freepngimg.com/thumb/apple/44-apple-png-image.png</href></Icon><hotSpot x=\"32\" xunits=\"pixels\" y=\"1\" yunits=\"pixels\"/></IconStyle></Style><Style id=\"Robot-1\"><IconStyle><Icon><href>https://www.freepngimg.com/thumb/banana/9-banana-png-image.png</href></Icon><hotSpot x=\"32\" xunits=\"pixels\" y=\"1\" yunits=\"pixels\"/></IconStyle></Style><Style id=\"Robot-2\"><IconStyle><Icon><href>https://www.freepngimg.com/thumb/technology/40061-1-machining-robot-download-free-image.png</href></Icon><hotSpot x=\"32\" xunits=\"pixels\" y=\"1\" yunits=\"pixels\"/></IconStyle></Style><Style id=\"Robot-3\"><IconStyle><Icon><href>https://www.freepngimg.com/thumb/symbol/83754-and-daily-black-internet-logo-white-dot.png</href></Icon><hotSpot x=\"32\" xunits=\"pixels\" y=\"1\" yunits=\"pixels\"/></IconStyle></Style><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-3</styleUrl><Point><coordinates>35.18753053591606,32.10378225882353,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-3</styleUrl><Point><coordinates>35.18958953510896,32.10785303529412,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-3</styleUrl><Point><coordinates>35.19341035835351,32.10610841680672,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-3</styleUrl><Point><coordinates>35.197528356739305,32.1053088,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-3</styleUrl><Point><coordinates>35.2016888087167,32.10601755126051,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-3</styleUrl><Point><coordinates>35.20582803389831,32.10625380168067,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-3</styleUrl><Point><coordinates>35.20792948668281,32.10470908739496,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-3</styleUrl><Point><coordinates>35.20746249717514,32.10254648739496,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-3</styleUrl><Point><coordinates>35.20319591121872,32.1031462,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-3</styleUrl><Point><coordinates>35.19597880064568,32.10154696638656,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-3</styleUrl><Point><coordinates>35.18910131880549,32.103618700840336,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.197656770719604,32.10191878639921,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19597880064568,32.10154696638656,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.197656770719604,32.10191878639921,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19597880064568,32.10154696638656,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:00Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19682551409664,32.1017345889258,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.1978752407142,32.10196719698615,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.198746711868395,32.10216030556455,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.199514200668965,32.10233037277849,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20041538106706,32.10253006460388,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20126704605866,32.102718784350955,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:01Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20209395334702,32.10290201805888,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.203039697610954,32.10311158475476,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.203509746088955,32.10310208727626,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20394807743664,32.10304047529022,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20438381510772,32.10297922787215,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.204884394694126,32.10290886625495,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:02Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20528382089261,32.102852722788384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.205714371210455,32.10279220450624,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.206155296234755,32.10273022795223,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20658325287599,32.10267007423805,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20701639687044,32.102609191387934,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20746846272823,32.10257411350838,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:03Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20754280269746,32.1029183773833,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20761897822148,32.10327114160081,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.2076992837438,32.10364303158914,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20777958926611,32.10401492157747,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.207863107009324,32.10440168716534,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.207896724673496,32.10473316975559,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:04Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20769385223123,32.10488229514259,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:05Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:05Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.207485939479845,32.105035125508145,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:05Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:05Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20725660541468,32.105203702032576,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:05Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:05Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.207037351967756,32.105364868599885,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:05Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:05Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.206818098520834,32.1055260351672,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:05Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:05Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20659380476479,32.10569090671307,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.206366990854185,32.105857630748226,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20613765678902,32.106026207272656,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20591714326482,32.10618830008461,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20553363794908,32.10623699873721,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20502693722877,32.10620807828644,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:06Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.204545713081,32.10618061193656,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.204053166012095,32.10615249931961,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20355495748263,32.10612406356914,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20306241041373,32.106095950952195,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20255287896314,32.10606686893467,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.199963710098416,32.105723673136964,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20204617824283,32.1060379484839,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.195224052340706,32.10575624080796,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:07Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.201546402715984,32.10599329177306,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:08Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.195224052340706,32.10575624080796,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:08Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.201027039654534,32.105904815995316,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:08Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.195224052340706,32.10575624080796,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:08Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20055793753451,32.105824902389614,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:08Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.195224052340706,32.10575624080796,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:08Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20006091266925,32.10574023202167,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:08Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.195224052340706,32.10575624080796,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:08Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19956947235304,32.10565651300617,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:08Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.195224052340706,32.10575624080796,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:08Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19907523976231,32.105572318314444,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.195224052340706,32.10575624080796,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.1985837994461,32.105488599298944,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.195224052340706,32.10575624080796,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19808677458084,32.105403928931,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.195224052340706,32.10575624080796,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19762604928439,32.10532544235397,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19494883961552,32.105809680537625,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.197040801933504,32.10540347148367,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19038634163924,32.10748920705224,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.196380727734876,32.10553164210771,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19038634163924,32.10748920705224,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:09Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.195731904800994,32.105657628005204,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19038634163924,32.10748920705224,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.195090582710286,32.10578215741833,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19038634163924,32.10748920705224,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.194453011041155,32.10590595858928,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19038634163924,32.10748920705224,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19380418810728,32.10603194448677,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19038634163924,32.10748920705224,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.193270050069344,32.10617248269274,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19038634163924,32.10748920705224,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.192886986182415,32.10634739273077,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19038634163924,32.10748920705224,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:10Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19251283075797,32.10651823509349,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.192125312639796,32.10669517896917,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19175338433098,32.10686500441306,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19133691370972,32.10705516823347,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19095607693841,32.107229061352676,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19057524016711,32.10740295447187,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:11Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19019885762704,32.10757481375342,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18984251912757,32.10773752076554,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18970428817874,32.107800638123614,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19006153830165,32.10763751485693,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19043177933812,32.1074684598351,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19079985522233,32.107300393439125,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:12Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19115277504072,32.107139247424165,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19152301607719,32.106970192402336,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19188892680914,32.10680311463222,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19225483754109,32.106636036862106,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.192633739186604,32.106463027336844,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.193008310527595,32.106291995063295,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:13Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.193365560650506,32.106128871796614,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.193903706580365,32.10601262037923,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19451228942955,32.105894448143864,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.195117271196786,32.105776975152494,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19573305620987,32.10565740442914,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19633803797711,32.10553993143776,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.1992728373109,32.105605979924384,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:14Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.196943019744346,32.10542245844639,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.18893516072167,32.10655929420479,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19702580024717,32.105406384452394,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.18893516072167,32.10655929420479,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19572065353625,32.10565981273175,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.18893516072167,32.10655929420479,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19443050851166,32.10591032804237,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.18893516072167,32.10655929420479,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19330345680367,32.106157228910355,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.18893516072167,32.10655929420479,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19257296287976,32.10649077828519,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.18893516072167,32.10655929420479,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:15Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.191824652030874,32.10683246301064,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.18893516072167,32.10655929420479,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.191071886950745,32.107176181573735,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.18893516072167,32.10655929420479,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.190310213408125,32.107523967812135,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18957863208372,32.107831479298014,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.189212290435876,32.10710719782894,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18886121302336,32.10641309475441,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:16Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18849269077046,32.10568450208611,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18813071033271,32.10496884301548,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18778399413028,32.10428336233939,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18763152831233,32.103771742976456,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.187994671184015,32.103733930675276,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18834062386059,32.10369790830551,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:17Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.188680130213996,32.10366255716003,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18901319024424,32.10362787723883,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19054159926796,32.10318483874858,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.192664117844224,32.10254546303442,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19479927045964,32.10190228151242,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19671162866172,32.10170935314567,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:18Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19837534631973,32.10207801497717,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20001925781515,32.10244228797733,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20166316931057,32.102806560977506,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20539663533063,32.10283686555705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20326334681067,32.103136721232914,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20414000950604,32.10301349726083,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20502704690787,32.10288881501689,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:19Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20588814754356,32.102767778452595,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20676999759216,32.102643825344586,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20749461938407,32.10269524339029,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.207646052654724,32.10339652165401,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20779381481578,32.104080799232555,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20789924482806,32.104731317266314,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:20Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20747333870703,32.10504438795454,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20705247289513,32.10535375366421,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.206626566774105,32.10566682435244,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20619562034395,32.10598360001923,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.205680835923694,32.10624540020894,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.2047240490887,32.10619079064269,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:21Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.203784246635394,32.106137150477025,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.202850105642646,32.10608383344489,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19974476565152,32.105686374989176,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20189898026822,32.10602954701217,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-0</styleUrl><Point><coordinates>35.188500771222955,32.10570047767076,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.199957598511865,32.10572263200137,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-0</styleUrl><Point><coordinates>35.188500771222955,32.10570047767076,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19737833987598,32.10533792968729,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-0</styleUrl><Point><coordinates>35.188500771222955,32.10570047767076,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.194265489962,32.10594237069838,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-0</styleUrl><Point><coordinates>35.188500771222955,32.10570047767076,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:22Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19204068224617,32.106733821884546,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:23Z</when></TimeStamp><styleUrl>Robot-0</styleUrl><Point><coordinates>35.188500771222955,32.10570047767076,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:23Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19020331185828,32.10757277991577,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:23Z</when></TimeStamp><styleUrl>Robot-0</styleUrl><Point><coordinates>35.188500771222955,32.10570047767076,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:23Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18901167477158,32.106710567500635,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:23Z</when></TimeStamp><styleUrl>Robot-0</styleUrl><Point><coordinates>35.188500771222955,32.10570047767076,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:23Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18803040250056,32.10477052785133,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:23Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19565793847981,32.10567199049591,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:23Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18807452649018,32.10485776387326,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:23Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19565793847981,32.10567199049591,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:23Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18930218089395,32.10728491716119,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19565793847981,32.10567199049591,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.1909535810328,32.10723020100316,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19565793847981,32.10567199049591,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19275065740865,32.10640964154047,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19565793847981,32.10567199049591,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.195246910146906,32.10575180236863,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.188827745913414,32.10634692808267,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.196797024530596,32.1054508072255,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.188827745913414,32.10634692808267,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19368417461662,32.1060552482366,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.188827745913414,32.10634692808267,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:24Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19182910626211,32.10683042917299,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.188827745913414,32.10634692808267,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.189924922405574,32.107699894768984,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.18867176864414,32.103663427809636,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18886993544354,32.106430339551295,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19580080140219,32.10564424993287,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18796498434916,32.104641191874705,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19580080140219,32.10564424993287,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.188155389953906,32.10501763624552,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19580080140219,32.10564424993287,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18936099068575,32.10740118797737,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19580080140219,32.10564424993287,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:25Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.1910077098393,32.107205485356694,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19580080140219,32.10564424993287,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19282643773775,32.10637503963542,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19580080140219,32.10564424993287,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.195372948015084,32.10572732882876,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.188081181912885,32.104870922080536,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19666575977519,32.10547629570188,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.188081181912885,32.104870922080536,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19357166196912,32.10607709550206,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.188081181912885,32.104870922080536,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.191817970684006,32.106835513767116,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.188081181912885,32.104870922080536,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:26Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.1899583291399,32.1076846409866,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20103595448387,32.10266757695841,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.188875386956155,32.106441117549345,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20103595448387,32.10266757695841,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.187975887374385,32.10466274787081,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20103595448387,32.10266757695841,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.187965662729766,32.103736951184544,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20103595448387,32.10266757695841,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.18887351990898,32.10364242043159,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20103595448387,32.10266757695841,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.192828360353104,32.10249598753272,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20103595448387,32.10266757695841,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19746426284034,32.10187612873611,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19945252489246,32.10563659049281,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:27Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20157404157889,32.102786811236534,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:28Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19945252489246,32.10563659049281,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:28Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20450571790855,32.1029620931778,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:28Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19945252489246,32.10563659049281,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:28Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20667143788084,32.102657678927244,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:28Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19945252489246,32.10563659049281,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:28Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20770111929859,32.10365153193173,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:28Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19945252489246,32.10563659049281,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:28Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.207488459634405,32.10503327301886,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:28Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19945252489246,32.10563659049281,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:28Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.206442595491055,32.10580205606984,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:28Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19945252489246,32.10563659049281,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20489389290556,32.10620048464853,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19945252489246,32.10563659049281,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20253023312089,32.10606557640055,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20641810155373,32.10582006082121,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20020890321902,32.10576544286156,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20641810155373,32.10582006082121,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19796112222726,32.1053825235009,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20641810155373,32.10582006082121,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.19949198938419,32.10564331344224,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20641810155373,32.10582006082121,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.2018271888758,32.10602544944659,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.20641810155373,32.10582006082121,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:29Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20359419706113,32.106126303207354,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:30Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19879105430872,32.10552390607473,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:30Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.205361205246454,32.106227156968124,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:30Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19879105430872,32.10552390607473,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:30Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.204115442078276,32.106156053788425,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:30Z</when></TimeStamp><styleUrl>Robot-1</styleUrl><Point><coordinates>35.19879105430872,32.10552390607473,0.0</coordinates></Point></Placemark><Placemark><TimeStamp><when>2020-01-19T10:53:30Z</when></TimeStamp><styleUrl>Robot-2</styleUrl><Point><coordinates>35.20169516768799,32.10601791420516,0.0</coordinates></Point></Placemark></Document></kml>";
	game.sendKML(remark); // Should be your KML (will not work on case -1).
	System.out.println(res);
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
		MenuItem item1 = new MenuItem("Automatic");
		item1.addActionListener(this);
		MenuItem item2 = new MenuItem("Manual");
		item2.addActionListener(this);
		MenuItem item3 = new MenuItem("Test Boaz");
		item3.addActionListener(this);
		menu.add(item1);
		menu.add(item2);
		menu.add(item3);
		this.addMouseListener(this);
	}	
	
	/**
	 * Draw the graph while the game is running
	 * @param mode
	 * @throws JSONException
	 */
	
	public void paint(int mode) throws JSONException//add text in case two edges go the same direction
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

