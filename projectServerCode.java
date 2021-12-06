import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


////////////////////////////////////////////////////////////////////////////////////////////////////
//								TCP-Tac-Toe ---- Server Code									  //
//								Sam Bogonis & Shemar Mahase										  //
//							Network Programming - Final Project									  //
//									projectServerCode											  //
////////////////////////////////////////////////////////////////////////////////////////////////////

public class projectServerCode {
	
////////////////////////////////////////////////////////////////////////////////////////////////////
// People class: defines individual players and ties their information to their thread connection
////////////////////////////////////////////////////////////////////////////////////////////////////
	static class Players implements Runnable{
		String name;
		String lobbyChoice;
		Socket connection;
		String move;
		boolean isTurn = false;
		
		
		Players(Socket c){
			connection = c;
		}
		
		public void setName(String n) {
			name = n;
		}
		
		public String getName() {
			if(name == null) {
				name = "none";
			}
			return name;
		}
		
		public void send(String s) {
			try {
				DataOutputStream outToClient = new DataOutputStream(connection.getOutputStream());
				outToClient.writeBytes(s + "\n");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void setMove(String s) {
			move = s;
		}
		
		public String getMove() {
			if(move == null) {
				return "";
			}
			return move;
		}
		public Socket getConnection() {
			return connection;
		}

		@Override
		public void run() {
			try {
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String s;
				while(true) {
					s = inFromClient.readLine();
					if(s.toUpperCase().equals("QUIT")) {
						connection.close();
					}
					if(isTurn && s != null && !s.equals("")) {
						move = s;
						System.out.println("hmmmst");
						System.out.println(move);
						s = null;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////
//newLobby class: second iteration based on code found on stack overflow
//https://stackoverflow.com/questions/58937039/java-create-a-team-then-add-players
////////////////////////////////////////////////////////////////////////////////////////////////////
	static class Lobby {
		String lobbyName;
		public Lobby(String lobbyName) {
			this.lobbyName = lobbyName;
		}
		
		public void addPlayer (Players player) {
			players.add(player);
		}
		
		public String getLobbyName() {
			return lobbyName;
		}
		
		public List<Players> getPlayers() {
			return players;
		}

		public static boolean lobbyExists(String userLobbyChoice) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
////////////////////////////////////////////////////////////////////////////////////////////////////
//Game class: based on code found on stack overflow
//https://stackoverflow.com/questions/58937039/java-create-a-team-then-add-players
////////////////////////////////////////////////////////////////////////////////////////////////////
	static class Game  implements Runnable {
		String name;
		HashMap<String, Lobby> lobbies = new LinkedHashMap<String, Lobby>();
		String[] allLobbyNames;
		int numPlayers = 0;
		Players player1;
		Players player2;
		boolean full = false;
		
		public Game(String n) {
			name = n;
		}
		
		public void addPlayer (Players p) {
			if(numPlayers == 0) {
				player1 = p;
				numPlayers = 1;
			}
			else if(numPlayers == 1) {
				player2 = p;
				numPlayers = 2;
				full = true;
				Thread thread = new Thread(this);
				thread.start();
			}
		}
		
		public boolean isFull() {
			return full;
		}
		
		public String getName() {
			return name;
		}
		
		public String getPlayers() {
			if(player2 == null) {
				return "{"+player1.getName() + ",None}";
			}
			return "{"+player1.getName() + "," + player2.getName()+"}";
		}
		
		public void print() {
			for(Map.Entry<String , Lobby> entry : lobbies.entrySet() ) {
				Lobby lobby = entry.getValue();
				System.out.println("Lobby Name: " + lobby.getLobbyName());
				System.out.println("Players: " + lobby.getPlayers());	
			}
			
		}

		@Override
		public void run() {
			sendBoth("Game is beginning\n");
			TicTacToe TTT = new TicTacToe();
			Thread thread = new Thread(player1);
			thread.start();
			thread = new Thread(player2);
			thread.start();
			sendBoth(TTT.printBoard());
			while(true) {
				boolean check = false;
				while(!check) {
					player1.send("Please send a move " + player1.getName());
					awaitMove(player1);
					check = TTT.checkMove(player1.move, "x");
					if(!check) {
						player1.send("invalid move");
					}
					player1.setMove("");
				}
				sendBoth(TTT.printBoard());
				player2.send(player1.getName() + " has made a move!");
				if(TTT.checkWinner()) {
					sendBoth(player1.getName() + "wins!");
					break;
				}
				check = false;
				while(!check) {
					player2.send("Please send a move " + player2.getName());
					awaitMove(player2);
					check = TTT.checkMove(player2.move, "o");
					if(!check) {
						player2.send("invalid move");
					}
					player2.setMove("");
				}
				sendBoth(TTT.printBoard());
				player1.send(player2.getName() + " has made a move!");
				if(TTT.checkWinner()) {
					sendBoth(player2.getName() + "wins!");
					break;
				}
			}
		}
		
		private void sendBoth(String s) {
			player1.send(s + "\n");
			player2.send(s + "\n");
		}
		
		private String awaitMove(Players p) {
			String s = "";
			while(s.equals("")) {
				String previous = s;
				p.isTurn = true;
				s = p.getMove();
				System.out.println(s);
			}
			p.isTurn = false;
			return s;
		}
		
}

////////////////////////////////////////////////////////////////////////////////////////////////////	
// Lists: keeps track of players, previous messages, etc.
////////////////////////////////////////////////////////////////////////////////////////////////////	
	static List<Players> players = new ArrayList<>();
	static List<Game> games = new ArrayList<>();
	static List<String> prevMsg = new ArrayList<>();
	
	static String[] board;
	static String turn;
	
////////////////////////////////////////////////////////////////////////////////////////////////////
// ClientRequest: server-side socket handling
////////////////////////////////////////////////////////////////////////////////////////////////////
	static class ClientRequest implements Runnable {
			Socket connectionSocket;
			private boolean noLobbies;
			ClientRequest(Socket c){
				connectionSocket = c;
				
		}
			
//////////////////////////////////////////////////////////////////////////////////////////////////// 
// run(): handles users inputs, where the magic happens
////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void run() {
		String name;
		try{
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
		String clientMessage = "";
		outToClient.writeBytes("\t-= Welcome to TCP Tic-Tac-Toe! =-\nbefore you can play, please tell us your name: \n");
		name = inFromClient.readLine();
		Players p = new Players(connectionSocket);
		p.setName(name);		
		while(true) {
			outToClient.writeBytes("\t-= Hi " + p.getName() + "! =-\nWould you like to create a new lobby (1), or join an existing lobby? (2): \n");
			clientMessage = inFromClient.readLine();
			if (clientMessage.contentEquals("1") == true) {
				//user chooses to create new lobby
				System.out.println("Code 01");
				outToClient.writeBytes("User Chose to Create New Lobby! Please give the lobby a name: \n");
				clientMessage = inFromClient.readLine();
				Game game = new Game(clientMessage);
				game.addPlayer(p);
				games.add(game);
				outToClient.writeBytes("Created Lobby called " + game.getName() + "! Waiting for another player to join.\n");
				break;
			}
			
			
//BUG: if player selects to join existing lobby and there are no lobbies to join, 
//the server prints the "Would you like to create a new lobby (1)..." message twice
//if the player then selects to create a new lobby, nothing happens
			
			if (clientMessage.contentEquals("2") == true) {
				outToClient.writeBytes("Here is a list of currently available lobbies: \n ");
				outToClient.writeBytes(showLobbies() + "\n");
				if(noLobbies) {
					System.out.println(noLobbies);
					outToClient.writeBytes("\t-= Hi " + name + "! =-\nWould you like to create a new lobby (1), or join an existing lobby? (2):\n");
					noLobbies = false;
					clientMessage = " ";
					continue;
				}
				while(true) {
					clientMessage = inFromClient.readLine();
					if(checkValid(clientMessage)) {
						outToClient.writeBytes("Joining lobby, " + clientMessage+"\n");
						games.get(Integer.parseInt(clientMessage) - 1).addPlayer(p);
						break;
					}
					else if (!clientMessage.equals("")) {
						outToClient.writeBytes("Invalid Lobby\n");
						clientMessage = "";
					}
				}
				break;
			}
		}
//		inFromClient.close();
//		outToClient.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String showLobbies() {
		int count = 0;
		String lobbies = "";
		for(int i = 0 ; i < games.size();i++) {
			if(!games.get(i).isFull()) {
				count++;
				lobbies += (i + 1) + ". Name:" + games.get(i).getName() + " Players:" + games.get(i).getPlayers();
				lobbies += "\n";
			}
		}
		lobbies += " " + "Please input the number of the lobby you want to join.\n";
		if(count == 0) {
			lobbies = "Sorry there seems to be no games available right now \n Sending you back to lobby selection.\n";
			noLobbies = true;
			return lobbies;
		}
		noLobbies = false;
		return lobbies;
	}
	
	public boolean checkValid(String s) {
		for(int i = 0; i < games.size(); i++) {
			if(Integer.parseInt(s) == i + 1 && !games.get(i).isFull()) {
				return true;
			}
		}
		return false;
	}
}
////////////////////////////////////////////////////////////////////////////////////////////////////
// main(String[] args): sets up multithreading and adds new users to players 
////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) throws Exception {

		
		ServerSocket serverSocket;
		int port = 1234;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server listening to port " + port);

			while (true) {
				Socket connectionSocket = serverSocket.accept();
				ClientRequest clientRequest = new ClientRequest(connectionSocket);
				Thread thread = new Thread(clientRequest);
				thread.start();
				players.add(new Players(connectionSocket));
				
			}
		}
			catch (Exception ex) {
			// TODO Auto-generated catch block
			System.out.println("Error!\r\n");
		}

	
		


	}
////////////////////////////////////////////////////////////////////////////////////////////////////
//displayGameBoard(): displays current game board
////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void displayGameBoard() {
		//this was sourced from an existing GitHub game
		//https://gist.github.com/xaviablaza-zz/3844825
		//this needs to be altered to be sent to the players
		
		
		System.out.println("/---|---|---\\");
		System.out.println("| " + board[0] + " | " + board[1] + " | " + board[2] + " |");
		System.out.println("|-----------|");
		System.out.println("| " + board[3] + " | " + board[4] + " | " + board[5] + " |");
		System.out.println("|-----------|");
		System.out.println("| " + board[6] + " | " + board[7] + " | " + board[8] + " |");
		System.out.println("/---|---|---\\");
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////
// sendToAll(String msg, String name): sends a name and string message to users connected to the server 
////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void sendToAll(String msg, String name) throws IOException {
		if(prevMsg.size() < 5) {
			prevMsg.add(name + ": " + msg);
		}
		else {
			prevMsg.remove(0);
			prevMsg.add(name + ": " + msg);
		}
		for(int i = 0; i < players.size(); i++) {
			if(name.equals(players.get(i).getName())) {
				continue;
			}
			DataOutputStream outToClient = new DataOutputStream(players.get(i).getConnection().getOutputStream());
			outToClient.writeBytes(name + ": " + msg);
		}
	}
////////////////////////////////////////////////////////////////////////////////////////////////////
// sentToAll(String msg): sends a string message to users connected to the server
////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void sendToAll(String msg) throws IOException {
		if(prevMsg.size() < 5) {
			prevMsg.add(msg);
		}
		else {
			prevMsg.remove(0);
			prevMsg.add(msg);
		}
		for(int i = 0; i < players.size(); i++) {
			DataOutputStream outToClient = new DataOutputStream(players.get(i).getConnection().getOutputStream());
			outToClient.writeBytes(msg);
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////
// closeConnection: closes TCP connection of specific user, removes from players
////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void closeConnection(String name) {
		for(int i = 0; i < players.size(); i++) {
			if(name.equals(players.get(i).getName())) {
				players.remove(i);
			}
		}
	}
////////////////////////////////////////////////////////////////////////////////////////////////////
}
