
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SudokuParser {
	/**
	 * Reads a Sudoku puzzle from a file into a 1D array.
	 * The function dynamically allocates memory for the array.
	 * @param fileName Path of the input file.
	 * @param size Size of the puzzle (9 for a 9x9 puzzle). Updated by function.
	 * @return 1D array of type uint and length of size containing the puzzle.
	 */
	ArrayList<String> ReadSudokuFromFile(String fileName) throws IOException
	{
		FileReader fr = null;
		BufferedReader br = null; 
		int size = 0;
		ArrayList<String> puzzle = new ArrayList<String>();
		try {
			fr = new FileReader(fileName); 
			br = new BufferedReader(fr);
			
			//read the size of the puzzle
			String sCurrentLine = br.readLine();
			if(sCurrentLine != null){
				System.out.println("Size of the puzzle" + sCurrentLine);
				puzzle.add(sCurrentLine);
			}
			
			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
				String delims = "[ ]+";
				String[] numbers = sCurrentLine.split(delims);
				puzzle.addAll(Arrays.asList(numbers));
			}
			//char[] cbuf = new char[size * size + 1];
		    //br.read(cbuf);  
		    //System.out.println("do something");
		    br.close();
		    return puzzle;
	    }
	    finally {
	      if (fr != null) {
	        fr.close();
	      }
	    }
	}
	
	void WriteSudokuToFile(String fileName, SudokuCell[] puzzle, int size)
	{
		
		FileWriter fileWriter = null;
        try {
            File newTextFile = new File(fileName);
            fileWriter = new FileWriter(newTextFile);
            
            StringBuilder string_puzzle = new StringBuilder();
            for(int i = 0 ; i < size*size; i++){
    
            	if(i % size == 0){
            		string_puzzle.append("\n");
            	}
            	string_puzzle.append(Integer.toString(puzzle[i].getSolution()));
            	string_puzzle.append(" ");
            }
            fileWriter.write(string_puzzle.toString());
            fileWriter.close();
        } catch (IOException ex) {
            //Logger.getLogger(WriteStringToFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {
                //Logger.getLogger(WriteStringToFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
	}

	void WriteSudokuToScreen(SudokuCell[] puzzle, int size)
	{       
        StringBuilder string_puzzle = new StringBuilder();
        for(int i = 0 ; i < size*size; i++){
        	if(i % size == 0){
        		string_puzzle.append("\n");
        	}
        	string_puzzle.append(Integer.toString(puzzle[i].getSolution()));
        	string_puzzle.append(" ");
        }
	}   
}