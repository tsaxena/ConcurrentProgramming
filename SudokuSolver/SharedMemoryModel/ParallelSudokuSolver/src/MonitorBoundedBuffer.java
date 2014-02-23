import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

//locks, monitors and semaphores
public class MonitorBoundedBuffer<E> {
		
	//circular buffer
	private LinkedList<E> buffer = null;
	
	// for mutual exclusion
	private Lock mutex  = null;
	
	// Number of slots in the buffer.
	private int numSlots = 0;
    
	// Slot number to be used next for a deposit (if not occupied).
	private int putIn = 0;
   
    // Slot number to be used next for a fetch (if occupied).
	private int takeOut = 0;
   
	// Number of items currently in the buffer (number of slots occupied).
	private int count = 0;
  
   // Solver blocks if the number of unoccupied buffer slots is zero.
	private Condition spaces = null;
	
	// Solver blocks if the number of occupied buffer slots is zero.
	private Condition elements = null;
   
	
	//Thread blocks till one of the work threads
	public MonitorBoundedBuffer(int numSlots){
		this.numSlots = numSlots ;
		LinkedList<E> es = new LinkedList<E>();
		
		this.buffer = es;
		putIn = 0;
		takeOut = 0;
		mutex = new ReentrantLock();
		elements = mutex.newCondition();
	    spaces   = mutex.newCondition();
	}
	
	public void deposit(E e) throws InterruptedException {
		 try{	
			 mutex.lock();
			 System.out.println("Begin Deposit");
			 /* while(count == numSlots){
				 spaces.await();
			 }*/
			 buffer.push(e);  
			 count++;
			 elements.signal();
			 System.out.println("End Deposit");
		 }finally{
			 mutex.unlock();
		 }
	  }

    public E fetch() throws InterruptedException {
    	 try{	
			 mutex.lock(); 
			 System.out.println("Begin Fetch");
			 while(count == 0 ){
				 System.out.println("No elements in the buffer..will wait");
				 elements.await();
			 }
			 E value = buffer.poll();  
			 count--;
			 System.out.println("End Fetch");
			 spaces.signal();
			 return value;
			 
		 }finally{
			 mutex.unlock();
		 }
    }
}
