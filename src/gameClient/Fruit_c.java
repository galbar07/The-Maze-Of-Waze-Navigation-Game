package gameClient;

import java.util.Collection;

import Server.game_service;
import oop_dataStructure.OOP_DGraph;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_node_data;
import oop_elements.OOP_Edge;
import oop_utils.OOP_Point3D;

public class Fruit_c {
	private int type;
	private double value;
	private int src;
	private int dest;
	

	public Fruit_c(double value,int type,int src,int dest) {
		this.value =value;
		this.type = type;
		this.src = src;
		this.dest = dest;
	}
	
	public Fruit_c() {
		
	}
	
	
	public double getValue() {
		
		return this.value;
	}
	
	
	public int getSrc() {
		
		return this.src;
	}
	
	public int getDest() {
		
		return this.dest;
	}
	
	public int getType() {
		
		return this.type;
	}

	/**
	 * Associate fruit to edge
	 * @param p_fruit
	 * @param game
	 * @param type
	 * @return
	 */
	public  oop_edge_data assos(OOP_Point3D p_fruit,game_service game, int type) {//associate the fruit to the right edge
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
				if (Math.abs(total_dist-dist_src_dest) <= 0.0000001) {
					if (type==-1)
						return new OOP_Edge(e.getDest(), e.getSrc());
					else
						return e;
					
				}
				
			}
			
			
		}
		return null;
	}

}
