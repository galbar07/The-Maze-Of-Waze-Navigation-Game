package dataStructure;

import dataStructure.edge_data;

public class EdgeData implements edge_data,java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int src;
	private int dest;
	private double weight;
	private String info;
	private int tag=0;
	
	
/**
 * Constructor that gets the src and dest nodes keys, along with the edge weight
 * and creates it
 * @param src the given src node key
 * @param dest the given dest node key
 * @param weight the given weight for the edge size
 */
	public EdgeData(int src, int dest, double weight) {
		this.src=src;
		this.dest=dest;
		if (weight<0)
			throw new RuntimeException("The weight must be positive");
		else
			this.weight=weight;
		this.info="";
	}
	
	/**
	 * The id of the source node of this edge.
	 * @return the id of this
	 */
	@Override
	public int getSrc() {
		return this.src;
	}
	/**
	 * The id of the destination node of this edge
	 * @return the id of this
	 */
	@Override
	public int getDest() {
		return this.dest;
	}
	/**
	 * @return the weight of this edge (positive value).
	 */
	@Override
	public double getWeight() {
		return this.weight;
	}
	/**
	 * Return the remark (meta data) associated with this edge.
	 * @return the info of this
	 */
	@Override
	public String getInfo() {
		return this.info;
	}
	/**
	 * Allows changing the remark (meta data) associated with this edge.
	 * @param s the gives String to put into info
	 */
	@Override
	public void setInfo(String s) {
		this.info=s;
	}
	/**
	 * Temporal data (aka color: e,g, white, gray, black) 
	 * which can be used be algorithms 
	 * @return the tag of this
	 */

	@Override
	public int getTag() {
		return this.tag;
	}
	/** 
	 * Allow setting the "tag" value for temporal marking an edge - common 
	 * practice for marking by algorithms.
	 * @param t - the new value of the tag
	 */
	@Override
	public void setTag(int t) {
		this.tag=t;
	}

}
