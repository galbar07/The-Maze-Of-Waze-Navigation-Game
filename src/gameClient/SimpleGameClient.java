package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Fruit;
import Server.Game_Server;
import Server.game_service;
import oop_dataStructure.OOP_DGraph;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_graph;
import oop_dataStructure.oop_node_data;
import oop_elements.OOP_Edge;
import oop_elements.OOP_NodeData;
import oop_utils.OOP_Point3D;
import utils.StdDraw;

import com.google.gson.Gson;

/**
 * This class represents a simple example for using the GameServer API:
 * the main file performs the following tasks:
 * 1. Creates a game_service [0,23] (line 36)
 * 2. Constructs the graph from JSON String (lines 37-39)
 * 3. Gets the scenario JSON String (lines 40-41)
 * 4. Prints the fruits data (lines 49-50)
 * 5. Add a set of robots (line 52-53) // note: in general a list of robots should be added
 * 6. Starts game (line 57)
 * 7. Main loop (should be a thread) (lines 59-60)
 * 8. move the robot along the current edge (line 74)
 * 9. direct to the next edge (if on a node) (line 87-88)
 * 10. prints the game results (after "game over"): (line 63)
 *  
 * @author boaz.benmoshe
 *
 */
public class SimpleGameClient {
	
	public static void main(String[] a) throws JSONException {
		test1();
}
	
	public static void test1() throws JSONException {
		int scenario_num = 11;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String g = game.getGraph();
		OOP_DGraph gg = new OOP_DGraph();
		gg.init(g);
		Fruit_c fruit = new Fruit_c();
		Robot_c robot = new Robot_c(game);
		
		String info = game.toString();
		print_info(info,game);
		robot.place_robots(game,gg);//place the robots
		game.startGame();
		
		MyGameGUI paint = new MyGameGUI(game);
		//paint.setVisible(true);

		
		// should be a Thread!!!
		while(game.isRunning()) {
			
			robot.moveRobots(game, gg);
		}
		String results = game.toString();
		System.out.println("Game Over: "+results);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	private static void print_info(String info,game_service game) {
		Fruit_c fruit = new Fruit_c();
		Robot_c robot = new Robot_c(game);
		String g = game.getGraph();
		OOP_DGraph gg = new OOP_DGraph();
		gg.init(g);
		JSONObject line;
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			int rs = ttt.getInt("robots");
			System.out.println(info);
			System.out.println(g);
			Iterator<String> fruit_iter = game.getFruits().iterator();
			while(fruit_iter.hasNext()) {
				System.out.println(fruit_iter.next());
			}
			// the list of fruits should be considered in your solution
			Iterator<String> f_iter = game.getFruits().iterator();
			ArrayList<oop_edge_data> fruit_location = new ArrayList<oop_edge_data>();
			while(f_iter.hasNext()) {
				JSONObject line2 = new JSONObject(f_iter.next());
				ttt = line2.getJSONObject("Fruit");
				int type=ttt.getInt("type");
				double value=ttt.getInt("value");
				String p[] = ttt.getString("pos").split(",");
				OOP_Point3D p_fruit = new OOP_Point3D(Double.parseDouble(p[0]),Double.parseDouble(p[1]));
				oop_edge_data E = fruit.assos(p_fruit,game,type);
				if(E==null) {
					throw new RuntimeException("The fruit isn't in the graph");
				}
				fruit_location.add(new OOP_Edge(E.getSrc(),E.getDest()));
				
			}	
			
		}
		catch (JSONException e) {e.printStackTrace();}
		
	}


	


}
