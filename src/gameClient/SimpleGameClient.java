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
		int scenario_num = 5;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String g = game.getGraph();
		OOP_DGraph gg = new OOP_DGraph();
		gg.init(g);
		
		String info = game.toString();
		

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
			ArrayList<OOP_Edge> fruit_location = new ArrayList<OOP_Edge>();
			while(f_iter.hasNext()) {
				JSONObject line2 = new JSONObject(f_iter.next());
				ttt = line2.getJSONObject("Fruit");
				String p[] = ttt.getString("pos").split(",");
				OOP_Point3D p_fruit = new OOP_Point3D(Double.parseDouble(p[0]),Double.parseDouble(p[1]));
				oop_edge_data E = assos(p_fruit,game);
				if(E==null) {
					throw new RuntimeException("The fruit isn't in the graph");
				}
				fruit_location.add(new OOP_Edge(E.getSrc(),E.getDest()));
				
			}	
			int src_node = 0;  // arbitrary node, you should start at one of the fruits
			for(int a = 0;a<rs;a++) {
				game.addRobot(src_node+a);
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		
		game.startGame();
		MyGameGUI paint = new MyGameGUI(game);
		paint.setVisible(true);

		Thread t=new Thread();
		t.start();
		// should be a Thread!!!
		while(game.isRunning()) {
			moveRobots(game, gg);
		}
		String results = game.toString();
		System.out.println("Game Over: "+results);
	}
	private static oop_edge_data assos(OOP_Point3D p_fruit,game_service game) {//associate the fruit to the right edge
		String g = game.getGraph();
		OOP_DGraph gg = new OOP_DGraph();
		gg.init(g);
		
		Collection<oop_node_data> node_list = gg.getV();
		
		for (oop_node_data n : node_list) {
			
			
			Collection<oop_edge_data> edge_list = gg.getE(n.getKey());
			
			for (oop_edge_data e : edge_list) {
				
				OOP_Point3D p_source =  gg.getNode(e.getSrc()).getLocation();
				OOP_Point3D p_dest =  gg.getNode(e.getDest()).getLocation();
				double dist_src_dest=Math.sqrt(Math.pow(p_source.x()-p_dest.x(), 2)+Math.pow(p_source.y()-p_dest.y(), 2));
				double dist_src_p=Math.sqrt(Math.pow(p_source.x()-p_fruit.x(), 2)+Math.pow(p_source.y()-p_fruit.y(), 2));
				double dist_dest_p=Math.sqrt(Math.pow(p_dest.x()-p_fruit.x(), 2)+Math.pow(p_dest.y()-p_fruit.y(), 2));
				double total_dist=dist_src_p+dist_dest_p;
				if (Math.abs(total_dist-dist_src_dest)<= 0.0000001) {
					return e;
				}
				
			}
			
			
		}
		return null;
	}

	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param gg
	 * @param log
	 */
	private static void moveRobots(game_service game, oop_graph gg) {
		List<String> log = game.move();
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
		int ans = -1;
		Collection<oop_edge_data> ee = g.getE(src);
		Iterator<oop_edge_data> itr = ee.iterator();
		int s = ee.size();
		int r = (int)(Math.random()*s);
		int i=0;
		while(i<r) {itr.next();i++;}
		ans = itr.next().getDest();
		return ans;
	}

}
