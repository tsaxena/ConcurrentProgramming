import java.util.Random;

class Writer implements Runnable{
	
	//name
	private String name_ = null;
   
	//Time (milliseconds) to sleep between calls to the bounded buffer.
	private int wNap_ = 0;
   
	//The bounded buffer to use.
	private MonitorReaderWriter<Integer> db_ = null;
   
	// Thread internal to the producer.
	public Thread me_ = null;

	//constructor
	public Writer(String name, int pNap, MonitorReaderWriter<Integer> db){
		this.name_ 	= name;
		this.wNap_ 	= pNap*1000;
		this.db_ 	= db;
	}

	
	//execute method
	public void run(){
		if (Thread.currentThread() != me_) return;
		int napping;
		Random generator = new Random();
		while(true){
			//sleep
			try{
				napping = 1 +  generator.nextInt(this.wNap_);
		         System.out.println( name_ + " napping for " + napping + " ms");
		         Thread.sleep(napping);
		         System.out.println(name_ + " wants to write");
		         int e = generator.nextInt();
		         db_.request_write(e);
		         napping = 1 + generator.nextInt(this.wNap_);
		         System.out.println(name_ + " writing value " + e + " for " + napping + " ms");
		         Thread.sleep(napping);
		         db_.release_write();
		         System.out.println(name_ + " finished writing");
				
			}catch(InterruptedException e){
				
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
	public static Writer newInstance(String name, int pNap, MonitorReaderWriter<Integer> bb){
		Writer instance = new Writer(name, pNap, bb);
		instance.me_ 	  = new Thread(instance);
		instance.me_.start();
		return instance;
	}
}


class Reader implements Runnable{
	//name
	private String name_ = null;
   
	//Time (milliseconds) to sleep between calls to the bounded buffer.
	private int rNap_ = 0;
   
	//The bounded buffer to use.
	private MonitorReaderWriter<Integer> db_ = null;
   
	// Thread internal to the producer.
	public Thread me_ = null;

	//constructor
	public Reader(String name, int rNap, MonitorReaderWriter<Integer> db){
		this.name_ 	= name;
		this.rNap_ 	= rNap*1000;
		this.db_ 	= db;
	}

	
	//execute method
	public void run(){
		int napping;
		Random generator = new Random();
		while(true){
			napping = 1 + generator.nextInt(rNap_);
	         System.out.println( name_
	            + " napping for " + napping + " ms");
	        
	         try { 
	        	 Thread.sleep(napping);
	        	 System.out.println( name_
	        			 			+ " wants to read");
				int e = db_.request_read();
				napping = 1 + (int) generator.nextInt(rNap_);
				System.out.println( name_
									+ " reading value " + e + " for " + napping + " ms");
				Thread.sleep(napping);
				db_.release_read();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         
	         System.out.println(name_
	            + " finished reading");
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
	public static Reader newInstance(String name, int pNap, MonitorReaderWriter<Integer> bb){
		Reader instance = new Reader(name, pNap, bb);
		instance.me_ 	  = new Thread(instance);
		instance.me_.start();
		return instance;
	}

}


public class ReaderWriter{
	   /**
     * Driver.
     * @param args Command line arguments.
     */
   public static void main(String[] args) {
      int numProducers = 1;
      int numConsumers = 3;
      int pNap = 2;       // defaults
      int cNap = 2;       // in
      int runTime = 60;   // seconds
     

      // create the bounded buffer
      MonitorReaderWriter<Integer> bb = new MonitorReaderWriter<Integer>(0);

      // start the Producers and Consumers
      // (they have self-starting threads)
      Writer[] p = new Writer[numProducers];
      Reader[] c = new Reader[numConsumers];

      for (int i = 0; i < numProducers; i++)
         p[i] = Writer.newInstance("WRITER"+i, pNap, bb);
      
      for (int i = 0; i < numConsumers; i++)
         c[i] = Reader.newInstance("READER"+i, cNap, bb);
      
      System.out.println("All threads started");

      // let them run for a while
      try {
         Thread.sleep(runTime*1000);
         System.out.println("time to terminate the threads and exit");
         
         for (int i = 0; i < numProducers; i++) {
            p[i].timeToQuit();
            System.out.println("WRITER" + i + " told");
         }
         
         for (int i = 0; i < numConsumers; i++) {
            c[i].timeToQuit();
            System.out.println("READER" + i + " told");
         }
         
         for (int i = 0; i < numProducers; i++) {
            p[i].pauseTilDone();
            System.out.println("WRITER" + i + " done");
         }
         for (int i = 0; i < numConsumers; i++) {
            c[i].pauseTilDone();
            System.out.println("READER" + i + " done");
         }
      } catch (InterruptedException e) { /* ignored */ }
      System.out.println("all threads are done");
   }
	
}