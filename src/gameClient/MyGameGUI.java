package gameClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Fruit;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.*;
import oop_dataStructure.OOP_DGraph;
import utils.Point3D;
import utils.StdDraw;


public class MyGameGUI extends JFrame implements ActionListener, MouseListener,Runnable {

	private static final long serialVersionUID = 1L;
	DGraph gg ;
	game_service game;
	static int i=0;
	static int mc=0;
	Thread t ;
	
	public MyGameGUI(game_service game) throws JSONException {
		this.game = game;
		gg = new DGraph();
		set_scale();
		//initGUI();
		StdDraw.enableDoubleBuffering();
		//StdDraw.show();
		 t = new Thread(this);
		t.start();
	}
	private void set_scale() throws JSONException {
		
		StdDraw.setCanvasSize(800,800);

		double max_x = Double.MIN_VALUE;
		double max_y = Double.MIN_VALUE;
		double min_x = Double.MAX_VALUE;
		double min_y = Double.MAX_VALUE;
	
		gg.init(this.game.getGraph());
		gg.getV();
		Collection<node_data>search = gg.getV();
		for (node_data v : search) {
			max_x = Math.max(max_x, v.getLocation().x());
			max_y = Math.max(max_y, v.getLocation().y());
			min_y = Math.min(min_y, v.getLocation().y());
			min_x = Math.min(min_x, v.getLocation().x());

		}
		StdDraw.setXscale(min_x-0.002,max_x+0.002);
		StdDraw.setYscale(min_y-0.002,max_y+0.002);
		paint();
	}
	

	private void initGUI() throws JSONException  
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
	

		MenuItem item4 = new MenuItem("Is connected");
		item4.addActionListener(this);
		MenuItem item5 = new MenuItem("find Shortest path");
		item5.addActionListener(this);
		MenuItem item6= new MenuItem("find Shortest path distance");
		item6.addActionListener(this);
		MenuItem item7= new MenuItem("TSP");
		item7.addActionListener(this);

		menu.add(item1);
		menu.add(item2);
		
		
		this.addMouseListener(this);

	}	
	public void paint() throws JSONException//add text in case two edges go the same direction
	{
		
		
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
					StdDraw.setPenRadius(0.020);
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
	}
	
	private void paint_robots() throws JSONException {
		List<String> robots = game.getRobots();
		Iterator<String> r_iter=robots.iterator(); 
		JSONObject line2 ;
	    System.out.println(robots.get(0));
	    int count=0;
	    JSONObject line = new JSONObject(this.game.toString());
	    JSONObject ttt1 = line.getJSONObject("GameServer");
		int rs = ttt1.getInt("robots");
	    while(count<rs) {
			line2 = new JSONObject(r_iter.next().replaceAll("\\s+",""));
			JSONObject ttt = line2.getJSONObject("Robot");
			String[] posOfRobots = ttt.getString("pos").split(",");
			Point3D p_robot = new Point3D(Double.parseDouble(posOfRobots[0]),Double.parseDouble(posOfRobots[1])); 
			StdDraw.picture(p_robot.x(),p_robot.y(),"robot.png",0.0007,0.0007);//change 
			count++;
	    }
		
	}

	private void paint_fruit() throws JSONException {
		Iterator<String> f_iter = game.getFruits().iterator();
		JSONObject line2 ;
		int count=0;
		while(f_iter.hasNext()) {
			

			line2 = new JSONObject(f_iter.next().replaceAll("\\s+",""));
			JSONObject ttt = line2.getJSONObject("Fruit");
			double rid = ttt.getDouble("value");
			int type = ttt.getInt("type");
			String p[] = ttt.getString("pos").split(",");
			Point3D p_fruit = new Point3D(Double.parseDouble(p[0]),Double.parseDouble(p[1])); 
			if(type==1) {
			StdDraw.picture(p_fruit.x(),p_fruit.y(),"apple.png",0.0007,0.0007);//change 
			}
			else{
				StdDraw.picture(p_fruit.x(),p_fruit.y(),"banana.png",0.0007,0.0007);
			}
			count++;
		}
		
		System.out.println(count);
	}

//	private void drawfromfile() {
//		JFileChooser jf = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
//		int returnV = jf.showOpenDialog(null);
//		Graph_Algo gr = new Graph_Algo();
//		if (returnV == JFileChooser.APPROVE_OPTION) {
//			File selected = jf.getSelectedFile();
//			gr.init(selected.getAbsolutePath());
//		}
//		this.graph = gr.copy();
//		paint();
//	}

//	private void Savetofile() {
//		try {
//			Graph_Algo gr = new Graph_Algo();
//			gr.init(this.graph);
//			JFileChooser jf = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
//			int returnV = jf.showOpenDialog(null);
//			if (returnV == JFileChooser.APPROVE_OPTION) {
//				gr.save(jf.getSelectedFile()+".txt");
//
//			}
//			JFrame Show = new JFrame();
//			JOptionPane.showMessageDialog(Show,"The Graph was successfully saved");
//
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//	}

