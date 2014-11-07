import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import au.com.bytecode.opencsv.CSVWriter;


public class UFOAnalysis {
	final Pattern pattern = Pattern.compile("\\(([^),]+)\\)");
    ResultSet rs = null;
    Connection c = null;
    Statement stmt = null;
      
	public boolean isValidDate(String dateString) {
	    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	    try {
	    	df.setLenient(false);
	        df.parse(dateString);
	        return true;
	    } catch (java.text.ParseException e) {
	        return false;
	    }
	}
	
	public void selectFromTable( String args )
	  {
	    try {
	      stmt = c.createStatement();
	      rs = stmt.executeQuery(args);
    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    System.out.println(args  + "\nSQL successfully executed");
	  }
	
	public void closeAllConn()
	{
		try
		{
			rs.close();
		    stmt.close();
		    c.close();
		    System.out.println("Database closed successfully");
		}
	    catch(Exception e)
	    {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);	    	
	    }
	}
	public void SQLiteJDBC()
	  {
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      c.setAutoCommit(false);
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Opened database successfully");
	  }
	
	
	public void createTable()
	  {
	    Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Opened database successfully");
 
	      stmt = c.createStatement();
	      stmt = c.createStatement();
	      String delsql = "DROP TABLE UFO_DATA;"; 
	      stmt.executeUpdate(delsql);

	      String sql = "CREATE TABLE IF NOT EXISTS UFO_DATA" +
	                   "(sighted_at  TEXT NOT NULL," +
	                   " reported_at TEXT    NOT NULL, " +  
	                   " city    TEXT   NULL, " +
	                   " state    TEXT   NULL, " +
	                   " country    TEXT  NULL, " +
	                   " shape       TEXT, " + 
	                   " duration    INT," +
	                   " description TEXT)"; 
	      stmt.executeUpdate(sql);
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Table created successfully");
	  }
	
