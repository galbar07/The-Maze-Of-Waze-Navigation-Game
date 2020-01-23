package gameClient;
import java.io.IOException;
import java.util.ArrayList;
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
import oop_dataStructure.OOP_DGraph;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_graph;
import oop_utils.OOP_Point3D;

public class SimpleGameClient {
	
	/**
	 * By using simple game client we activate the game
	 * @param a
	 * @throws JSONException
	 * @throws IOException
	 */
	public static void main(String[] a) throws JSONException, IOException {

		MyGameGUI paint = new MyGameGUI();

	}
	
}