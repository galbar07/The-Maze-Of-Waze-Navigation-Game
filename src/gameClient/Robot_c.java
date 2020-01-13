package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.game_service;
import oop_dataStructure.OOP_DGraph;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_graph;
import oop_elements.OOP_Edge;
import oop_utils.OOP_Point3D;

public class Robot_c {
	
	
	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param gg
	 * @param log
	 */
	public void moveRobots(game_service game, oop_graph gg) {
		List<String> log = game.move();
		System.out.println("hi ccxvxcvxc" + game.move());
		if(log!=null) {
			long t = game.timeToEnd();
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
				
					if(dest==-1) {	
						dest = nextNode(gg, src);
						game.chooseNextEdge(rid, dest);
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println(ttt);
					}
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}
	
	
	
	
	/**
	 * a very simple random walk implementation!
	 * @param g
	 * @param src
	 * @return
	 */
	private static int nextNode(oop_graph g, int src) {
		
		
		
		
		
		
		
		
		return 0;
	}




	public void place_robots(game_service game, oop_graph gg) {
		String g = game.getGraph();	
		Fruit_c temp = new Fruit_c();
		ArrayList<Fruit_c> fruits = new ArrayList<Fruit_c>();
		
		JSONObject line;
		String info = game.toString();
		int num_robots=0;
		
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			 num_robots = ttt.getInt("robots");
		
		
		Iterator<String> f_iter = game.getFruits().iterator();
		while(f_iter.hasNext()) {
			JSONObject line2 = new JSONObject(f_iter.next());
			ttt = line2.getJSONObject("Fruit");
			int type=ttt.getInt("type");
			double value=ttt.getDouble("value");
			String p[] = ttt.getString("pos").split(",");
			OOP_Point3D p_fruit = new OOP_Point3D(Double.parseDouble(p[0]),Double.parseDouble(p[1]));
			oop_edge_data E = temp.assos(p_fruit,game,type);//Maybe add value for 2 fruits on the same edge
			fruits.add(new Fruit_c(value,type,E.getSrc(),E.getDest()));
			
		}
		}
		catch (JSONException e) {e.printStackTrace();}
		
		
		double max_value = Double.MIN_VALUE;
		int rm=0;
		
		for(int i=0;i<num_robots;i++) {
			
			for (int j = 0; j < fruits.size(); j++) {
				
				if(max_value<fruits.get(j).getValue()) {
					max_value = fruits.get(j).getValue();
					rm = j;
				}
				
			}
			
			if(fruits.get(rm).getType()==1)
				game.addRobot(fruits.get(rm).getSrc());
			else
				game.addRobot(fruits.get(rm).getSrc());
		
			max_value = Double.MIN_VALUE;
			fruits.remove(rm);
			rm=0;
			
		}
		
		
		
		
		
	}
	

}
