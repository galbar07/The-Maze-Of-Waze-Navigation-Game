package gameClient;
import java.io.IOException;
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
	
	/**
	 * By using simple game client we activate the game
	 * @param a
	 * @throws JSONException
	 * @throws IOException
	 */
	public static void main(String[] a) throws JSONException, IOException {
		
		test1();
	}
	
	public static void test1() throws JSONException, IOException {

		MyGameGUI paint = new MyGameGUI();
		
	}

	

}