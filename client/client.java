/*
*  Timothy Kim
*  
*  VROOM Client
*
*  April 9, 2015
*
*/

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

// trivial client for VROOM VSP server
public class Client {

   private BufferedReader in;
   private static String DEFAULT = "140.160.137.170"; // linux-09.cs.wwu.edu

   // attempts to connect to server and get coordinates
   // reads buffer and prints coordinates
   public void connect(String addr) throws IOException {
      Socket socket = new Socket(addr, 49152);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      
      String coordinate;
      while ((coordinate = in.readLine()) != null) {
         System.out.println(coordinate);
      }
      socket.close();
      System.exit(0);
   }
   
   // gets server IP from user input and runs connect method
   public static void main(String[] args) throws IOException {
   
      Client client = new Client();
      
      Scanner reader = new Scanner(System.in);      
      System.out.println("Enter IP Address of server running service on port 49152:");
      String serverAddress = reader.nextLine();
      
      if (serverAddress.equals("")) {
         client.connect(DEFAULT);
      }
      else {
         client.connect(serverAddress);
      }
   }
}
      