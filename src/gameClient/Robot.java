package gameClient;
import java.util.List;

import dataStructure.node_data;

public class Robot implements Robots{
	
	  public List<node_data> list_to_go_through;
		
		public Robot(List<node_data> list_to_go_through) {
			this.list_to_go_through = list_to_go_through;
			
		}
		public int getSize() {
			return this.list_to_go_through.size();
		}
		public List<node_data> getNodeList(){
			return this.list_to_go_through;
		}
		public void SetNodeList(List<node_data> temp) {
			this.list_to_go_through = temp;
		}


	}
