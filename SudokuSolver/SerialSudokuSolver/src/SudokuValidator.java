
public class SudokuValidator {

	int size = 0;
	SudokuCell[] puzzle; ;
	int errCol ;
	int errRow ;
	
	
	SudokuValidator(SudokuCell [] p, int size){
		this.size = size;
		errCol = 0;
		errRow = 0;
		this.puzzle = p;
	}
	
	boolean validateSudokuPuzzle()
	{
		int mask, maskBit;
		
		System.out.println("Verifying Rows...");
		for (int row=0; row < size; row++)
		{
			//mask: 0x000010110 => For a given row, #s 2, 3 and 5 have been found
			mask = 0;
			for (int col=0; col < size ; col++)
			{		
				if (this.puzzle[row * size + col].getAllPossibleValueBits() == 0) {
					errRow = row;
					errCol = col;
					return (false);
				}

				//maskBit: Current number - 0x000010000 => 5
				maskBit = puzzle[row * size + col].getAllPossibleValueBits();
				//1 << (puzzle[row][col] - 1);

				/*System.out.println("Comparing " + //puzzle[row][col] +
						 " Mask " + mask + " MaskBit " + maskBit
						 + " Ans " + (maskBit & mask));*/

				//if the number didn't occur yet
				if ((maskBit & mask) == 0)
					mask += maskBit;

				//otherwise, validation failure
				else
				{
					errRow = row;
					errCol = col;
					return false;
				}
			}
		}

		System.out.println("Verified! VerifyingColumns...");
		for (int col=0; col < this.size; col++)
		{
			mask = 0;
			for (int row=0; row < this.size; row++)
			{
				maskBit = puzzle[row * size + col].getAllPossibleValueBits();
				//1 << (this.puzzle[row][col] - 1);
				/*System.out.println("Comparing " + //puzzle[row][col] +
						 " Mask " + mask + " MaskBit " + maskBit
						 + " Ans " + (maskBit & mask));*/


				if ((maskBit & mask) == 0)
					mask += maskBit;
				else
				{
					errRow = row;
					errCol = col;
					return false;
				}
			}
		}

		System.out.println("Verified! Verifying boxes...");

		//currRowBox: Current Row wrt to a box. For a 9x9 puzzle, currRowBox = 0 to 2
		//currBox: Points to the first item for a given box
		//int currRowBox, *currBox;

		//For a 9x9 puzzle, the box size is 3
		int boxSize = (int) Math.sqrt(size);
		
		//The first two for-loops jumps to a box (from 0 to 8 for a 9x9 puzzle)
		for (int boxX =0; boxX < boxSize; boxX++)
		{
			for (int boxY=0; boxY < boxSize; boxY++)
			{
				
				//absolution origin of the box
				int currBoxOrigin = (int) (boxX * boxSize * size + boxY*boxSize);
				
				mask = 0;

				//Iterates over the elements in a box (3x3 box for a 9x9 puzzle)
				for (int boxRow=0; boxRow < boxSize; boxRow++)
				{
					for (int boxCol=0; boxCol < boxSize; boxCol++)
					{
						maskBit = this.puzzle[currBoxOrigin + boxRow*size + boxCol].getAllPossibleValueBits();

						/*System.out.println("Comparing " + //puzzle[actualRow+boxRow][actualCol+boxCol] +
								 " Mask " + mask + " MaskBit " + maskBit
								 + " Ans " + (maskBit & mask));*/

						if ((maskBit & mask) == 0)
							mask += maskBit;
						else
						{
							errRow = boxX*boxSize + boxRow;
							errCol = boxY*boxSize + boxCol;
							return false;
						}
					}
				}
			}
		}
		
		System.out.println("All verified");

		return true;
	}
}
