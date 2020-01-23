package Tests;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;
import algorithms.robot_algo;
import dataStructure.DGraph;
import gameClient.Fruit_c;
import gameClient.Robot;
import gameClient.game_manager;

class RobotAlgoTest {
	private  game_service game_= Game_Server.getServer(-1);
	public  ArrayList<Fruit_c> all_fruits;
	private  ArrayList<Robot>game_robots;
	
	
	
	/**
	 * This test is meant to check 
	 * @throws JSONException
	 */
	@Test
	void GetRobotTest() throws JSONException {
		DGraph gg = new DGraph();
		gg.init(game_.getGraph());//build the graph from string 
		robot_algo robot = new robot_algo(this.game_);
		game_manager manage = new game_manager(robot);
		JSONObject line;
		String info = game_.toString();
		line = new JSONObject(info);
		JSONObject ttt = line.getJSONObject("GameServer");
		int rs = ttt.getInt("robots");
		game_robots = robot.getRobots(rs);
		assertTrue(rs==game_robots.size());
		

		
		
		
		
	}

}
