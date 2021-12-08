import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

////////////////////////////////////////////////////////////////////////////////////////////////////
//								TCP-Tac-Toe ---- Client Code									  //
//								Sam Bogonis & Shemar Mahase										  //
//							Network Programming - Final Project									  //
//									projectClientCode											  //
////////////////////////////////////////////////////////////////////////////////////////////////////

public class projectClientCode {
	
////////////////////////////////////////////////////////////////////////////////////////////////////
//GameProject: initializes DataOutputStream and BufferedReader
////////////////////////////////////////////////////////////////////////////////////////////////////
	static class GameProject implements Runnable {
		DataOutputStream outToServer;
		BufferedReader inFromServer;
		
		GameProject(DataOutputStream outToServer,BufferedReader inFromServer){
			this.outToServer = outToServer;
			this.inFromServer = inFromServer;
		}

////////////////////////////////////////////////////////////////////////////////////////////////////
//run(): handles users inputs, where the magic happens
////////////////////////////////////////////////////////////////////////////////////////////////////
		@Override
		public void run() {
			while(true) {
				try {
					String modifiedMessage;
					modifiedMessage = inFromServer.readLine();
					System.out.println(modifiedMessage);
				} catch (IOException e) {
					break;
				}
			}
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////
//main(String[] args): sets up multithreading and adds new users to players 
////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String argv[]) throws Exception {
		Socket connectionSocket = new Socket("localhost", 1234);
		
		DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		
		GameProject gameproject = new GameProject(outToServer,inFromServer);
		Thread thread = new Thread(gameproject);
		thread.start();
			
		Scanner s = new Scanner(System.in);
		String name = s.nextLine();
		outToServer.writeBytes(name + "\r\n");
		
		String lobbyChoice = s.nextLine();
		outToServer.writeBytes(lobbyChoice + "\r\n");
		
		String lobbyNameSelect = s.nextLine();
		outToServer.writeBytes(lobbyNameSelect+"\r\n");
		
		String play = "";
		
		while(!play.toUpperCase().equals("QUIT")) {
			
			play = s.nextLine();
			outToServer.writeBytes(play + "\r\n");
		}
		
		s.close();
		outToServer.close();
		connectionSocket.close();	
		
	}
////////////////////////////////////////////////////////////////////////////////////////////////////
}
