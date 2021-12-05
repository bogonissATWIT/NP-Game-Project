import java.util.Scanner;
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
			if(board[spot1][spot2].equals("_")) {
				board[spot1][spot2] = icon;
				return true;
			}
		}
		catch(Exception ex){
			
		}
		return false;
	}
	
	public TicTacToe(){
		for(int i = 0; i < 3;i++) {
			for(int j = 0; j < 3;j++) {
				board[i][j] = "_";
			}
		}
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////
//checkRows(int[][] A): Checks the rows of the board for a win
////////////////////////////////////////////////////////////////////////////////////////////////////
	public  boolean checkRows(String[][] A) {
		for (int i = 0; i < A.length; i++) {
			if ( (A[i][0] == A [i][1]) && (A[i][1] == A[i][2]) && A[i][0] != "_" ) {
				return true;
			}
		}
		return false;
	}
////////////////////////////////////////////////////////////////////////////////////////////////////
//checkCols(int[][] A): Checks the columns of the board for a win
////////////////////////////////////////////////////////////////////////////////////////////////////	
	public  boolean checkCols(String[][] A) {
		for (int i = 0; i < A[0].length; i++) {
			if ( (A[0][i] == A[1][i]) && (A[1][1] == A[1][i]) && A[0][i] != "_" ) {
				return true;
			}
		}
		return false;
	}
////////////////////////////////////////////////////////////////////////////////////////////////////
//checkDiags(int[][] A): Checks the diagonals of the board for a win
////////////////////////////////////////////////////////////////////////////////////////////////////
	public  boolean checkDiags(String[][] A) {
		if( (A[0][0]==A[1][1]) && (A[1][1]==A[2][2]) && A[0][0] !="_") {
			return true;
		}
		else if ( (A[0][2]==A[1][1]) && (A[1][1]==A[2][0]) && A[1][1] !="_") {
			return true;
		}
		else {
			return false;
		}
	}
////////////////////////////////////////////////////////////////////////////////////////////////////
//checkHit(int[][] A): Checks if there is a 3 in a row on the board
////////////////////////////////////////////////////////////////////////////////////////////////////	
	private boolean check(String a,String b,String c) {
		if(winnerCheck) {
			return winner;
		}
		String result = a+b+c;
		if(result.equals("xxx")||result.equals("ooo")) {
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
//isFree(int[][] A, int row, int col): Checks if the space the player chose is free
////////////////////////////////////////////////////////////////////////////////////////////////////

	  public static boolean isFree(int[][] A, int row, int col) {
		  if(A[row][col] == 0) {
			  return true;
		  }
		  else {
			  return false;
		  }
	  }

	  
////////////////////////////////////////////////////////////////////////////////////////////////////
//printBoard(int[][] A): Prints game board as is 
////////////////////////////////////////////////////////////////////////////////////////////////////
	  
	  public String printBoard() {
		  String s = "";
		  s+="-------------\n";
		  for (int i = 0; i < 3; i++) {
			  s+="| ";
			  
			  for (int j = 0; j < 3; j++) {
				  s+=board[i][j] + " | ";
			  }
			  
			  s+="\n";
			  s+="-------------\n";
		  }
		  return s;
	  }
	
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////
//main(String[] args): Plays the game with two players, determines a winner or a draw
////////////////////////////////////////////////////////////////////////////////////////////////////

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		int [][] grid = new int[3][3];
//		int foundWinner = 0;
//		
//		printBoard(grid);
//		
//		int i = 0;
//		while (i < 9) {
//			if (i % 2 == 0) {
//				if (getWinner("Player 1 turn", grid, 1)) {
//					foundWinner = 1;
//					System.out.println("Player 1 Wins!");
//					break;
//				}
//				printBoard(grid);
//				System.out.println();
//			}
//			else {
//				if (getWinner("Player 2 turn", grid, 2)) {
//					foundWinner = 1;
//					System.out.println("Player 2 Wins!");
//					break;
//				}
//				printBoard(grid);
//				System.out.println();
//			}
//			i++;
//		}
//		if (foundWinner == 0) {
//			System.out.println("Draw!");
//		}
//		
//		
//	}

}
