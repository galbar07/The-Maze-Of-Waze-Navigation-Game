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
import algorithms.Graph_Algo;
import dataStructure.node_data;
import oop_dataStructure.OOP_DGraph;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_graph;
import oop_dataStructure.oop_node_data;
import oop_elements.OOP_Edge;
import oop_utils.OOP_Point3D;

public class Robot_c implements Comparable<Fruit_c> {
	private game_service game_;
	private ArrayList<Fruit_c> all_fruits;
	private ArrayList<Fruit_c> fruits_available;
	private ArrayList<Robot_c>game_robots;
	private static List<node_data> list_to_go_through;
	//????
	
	
	
	public Robot_c(game_service game) {
		this.game_=game;
		this.all_fruits=get_fruits(game);
		this.fruits_available=get_fruits(game);
		this.list_to_go_through=null;
		for(int i=0;i<game.getRobots().size();i++) {
			
		}
		
	}

	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen.
	 * @param game the given game from the server
	 * @param gg the graph made out of the game
	 * @param log a list of all the moves
	 */
	public void moveRobots(game_service game, oop_graph gg) {
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

					if(dest==-1) {	
						dest = nextNode(game,gg, src,rid,get_fruits(game));
						game.chooseNextEdge(rid, dest);
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println(ttt);
					}
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
				OOP_Point3D p_fruit = new OOP_Point3D(Double.parseDouble(p[0]),Double.parseDouble(p[1]));
				oop_edge_data E = temp.assos(p_fruit,game,type);
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
	private static int nextNode(game_service game, oop_graph g, int src, int id, ArrayList<Fruit_c>fruits) {
		
		if (id<0)
			//if there is no path to any of the fruits
			throw new RuntimeException("There is no path to any fruit");
		double most_expensive_fruit=fruits.get(id).getValue()%fruits.size();
		//can't remember where and if we use it
		String graph = game.getGraph();
		OOP_DGraph gg = new OOP_DGraph();
		Graph_Algo gr=new Graph_Algo();
		gg.init(graph);
		gr.init(graph);
		int type=fruits.get(id).getType();
		if (type==-1) {
			//if it's a banana- find the shortestPath from your current position to its src node
			list_to_go_through=gr.shortestPath(src ,fruits.get(id).getSrc()); 
			//and from it to its dest node
			list_to_go_through=gr.shortestPath(list_to_go_through.get(0).getKey(), fruits.get(id).getDest());
		}
		else {
			//if it's an apple- find the shortestPath from your current position to its dest node
			list_to_go_through=gr.shortestPath(src ,fruits.get(id).getDest()); 
			//and from it to its src node
			list_to_go_through=gr.shortestPath(list_to_go_through.get(0).getKey(), fruits.get(id).getSrc());
			
		}
		if (list_to_go_through==null) {
			//if there is no path, return recursively with the first fruit 
			//which no robot is aimed to
			int size_of_list=list_to_go_through.size();
			nextNode(game, gg, src, size_of_list--, fruits);
		}
		
		return list_to_go_through.get(0).getKey();
		//??????
	}

	/**
	 * this function places the first robot on the src node of the edge
	 * which has the fruit with the biggest value, the second robot on the src
	 * node of the edge which has the fruit with the second max value etc.
	 * @param game the given game from the server
	 * @param gg the graph made out of the game
	 */


	public void place_robots(game_service game, oop_graph gg) {
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
				OOP_Point3D p_fruit = new OOP_Point3D(Double.parseDouble(p[0]),Double.parseDouble(p[1]));
				oop_edge_data E = temp.assos(p_fruit,game,type);
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

	@Override
	public int compareTo(Fruit_c o) {
		
		return 0;
	}
}
