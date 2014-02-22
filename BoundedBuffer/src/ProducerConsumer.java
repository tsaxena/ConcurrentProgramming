
class Producer implements Runnable{
	
	//name
	private String name_ = null;
   
	//Time (milliseconds) to sleep between calls to the bounded buffer.
	private int pNap_ = 0;
   
	//The bounded buffer to use.
	private MonitorBoundedBuffer<Integer> bb_ = null;
   
	// Thread internal to the producer.
	public Thread me_ = null;

	//constructor
	public Producer(String name, int pNap, MonitorBoundedBuffer<Integer> bb){
		this.name_ 	= name;
		this.pNap_ 	= pNap*1000;
		this.bb_ 	= bb;
	}

	
	//execute method
	public void run(){
		if (Thread.currentThread() != me_) return;
		
	    int napping;
		while(true){
			//sleep
			try{
				napping = 1 + (int)Math.random(); 
				Thread.sleep(napping);//this.pNap_);
			}catch(InterruptedException e){
				
			}
			
			int item = (int)Math.random();
			//produce
			try{
				bb_.deposit(item);
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
	public static Producer newInstance(String name, int pNap, MonitorBoundedBuffer<Integer> bb){
		Producer instance = new Producer(name, pNap, bb);
		instance.me_ 	  = new Thread(instance);
		instance.me_.start();
		return instance;
	}
}


class Consumer implements Runnable{
	//name
	private String name_ = null;
   
	//Time (milliseconds) to sleep between calls to the bounded buffer.
	private int pNap_ = 0;
   
	//The bounded buffer to use.
	private MonitorBoundedBuffer<Integer> bb_ = null;
   
	// Thread internal to the producer.
	public Thread me_ = null;

	//constructor
	public Consumer(String name, int pNap, MonitorBoundedBuffer<Integer> bb){
		this.name_ 	= name;
		this.pNap_ 	= pNap*1000;
		this.bb_ 	= bb;
	}

	
	//execute method
	public void run(){
		while(true){
			//sleep
			try{
				Thread.sleep(this.pNap_);
			}catch(InterruptedException e){
				System.out.println(this.name_
			               + " interrupted from sleep");
			            return;
			}
			System.out.println(this.name_
		            + " wants to consume");
			//consume
			try{
				int item = bb_.fetch();
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
	public static Consumer newInstance(String name, int pNap, MonitorBoundedBuffer<Integer> bb){
		Consumer instance = new Consumer(name, pNap, bb);
		instance.me_ 	  = new Thread(instance);
		instance.me_.start();
		return instance;
	}

}


class ProducerConsumer{
	   /**
     * Driver.
     * @param args Command line arguments.
     */
   public static void main(String[] args) {
      int numSlots = 10;
      int numProducers = 1;
      int numConsumers = 1;
      int pNap = 2;       // defaults
      int cNap = 2;       // in
      int runTime = 60;   // seconds
     

      // create the bounded buffer
      MonitorBoundedBuffer<Integer> bb = new MonitorBoundedBuffer<Integer>(numSlots);

      // start the Producers and Consumers
      // (they have self-starting threads)
      Producer[] p = new Producer[numProducers];
      Consumer[] c = new Consumer[numConsumers];

      for (int i = 0; i < numProducers; i++)
         p[i] = Producer.newInstance("PRODUCER"+i, pNap*10, bb);
      
      for (int i = 0; i < numConsumers; i++)
         c[i] = Consumer.newInstance("Consumer"+i, cNap*10, bb);
      
      System.out.println("All threads started");

      // let them run for a while
      try {
         Thread.sleep(runTime*1000);
         System.out.println("time to terminate the threads and exit");
         
         for (int i = 0; i < numProducers; i++) {
            p[i].timeToQuit();
            System.out.println("PRODUCER" + i + " told");
         }
         
         for (int i = 0; i < numConsumers; i++) {
            c[i].timeToQuit();
            System.out.println("CONSUMER" + i + " told");
         }
         
         for (int i = 0; i < numProducers; i++) {
            p[i].pauseTilDone();
            System.out.println("PRODUCER" + i + " done");
         }
         for (int i = 0; i < numConsumers; i++) {
            c[i].pauseTilDone();
            System.out.println("CONSUMER" + i + " done");
         }
      } catch (InterruptedException e) { /* ignored */ }
      System.out.println("all threads are done");
   }
	
}
