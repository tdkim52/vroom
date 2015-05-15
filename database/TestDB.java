import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestDB {

   public static void main(String[] args) {
      Connection con = null;
      Statement st = null;
      ResultSet rs = null;
      
      PreparedStatement psAll = null;
      String allQuery = "SELECT username FROM users"; 
   
      //String url = "jdbc:mysql://50.87.146.115:3306/tdk_vsp";
      String url = "jdbc:mysql://gator3008.hostgator.com:3306/tdk_vsp";
      String user = "tdk_admin";
      String password = "admin";
   
      try {
         Class.forName("com.mysql.jdbc.Driver");
         System.out.println("Connecting to database...");
         con = DriverManager.getConnection(url, user, password);
         System.out.println("Database connection established");
         
         psAll = con.prepareStatement(allQuery);
         rs = psAll.executeQuery();
      
         while (rs.next()) {
            System.out.println(rs.getString(1));
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
            if (st != null) {
               st.close();
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
