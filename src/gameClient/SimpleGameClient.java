package gameClient;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;

public class SimpleGameClient {
	
	public static void main(String[] a) throws JSONException {
		test1();
	}
	
	public static void test1() throws JSONException {
		int scenario_num = 23;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		Fruit_c fruit = new Fruit_c();
		Robot_c robot = new Robot_c(game);
		String g = game.getGraph();
		System.out.println(g);
		
//		DGraph gg = new DGraph();
//		gg.init(g);
//		robot.place_robots(game,gg);//place the robots
		
		
		//String info = game.toString()
		MyGameGUI paint = new MyGameGUI(game);

	game.startGame();

		paint.setVisible(true);
		
//		game.startGame();


	
		// should be a Thread!!!
//		while(game.isRunning()) {
//
//			robot.moveRobots(game, gg);
//		}
//		String results = game.toString();
//		System.out.println("Game Over: "+results);
	}

	

}