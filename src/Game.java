import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * The core functionality of the game. It stores the board and moves of the
 * player and computer. Known Limitation: It is designed to not lose, not win.
 */
public class Game {
	int size;// size of the board, currently only supports 3X3
	int row;// Players choice of row
	int col;// Players choice of col
	int score;
	Square[][] board;
	String playerMove;
	String computerMove;
	Scanner scnr;
	ArrayList<Square> squaresOfOne;// An array low one-point squares
	Random r;
	boolean firstMove;
	int movesRemaining;
	boolean won;
	boolean lost;

	public Game() {
		movesRemaining = 9;
		r = new Random();
		size = 3;
		row = 0;
		col = 0;
		score = 0;
		won = false;
		scnr = new Scanner(System.in);
		board = new Square[size][size];
		firstMove = true;
		computerMove = "O";
		playerMove = "X";

		// Create all the necessary squares and add them to the board
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j] = new Square();
			}
		}
	}

	/**
	 * Calculates the points on the board based on the player last move based on
	 * row, column and diagonal and picks the highest. String move: the
	 * characters symbol, right now "X" but could be changed to anything
	 */
	public void calculateComputerMove(String move) {
		// Double check to make there are moves remaining
		if (movesRemaining <= 0)
			return;

		int currentMoveCount = movesRemaining;

		// Creates array of low one-point squares to chose if there is nothing
		// better
		squaresOfOne = new ArrayList<Square>();

		if (firstMove) {
			calculateFirstMove();
			return;
		}

		int currentRow = row;
		int currentCol = col;

		playerMove = move;

		// It will scan through the row the player chose to calculate the score
		// of the squares in that row
		score = 1;// default point is one

		if (currentCol + 1 == size)
			checkEachSquare(row, 0, 0, 1);
		else
			checkEachSquare(row, col + 1, 0, 1);

		// Scans the column to calculate the score
		if (currentMoveCount == movesRemaining) {// don't make another move if
			// one was already made
			score = 1;
			if (currentRow + 1 == size) {
				checkEachSquare(0, col, 1, 0);
			} else {
				checkEachSquare(row + 1, col, 1, 0);
			}
		}

		// Check the right diagonal
		if (currentMoveCount == movesRemaining && isRightDiagonalPossible()
				&& size % 2 != 0) {//if the size changes to 4x4, a diagonal is not possible
			score = 1;
			if (currentRow + 1 == size && currentCol + 1 == size)
				checkEachSquare(0, 0, 1, 1);
			else
				checkEachSquare(row + 1, col + 1, 1, 1);
		}

		// And the left diagonal
		if (currentMoveCount == movesRemaining && isLeftDiagonalPossible()
				&& size % 2 != 0) {
			score = 1;
			if (currentRow + 1 == size && currentCol - 1 < 0){
				checkEachSquare(0, size - 1, 1, -1);
			}
			else
				checkEachSquare(row + 1, col - 1, 1, -1);
		}

		// if an important move has not been made, choose an average square
		if (currentMoveCount == movesRemaining) {
			if (squaresOfOne.size() != 0)
				chooseOneSquare();
			else
				findOpenSquare();

		}
	}

	/**
	 * Since the first few moves make a difference for the rest of the game, the
	 * first move is calculated separately. Mostly it focuses on the corners. If
	 * the first move the player made is a corner it will pick the opposite
	 * corner. Otherwise it will pick a random corner
	 */
	private boolean calculateFirstMove() {
		firstMove = false;
		if (row == 0 && col == 0) {
			board[size - 1][size - 1].value = computerMove;
			movesRemaining--;
			return true;
		}
		if (row == size - 1 && col == size - 1) {
			board[0][0].value = computerMove;
			movesRemaining--;
			return true;
		}
		if (row == 0 && col == size - 1) {
			board[size - 1][0].value = computerMove;
			movesRemaining--;
			return true;
		}
		if (col == size - 1 && row == 0) {
			board[0][size - 1].value = computerMove;
			movesRemaining--;
			return true;
		}
		squaresOfOne.add(board[0][0]);
		squaresOfOne.add(board[size - 1][0]);
		squaresOfOne.add(board[0][size - 1]);
		squaresOfOne.add(board[size - 1][size - 1]);
		chooseOneSquare();

		return false;

	}

	/**
	 * If there are not high priority square, pick a random low priority square
	 */
	private void chooseOneSquare() {
		int i = r.nextInt(squaresOfOne.size());
		Square s = squaresOfOne.get(i);
		if (s.value == "") {
			s.value = computerMove;
			movesRemaining--;
			checkifPlayerWon(computerMove, row, col);
		} else
			System.out.println("Problem");

	}

	/**
	 * A recursive method that will calculate the score of the row as it adds to
	 * the stack and then set the score for each individual square as it removes
	 * from the stack.
	 * currentRow: the row the user selected
	 * currentCol: the column the user selected
	 * rowIncrement: what the row is incremented by in each recursive call
	 * colIncrement: what the col is incremented by
	 */
	private void checkEachSquare(int currentRow, int currentCol,
			int rowIncrement, int colIncrement) {
		//if the currentRow equals the row it means we looped around the
		//board and we can stop
		if (rowIncrement != 0 && currentRow == row) {
			return;
		}
		if (colIncrement != 0 && currentCol == col) {
			return;
		}
		calculateScore(currentRow, currentCol);
		
		int nextRow = currentRow;
		int nextCol = currentCol;
		
		//calculate the next square, may need to loop around 
		if (rowIncrement != 0 && nextRow + 1 == size) {
			nextRow = 0;
		} else {
			nextRow = nextRow + rowIncrement;
		}
		if (colIncrement != 0) {
			if (nextCol + colIncrement == size) {
				nextCol = 0;
			} else if (nextCol + colIncrement < 0) {
				nextCol = size - 1;
			} else {
				nextCol = nextCol + colIncrement;
			}
		}

		checkEachSquare(nextRow, nextCol, rowIncrement, colIncrement);

		setScore(currentRow, currentCol);
	}


	/**
	 * Calculates whether or not the left diagonal is possible
	 */
	private boolean isLeftDiagonalPossible() {
		if (row + col == size - 1)
			return true;
		return false;
	}

	/**
	 * Calculates whether or not the right diagonal is possible
	 */
	private boolean isRightDiagonalPossible() {
		if (row == col)
			return true;
		return false;
	}


	/**
	 * Calculates the score. If the current square's value is the same as the
	 * players, it means the player has two in this row, column or diagonal and
	 * the score should be higher. If the current square's value is the same as
	 * the computers in means the player already can't win in this row, column,
	 * or diagonal and the score is lower
	 */
	private void calculateScore(int currentRow, int currentCol) {

		if (board[currentRow][currentCol].value.equals(playerMove)) {
			score = 2;

		} else if (board[currentRow][currentCol].value.equals(computerMove)) {
			score = -1;
		}
	}

	/**
	 * Set the score for a square. Also will add the square to the low priority
	 * list if its low or make a move if its score is high enough
	 */
	private void setScore(int row, int col) {
		if (score == 2 && board[row][col].value.equals("")) {
			board[row][col].value = computerMove;
			movesRemaining--;
			checkifPlayerWon(computerMove, row, col);
		} else if (board[row][col].value.equals("") && score == 1) {
			board[row][col].score = score;
			squaresOfOne.add(board[row][col]);
		}
	}

	/**
	 * Get the move from the user and set row and col
	 */
	public void getPlayerMove() {
		boolean valid = false;
		boolean validRow=false;
		boolean validCol=false;
		while (!valid) {
			valid = true;
			while (!validRow) {
				validRow = true;
				System.out.println("Which row (0-2): ");
				row=checkInput();
				if (row==-1){
					validRow=false;
				}
			}
			while (!validCol){
				validCol=true;
				System.out.println("Which column (0-2): ");
				col=checkInput();
				if (col==-1){
					validCol=false;
				}
			}
			if (!board[row][col].value.equals("")){
				scnr.nextLine();
				System.out.println("Invalid choice.");
				valid=false;
				validRow=false;
				validCol=false;
			}
		
		}
		board[row][col].value = playerMove;
		board[row][col].score = 0;
		movesRemaining--;
		checkifPlayerWon(playerMove, row, col);

	}
	/**
	 * Checks the user input to make sure everything is a-okay
	 */
	private int checkInput(){
		int input=0;
		if (scnr.hasNextInt()) {
			input = scnr.nextInt();
			if (input > 2 || input < 0) {
				System.out.println("Invalid choice.");
				return -1;
			}
		}
		else{
			scnr.nextLine();
			System.out.println("Invalid choice.");
			return -1;
		}
		return input;
	}

	/**
	 * Scans through the board to see if the player won
	 */
	private void checkifPlayerWon(String playerMove, int row, int col) {

		// only possible if the player made 3 moves
		if (movesRemaining > 5)
			return;

		// check the row
		won = true;
		for (int i = 0; i < size; i++) {
			if (!board[i][col].value.equals(playerMove)) {
				won = false;
				i = size;
			}
		}
		if (isWon(playerMove)) {
			return;
		}
		// check the column
		won = true;
		for (int i = 0; i < size; i++) {
			if (!board[row][i].value.equals(playerMove)) {
				won = false;
				i = size;
			}
		}
		if (isWon(playerMove)) {
			return;
		}

		// right diagonal
		if (isRightDiagonalPossible()) {
			won = true;
			for (int i = 0; i < size; i++) {
				if (!board[i][i].value.equals(playerMove)) {
					won = false;
					i = size;
				}
			}
			if (isWon(playerMove)) {
				return;
			}
		}

		// And left diagonal
		if (isLeftDiagonalPossible()) {
			won = true;
			for (int i = 0; i < size; i++) {
				if (!board[i][size - i - 1].value.equals(playerMove)) {
					won = false;
					i = size;
				}
			}
			if (isWon(playerMove)) {
				return;
			}
		}

	}

	private boolean isWon(String move) {
		if (won) {
			movesRemaining = 0;
			if (move.equals(computerMove)) {
				lost = true;
				won = false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Towards the end of the game, the board is full and its easiest just to
	 * find an open square when the player can no longer win
	 */
	private void findOpenSquare() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j].value.equals("")) {
					board[i][j].value = computerMove;
					checkifPlayerWon(computerMove, i, j);
					i = size;
					j = size;
				}
			}
		}
		movesRemaining--;
	}

	/**
	 * A useful method for debugging. It will display the score instead of the
	 * value of the squares in the board
	 */
	public void displayScoreBoard() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j].score == 0) {
					System.out.print("_");
				} else {
					System.out.print(board[i][j].score);
				}
				if (j != 2) {
					System.out.print("|");
				}
			}
			System.out.println();
		}
	}

	/**
	 * Displays the board
	 */
	public void displayBoard() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j].value.equals("")) {
					System.out.print("_");
				} else {
					System.out.print(board[i][j].value);
				}
				if (j != 2) {
					System.out.print("|");
				}
			}
			System.out.println();
		}

	}

}
