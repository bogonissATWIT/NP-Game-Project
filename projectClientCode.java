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
			// TODO Auto-generated method stub
			while(true) {
				try {
					String modifiedMessage;
					modifiedMessage = inFromServer.readLine();
					System.out.println(modifiedMessage);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					break;
				}
			}
		}
	}
	

////////////////////////////////////////////////////////////////////////////////////////////////////
//main(String[] args): sets up multithreading and adds new users to players 
////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String argv[]) throws Exception {
		// TODO Auto-generated method stub
		Socket connectionSocket = new Socket("localhost", 1234);
		
		DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		
		GameProject gameproject = new GameProject(outToServer,inFromServer);
		Thread thread = new Thread(gameproject);
		thread.start();
		//optional formatting changes just to make it look a little nicer
		
		//I had the idea of basically taking the chatroom lab and adding to that and making the game. 
		//This would make it look more professionally done and neat I guess. Having the names and all...
		//Instead of "Name: 1 2" for like the row and columnm, we could have the server announce "Name played at position 1 2!"
		//if that makes any sense... Obviously not going to stick you with all that just wanted to write it down so I knew what to do later
		
		
		System.out.println("\t-= Welcome to TCP Tic-Tac-Toe! =-\nbefore you can play, please tell us your name: ");
		Scanner s = new Scanner(System.in);
		String name = s.nextLine();
		outToServer.writeBytes(name + "\r\n");
		
		System.out.printf("\n\t-= Hi %s! =-\n Would you like to create a new lobby (1), or join an existing lobby? (2): ", name);
		String lobbyChoice = s.nextLine();
		outToServer.writeBytes(lobbyChoice + "\r\n");
		
		
		String play = s.nextLine();
		
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

