package dataStructure;

import dataStructure.node_data;
import utils.Point3D;

public class NodeData implements node_data, Comparable<node_data>,java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int counter=1;
	private int key;
	private Point3D location=null;
	private double weight=Double.MAX_VALUE;
	private String info;
	private int tag=0;
	private int id;
	
	/**
	 * Constructor to make a node from a given point (of type Poind3D)
	 * @param p the given point
	 */
	public NodeData(Point3D p) { 
		this.key=counter++;
		double x=p.x();
		double y=p.y();
		double z=p.z();
		this.location=new Point3D(x,y,z);
		this.weight=Double.MAX_VALUE;
		this.info=""; 
		this.tag=0;	
	}
	
	 public NodeData(Point3D p,int id) {
		double x=p.x();
		double y=p.y();
		double z=p.z();
		this.location=new Point3D(x,y,z);
		this.key = id;
		
	}
	
	
	/**
	 * Return the key (id) associated with this node.
	 * @return the key of this
	 */
	@Override
	public int getKey() {
		return this.key;
	}
	/** Return the location (of applicable) of this node, if
	 * none return null.
	 * 
	 * @return the location of this
	 */

	@Override
	public Point3D getLocation() {
		return this.location;
	}
	/** Allows changing this node's location.
	 * 
	 * @param p - new new location  (position) of this node.
	 */
	@Override
	public void setLocation(Point3D p) {
		this.location=p;		
	}
	/**
	 * Return the weight associated with this node.
	 * @return this weight
	 */
	@Override
	public double getWeight() {
		return this.weight;
	}
	/**
	 * Allows changing this node's weight.
	 * @param w - the new weight
	 */
	@Override
	public void setWeight(double w) {
		this.weight=w;
	}
	/**
	 * return the remark (meta data) associated with this node.
	 * @return this info
	 */
	@Override
	public String getInfo() {
		return this.info;
	}
	/**
	 * Allows changing the remark (meta data) associated with this node.
	 * @param s is the given String to put into info
	 */

	@Override
	public void setInfo(String s) {
		this.info=s;
	}
	/**
	 * Temporal data (aka color: e,g, white, gray, black) 
	 * which can be used be algorithms 
	 * @return this tag
	 */
	@Override
	public int getTag() {
		return this.tag;
	}
	/** 
	 * Allow setting the "tag" value for temporal marking an node - common 
	 * practice for marking by algorithms.
	 * @param t - the new value of the tag
	 */
	@Override
	public void setTag(int t) {
		this.tag=t;
	}

/**
 * Comparator for nodes weights
 */
	@Override
	public int compareTo(node_data n) {
		if (this.getWeight()>n.getWeight())
			return 1;
		else if (this.getWeight()<n.getWeight())
			return -1;
		else
			return 0;
	}
	

}
