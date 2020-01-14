package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.game_service;
import Server.robot;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;

public class Robot_c {
	private static game_service game_;
	private static ArrayList<Fruit_c> all_fruits;
	private static ArrayList<robot_inner>game_robots;
	
	
	
	public Robot_c(game_service game) {
		Robot_c.game_=game;
		Robot_c.all_fruits=get_fruits(game);
		JSONObject line;
		String info = game.toString();
		try {
			
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			int rs = ttt.getInt("robots");
			Robot_c.game_robots = getRobots(game, rs);
				
			
		}
			catch (JSONException e) {e.printStackTrace();}
	
		
	}
	
	
	public ArrayList<robot_inner>getRobots(game_service game,int robot_size){
		ArrayList<robot_inner> rob = new ArrayList<robot_inner>();
		for(int i=0;i<robot_size;i++) {
			List<node_data> n1 = null;
			robot_inner ri = new robot_inner(n1);
			rob.add(ri);
		}
		return rob;
		
	}
	

	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen.
	 * @param game the given game from the server
	 * @param gg the graph made out of the game
	 * @param log a list of all the moves
	 */
	public void moveRobots(game_service game, graph gg) {
		List<String> log = game.move();
		//System.out.println( game.move());
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
					
					//  id = 0
					//|  null   |
					if(Robot_c.game_robots.get(rid).list_to_go_through==null  ||(dest ==-1 && Robot_c.game_robots.get(rid).list_to_go_through.size()==1)) {
						Robot_c.game_robots.get(rid).list_to_go_through = nextNode(src,rid,Robot_c.game_robots.get(rid).list_to_go_through);
					}
					if(Robot_c.game_robots.get(rid).list_to_go_through!=null && Robot_c.game_robots.get(rid).list_to_go_through.size()>1) {
					// 3->2
						Robot_c.game_.chooseNextEdge(rid,Robot_c.game_robots.get(rid).list_to_go_through.get(1).getKey());
						Robot_c.game_robots.get(rid).list_to_go_through.remove(1);
						//System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						//System.out.println(ttt);
					}
						Robot_c.all_fruits=get_fruits(game);//Fruit update
//						if (Robot_c.game_robots.get(rid).list_to_go_through.contains(gg.getNode(src))&&Robot_c.game_robots.get(rid).list_to_go_through.size()==1)
//							dest=-1;
					
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}
	
	private static ArrayList<Fruit_c> get_fruits(game_service game){
		//String g = game.getGraph();	
		Fruit_c temp = new Fruit_c();
		ArrayList<Fruit_c>fruits = new ArrayList<>();
		//this array list will hold all the fruits
		JSONObject line;
		String info = game.toString();

		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			Iterator<String> f_iter = game.getFruits().iterator();
			//goes through all the fruits
			while(f_iter.hasNext()) {
				JSONObject line2 = new JSONObject(f_iter.next());
				ttt = line2.getJSONObject("Fruit");
				int type=ttt.getInt("type");
				double value=ttt.getDouble("value");
				String p[] = ttt.getString("pos").split(",");
				Point3D p_fruit = new Point3D(Double.parseDouble(p[0]),Double.parseDouble(p[1]));
				edge_data E = temp.assos(p_fruit,game,type);
				//each fruit will be associated to its edge
				fruits.add(new Fruit_c(value,type,E.getSrc(),E.getDest()));
			//	shortestPath(E.getSrc(),E.getDest());

			}
			
			Collections.sort(fruits,Comparator.comparing(Fruit_c::getValue));
			Collections.reverse(fruits);
}
		catch (JSONException e) {e.printStackTrace();}


