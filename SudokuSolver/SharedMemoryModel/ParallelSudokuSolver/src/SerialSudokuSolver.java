import java.io.IOException;
import java.util.*;




public class SerialSudokuSolver{

		int size;
		SudokuCell[]	puzzle;
		
		
		//Initialize an unsolved Sudoku Grid and other data structures needed to solve it.
		SerialSudokuSolver(String input_puzzle_file){
			
			/* 
			 * READ THE PUZZLE FROM THE FILE
			 */
			SudokuParser sudoku_parser = new SudokuParser();
			try {
				ArrayList<String> string_puzzle =  sudoku_parser.ReadSudokuFromFile(input_puzzle_file);
				System.out.println(" Size of the puzzle: " + string_puzzle.get(0));
				
				/*
				 * GET THE SIZE OF THE PUZZLE
				 */
				this.size = Integer.parseInt(string_puzzle.get(0));
				//Create uninstantiated references to SudokuCell;
				this.puzzle = new SudokuCell[this.size * this.size];
				
				/*
				 * INITIALIZE THE PUZZLE LIST 
				 */
				for(int i = 1 ; i <= this.size * this.size; i++){
					int cell_value = Integer.parseInt(string_puzzle.get(i));
					if (cell_value == 0)
					{
						/*
						 * set all bits from 0...size-1 as 1
						 */ 
						int possible_values  = (1 << size) - 1;
						puzzle[i-1] = new SudokuCell(false, size, possible_values, i -1); 
					}
					else
					{
						/* set just one bit according to read cell value
						 * 
						 */
						int possible_values = 1 << (cell_value - 1);
						puzzle[i-1] = new SudokuCell(true, 1, possible_values, i-1); 
					}
					
					
				}
				/*
				* INITIALIZE THE PEER LIST WITH INDEX OF THE PEERS
				*/
				generatePeerLists();
					
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		SerialSudokuSolver(SerialSudokuSolver orig, SudokuCell new_cell){
			this.size = orig.size;
			//this.num_singletons = orig.countSingletons() + 1;
	
			this.puzzle = new SudokuCell[orig.size * orig.size];
			for(int i = 0; i < orig.size * orig.size; i++){
				if(i == new_cell.getIndex() ){
					this.puzzle[i] = new_cell;
					System.out.println(" Replace the old cell at index "+ new_cell.getIndex());
					System.out.println(" value " + this.puzzle[i].getAllPossibleValueBits());
				}else{
					//create an exact copy of the orig cell
					this.puzzle[i] = new SudokuCell(orig.puzzle[i]);
				}
			}
			generatePeerLists();
			System.out.println("No of singletons in new Instance" + this.countSingletons());
		}
		
		void generatePeerLists(){
			/* 
			 * 	initialize the peer list based on the size of the puzzle
			 */
			for(int index = 0; index < this.size*this.size; index++){
				
				/*
				 * CREATE THE PEER LIST OF A CELL
				 */
				ArrayList<SudokuCell> peer_list = new ArrayList<SudokuCell>();
				
				int col = index%size;
				int row = (int)Math.floor((index - col)/size);
				int boxSize   = (int) Math.sqrt(size);
				int num_peers = (size-1)*2 + (int)((boxSize-1)*(boxSize-1));

				/*
				 * peers in the same row
				 */
				for (int i=0; i < size; i++){
					int elem = row*size + i;
					if (elem != row*size + col)
					{
						peer_list.add(this.puzzle[elem]);
					}
				}

				/*
				* peers in same column
				*/
				for (int i=0; i < size; i++) {
					int elem = i*size + col;
					if (elem != row*size + col) {
						peer_list.add(this.puzzle[elem]);
					}
				}

				/*
				 * peers in the same box
				 */
				int boxX = (int) Math.floor(row / boxSize);
				int boxY = (int) Math.floor(col / boxSize);
						
				int currBoxOrigin = (int) (boxX * boxSize * size + boxY*boxSize);

				//currRowBox: Current Row wrt to a box. For a 9x9 puzzle, currRowBox = 0 to 2
				//currBox: Points to the first item for a given box
				//Iterates over the elements in a box (3x3 box for a 9x9 puzzle)

				for (int boxRow=0; boxRow < boxSize; boxRow++)
				{
					for (int boxCol=0; boxCol < boxSize; boxCol++)
					{
						int absPeerIndex = currBoxOrigin + boxRow*size + boxCol;

						int peerCol = absPeerIndex %size;
						int peerRow = (int)Math.floor((absPeerIndex - peerCol) /size);
						//dont add itself, same column or same row
						if (absPeerIndex != row*size + col &&  (peerCol != col) && peerRow != row)
						{
							peer_list.add(this.puzzle[absPeerIndex]);
						}
					}
				}
				/*
				 * SET THE PEER LIST OF THE CELL
				 */
				this.puzzle[index].setPeerList(peer_list);
				//this.puzzle[index].printPeerList();
			}
		}
		
		
		void printNumOfPossibilities(){
			// for each non-singleton cell:
			for (int i=0; i< this.size * this.size; ++i)
			{
				System.out.println(this.puzzle[i].getNumberOfPossibilities());
			}
		}
		
		
		void doConstraintPropagation(){
			System.out.println("Constraint Propagation Begin: Num of singletons" + countSingletons());
			boolean change;
			int round = 0;
			//Scanner in = new Scanner(System.in);
			do
			{
				// nothing changed = NOCHANGE = false
				change = false;
				//in.nextInt();
				System.out.println("**************Round "+ round + "*********");
				// for each non-singleton cell:
				for (int i=0; i< this.size * this.size; ++i)
				{
					if(!this.puzzle[i].isSingleton()){
						boolean is_pruned = this.puzzle[i].prunePossibleValues();
						change = change || is_pruned;
					}
					System.out.println(" ");
					System.out.println("INDEX "+ this.puzzle[i].getIndex()+" possible values: ");
					this.puzzle[i].printAllPossibleValueInts();
				}
				round++;
			} while (change); 
			System.out.println("Constraint Propagation End: Number of Singletons" + countSingletons());
		}
		
		
		SudokuCell pickSearchCandidate(){
			// Pick a cell with least number of possibilities.
			int minIndex = 0;
			int minPossibilities = this.size;
			
			for (int x = 0; x < this.size ; x++){
				for(int y = 0; y < this.size; y++){
					SudokuCell sc = this.puzzle[x * this.size + y];
					int np = sc.getNumberOfPossibilities();
					if(np < minPossibilities && np > 1){
						minPossibilities = np;
						minIndex = x * this.size + y;
					}
				}
			}

			System.out.println("SearchCandidate picked cell at index:" + minIndex);
			return (this.puzzle[minIndex]);
			
		}
		
		
		boolean doSearch(){
			
			LinkedList<SerialSudokuSolver> puzzle_list = new LinkedList<SerialSudokuSolver>();
			boolean solved = false;
			puzzle_list.push(this);
			
			SerialSudokuSolver current = null;
			
			//Scanner in = new Scanner(System.in);
			while(!solved){
				
				System.out.println("Current Length of the puzzle list: " + puzzle_list.size());
				//in.nextInt();
				if (puzzle_list.size() > 0) {
					current = puzzle_list.poll();
				}
				else {
					System.err.println("Puzzle List is empty!! Yet did not reach a soln. Quiting in vain.\n");
					//exit(-1);
					return false;
				}
				
				
				if (current != this) {
					//The very first puzzle has already been constraint-propagated!
					current.doConstraintPropagation();
				}
						
				//Does current need more searching?			
				if (current.countSingletons() == size*size) // i.e. done!
				{
					System.out.println("SOLUTION FOUND");
					if (current.verifySolution())
					{
						solved = true;
						System.out.println("VALID SOLUTION FOUND");
						if (current != this) {
							System.out.println("GO BACK");
							this.puzzle = current.puzzle;
							return true;//no more dangling pointer (and therefore, double dealloc)!
						}
					}
					else
					{
						System.out.println("INVALID SOLUTION,...NEED TO POP");
						continue; //EXIT(1); in case of multiple threads
					}
				}else if (current.countSingletons() > size*size) // i.e. something has gone horribly wrong
				{
					System.err.println("ERROR: WTH! number of singletons in the puzzle is more than ");
					//assert(current.num_singletons <= size*size);
				} else { // i.e. num_singletons < size*size ==> therfore, more searching is needed.
				
					SudokuCell candidate_cell = current.pickSearchCandidate();
					System.out.println("Candidate Cell: " + candidate_cell.getIndex());
						
					ArrayList<Integer> all_possible_values = candidate_cell.getAllPossibleValueInts();//for each possible value for candidate
					for (int i=0; i< all_possible_values.size(); i++){
						int mask = 1 << (all_possible_values.get(i) - 1);
						if( candidate_cell.getAllPossibleValueBits() != mask ){
							System.out.println("Going to try value: "+ all_possible_values.get(i)+ " mask value" + mask);
								
							//make copy of the puzzle	
							SudokuCell new_cell = new SudokuCell(true, 1, mask, candidate_cell.getIndex()); 
							SerialSudokuSolver child = new SerialSudokuSolver(current, new_cell);
											
							puzzle_list.push(child);
							System.out.println("Push a child");
						}
					}
				}
				
				
			}
			
			System.out.println("doSearch() finished by trying ");
			return true;
		}
		
		
		int countSingletons(){
			int num_singleton = 0;
			for(int i = 0; i < this.size * this.size; i++){
				num_singleton = num_singleton + (this.puzzle[i].isSingleton() ? 1: 0);
			}
			//System.out.println("how many singletons" +num_singleton);
			return (num_singleton);
		}
		
		boolean isSolution(){
			return (countSingletons() == size*size);
		}
		
		
		boolean verifySolution(){
			SudokuValidator sv = new SudokuValidator(this.puzzle, this.size);
			return sv.validateSudokuPuzzle();
		}
		
		
		boolean solve(){
			// Phase 1: This is called "constraint propagation" or "markup" phase
			
			doConstraintPropagation();;
			//printNumOfPossibilities() ;
			boolean retVal = true;
			
			// Phase 2: Search. i.e. we face a situation where we can't solve unless we try something
			// and see if a contradiction happens down the road.
			// Only if the puzzle is not already solved, then we do searching.	
			if (!isSolution())
			{
				System.err.println("doConstraintPropagation was not enough!\n");
				retVal = doSearch();
			}

			return retVal;
		}
		
		//Print puzzle to screen
		void printPuzzleToFile()
		{
			// Read the puzzle
			SudokuParser sudoku_parser = new SudokuParser();	
			sudoku_parser.WriteSudokuToFile("Solved.txt", puzzle, size);
			//System.out.println(this.puzzle[0].getFinalValue());

		}
		
		//Print puzzle to screen
		void printPuzzleToScreen()
		{
			// Read the puzzle
			SudokuParser sudoku_parser = new SudokuParser();	
			sudoku_parser.WriteSudokuToScreen(puzzle, size);
			//System.out.println(this.puzzle[0].getFinalValue());

		}
		
		
		/*public static void main(String[] arg){
			System.out.print("Serial Sudoku Solver");

			SerialSudokuSolver sudoku = new SerialSudokuSolver("SudokuProblem.txt");
			boolean retValSolve = sudoku.solve();
			
			if(retValSolve){ // solution found
				//verify the solution
				if (sudoku.verifySolution()){
					System.err.println("Valid Solution...printing to file..");
					sudoku.printPuzzleToFile();//print solution to the file
				}else{
					System.out.println("Invalid Solution");
				}
				//sudoku
				
			}
		
		}*/
}
