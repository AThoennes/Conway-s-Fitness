import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Conway's Game of Life
 * 
 * @author Alex Thoennes
 * 
 * Rules:
 * 1) Any live cell with 2 or 3 live neighbors stays alive. (stable)
 * 2) Any live cell with more than 3 or less than 2 live neighbors dies. (over population/ under population)
 * 3) Any dead cell with exactly 3 live neighbors comes alive. (re-population)
 * 
 * This version utilizes the dead border approach.
 * 
 */
public class LifeMain
{
	public static void main(String[] args) throws IOException 
	{
		final int ROWS = 32;
		final int COLUMNS = 32;
		
		String design1 = "Design1.txt";
		String design2 = "Design2.txt";
		String design3 = "Design3.txt";

		// create new game of life  with a 32x32 board
		GameOfLife g1 = new GameOfLife(ROWS, COLUMNS);

		// pass in the number of generations you want to run and the design
		float firstFitness = g1.fitness(1000, design1, rows(design1));

		System.out.println("Fitness of first design: " + firstFitness);

		float secondFitness = g1.fitness(1000, design2, rows(design2));

		System.out.println("Fitness of second design: " + secondFitness);
		
		float thirdFitness = g1.fitness(1000, design3, rows(design3));

		System.out.println("Fitness of third design: " + thirdFitness);
	}

	/**
	 * Counts the number of rows in the compact textFile
	 * to be used in the creation of the game boards.
	 * 
	 * @return rows
	 * @throws IOException
	 */
	private static int rows(String fileName) throws IOException
	{
		int rows = 0;

		BufferedReader buffer = new BufferedReader(new FileReader(fileName));

		while (buffer.ready())
		{
			// read the next line and add 1 to rows
			buffer.readLine();
			rows ++;
		}

		// close the buffer
		buffer.close();

		return rows;
	}
}