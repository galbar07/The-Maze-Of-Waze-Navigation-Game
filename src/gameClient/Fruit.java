package gameClient;

import Server.game_service;
import dataStructure.edge_data;
import utils.Point3D;

public interface Fruit {

	/**
	 * @return this fruit value
	 */
	public double getValue() ;
	
	/**
	 * @return this fruit source
	 */
	public int getSrc() ;
	
	/**
	 * @return this fruit dest
	 */
	public int getDest() ;
	 
	/**
	 * @return this fruit type
	 */
	public int getType() ;
	/**
	 * @return this fruit tag
	 */
	public int getTag() ;	
	
	/**
	 * @set this fruit tag
	 */
	public void setTag(int tag) ;
	
	/**
	 * Associate a fruit with its right edge
	 * @param p_fruit
	 * @param game
	 * @param type
	 * @return its edge, null if there is no edge for this fruit
	 */
	public edge_data assos(Point3D p_fruit,game_service game, int type) ;

	 /**
	  * Comparator for fruits
	  * @param f
	  * @return
	  */
	public int compareTo(Fruit_c f) ;

	
	
}
