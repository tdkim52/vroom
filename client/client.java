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

   public void connect(String addr) throws IOException {
      Socket socket = new Socket(addr, 49152);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String message = in.readLine();
      System.out.println(message);
      System.exit(0);
   }
   
   //
   public static void main(String[] args) throws IOException {
      
      Scanner reader = new Scanner(System.in);      
      System.out.println("Enter IP Address of server running service on port 49152:");
      String serverAddress = reader.nextLine();
      
      Client client = new Client();
      client.connect(serverAddress);
     
   }
}
      