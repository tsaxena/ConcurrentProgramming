import java.util.ArrayList;

class SudokuCell{
			private int index_;
			private ArrayList<SudokuCell>	peer_list;
			private boolean is_singleton;
			private int 	num_possible;
			private int 	possibe_values;	

			SudokuCell(boolean is_singleton, int num_possible, int possible, int index){
				this.index_ = index;
				this.is_singleton    = is_singleton;
				this.num_possible    = num_possible;
				this.possibe_values  = possible;
				this.peer_list		 = new ArrayList<SudokuCell>();
			}
			
			SudokuCell(SudokuCell orig_cell){
				this.index_ 		= orig_cell.index_;
				this.is_singleton   = orig_cell.is_singleton;
				this.num_possible    = orig_cell.num_possible;
				this.possibe_values  = orig_cell.possibe_values;
				this.peer_list		 = new ArrayList<SudokuCell>();
			}
			
			
			
			int getNumberOfPossibilities(){
				/*if(this.is_singleton == true){
					return this.possibe_values;
				}
				throw new IllegalArgumentException("Unsolved");*/
				//hack for now
				return num_possible;
			}
			
			boolean isSingleton(){
				return this.is_singleton;
			}
			void setPeerList(ArrayList<SudokuCell> s){
				this.peer_list = s;
			}
			
			
			
			
			int getAllPossibleValueBits(){
				return this.possibe_values;
			}
		
			
			
			ArrayList<Integer> getAllPossibleValueInts() {
				ArrayList<Integer> all_possible_ints = new ArrayList<Integer>();
				int value = this.possibe_values;
				//System.out.println("No of possible value: "+ this.num_possible);
			    int index = 1;
			    while (value > 0) {           // until all bits are zero
			        if ((value & 1) == 1){     // check lower bit
			            all_possible_ints.add(index);
			        	System.out.print(index + "   ");
			        }
			        value >>= 1;              // shift bits, removing lower bit
			        index++;
			    }
			    
			    return all_possible_ints;
			}
			
			int getSolution() {
				int value = this.possibe_values;
			    int index = 1;
			    while (value > 0) {           // until all bits are zero
			        if ((value & 1) == 1){     // check lower bit
			            System.out.print(" " + index);
			            break;
			        }
			        value >>= 1;              // shift bits, removing lower bit
			        index++;
			    }
			    
			    return index;
			}
			
			boolean prunePossibleValues(){
				boolean retVal = false;
				
				if (this.is_singleton)
					return false;
				
				//if more than one possible values
				int step2mask = 0;

				// for each of its peers:
				// if peer is singleton, remove it from my possibility list
				//System.out.println("Possible Values for Index " + this.index_);
				
				for(int peer_index = 0; peer_index < this.peer_list.size(); ++peer_index)
				{
					SudokuCell sc = this.peer_list.get(peer_index);
					{
						// if it's the first time we're eliminating this possibility
						// decrement num_possible for this cell
						//System.out.println("Consider value of peer with index " + sc.getIndex());
						
						if (sc.isSingleton() && (this.possibe_values & sc.getAllPossibleValueBits()) != 0)
						{
							//System.out.println("remove value of index " + sc.getIndex());
							--this.num_possible;
							this.possibe_values = this.possibe_values & ~sc.getAllPossibleValueBits();
							retVal = retVal | true;
						}
					}
					step2mask = sc.getAllPossibleValueBits()| step2mask;
				}
				
				//System.out.println(this.num_possible);
				//bitCount(this.possibe_values);

				/*step2mask = (~step2mask) & ((1 << size) - 1);
				
				for (int j=0; j< size; j++)
				{
					int mask = 1 << j;
					if (step2mask == mask)
					{
						this.possibe_values = mask;
						this.num_possible = 1;
						retVal = true;
					}
				}*/
	
				// if the cell became a singleton, add it to the grid
				if (this.num_possible == 1)
				{
					this.is_singleton = true;
				}
				return retVal;
			}
			

			int getIndex(){
				return this.index_;
			}
			
			void printAllPossibleValueInts() {
				int value = this.possibe_values;
				//System.out.println("No of possible value: "+ this.num_possible);
			    int index = 1;
			    while (value > 0) {           // until all bits are zero
			        if ((value & 1) == 1){     // check lower bit
			        	System.out.print(index + "   ");
			        }
			        value >>= 1;              // shift bits, removing lower bit
			        index++;
			    }
			}
			
			
			void printPeerList(){
				System.out.println("Peer list of index "+ this.index_+ " of size " + this.peer_list.size());
				for(int i = 0; i < this.peer_list.size(); i++){
					SudokuCell sc = this.peer_list.get(i);
					System.out.println(" "+ sc.getIndex());
				}
			}
}