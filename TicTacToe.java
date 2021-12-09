////////////////////////////////////////////////////////////////////////////////////////////////////
//								TCP-Tac-Toe ---- Game Code										  //
//								Sam Bogonis & Shemar Mahase										  //
//							Network Programming - Final Project									  //
//										TicTacToe												  //
////////////////////////////////////////////////////////////////////////////////////////////////////

public class TicTacToe {
	String[][] board = new String[3][3];
	boolean winnerCheck = false;
	boolean winner;
	
////////////////////////////////////////////////////////////////////////////////////////////////////	
//				Tic-Tac-Toe code based off of the following GitHub project: 					  //
//			https://github.com/aoyshi/Java-Tic-Tac-Toe/blob/master/TicTacToe.java				  //
////////////////////////////////////////////////////////////////////////////////////////////////////	
	
////////////////////////////////////////////////////////////////////////////////////////////////////
//getValidInt(String prompt): Checks to see if the player entered a valid play
////////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean checkMove(String move,String icon) {
		int spot1 = -1;
		int spot2 = -1;
		try {
			spot1 = Integer.parseInt(move.split(" ")[0]);	
			spot2 = Integer.parseInt(move.split(" ")[1]);	
			if(board[spot2][spot1].equals("_")) {
				board[spot2][spot1] = icon;
				return true;
			}
		}
		catch(Exception ex){
		}
		return false;
	}
	
	public TicTacToe(){
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				board[i][j] = "_";
			}
		}
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////
//checkHit(int[][] A): Checks if there is a 3 in a row on the board
////////////////////////////////////////////////////////////////////////////////////////////////////	
	private boolean check(String a,String b,String c) {
		if(winnerCheck) {
			return winner;
		}
		String result = a + b + c;
		if(result.equals("xxx") || result.equals("ooo")) {
			winnerCheck = true;
			return true;
		}
		return false;
	}
	public boolean checkWinner() {
	    winnerCheck = false;
	    winner = check(board[0][0] , board[0][1] , board[0][2]);
	    winner = check(board[1][0] , board[1][1] , board[1][2]);
	    winner = check(board[2][0] , board[2][1] , board[2][2]);
	    winner = check(board[0][0] , board[1][0] , board[2][0]);
	    winner = check(board[0][1] , board[1][1] , board[2][1]);
	    winner = check(board[0][2] , board[1][2] , board[2][2]);
	    winner = check(board[0][0] , board[1][1] , board[2][2]);
	    winner = check(board[0][2] , board[1][1] , board[2][0]);

	    return winner;
	  }	  
	
////////////////////////////////////////////////////////////////////////////////////////////////////
//printBoard(int[][] A): Prints game board as is 
////////////////////////////////////////////////////////////////////////////////////////////////////
	  public String printBoard() {
		  String s = "";
		  s += "-------------\n";
		  for (int i = 0; i < 3; i++) {
			  s += "| ";
			  
			  for (int j = 0; j < 3; j++) {
				  s += board[i][j] + " | ";
			  }
			  
			  s += "\n";
			  s += "-------------\n";
		  }
		  return s;
	  }

}
