package gameClient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;

import algorithms.robot_algo;
import dataStructure.edge_data;
import utils.Point3D;

public class game_manager {

	private robot_algo robot;
	
	
	public game_manager(robot_algo robot) {
		this.robot = robot;
		
	}
	
	//locate the robots in manual mode by the user decision
	public void locate_robots_manual() throws JSONException{
		String info = this.robot.get_game().toString();
		JSONObject line;
			
	    line = new JSONObject(info);
		JSONObject ttt = line.getJSONObject("GameServer");
		System.out.println(info);
		int robot_size = ttt.getInt("robots");		
		System.out.println("robot size is " + robot_size);
		for(int i=0;i<robot_size;i++) {
			 String inputString = JOptionPane.showInputDialog(null, "Enter location for the robot " + i);
			 int input = Integer.parseInt(inputString);
			 this.robot.get_game().addRobot(input);
		}
	}
	//Moves the robots in manual mode 
		public void move_robots_manual() throws JSONException {
			List<String> log = this.robot.get_game().move();
			if(log!=null) {
				for(int i=0;i<log.size();i++) {
					String robot_json = log.get(i);
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
					
						
					if(this.robot.get_inner_robots().get(rid).getNodeList() == null || (dest == -1 && this.robot.get_inner_robots().get(rid).getSize()==1)) {
						String inputString = JOptionPane.showInputDialog(null, "Enter next node for robot" + rid);
				        int destantaion = Integer.parseInt(inputString);
						this.robot.get_inner_robots().get(rid).SetNodeList(this.robot.nextNodemanual(src, destantaion,this.robot.get_inner_robots().get(rid).getNodeList()));
					}
					if(dest==-1 && this.robot.get_inner_robots().get(rid).getNodeList()!=null && this.robot.get_inner_robots().get(rid).getNodeList().size()>1) {
						this.robot.get_game().chooseNextEdge(rid, this.robot.get_inner_robots().get(rid).getNodeList().get(1).getKey());
						this.robot.get_inner_robots().get(rid).list_to_go_through.remove(1);	
					}
					
							
				}
				
			}
		}
		

		/** 
		 * Moves each of the robots along the edge, 
		 * in case the robot is on a node the next destination (next edge) is chosen.
		 * @param game the given game from the server
		 * @param gg the graph made out of the game
		 * @param log a list of all the moves
		 */
		//Moves the robots in auto mode 
			public void move_robots_Auto() throws JSONException {
				List<String> log = this.robot.get_game().move();

				List<String> fruits = this.robot.get_game().getFruits();
				for (int i = 0; i < fruits.size(); i++) {
					Fruit_c f=new Fruit_c();
					f=this.robot.get_inner_fruit().get(i);
				
				}
				if(log!=null) {
					long t = this.robot.get_game().timeToEnd();
					for(int i=0;i<log.size();i++) {
						String robot_json = log.get(i);
						JSONObject line = new JSONObject(robot_json);
						JSONObject ttt = line.getJSONObject("Robot");
						int rid = ttt.getInt("id");
						int src = ttt.getInt("src");
						int dest = ttt.getInt("dest");
						String p[] = ttt.getString("pos").split(",");
						
						if(this.robot.get_inner_robots().get(rid).getNodeList() == null || (dest == -1 && this.robot.get_inner_robots().get(rid).getSize()==1)) {
							this.robot.get_inner_robots().get(rid).SetNodeList(this.robot.nextNode(src, rid,this.robot.get_inner_robots().get(rid).getNodeList()));
						}
						if( this.robot.get_inner_robots().get(rid).getNodeList()!=null && this.robot.get_inner_robots().get(rid).getNodeList().size()>1) {
							this.robot.get_game().chooseNextEdge(rid, this.robot.get_inner_robots().get(rid).getNodeList().get(1).getKey());
							this.robot.get_inner_robots().get(rid).list_to_go_through.remove(1);
						}
						this.robot.all_fruits = this.robot.update_inner_fruit();//gal change it tommrow
				}
			}
			}
			
			/**
			 * this function places the first robot on the src node of the edge
			 * which has the fruit with the biggest value, the second robot on the src
			 * node of the edge which has the fruit with the second max value etc.
			 * @param game the given game from the server
			 * @param gg the graph made out of the game
			 */
			public void locate_robots_auto() throws JSONException {
				Fruit_c temp = new Fruit_c();
				ArrayList<Fruit_c> fruits = new ArrayList<Fruit_c>();
				//this array list will hold all the fruits
				JSONObject line;
				String info = this.robot.get_game().toString();
				int num_robots=0;
				line = new JSONObject(info);
				JSONObject ttt = line.getJSONObject("GameServer");
				num_robots = ttt.getInt("robots");

				Iterator<String> f_iter = this.robot.get_game().getFruits().iterator();
				//goes through all the fruits
				while(f_iter.hasNext()) {
					JSONObject line2 = new JSONObject(f_iter.next());
					ttt = line2.getJSONObject("Fruit");
					int type=ttt.getInt("type");
					double value=ttt.getDouble("value");
					String p[] = ttt.getString("pos").split(",");
					Point3D p_fruit = new Point3D(Double.parseDouble(p[0]),Double.parseDouble(p[1]));
					edge_data E = temp.assos(p_fruit,this.robot.get_game(),type);
					//each fruit will be associated to its edge
					fruits.add(new Fruit_c(value,type,E.getSrc(),E.getDest()));

				}

				double max_value = Double.MIN_VALUE;
				int rm=0;

				for(int i=0;i<num_robots;i++) {//there is no need to run for more then the number of robots
					for (int j = 0; j < fruits.size(); j++) {
						if(max_value<fruits.get(j).getValue()) {//for every fruit in the list 
							max_value = fruits.get(j).getValue();
							//holds the max value
							rm = j;
						}
					}
					this.robot.get_game().addRobot(fruits.get(rm).getSrc());
					//add the robot in the index you found

					max_value = Double.MIN_VALUE;
					fruits.remove(rm);//remove that fruit from the list so the second robot will be placed
					rm=0;            //in the second max value and so on
				}
			}			
}
