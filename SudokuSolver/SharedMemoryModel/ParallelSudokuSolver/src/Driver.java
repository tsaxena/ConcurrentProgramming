import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.SynchronousQueue;



//A driver for the buffer
class WorkerThread implements Runnable{
	
		private String name_ = null;
	   
		//Time (milliseconds) to sleep between calls to the bounded buffer.
		private int pNap_ = 0;
	   
		//The bounded buffer to use.
		private MonitorBoundedBuffer<ParallelSudokuSolver> bb_ = null;
		
		//The message queue to send message to when done
		private SynchronousQueue<Integer> mq_ = null;
		
		// Thread internal to the solver.
		public Thread me_ = null;

		//constructor
		WorkerThread(String name, int pNap, MonitorBoundedBuffer<ParallelSudokuSolver> bb, SynchronousQueue<Integer> mq){
			this.name_ 	= name;
			this.pNap_ 	= pNap;
			this.bb_ 	= bb;
			this.mq_ 	= mq;
		}

		
		//execute method
		public void run(){
			
			// check if any other thread has already solved the problem
			while(true){
				//sleep
				/*try{
					Thread.sleep(this.pNap_);
				}catch(InterruptedException e){
					System.out.println(this.name_
				               + " interrupted from sleep");
				            return;
				}*/
				System.out.println(this.name_ + " wants to consume");
				//consume
				try{
					
					//If another thread has already reached a valid solution then time to quit.
					//
					ParallelSudokuSolver sudoku = bb_.fetch();
					//
					// Phase 1: This is called "constraint propagation" or "markup" phase
					
					sudoku.doConstraintPropagation();
					
					// Phase 2: Search. i.e. we face a situation where we can't solve unless we try something
					// and see if a contradiction happens down the road.
					// Only if the puzzle is not already solved, then we do searching.	
					if (!sudoku.isSolution())
					{
						System.out.println("doConstraintPropagation was not enough!\n");
						ArrayList<ParallelSudokuSolver> child_list = sudoku.doParallelSearch();
						//add them to the global list
						for(Iterator<ParallelSudokuSolver> i = child_list.iterator(); i.hasNext(); ){
							bb_.deposit(i.next());
						}
					}else{
						//verify the solution
						if (sudoku.verifySolution()){
							//Tell coordinator that thread found a valid solution.send();
							System.out.println("Valid Solution...printing to file..");
							sudoku.printPuzzleToFile();//print solution to the file
							mq_.put(1); //last line...so ok to be blocked
						}else{
							System.out.println("Invalid Solution...Keep Working");
							
						}
					}
					
				}catch(InterruptedException e){
					System.out.println(
							this.name_
				               + " interrupted from fetch");
				            return;
				}		
			}		
		}
		
		public void timeToQuit() { 
			this.me_.interrupt(); 
		}

		//Caller blocks until the thread inside this object terminates.
		public void pauseTilDone() throws InterruptedException { 
			this.me_.join(); 
		}
		
		
		//factory method
		public static WorkerThread newInstance(String name, int pNap, MonitorBoundedBuffer<ParallelSudokuSolver> bb, SynchronousQueue<Integer> mq){
			WorkerThread instance 	= new WorkerThread(name, pNap, bb, mq);
			instance.me_ 	  		= new Thread(instance);
			instance.me_.start();
			return instance;
		}

	}


class Driver{
	
	public static void main(String[] args){
		int numSlots = 10;
		int numSolvers = 4;
		int runTime = 60;
		int pNap = 2;    
		
		
		//create a queue for message passing
		//The message queue to send message to when done
		final SynchronousQueue<Integer> mq = new SynchronousQueue<Integer>();
	     
	    //read in the original sudoku problem from the file
		ParallelSudokuSolver original_problem = new ParallelSudokuSolver("SudokuProblem.txt");
	    
		// create the bounded buffer and add the original problem to the buffer
		MonitorBoundedBuffer<ParallelSudokuSolver> bb = new MonitorBoundedBuffer<ParallelSudokuSolver>(numSlots);
		try {
			bb.deposit(original_problem);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	      
	    // start the solver threads
	    // (they have self-starting threads)
	    WorkerThread[] workers = new WorkerThread[numSolvers];
	 
	    for (int i = 0; i < numSolvers; i++)
	         workers[i] = WorkerThread.newInstance("Parallel Solver "+i, pNap*10, bb, mq);
	      
	      
	    System.out.println("All threads started");    
	   
	    // let them run for a while
	    try { 
	    	System.out.println("Just waiting to receive solution");
	    	mq.take();
	       
	    	System.out.println("Solution received");  
	        System.out.println("time to terminate the threads and exit");     
	        for (int i = 0; i < numSolvers; i++) {
	           workers[i].timeToQuit();
	           System.out.println("Parallel Solver" + i + " told");
	        }
	         
	        for (int i = 0; i < numSolvers; i++) {
	           workers[i].pauseTilDone();
	           System.out.println("Parallel Solver" + i + " done");
	        }  
	     } catch (InterruptedException e) { 
	    	 // ignored 
	     }
	     
	     System.out.println("all threads are done"); 	
	}
}
