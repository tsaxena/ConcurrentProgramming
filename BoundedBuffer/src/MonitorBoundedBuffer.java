import java.util.concurrent.*;
import java.util.concurrent.locks.*;
//locks, monitors and semaphores
public class MonitorBoundedBuffer<E> {
	
	//circular buffer
	private E[] buffer = null;
	
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
  
   // Producer blocks if the number of unoccupied buffer slots is zero.
	private Condition spaces = null;
	
	// Consumer blocks if the number of occupied buffer slots is zero.
	private Condition elements = null;
   
	public MonitorBoundedBuffer(int numSlots){
		this.numSlots = numSlots ;
		E[] es = (E[])new Object[numSlots];
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
			 while(count == numSlots){
				 spaces.await();
			 }
			 buffer[putIn] = e;  
			 putIn = (putIn+1)%numSlots;
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
			 while(count == 0){
				 elements.await();
			 }
			 E value = buffer[takeOut];  
			 takeOut = (takeOut+1)%numSlots;
			 count--;
			 System.out.println("End Fetch");
			 spaces.signal();
			 return value;
			 
		 }finally{
			 mutex.unlock();
		 }
    }
	
	
}

