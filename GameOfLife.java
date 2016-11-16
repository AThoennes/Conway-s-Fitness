import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Alex Thoennes
 */
public class GameOfLife 
{
	// these are the two game boards the program will use to create each other
	// I look at one board and use those cells to create the next board
	int [][] board1;
	int [][] board2;

	// number of generations to perform
	int generations;
	
	// number of live cells at the beginning and the end
	float startLiveCells;
	float endLiveCells;

	// booleans used to help determine which board to modify and which board to use
	private boolean b1; // board1
	private boolean b2; // board2

	/**
	 * Constructor for the game of life that initializes 
	 * the game boards, booleans, generation counter,
	 * and calls the setUp() method.
	 * 
	 * @param rows
	 * @param columns
	 * @throws IOException
	 */
	public GameOfLife(int rows, int columns)
	{
		// fill boards with proper amount of rows and columns
		// in this case, they happen to both be 32 X 32 arrays
		board1 = new int [rows][columns];
		board2 = new int [rows][columns];
	}

	private void fillBoard(int [][] array)
	{
		for (int i = 0; i < array.length; i ++)
		{
			for (int j = 0; j < array.length; j ++)
			{
				array [i][j] = 0;
			}
		}
	}

	/**
	 * This method looks at the text file and 
	 * 
	 * @throws IOException
	 */
	private void setUp(int rows, String fileName) throws IOException
	{
		// want to start on board 1
		b1 = true;
		b2 = false;

		// generation counter
		generations = 1;
		
		// fill the current board with 0's
		fillBoard(currentBoard());
		
		// new buffer
		BufferedReader buffer = new BufferedReader(new FileReader(fileName));

		// start at the first line because the 0th line will always be dead
		int lineNum = 0;

		while (buffer.ready())
		{
			if (lineNum < rows)
			{
				// take the line you just read and split it up into an array
				String [] array = buffer.readLine().split("");

				// then take each element in that array and convert it from
				// a string to a hex, then finally to a binary string. You do this
				// because when you first read something in, it is a string. 
				for (int i = 0; i < array.length; i ++)
				{
					// convert to hex
					int hex = Integer.parseInt(array[i], 16);
					// convert to binary string
					String binary = Integer.toBinaryString(hex);

					// because some 0's may be lost when converting, this bit of code
					// adds the 0's that were lost back in
					if (binary.length() < 4)
					{
						// find the difference
						int diff = 4 - binary.length();

						// then add that number of 0's to the front of the string
						for (int q = 0; q < diff; q ++)
						{
							binary = "0" + binary;
						}
					}

					String [] bin = binary.split("");

					// counter
					int index = 0;

					// used in filling the 8x8 middle box
					int spot = (board1.length / 2) - 4;

					// deals with filling the correct spots in the box
					if (i == 0)
					{
						for (int j = spot; j < spot + 4; j ++)
						{
							board1[spot + lineNum][j] = Integer.valueOf(bin[index]);

							// increment the counter
							index ++;
						}
					}
					else if (i == 1)
					{
						for (int j = spot + 4; j < spot + 8; j ++)
						{
							board1[spot + lineNum][j] = Integer.valueOf(bin[index]);

							// increment the counter
							index ++;
						}
					}
				}

				// mark that you have read this line
				lineNum++;
			}
		}

		// close the buffer
		buffer.close();
	}

	/**
	 * Called by the main class because it does not have access to
	 * either of the game boards.
	 * 
	 * @param iterations
	 * @throws IOException 
	 */
	public void playGame(int iterations)
	{
		// iterations is the number of times you want to perform the run method
		run(iterations);
	}

