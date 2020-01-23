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
	 * This test is meant to check robot size array compare to the original robot there is in the game.
	 * @throws JSONException
	 */
	@Test
	void GetRobotTest() throws JSONException {
		DGraph gg = new DGraph();
		gg.init(game_.getGraph());//build the graph from string 
		robot_algo robot = new robot_algo(this.game_);
		JSONObject line;
		String info = game_.toString();
		System.out.println(info);
		line = new JSONObject(info);
		JSONObject ttt = line.getJSONObject("GameServer");
		int rs = ttt.getInt("robots");
		game_robots = robot.getRobots(rs);
		assertTrue(rs==game_robots.size());
	}
	
	
	/**
	 * This test meant to check if the creation of the fruit list is equal to the amount
	 * of the fruit there is.
	 * @throws JSONException
	 */
	@Test
	void GetFruitTest() throws JSONException {
		DGraph gg = new DGraph();
		gg.init(game_.getGraph());//build the graph from string 
		robot_algo robot = new robot_algo(this.game_);
		JSONObject line;
		String info = game_.toString();
		System.out.println(info);
		line = new JSONObject(info);
		JSONObject ttt = line.getJSONObject("GameServer");
		int fs = ttt.getInt("fruits");
		all_fruits = robot.get_inner_fruit();
		assertTrue(fs==all_fruits.size());
	}
	
	
	
	

}
