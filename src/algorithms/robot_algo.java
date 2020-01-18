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

public class robot_algo {
	private  game_service game_;
	public  ArrayList<Fruit_c> all_fruits;
	private  ArrayList<Robot>game_robots;
	static int count=0;

	public robot_algo(game_service game) throws JSONException {
		game_=game;
		count++;
		this.all_fruits=get_fruits(game);
		JSONObject line;
		String info = game.toString();
		line = new JSONObject(info);
		JSONObject ttt = line.getJSONObject("GameServer");
		int rs = ttt.getInt("robots");
		this.game_robots = getRobots(rs+count++);
		}
	


	public ArrayList<Robot>getRobots(int robot_size){
		ArrayList<Robot> rob = new ArrayList<Robot>();
		for(int i=0;i<robot_size;i++) {
			List<node_data> n1 = null;
			Robot ri = new Robot(n1);
			rob.add(ri);
		}
		return rob;

	}

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
	 * a very simple random walk implementation!
	 * @param g
	 * @param src
	 * @return
	 */
	public List <node_data> nextNode(int src, int id,List<node_data>list_to_go_through) {
		
		DGraph gg = new DGraph();
		gg.init(this.game_.getGraph().toString());
		Graph_Algo gr=new Graph_Algo();
		gr.init(gg);
		long t = this.get_game().timeToEnd();
		
		

		if(t%5!=0) {
			list_to_go_through=gr.shortestPath(src, this.bestNode(src));

		//list_to_go_through = gr.shortestPath(src ,this.all_fruits.get(id%this.all_fruits.size()).getSrc()); 
			//and from it to its dest node
			//if(list_to_go_through==null) { return null;}
			//	list_to_go_through.add(gg.getNode(this.all_fruits.get(id%this.all_fruits.size()).getDest()));

		}
		else {
			list_to_go_through = gr.shortestPath(src ,this.all_fruits.get(id%this.all_fruits.size()).getSrc()); 
			list_to_go_through.add(gg.getNode(this.all_fruits.get(id%this.all_fruits.size()).getDest()));

		}
		//if (gr.shortestPathDist(src ,this.all_fruits.get(id%this.all_fruits.size()).getSrc())>t)
			//list_to_go_through=gr.shortestPath(src, this.bestNode(src));
		//	list_to_go_through=gr.shortestPath(src, this.bestNode(src));
			//	list_to_go_through=gr.shortestPath(src, this.bestNode(src));
			//if (list_to_go_through.size()==1) {
				//list_to_go_through = gr.shortestPath(src ,this.all_fruits.get(id%this.all_fruits.size()).getSrc()); 
				//list_to_go_through.add(gg.getNode(this.all_fruits.get(id%this.all_fruits.size()).getDest()));

			

		return list_to_go_through;
	}
	
	private int bestNode(int src) 
	{
		DGraph gg = new DGraph();
		gg.init(this.game_.getGraph());
		Graph_Algo gr=new Graph_Algo();
		gr.init(gg);
		double minpath=Double.POSITIVE_INFINITY;
		int dest=0;int placeFruit=0;
		boolean isGetDest=false;
		for(int i=0;i<this.all_fruits.size();i++) //find the fruit that is closest to the src
		{
			if(this.all_fruits.get(i).getTag()==0&&gr.shortestPathDist(src, this.all_fruits.get(i).getSrc())<minpath)
			{ //check if there isn't robot that moves already to that fruit and if its the closest fruit until now
					placeFruit=i;
				 	isGetDest=true;
					minpath=gr.shortestPathDist(src, this.all_fruits.get(i).getSrc());
					if(src==this.all_fruits.get(i).getSrc()) 
					{
						dest=this.all_fruits.get(i).getDest();
					}
					else
						dest=this.all_fruits.get(i).getSrc();
			}
		}
		if(!isGetDest) //if there isnt a fruit available for this robots
		{
			if(src==this.all_fruits.get(0).getSrc()) 
			{
			
				dest=this.all_fruits.get(0).getDest();
			}
			else
				dest=this.all_fruits.get(0).getSrc();
		}
		this.all_fruits.get(placeFruit).setTag(1);
		gg.getEdge(this.all_fruits.get(placeFruit).getDest(), this.all_fruits.get(placeFruit).getSrc()).setTag(1);
		List<node_data> node=gr.shortestPath(src, dest);
		return node.get(1).getKey();
	}
	

	public List<node_data> nextNodemanual(int src,int dest,List<node_data>list_to_go_through) {
		DGraph gg = new DGraph();
		gg.init(this.game_.getGraph().toString());
		Graph_Algo gr=new Graph_Algo();
		gr.init(gg);
		
		list_to_go_through = gr.shortestPath(src, dest);
		
		return list_to_go_through;
	}
	
	public game_service get_game() {
		return this.game_;
	}
	
	public ArrayList<Robot> get_inner_robots(){
		return this.game_robots;
	}
	public ArrayList<Fruit_c> get_inner_fruit(){
		return this.all_fruits;
	}
	public ArrayList<Fruit_c> update_inner_fruit(){
		return this.get_fruits(this.game_);
	}
		

}
