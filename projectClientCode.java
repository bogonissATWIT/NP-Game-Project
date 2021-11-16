import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class projectClientCode {
	
	
//testing to make sure that the pull works as expected with the new egit install
	//and testing the push...
	static class GameProject implements Runnable {
		DataOutputStream outToServer;
		BufferedReader inFromServer;
		
		GameProject(DataOutputStream outToServer,BufferedReader inFromServer){
			this.outToServer = outToServer;
			this.inFromServer = inFromServer;
		}

		
		//run() contents just copied from ChatServerCode to just have a basic model
		
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
	

		
	public static void main(String argv[]) throws Exception {
		// TODO Auto-generated method stub
		Socket connectionSocket = new Socket("10.220.35.205", 1234);
		
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
		
		String play = s.nextLine();
		
		while(!play.toUpperCase().equals("QUIT")) {
			
			play = s.nextLine();
			outToServer.writeBytes(play + "\r\n");
		}
		
		s.close();
		outToServer.close();
		connectionSocket.close();
		
		
		
	}

}

