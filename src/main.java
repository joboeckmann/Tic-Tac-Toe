import java.util.Scanner;


public class main {
	
	/**
	 * The main method of the tic tac toe game
	 */
	public static void main(String[] args) {
		
		System.out.println("Welcome to tic tac toe!");
		
		Game g=new Game();
		
		while (g.movesRemaining>0){
			g.displayBoard();
			g.getPlayerMove();
			g.calculateComputerMove("X");
			System.out.println();
		}
		//Display the final board after the game is over
		g.displayBoard();
		System.out.println();
		
		if (g.won){
			System.out.println("Congratulations! You won!");
		}
		else if (g.lost){
			System.out.println("Sorry. You lost!");
		}
		else{
			System.out.println("Good try!");
		}
	}





}