	 public void parseJSON(File fileName, boolean hasHeader)
	{		
		CSVWriter writer = null;
	    String line=null;
	    String shape=null;
		String city=null;
		String country="US";
		String state=null;
		try {
			writer = new CSVWriter(new FileWriter("src//UFO//chimps_16154-2010-10-20_14-33-35//ufo_awesome.bad"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		JSONParser parser=new JSONParser();
		ArrayList<ArrayList<String>> fileData = new ArrayList<ArrayList<String>>();
		System.out.println("############################");
		System.out.println("Reading File..........");
		System.out.println("############################");
		fileData = new ArrayList<ArrayList<String>>();
	
		int rowIndex=0;
		 try 
		 {	 
		   Scanner scanner = new Scanner(fileName);
		   try {
			    stmt = c.createStatement();
				String deletsql = "DELETE FROM UFO_DATA;";
			    stmt.executeUpdate(deletsql);
			    
			    while (scanner.hasNext())
				   {
					   line = scanner.nextLine();
					   if (hasHeader)
					   {	
						   hasHeader=false;
						   continue;
					   }
					   fileData.add(new ArrayList<String>());
					   System.out.println(line);
					   try{
						   Object obj= parser.parse(line);
						   JSONObject jsonObject = (JSONObject)obj;
						   Iterator<String> keysAttr;
							Iterator iterator = jsonObject.keySet().iterator();
							keysAttr= iterator;
							while(keysAttr.hasNext())
							{
								String key=keysAttr.next();

							}	
							//Adding entry to the Table
							String sight = jsonObject.get("sighted_at").toString();
							
							if (!isValidDate(sight))
							{
								String temp = "InvalidDate," + line;
								String [] badRecs =temp.split(",");
								
								writer.writeNext(badRecs);
								continue;
							}
							
							String inFormatSight = sight.substring(0,4) + "-" + sight.substring(4,6) + "-" + sight.substring(6,8) + " 12:00:00.000";
							
							String report = jsonObject.get("sighted_at").toString();
							String inFormatReport = report.substring(0,4) + "-" + report.substring(4,6) + "-" + report.substring(6,8) + " 12:00:00.000";

							int duration=0;
							
							String dur = jsonObject.get("reported_at").toString();
							int temp = Integer.parseInt(dur.replaceAll("[\\D]", ""));
							if (dur.isEmpty())
							{
								duration = -1;
							}
							else if (dur.contains("min"))
							{
								duration = temp*60; 
							} 
							else if (dur.contains("sec"))
							{
								duration = temp; 
							}
							else if (dur.contains("hour"))
							{
								duration = temp*60*60; 
							}

							/*
							 * Check for shape column having null value.
							 */
							String location= jsonObject.get("location").toString();
							
							if (location.contains("("))
							{
								city = location.substring(0, location.indexOf("("));
								Matcher m = pattern.matcher(location);
								while (m.find())
									country = m.group(1);
							}								
							else
								city = location.substring(0, location.indexOf(","));
							
							state = location.substring(location.length()-2).toUpperCase();
							//!state.matches("\\w\\w")
							if (location.substring(location.length()-1).matches("\\W"))								
								state=null;
							if (!jsonObject.get("shape").toString().isEmpty())
								shape= "\"" +jsonObject.get("shape").toString() +"\"";
							
							String sql = new String("INSERT INTO UFO_DATA (sighted_at,reported_at,city,state,country,shape,duration,description) " +
					                   "VALUES ( \"" +
					                   inFormatSight + "\",\"" + 
					                   inFormatReport  + "\"," +  "\""+
									city + "\"" + "," + "\""+ 
									state + "\"" + "," + "\""+
									country + "\"" + "," +
					                shape  + "," + 
									duration + "," + "\"" +
					                jsonObject.get("description").toString() + "\""+ 
					                ");");
							System.out.println(sql);
							stmt.executeUpdate(sql);
					   }     
					   catch (ParseException e) {
						   	  String temp = "InvalidJSONFormat," + line;
						      String [] badRecs = temp.split(",");
						      writer.writeNext(badRecs);
							  continue;
							//e.printStackTrace();
						}
					   
				   rowIndex++;
				   }
				   scanner.close();
				   c.commit();
				   writer.close();
		    } catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		    }
		    System.out.println("Records created successfully");
		 } 
		 catch (FileNotFoundException e) 
		 {
			 e.printStackTrace();
		 }
		 System.out.println("Number of records read: " + rowIndex);
		 
	}
	
	public ResultSet getResultSet()
	{
		return this.rs;
	}

	public static void main (String[] args) throws SQLException, ClassNotFoundException
	{
		ResultSet rs=null;
		UFOAnalysis ufoa = new UFOAnalysis();
		ufoa.SQLiteJDBC();
		ufoa.createTable();
		ufoa.parseJSON(new File("src//UFO//chimps_16154-2010-10-20_14-33-35//ufo_awesome.json"),false);
		
		/*
		 * Fetching the count of Female
		 */
		ufoa.selectFromTable("SELECT CAST(M.COUNT AS REAL)/C.COUNT*100 AS COUNT FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA " +
				"WHERE LOWER(DESCRIPTION) LIKE \"% woman %\" OR " +
				"LOWER(DESCRIPTION) LIKE \"% girl %\" OR " +
				"LOWER(DESCRIPTION) LIKE \"% female %\" " +
				") AS M " +
				"ON C.X=M.X;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         String count= rs.getString("COUNT");
	         System.out.println( "Fe-male Count = " + count + " %");
		}
		
		
		/*
		 * Fetching the count of Male
		 */
		ufoa.selectFromTable("SELECT CAST(M.COUNT AS REAL)/C.COUNT*100 AS COUNT FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA " +
				"WHERE LOWER(DESCRIPTION) LIKE \"% man %\" OR " +
				"LOWER(DESCRIPTION) LIKE \"% BOY %\" OR " +
				"LOWER(DESCRIPTION) LIKE \"% male %\" " +
				") AS M " +
				"ON C.X=M.X;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         String count= rs.getString("COUNT");
	         System.out.println( "Male Count = " + count + " %");
		}
		
		
		
		/*
		 * Fetching sitings by state
		 */
		ufoa.selectFromTable("SELECT STATE, COUNT(STATE) AS COUNT FROM UFO_DATA GROUP BY STATE ORDER BY Count DESC;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         System.out.println(rs.getString("STATE") + "\t:\t"+ rs.getString("COUNT"));
		}

		
		/*
		 * Looking for any specific Month having more citings
		 */
		ufoa.selectFromTable("SELECT M.Month as Month, CAST(M.Count AS REAL)/C.COUNT*100  AS Count FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN  " +
				"(SELECT 1 AS X, strftime('%m',date(sighted_at)) AS Month, COUNT(*) AS Count FROM UFO_DATA " +
				"GROUP BY 1,strftime('%m',date(sighted_at))) AS M " +
				"ON C.X=M.X ORDER BY Count DESC;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         System.out.println(rs.getString("Month") + "\t=\t" + rs.getString("Count") + " %");
		}
		
		
		/*
		 * Looking for any specific Date having more citings
		 */
		ufoa.selectFromTable("SELECT M.Date as Date, CAST(M.Count AS REAL)/C.COUNT*100  AS Count FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN  " +
				"(SELECT 1 AS X, strftime('%d',date(sighted_at)) AS Date, COUNT(*) AS Count FROM UFO_DATA " +
				"GROUP BY 1,strftime('%d',date(sighted_at))) AS M " +
				"ON C.X=M.X ORDER BY Count DESC;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         System.out.println(rs.getString("Date") + "\t=\t" + rs.getString("Count") + " %");
		}
		
		/*
		 * Looking for any specific Year having more citings
		 */
		ufoa.selectFromTable("SELECT M.Year as Year, CAST(M.Count AS REAL)/C.COUNT*100  AS Count FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN  " +
				"(SELECT 1 AS X, strftime('%Y',date(sighted_at)) AS Year, COUNT(*) AS Count FROM UFO_DATA " +
				"GROUP BY 1,strftime('%Y',date(sighted_at))) AS M " +
				"ON C.X=M.X ORDER BY Count DESC;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         System.out.println(rs.getString("Year") + "\t=\t" + rs.getString("Count") + " %");
		}
		
		/*
		 * Fetching sitings by Sunday
		 */
		ufoa.selectFromTable("SELECT CAST(M.SundaySight AS REAL)/C.COUNT*100  AS SundaySight FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN  " +
				"(SELECT 1 AS X, COUNT(1) AS SundaySight FROM UFO_DATA " +
				"where CAST(strftime('%w',date(sighted_at)) AS INTEGER)= 0) AS M " +
				"ON C.X=M.X;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         System.out.println("# of sighting on Sunday = " + rs.getString("SundaySight") + " %");
		}

		/*
		 * Fetching sitings by Monday
		 */
		ufoa.selectFromTable("SELECT CAST(M.MondaySight AS REAL)/C.COUNT*100  AS MondaySight FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN  " +
				"(SELECT 1 AS X, COUNT(1) AS MondaySight FROM UFO_DATA " +
				"where CAST(strftime('%w',date(sighted_at)) AS INTEGER)= 1) AS M " +
				"ON C.X=M.X;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         System.out.println("# of sighting on Monday = " + rs.getString("MondaySight") + " %");
		}
		
		/*
		 * Fetching sitings by Tuesday
		 */
		ufoa.selectFromTable("SELECT CAST(M.TuesdaySight AS REAL)/C.COUNT*100  AS TuesdaySight FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN  " +
				"(SELECT 1 AS X, COUNT(1) AS TuesdaySight FROM UFO_DATA " +
				"where CAST(strftime('%w',date(sighted_at)) AS INTEGER)= 2) AS M " +
				"ON C.X=M.X;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         System.out.println("# of sighting on Tuesday = " + rs.getString("TuesdaySight") + " %");
		}

		
		/*
		 * Fetching sitings by Wednesday
		 */
		ufoa.selectFromTable("SELECT CAST(M.WednesdaySight AS REAL)/C.COUNT*100  AS WednesdaySight FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN  " +
				"(SELECT 1 AS X, COUNT(1) AS WednesdaySight FROM UFO_DATA " +
				"where CAST(strftime('%w',date(sighted_at)) AS INTEGER)= 3) AS M " +
				"ON C.X=M.X;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         System.out.println("# of sighting on Wednesday = " + rs.getString("WednesdaySight") + " %");
		}

		
		/*
		 * Fetching sitings by Thursday
		 */
		ufoa.selectFromTable("SELECT CAST(M.ThursdaySight AS REAL)/C.COUNT*100  AS ThursdaySight FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN  " +
				"(SELECT 1 AS X, COUNT(1) AS ThursdaySight FROM UFO_DATA " +
				"where CAST(strftime('%w',date(sighted_at)) AS INTEGER)= 4) AS M " +
				"ON C.X=M.X;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         System.out.println("# of sighting on Thursday = " + rs.getString("ThursdaySight") + " %");
		}

		
		/*
		 * Fetching sitings by Friday
		 */
		ufoa.selectFromTable("SELECT CAST(M.FridaySight AS REAL)/C.COUNT*100  AS FridaySight FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN  " +
				"(SELECT 1 AS X, COUNT(1) AS FridaySight FROM UFO_DATA " +
				"where CAST(strftime('%w',date(sighted_at)) AS INTEGER)= 5) AS M " +
				"ON C.X=M.X;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         System.out.println("# of sighting on Friday = " + rs.getString("FridaySight") + " %");
		}
		
		
		/*
		 * Fetching sitings by Saturday
		 */
		ufoa.selectFromTable("SELECT CAST(M.SaturdaySight AS REAL)/C.COUNT*100  AS SaturdaySight FROM " +
				"(SELECT 1 AS X, COUNT(*) AS COUNT FROM UFO_DATA) AS C " +
				"JOIN  " +
				"(SELECT 1 AS X, COUNT(1) AS SaturdaySight FROM UFO_DATA " +
				"where CAST(strftime('%w',date(sighted_at)) AS INTEGER)= 5) AS M " +
				"ON C.X=M.X;");
		rs=ufoa.getResultSet();
		while ( rs.next() ) 
		{
	         System.out.println("# of sighting on Saturday = " + rs.getString("SaturdaySight") + " %");
		}
		rs.close();
		ufoa.closeAllConn();
		}
}
