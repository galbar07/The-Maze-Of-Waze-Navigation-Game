package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import gameClient.Fruit_c;
import gameClient.Robot;
import utils.Point3D;

/**
 * This class holds the algorithams of which the robot find his best way to navagiate 
 * through the graph 
 * @author Gal Bar Eden Reuveni
 *
 */
public class robot_algo {
	private  game_service game_;
	public  ArrayList<Fruit_c> all_fruits;
	private  ArrayList<Robot>game_robots;
	static int count=0;

	/**
	 * Constructor that compose of list of fruit which is sorted from the most value fruit to the lesser one
	 * and composed of list of robots that are in the game
	 * @param game
	 * @throws JSONException
	 */
	public robot_algo(game_service game) throws JSONException {
		game_=game;
		count++;
		this.all_fruits=get_fruits(game);
		JSONObject line;
		String info = game.toString();
		line = new JSONObject(info);
		JSONObject ttt = line.getJSONObject("GameServer");
		int rs = ttt.getInt("robots");
		count+=rs;
		this.game_robots = getRobots(rs+ count);
	}



	/**
	 * Initialized list of robots to be null by using the robot class
	 * @param robot_size
	 * @return
	 */
	public ArrayList<Robot>getRobots(int robot_size){
		ArrayList<Robot> rob = new ArrayList<Robot>();
		for(int i=0;i<robot_size;i++) {
			List<node_data> n1 = null;
			Robot ri = new Robot(n1);
			rob.add(ri);
		}
		return rob;

	}

	/**
	 * Initialized list of fruits in order of value
	 * @param game
	 * @return
	 */
	private  ArrayList<Fruit_c> get_fruits(game_service game){
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
			}

			Collections.sort(fruits,Comparator.comparing(Fruit_c::getValue));
			Collections.reverse(fruits);
		}
		catch (JSONException e) {e.printStackTrace();}


		return fruits;
	}

	/**
	 * 
	 * This method returns back the path to the fruit I desire 
	 * basically it goes to the most expensive one which is available
	 * and every 5 millisecond he chooses the best node to go to (closest neighbor that
	 * is the closest to a fruit).
	 * @param g
	 * @param src
	 * @return
	 * @throws JSONException 
	 */
	public List <node_data> nextNode(int src, int id,List<node_data>list_to_go_through) throws JSONException {

		DGraph gg = new DGraph();
		gg.init(this.game_.getGraph().toString());
		Graph_Algo gr=new Graph_Algo();
		gr.init(gg);
		long t = this.get_game().timeToEnd();
		JSONObject line = new JSONObject( this.get_game().toString());
		JSONObject ttt = line.getJSONObject("GameServer");
		int game_level = ttt.getInt("game_level");
		int p;
		switch(game_level) {
		case 9:
			p = 4;
			break;
		case 13:
			p=10;
			break;
		case 16:
			p=10;
			break;
		case 20:
			p=12;
			break;
		default:
			p=5;
			break;

		}
		if(t%p!=0) {
			list_to_go_through=gr.shortestPath(src, this.closestNextNode(src));

		}
		else {
			list_to_go_through = gr.shortestPath(src ,this.all_fruits.get(id%this.all_fruits.size()).getSrc()); 
			list_to_go_through.add(gg.getNode(this.all_fruits.get(id%this.all_fruits.size()).getDest()));

		}


		return list_to_go_through;
	}


	/**
	 * Find the best node to go to which can benefit me the most
	 * @param src
	 * @return
	 */
	private int closestNextNode(int src) 
	{
		DGraph gg = new DGraph();
		gg.init(this.game_.getGraph());
		Graph_Algo gr=new Graph_Algo();
		gr.init(gg);
		double smallestPath=Double.POSITIVE_INFINITY;
		int dest=0;int placeOfFruit=0;
		boolean isDest=false;
		for(int i=0;i<this.all_fruits.size();i++) //find the fruit that is closest to the src
		{
			if(this.all_fruits.get(i).getTag()==0&&gr.shortestPathDist(src, this.all_fruits.get(i).getSrc())<smallestPath)
			{ //check if there isn't robot that moves already to that fruit and if its the closest fruit until now
				placeOfFruit=i;
				isDest=true;
				smallestPath=gr.shortestPathDist(src, this.all_fruits.get(i).getSrc());
				if(src==this.all_fruits.get(i).getSrc()) 
				{
					dest=this.all_fruits.get(i).getDest();
				}
				else
					dest=this.all_fruits.get(i).getSrc();
			}
		}
		if(!isDest) //if there isn't a fruit available for this robots
		{
			if(src==this.all_fruits.get(0).getSrc()) 
			{

				dest=this.all_fruits.get(0).getDest();
			}
			else
				dest=this.all_fruits.get(0).getSrc();
		}
		this.all_fruits.get(placeOfFruit).setTag(1);
		gg.getEdge(this.all_fruits.get(placeOfFruit).getDest(), this.all_fruits.get(placeOfFruit).getSrc()).setTag(1);
		List<node_data> bestNode=gr.shortestPath(src, dest);
		return bestNode.get(1).getKey();
	}

	/**
	 * compute the best way to go to the fruit the user wants
	 * @param src
	 * @param dest
	 * @param list_to_go_through
	 * @return
	 */
	public List<node_data> nextNodemanual(int src,int dest,List<node_data>list_to_go_through) {
		DGraph gg = new DGraph();
		gg.init(this.game_.getGraph().toString());
		Graph_Algo gr=new Graph_Algo();
		gr.init(gg);

		list_to_go_through = gr.shortestPath(src, dest);

		return list_to_go_through;
	}

	/**
	 * return this game
	 * @return
	 */
	public game_service get_game() {
		return this.game_;
	}
	/**
	 * Return array list of robots playing the game
	 * @return
	 */
	public ArrayList<Robot> get_inner_robots(){
		return this.game_robots;
	}
	/**
	 * Return array list of fruits in the game
	 * @return
	 */
	public ArrayList<Fruit_c> get_inner_fruit(){
		return this.all_fruits;
	}
	/**
	 * update the fruit list based on the fruit location in current time
	 * @return
	 */
	public ArrayList<Fruit_c> update_inner_fruit(){
		return this.get_fruits(this.game_);
	}


}
