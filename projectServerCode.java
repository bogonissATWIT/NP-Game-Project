import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



////////////////////////////////////////////////////////////////////////////////////////////////////
//								TCP-Tac-Toe ---- Server Code									  //
//								Sam Bogonis & Shemar Mahase										  //
//							Network Programming - Final Project									  //
//									projectServerCode											  //
////////////////////////////////////////////////////////////////////////////////////////////////////

public class projectServerCode {
	
////////////////////////////////////////////////////////////////////////////////////////////////////
// Players class: defines individual players and ties their information to their thread connection
////////////////////////////////////////////////////////////////////////////////////////////////////
	static class Players implements Runnable {
		String name;
		String lobbyChoice;
		Socket connection;
		boolean isTurn = false;
		String move;
		
		Players(Socket c) {
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
				outToClient.writeBytes(s+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void setMove(String s) {
			move = s;
		}
	
		public synchronized String getMove() {
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
						System.out.println(s);
						move = s;
						System.out.println(getMove());
						s = null;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////
//Game class: handles some game aspects
////////////////////////////////////////////////////////////////////////////////////////////////////
	static class Game  implements Runnable {
		String name;
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
				return "{" + player1.getName() + ",None}";
			}
			return "{" + player1.getName() + "," + player2.getName() + "}";
		}
		
		@Override
		public void run() {
			sendBoth("Game is beginning");
			TicTacToe TTT = new TicTacToe();
			Thread thread = new Thread(player1);
			thread.start();
			thread = new Thread(player2);
			thread.start();
			sendBoth(TTT.printBoard());
			System.out.println("Game begins");
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
					sendBoth(player1.getName() + " wins!");
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
			player1.send(s);
			player2.send(s);
		}
		
		private String awaitMove(Players p) {
			String s = "";
			p.isTurn = true;
			while(s.equals("")) {
				p.isTurn = true;
				s = p.getMove();
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
			ClientRequest(Socket c) {
				connectionSocket = c;
		}
			
//////////////////////////////////////////////////////////////////////////////////////////////////// 
// run(): handles users inputs, where the magic happens
////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void run() {
		String name;
		try {
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		outToClient.writeBytes("\t-= Welcome to TCP Tic-Tac-Toe! =-\nbefore you can play, please tell us your name: \n");
		String clientMessage = "";
		name = inFromClient.readLine();
		Players p = new Players(connectionSocket);
		p.setName(name);		
		while(true) {
			outToClient.writeBytes("\t-= Hi " + name + "! =-\nWould you like to create a new lobby (1), or join an existing lobby? (2):\n");
			clientMessage = inFromClient.readLine();
			if (clientMessage.contentEquals("1") == true) {
				System.out.println("Code 01");
				outToClient.writeBytes("User Chose to Create New Lobby! Please give the lobby a name:\n");
				clientMessage = inFromClient.readLine();
				Game game = new Game(clientMessage);
				game.addPlayer(p);
				games.add(game);
				outToClient.writeBytes("Created Lobby called " + game.getName() + "! Waiting for another player to join.\n");
				break;
			}
			if (clientMessage.contentEquals("2") == true) {
				outToClient.writeBytes("Here is a list of currently available lobbies: \n ");
				outToClient.writeBytes(showLobbies() + "\n");
				if(noLobbies) {
					System.out.println(noLobbies);
					noLobbies = false;
					clientMessage = " ";
					continue;
				}
				while(true) {
					clientMessage = inFromClient.readLine();
					if(checkValid(clientMessage)) {
						outToClient.writeBytes("Joining lobby, " + clientMessage + "\n");
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
				lobbies +="\n";
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
	public static void main(String[] args) {
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
			System.out.println("Error!\r\n");
		}
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
