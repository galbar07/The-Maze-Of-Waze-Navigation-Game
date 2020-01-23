package gameClient;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * This class represents a simple example of using MySQL Data-Base.
 * Use this example for writing solution. 
 * @author boaz.benmoshe
 *
 */
public class SimpleDB {
	public static final String jdbcUrl="jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser="student";
	public static final String jdbcUserPassword="OOP2020student";
	public static int gamesPlayed;
	public static int maxUserLevel;
	public static int[] bestScores;
	public static int[][] rankInClass;
	private static HashMap <Integer,Integer[]> requiredResults= new HashMap<>();

	/** initialize hashMap consisting the required results for each of 11 stages 
	 * 
	 */

	private static void initHashResult() {
		int stage = 0;
		switch(stage) {	
		//init the required results for each stage

		case 0:
			requiredResults.put(stage,new Integer[]{125,290});
			break;
		case 1:
			requiredResults.put(stage,new Integer[] {436,580});
			break;
		case 3:
			requiredResults.put(stage,new Integer[] {713,580});
			break;
		case 5:
			requiredResults.put(stage,new Integer[] {570,500});
			break;
		case 9:
			requiredResults.put(stage,new Integer[] {480,580});
			break;
		case 11:
			requiredResults.put(stage,new Integer[] {1050,580});
			break;
		case 13:
			requiredResults.put(stage,new Integer[] {310,580});
			break;
		case 16:
			requiredResults.put(stage,new Integer[] {235,290});
			break;
		case 19:
			requiredResults.put(stage,new Integer[] {250,580});
			break;
		case 20:
			requiredResults.put(stage,new Integer[] {200,290});
			break;
		case 23:
			requiredResults.put(stage,new Integer[] {1000,1140});
			break;
		default:
			break;
		}
	}
	public static void printLog() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT * FROM Logs;";
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);

			while(resultSet.next())
			{
				System.out.println("Id: " + resultSet.getInt("UserID")+","+resultSet.getInt("levelID")+","+resultSet.getDouble("score")+""+resultSet.getInt("moves")+","+resultSet.getDate("time"));
			}
			resultSet.close();
			statement.close();		
			connection.close();		
		}

		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * this function returns the KML string as stored in the database (userID, level);
	 * @param id player's id
	 * @param level scenario played
	 * @return
	 */
	public static String getKML(int id, int level) {
		String ans = null;
		String allCustomersQuery = "SELECT * FROM Users where userID="+id+";";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);		
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			if(resultSet!=null && resultSet.next()) {
				ans = resultSet.getString("kml_"+level);
			}
		}
		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ans;
	}
	public static int allUsers() {
		int ans = 0;
		String allCustomersQuery = "SELECT * FROM Users;";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);		
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			while(resultSet.next()) {
				System.out.println("Id: " + resultSet.getInt("UserID"));
				ans++;
			}
			resultSet.close();
			statement.close();		
			connection.close();
		}
		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ans;
	}

	/**
	 * computes the player's statistics in each stage
	 * @param id
	 */
	public static void getStats(int id){
		maxUserLevel=0;
		bestScores=new int[24];
		gamesPlayed=0;
		initHashResult();
		boolean passed=true;
		for (int level : bestScores) 
			bestScores[level]=0;
		String allCustomersQuery = "SELECT * FROM Logs where UserID="+id+";";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);	
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			while(resultSet.next()) {
				int currentStage=resultSet.getInt("levelID");
				if(requiredResults.containsKey(currentStage)&&(requiredResults.get(currentStage)[0]>resultSet.getInt("score")||
						requiredResults.get(currentStage)[1]<resultSet.getInt("moves"))) 
					passed=false;

				if(currentStage>maxUserLevel&&passed)
					maxUserLevel=currentStage;
				if(currentStage>=0&&currentStage<=maxUserLevel&&bestScores[currentStage]<resultSet.getInt("score")&&passed)
					bestScores[currentStage]=resultSet.getInt("score");
				gamesPlayed++;
				passed=true;
			}
			resultSet.close();
			statement.close();		
			connection.close();	
		}
		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}


	}
	/**
	 * computes the player's rank in each of 11 stages, compared to all other students
	 * @param id player's id
	 */
	public static void getRank(int id){

		rankInClass= new int[][] {{0,1,3,5,9,11,13,16,19,20,23},{0,0,0,0,0,0,0,0,0,0,0}};
		// init the current rank in each stage to 0
		String allCustomersQuery2="SELECT * FROM Logs where levelID="+0+" order by score DESC;";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);	
			Statement statement = connection.createStatement();
			ResultSet resultSet2=statement.executeQuery(allCustomersQuery2);

			int pos;
			for(int i=0;i<rankInClass[0].length;i++) {
				int currLevel=rankInClass[0][i];
				ArrayList <Integer>ids= new ArrayList<>();
				pos=1;
				allCustomersQuery2 = "SELECT * FROM Logs where levelID="+currLevel+" order by score DESC;";
				Class.forName("com.mysql.jdbc.Driver");
				resultSet2 = statement.executeQuery(allCustomersQuery2);
				while(resultSet2.next()&&resultSet2.getInt("score")>bestScores[currLevel]) {
					if(!ids.contains(resultSet2.getInt("UserID"))) {
						// the user's best score was already found
						if(!requiredResults.containsKey(currLevel)) 
							pos++;
						else if(requiredResults.get(currLevel)[0]<=resultSet2.getInt("score")&&
								//score is at least the value initialized in requiredResults
								requiredResults.get(currLevel)[1]>=resultSet2.getInt("moves")) 
							//moves are at most the value initialized in requiredResults
							pos++;
						ids.add(resultSet2.getInt("UserID"));
					}

				}
				rankInClass[1][i]=pos;
			}
			resultSet2.close();
			statement.close();		
			connection.close();	

		}
		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}

