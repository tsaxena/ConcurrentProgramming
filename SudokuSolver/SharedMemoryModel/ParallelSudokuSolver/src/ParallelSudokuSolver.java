import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;


public class ParallelSudokuSolver extends SerialSudokuSolver{

	ParallelSudokuSolver(String name){
		super(name);
	}
	
	ParallelSudokuSolver(ParallelSudokuSolver orig, SudokuCell new_cell){
		super(orig, new_cell);
	}
	
	ArrayList<ParallelSudokuSolver> doParallelSearch(){
		
		ArrayList<ParallelSudokuSolver> puzzle_list = new ArrayList<ParallelSudokuSolver>();
		
		SudokuCell candidate_cell = this.pickSearchCandidate();
		System.out.println("Candidate Cell: " + candidate_cell.getIndex());
					
		ArrayList<Integer> all_possible_values = candidate_cell.getAllPossibleValueInts();//for each possible value for candidate
		for (int i=0; i< all_possible_values.size(); i++){
			int mask = 1 << (all_possible_values.get(i) - 1);
			if( candidate_cell.getAllPossibleValueBits() != mask ){
				System.out.println("Going to try value: "+ all_possible_values.get(i)+ " mask value" + mask);
							
				//make copy of the puzzle	
				SudokuCell new_cell = new SudokuCell(true, 1, mask, candidate_cell.getIndex()); 
				ParallelSudokuSolver child = new ParallelSudokuSolver(this, new_cell);
										
				puzzle_list.add(child);
				System.out.println("Push a child");
			}
		}
		return puzzle_list;
	}
}
			