	/**
	 * This is where the main process of determining whether 
	 * a cell lives or not is done.
	 * 
	 * @param iterations
	 * @param array
	 */
	private void run(int iterations)
	{
		// number of live cells to start
		startLiveCells = countLiveCells(currentBoard());

		// show the initial compact board
		System.out.println("\nInitial compact board: ");
		displayCompactBoard(currentBoard());

		// perform the game until the number of desired generations has been reached
		while (generations < iterations)
		{
			// for every cell
			for (int i = 0; i < currentBoard().length; i ++)
			{
				for (int j = 0; j < currentBoard().length; j ++)
				{
					// reinforces the idea of a dead border (no cells can live on the border, 
					// the other option is to wrap the matrix like a map in a civ game)
					if (atBorder(currentBoard(), i, j))
					{
						// nextBoard() and currentBoard() are dependent on the booleans b1 and b2
						nextBoard()[i][j] = 0;
					}
					else
					{
						// if you've found a live cell
						if (alive(currentBoard(), i, j))
						{
							// check to see if the cell has 2 or 3 live neighbors
							if (checkNeighbors(currentBoard(), i, j) == 2 || checkNeighbors(currentBoard(), i, j) == 3)
							{
								// stay alive if it does
								nextBoard()[i][j] = 1;
							}
							else
							{
								// otherwise it dies
								nextBoard()[i][j] = 0;
							}
						}
						// if you found a dead cell
						else if (!alive(currentBoard(), i, j))
						{
							// count the number of live neighbors
							if (checkNeighbors(currentBoard(), i, j) == 3)
							{
								// if the number is exactly 3, then the current cell
								// comes to life
								nextBoard()[i][j] = 1;
							}
							else
							{
								nextBoard()[i][j] = 0;
							}
						}
					}
				}
			}

			generations ++;

			if (b1)
			{
				// switch to board two and move on to
				// the next generation
				b1 = false;
				b2 = true;
			}
			else if (b2)
			{
				// otherwise switch to board 1 and 
				// move on to the next generation
				b1 = true;
				b2 = false;
			}
		}
		
		// show the final compact board
		System.out.println("\nFinal compact board:");
		displayCompactBoard(currentBoard());
		
		// number of live cells at the end of the game
		endLiveCells = countLiveCells(currentBoard());
	}
	/**
	 * Sets up the game for each design you pass into it. 
	 * The method then runs the game and when the desired
	 * number of generations is met you calculate the fitness
	 * of the design with the algorithm given to use in class.
	 * 
	 * @param iterations
	 * @param design
	 * @param rows
	 * @return
	 * @throws IOException
	 */
	public float fitness(int iterations, String design, int rows) throws IOException
	{
		// use the specified design
		setUp(rows, design);
		
		// run the specified number of generations
		run(iterations);
		
		// calculate the fitness
		return endLiveCells / (2 * startLiveCells);
	}

	/**
	 * Looks at the two boolean values and determines 
	 * which board is the current board
	 * 
	 * @return
	 */
	private int [][] currentBoard()
	{
		if (b1)
		{
			// on board1
			return board1;
		}
		else
		{
			// on board2
			return board2;
		}
	}

	/**
	 * Returns the board that you want to modify based
	 * off the cells in your current board
	 * 
	 * @return
	 */
	private int [][] nextBoard()
	{
		// if you're on board1
		// you want to modify board2
		if (b1)
		{
			return board2;
		}
		else
		{
			// otherwise modify board1
			return board1;
		}
	}

	/**
	 *  This method looks at the cells around your current cell
	 *  to determine how many live neighbors it has
	 * 
	 * @param array
	 * @param i
	 * @param j
	 * @return numAlive
	 */
	private int checkNeighbors(int [][] array, int i , int j)
	{
		// number of live neighboring cells
		int numAlive = 0;

		// use ternary and set the min/maxRow and min/maxCol
		// to the proper values only if they are not in the dead border
		int minRow = (i != 0) ? i - 1 : 0;
		int maxRow = (i != array.length - 1) ? i + 1: 0;

		int minCol = (j != 0) ? j - 1 : 0;
		int maxCol = (j != array.length - 1) ? j + 1: 0;

		// iterates over the neighboring cells and counts how many are alive
		for (int row = minRow; row <= maxRow; row++) 
		{
			for (int column = minCol; column <= maxCol; column++) 
			{
				if (array[row][column] == 1 && !(row == i && column == j)) 
				{
					numAlive++;
				}
			}
		}

		// finally return the number of live cells
		return numAlive;
	}

	/**
	 * Used to check if the current cell is at the border
	 * 
	 * @param array
	 * @param i
	 * @param j
	 * @return
	 */
	private boolean atBorder(int [][] array, int i, int j)
	{
		// check if the cell is at the border
		if (i == array.length - 1 || i == 0 || j == 0 || j == array.length - 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Check if the current cell is alive or dead
	 * 
	 * @param array
	 * @param i
	 * @param j
	 * @return
	 */
	private boolean alive(int [][] array, int i, int j)
	{
		// current cell 
		if (array[i][j] == 0)
		{
			return false;
		}
		else 
		{
			return true;
		}
	}

	/**
	 * counts and returns the number of 
	 * live cells in the board
	 * 
	 * @param array
	 * @return
	 */
	private int countLiveCells(int [][] array)
	{
		int numAlive = 0;
		
		for (int i = 0; i < array.length; i ++)
		{
			for (int j = 0; j < array.length; j ++)
			{
				if (array[i][j] == 1)
				{
					numAlive ++;
				}
			}	
		}
		
		return numAlive;
	}
	
	/**
	 * This method looks at the binary matrix and for every 4
	 * elements it reads in, it is then converted to hexadecimal
	 * and printed to provide a more compact form of output
	 * 
	 * @param array
	 */
	private void displayCompactBoard(int [][] array)
	{
		// num will contain the four elements
		String num = "";

		// for every space in board
		for (int row = 0; row < array.length; row ++)
		{
			for (int column = 0; column < array.length; column ++)
			{
				// add the next element
				num = num + array[row][column];

				// if your length is 4
				if (num.length() == 4)
				{
					// convert it to hexadecimal
					int binary = Integer.parseInt(String.valueOf(num), 2);
					String hex = Integer.toHexString(binary);

					// print out the hex value
					System.out.print(hex.toUpperCase());

					// then reset num
					num = "";
				}
			}
			System.out.println();
		}
	}
}