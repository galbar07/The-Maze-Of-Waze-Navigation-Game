package gameClient;

import java.util.Collection;

import Server.game_service;
import dataStructure.node_data;
import oop_dataStructure.OOP_DGraph;
import java.util.Comparator;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_node_data;
import oop_elements.OOP_Edge;
import oop_utils.OOP_Point3D;

public class Fruit_c {
	private int type;
	private double value;
	private int src;
	private int dest;
	private int tag;
	
	/**
	 * constructor for a new fruit
	 * @param value the given fruit value
	 * @param type the given fruit type
	 * @param src the given fruit src node of the edge which it's on
	 * @param dest the given fruit dest node of the edge which it's on
	 */
	public Fruit_c(double value,int type,int src,int dest) {
		this.value =value;
		this.type = type;
		this.src = src;
		this.dest = dest;
		this.tag=0;
	}
	/**
	 * default constructor
	 */
	public Fruit_c() {
		
	}
	
	/**
	 * 
	 * @return this fruit value
	 */
	public double getValue() {
		
		return this.value;
	}
	
	/**
	 * 
	 * @return this fruit src node of the edge which it's on
	 */
	public int getSrc() {
		
		return this.src;
	}
	/**
	 * 
	 * @return this fruit dest node of the edge which it's on
	 */
	public int getDest() {
		
		return this.dest;
	}
	/**
	 * 
	 * @return this fruit type
	 */
	public int getType() {
		
		return this.type;
	}
	
public int getTag() {
		
		return this.tag;
	}
	
	public void setTag(int tag) {
		this.tag=tag;
	}
	

	/**
	 * Associate a fruit with its right edge
	 * @param p_fruit
	 * @param game
	 * @param type
	 * @return its edge, null if there is no edge for this fruit
	 */
	public oop_edge_data assos(OOP_Point3D p_fruit,game_service game, int type) {
		String g = game.getGraph();
		OOP_DGraph gg = new OOP_DGraph();
		gg.init(g);
		
		Collection<oop_node_data> node_list = gg.getV();
		
		for (oop_node_data n : node_list) {
			//for every node in the graph
			
			Collection<oop_edge_data> edge_list = gg.getE(n.getKey());
			
			for (oop_edge_data e : edge_list) {
				//for every edge in the hash map with the src node n
				OOP_Point3D p_source =  gg.getNode(e.getSrc()).getLocation();
				OOP_Point3D p_dest =  gg.getNode(e.getDest()).getLocation();
				double dist_src_dest=Math.sqrt(Math.pow(p_source.x()-p_dest.x(), 2)+Math.pow(p_source.y()-p_dest.y(), 2));
				double dist_src_p=Math.sqrt(Math.pow(p_source.x()-p_fruit.x(), 2)+Math.pow(p_source.y()-p_fruit.y(), 2));
				double dist_dest_p=Math.sqrt(Math.pow(p_dest.x()-p_fruit.x(), 2)+Math.pow(p_dest.y()-p_fruit.y(), 2));
				double total_dist=dist_src_p+dist_dest_p;
				if (Math.abs(total_dist-dist_src_dest) <= 0.0000001) {
					if (type==-1)
						//if it's a banana
						return new OOP_Edge(e.getDest(), e.getSrc());
					else //if it's an apple
						return e;
					
				}
				
			}
			
			
		}
		return null;
	}
	
	public int compareTo(Fruit_c f) {
		if (this.getValue()>f.getValue())
			return 1;
		else if (this.getValue()<f.getValue())
			return -1;
		else
			return 0;
	}

}
