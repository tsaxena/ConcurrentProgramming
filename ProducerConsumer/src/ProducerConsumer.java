import java.util.Random;
import java.util.concurrent.Semaphore;

public class ProducerConsumer {
	
		Semaphore spaces_ ;
		Semaphore elements_ ;
		int rear_ = 0;
		int front_ = 0;
		int capacity_; 
		int[]  buf_;
		
		
		public class Producer implements Runnable{
			
			String name_;
			Semaphore mutexP = new Semaphore(1);
			
			Producer(String str ){
				this.name_ = str;
			}

			public void run(){
				
				Thread thread = Thread.currentThread();
				
				Random generator = new Random( 19580427 );
				for(int i = 0; i < 10; i++ ){
				
				try {
					spaces_.acquire();
					
					Thread.sleep(1000);
					
					this.mutexP.acquire();
					buf_[front_] = generator.nextInt()%10;
					System.out.println(thread.getName() +  " puts " + buf_[front_] + " at " + front_);
					front_ = (front_+ 1)%capacity_ ;
					this.mutexP.release();
					
					elements_.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//this.notify();
				}
			}

		}
		
		public class Consumer implements Runnable{
			
			String name_;
			Semaphore mutexC = new Semaphore(1);
			
			Consumer(String str){	
				name_ = str;	
			}

			public void run(){
				
				Thread thread = Thread.currentThread();
				
				for(int i = 0; i < 10; i++ ){
					try {
						elements_.acquire();
						Thread.sleep(1000);
						this.mutexC.acquire();
						System.out.println(thread.getName() + " gets "+ buf_[rear_] + " at "+ rear_);
						rear_ = (rear_+1)% capacity_;
						this.mutexC.release();
						
						spaces_.release();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		
	    public ProducerConsumer() {
	    	
	    	this.capacity_	= 20;
	    	this.spaces_	= new Semaphore(capacity_);
	    	this.elements_ 	= new Semaphore(0);
	    	this.buf_ 		= new int[capacity_];
	    	Producer p 		= new Producer("producer");
	    	Consumer c		= new Consumer("consumer");
	    	
	    	
	    	//two threads are created
	        Thread prod1 = new Thread(p);
	        prod1.setName("Producer 1");
	        Thread prod2 = new Thread(p);
	        prod2.setName("Producer 2");
	        //Thread prod3 = new Thread(p);
	        //prod3.setName("Producer 3");
	        Thread consmr1 = new Thread(c);
	        consmr1.setName("Consumer 1");
	        Thread consmr2 = new Thread(c);
	        consmr2.setName("Consumer 2");
	        
	        prod1.start();  
	        prod2.start(); 
	        //prod3.start();
	        consmr1.start();
	        consmr2.start();
	        
	    	//start threads with runnable objects.
	    	
	    	try {
				prod1.join();
				prod2.join();
				//prod3.join();
				consmr1.join();
				consmr2.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			
			System.out.println("Done!!");
	    	
	    }   
	    
	    public static void main(String[] args) {
		   new ProducerConsumer();
	    }

	}
