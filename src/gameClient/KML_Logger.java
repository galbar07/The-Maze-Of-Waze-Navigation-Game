package gameClient;

import java.io.FileWriter;

import Server.Game_Server;
import Server.game_service;

public class KML_Logger {
	game_service game;
	String file ;
	
	
	public KML_Logger(game_service game,String file) {
		this.game=game;
		this.file = file;		
	}
	
	
	
	
	

	public static void main(String[] args) {
		int scenario_num = 3;
		game_service game = Game_Server.getServer(scenario_num);
		String info = game.toString();
		Robot_c robot = new Robot_c(game);
		
		try{    
	           FileWriter fw=new FileWriter("01.kml");    
	           fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
	           		"<kml xmlns=\"http://www.opengis.net/kml/2.2\"\r\n" + 
	           		" xmlns:gx=\"http://www.google.com/kml/ext/2.2\">");    
	           fw.close();    
	          }catch(Exception e){System.out.println(e);}    
	          System.out.println("Success...");    
	         
		
		
		

	}

}
