package gameClient;

import java.util.List;

import dataStructure.node_data;

/**
 * This interface represent the data structure of robots that compose of an ArrayList
 * of Node to them to eat 
 * @author Gal bar
 *
 */

public interface Robots {
	
	/**
	 * Return the amont of nodes there is in the List
	 * @return
	 */
	public int getSize();
	
	/**
	 * Return list of nodes
	 * @return
	 */
	public List<node_data> getNodeList();
	
	/**
	 * Let the user set the List 
	 * @param temp
	 */
	public void SetNodeList(List<node_data> temp);
	
}
