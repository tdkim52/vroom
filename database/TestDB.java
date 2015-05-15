import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.StringBuilder;
import java.text.DecimalFormat;

public class TestDB {

   public static void main(String[] args) {
      Connection con = null;
      ResultSet rs = null;
      
      PreparedStatement psAll = null;
      String allQuery = "SELECT latitude, longitude FROM hazards";
      
      PreparedStatement psWithin = null;
      StringBuilder withinQuery = new StringBuilder("SELECT * FROM hazards WHERE "); 
      //String withinQuery = "SELECT * FROM hazards WHERE latitude BETWEEN ";   
   
      // database table structure
      int id;
      String type = null;
      String lat = null;
      String lon = null;
      String mess =  null;
      
      String latlon = null;
      
      DecimalFormat df = new DecimalFormat(".######");
      
      String lat1 = "48.732761";
      String lon1 = "-122.485227";
      String lat2 = "48.732801";
      String lon2 = "-122.486131";
      String lat3 = "48.733714";
      String lon3 = "-122.486008";
      
      //String url = "jdbc:mysql://50.87.146.115:3306/tdk_vsp";
      String url = "jdbc:mysql://gator3008.hostgator.com:3306/tdk_vsp";
      String user = "tdk_admin";
      String password = "admin";
   
      try {
         Class.forName("com.mysql.jdbc.Driver");
         System.out.println("Connecting to database...");
         con = DriverManager.getConnection(url, user, password);
         System.out.println("Database connection established");
         
         double btwLat1 = Double.parseDouble(lat1) - 0.005;
         double btwLat2 = Double.parseDouble(lat1) + 0.005;
         double btwLon1 = Double.parseDouble(lon1) - 0.005;
         double btwLon2 = Double.parseDouble(lon1) + 0.005;
         String btwLat1s = df.format(btwLat1);
         String btwLat2s = df.format(btwLat2);
         String btwLon1s = df.format(btwLon1);
         String btwLon2s = df.format(btwLon2);
         
         withinQuery.append("(latitude BETWEEN ").append(btwLat1s).append(" AND ").append(btwLat2s).append(") ");
         withinQuery.append("AND (longitude BETWEEN ").append(btwLon1s).append(" AND ").append(btwLon2s).append(")");
         //System.out.println(withinQuery.toString());
         
         psWithin = con.prepareStatement(withinQuery.toString());
         rs = psWithin.executeQuery();
         
         //psAll = con.prepareStatement(allQuery);
         //rs = psAll.executeQuery();
         
         while (rs.next()) {
            id = rs.getInt("id");
            type = rs.getString("type");
            lat = rs.getString("latitude");
            lon = rs.getString("longitude");
            mess = rs.getString("message");
            latlon = lat + "," + lon;
            System.out.println("hazards within range pulled from database:");
            System.out.println("id: " + id + " | " + "type: " + type +
                                 " | " + "lat,long: " + latlon + " | " +
                                 "message: " + mess);
         }
      
      } 
      catch (Exception ex) {
         System.out.println("exception");
         ex.printStackTrace();
      } 
      finally {
         try {
            if (rs != null) {
               rs.close();
            }
            if (psAll != null) {
               psAll.close();
            }
            if (con != null) {
               con.close();
            }
         
         } 
         catch (SQLException ex) {
            System.out.println("exception 2");
         }
      }
   
   }
}
