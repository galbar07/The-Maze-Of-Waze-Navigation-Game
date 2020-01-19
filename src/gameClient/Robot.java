package gameClient;
import java.util.List;

import dataStructure.node_data;

/**
 * This class represent robot in the game
 * this robot has list of nodes to go to
 * @author Gal bar Eden Reuvani
 *
 */
public class Robot implements Robots{
	
	  public List<node_data> list_to_go_through;
		
		public Robot(List<node_data> list_to_go_through) {
			this.list_to_go_through = list_to_go_through;
			
		}
		/**
		 * return the size of the list of nodes the robot has
		 */
		public int getSize() {
			return this.list_to_go_through.size();
		}
		/**
		 * return the list of nodes itself
		 */
		public List<node_data> getNodeList(){
			return this.list_to_go_through;
		}
		/**
		 * let the game make changes in the list of nodes to go to
		 */
		public void SetNodeList(List<node_data> temp) {
			this.list_to_go_through = temp;
		}


	}