//	private void Shortest_path_distance() {
//		try {
//			JFrame Show = new JFrame();
//			paint();
//			String Src = JOptionPane.showInputDialog(Show,"Enter Source-Node:");
//			String Dest = JOptionPane.showInputDialog(Show,"Enter Destination-Node:");	
//			int src = Integer.parseInt(Src);
//			int dest = Integer.parseInt(Dest);	
//			Graph_Algo gr = new Graph_Algo();
//			gr.init(this.graph);
//			paint();
//			double num = gr.shortestPathDist(src, dest);
//			JOptionPane.showMessageDialog(Show,"The 'cost' of the shortest path is: " + num);
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//	}


//	private void isconneted() {
//
//		JFrame Show = new JFrame();
//		Graph_Algo gr = new Graph_Algo();
//		gr.init(this.graph);
//		paint();
//		if (gr.isConnected()) 
//			JOptionPane.showMessageDialog(Show,"The Graph is connected");	
//		else
//			JOptionPane.showMessageDialog(Show,"The Graph is not connected");	
//	}



//	private void Shortest_path() {
//		try {
//			JFrame Show = new JFrame();
//			paint();
//			String Src = JOptionPane.showInputDialog(Show,"Enter Source Node:");
//			String Dest = JOptionPane.showInputDialog(Show,"Enter Destination Node:");
//
//			int src = Integer.parseInt(Src);
//			int dest = Integer.parseInt(Dest);
//
//			Graph_Algo gr = new Graph_Algo();
//			gr.init(this.graph);
//			String res = "";			
//			List<node_data> list = gr.shortestPath(src, dest);
//			if (list==null)
//				JOptionPane.showMessageDialog(Show,"There is no path between the source and destinetion node");	
//			else {
//
//				for(int i=0;i<list.size();i++) {
//					if(i==list.size()-1) {
//						res+=list.get(i).getKey();
//					}
//					else {
//						res+=list.get(i).getKey()+"->";
//					}
//
//				}
//				JOptionPane.showMessageDialog(Show,"The Graph Shorthest path from src to dest is : " + res);	
//			}
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//
//
//	}



//	private void tsp() {
//
//		Graph_Algo gr = new Graph_Algo();
//		gr.init(this.graph);
//
//		JFrame Show = new JFrame();
//		paint();
//		String num = JOptionPane.showInputDialog(Show,"Enter number of elements:");
//		int key = Integer.parseInt(num);
//		List<Integer>list = new ArrayList<Integer>();
//		for(int i=0;i<key;i++) {
//			String num2 = JOptionPane.showInputDialog(Show,"Enter vertices:");
//			int key2 = Integer.parseInt(num2);
//			list.add(key2);
//		}
//		List<node_data>tspList = gr.TSP(list);
//		String res="";
//		if(tspList==null) {
//			JOptionPane.showMessageDialog(Show,"There is no path between all nodes you entered");
//
//		} 
//		else {
//			for(int i=0;i<tspList.size();i++) {
//
//				if(i==tspList.size()-1) {
//					res+=""+tspList.get(i).getKey();
//				}
//				else {
//					res+=""+tspList.get(i).getKey()+"->";
//				}
//			}
//			JOptionPane.showMessageDialog(Show,"The Graph relatively short path between the vertices you entered is: " + res);	
//		}
//	}

	private Point3D pointreturn(int key) {
		Collection<node_data> Paint_node = gg.getV();
		for (node_data v : Paint_node) {
			if(v.getKey() == key) {
				return v.getLocation();
			}
		}
		return null;
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
	@Override
	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		switch(str)
		{
		case "Draw Graph":
			try {
				paint();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
//		case "Draw from file": drawfromfile();
//		break;
//		case "Save to file": Savetofile();
//		break;
//		case "find Shortest path distance":Shortest_path_distance();
//		break;
//		case "find Shortest path":Shortest_path();
//		break;
//		case "Is connected":isconneted();
//		break;
//		case "TSP":tsp();
//		break;
		}
	}
//	@Override
//	public void run() {
//		while(true) {
//			if(graph.getMC() != mc) {
//				mc=graph.getMC();
//				synchronized (this) {
//					paint();		
//				}						
//			}
//			try {
//				Thread.sleep(500);
//			}
//			catch (Exception e) {
//			}
//		}
//	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
	
		while(this.game.isRunning()) {
			try {
				paint_random();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
			
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
					StdDraw.setPenRadius(0.020);
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
		StdDraw.pause(250);
		
		StdDraw.show();
		StdDraw.clear();
		
	}
	}