		return fruits;
	}




	/**
	 * a very simple random walk implementation!
	 * @param g
	 * @param src
	 * @return
	 */
	private static List <node_data> nextNode(int src, int id,List<node_data>list_to_go_through) {
		
		if (id<0)
			//if there is no path to any of the fruits
			throw new RuntimeException("There is no path to any fruit");
		DGraph gg = new DGraph();
		gg.init(Robot_c.game_.getGraph());
		Graph_Algo gr=new Graph_Algo();
		gr.init(gg);
		
		
		
		
		int type=Robot_c.all_fruits.get(id%Robot_c.all_fruits.size()).getType();
		//System.out.println(type);
		if (type==-1) {
			//if it's a banana- find the shortestPath from your current position to its src node
			
			list_to_go_through=gr.shortestPath(src ,Robot_c.all_fruits.get(id%Robot_c.all_fruits.size()).getSrc()); 
			//and from it to its dest node
			if(list_to_go_through==null) { return null;
			//	int new_id=Robot_c.game_robots.size()-1;
				//return nextNode(src, new_id, list_to_go_through);
			}
			list_to_go_through.add(gg.getNode(Robot_c.all_fruits.get(id%Robot_c.all_fruits.size()).getDest()));
			
					//gr.shortestPath(list_to_go_through.get(0).getKey(), Robot_c.all_fruits.get(id%Robot_c.all_fruits.size()).getDest());
		}
		else {
			//if it's an apple- find the shortestPath from your current position to its dest node
		
			
			list_to_go_through=gr.shortestPath(src ,Robot_c.all_fruits.get(id%Robot_c.all_fruits.size()).getDest()); 
			//and from it to its src node
			if(list_to_go_through.size()<=0) { return null;
//				int new_id=Robot_c.game_robots.size()-1;
//				return nextNode(src, new_id, list_to_go_through);
				}
			list_to_go_through.add(gg.getNode(Robot_c.all_fruits.get(id%Robot_c.all_fruits.size()).getSrc()));
			
		}

		for(int i=0;i<list_to_go_through.size();i++) {
			System.out.print("targets are \t " + list_to_go_through.get(i).getKey());
		}
		return list_to_go_through;
	}

	/**
	 * this function places the first robot on the src node of the edge
	 * which has the fruit with the biggest value, the second robot on the src
	 * node of the edge which has the fruit with the second max value etc.
	 * @param game the given game from the server
	 * @param gg the graph made out of the game
	 */


	public void place_robots(game_service game, graph gg) {
		//String g = game.getGraph();	
		Fruit_c temp = new Fruit_c();
		ArrayList<Fruit_c> fruits = new ArrayList<Fruit_c>();
		//this array list will hold all the fruits
		JSONObject line;
		String info = game.toString();
		int num_robots=0;

		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			num_robots = ttt.getInt("robots");


			Iterator<String> f_iter = game.getFruits().iterator();
			//goes through all the fruits
			while(f_iter.hasNext()) {
				JSONObject line2 = new JSONObject(f_iter.next());
				ttt = line2.getJSONObject("Fruit");
				int type=ttt.getInt("type");
				double value=ttt.getDouble("value");
				String p[] = ttt.getString("pos").split(",");
				Point3D p_fruit = new Point3D(Double.parseDouble(p[0]),Double.parseDouble(p[1]));
				edge_data E = temp.assos(p_fruit,game,type);
				//each fruit will be associated to its edge
				fruits.add(new Fruit_c(value,type,E.getSrc(),E.getDest()));

			}
		}
		catch (JSONException e) {e.printStackTrace();}
		double max_value = Double.MIN_VALUE;
		int rm=0;

		for(int i=0;i<num_robots;i++) {
			//there is no need to run for more then the number of robots
			for (int j = 0; j < fruits.size(); j++) {
				//for every fruit in the list 
				if(max_value<fruits.get(j).getValue()) {
					max_value = fruits.get(j).getValue();
					//holds the max value
					rm = j;
				}

			}
			game.addRobot(fruits.get(rm).getSrc());
			//add the robot in the index you found

			max_value = Double.MIN_VALUE;
			fruits.remove(rm);
			//remove that fruit from the list so the second robot will be placed
			//in the second max value and so on
			rm=0;

		}


	}
	


}
//constructor for inner help
	class robot_inner{
	  List<node_data> list_to_go_through;
		
		public robot_inner(List<node_data> list_to_go_through) {
			this.list_to_go_through = list_to_go_through;
			
		}
		public int getSize() {
			return this.list_to_go_through.size();
		}


	}
