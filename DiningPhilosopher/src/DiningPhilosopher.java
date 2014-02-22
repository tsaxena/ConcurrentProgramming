
import java.util.concurrent.*;
public class DiningPhilosopher {
	Semaphore fork[] = {new Semaphore(1),
						new Semaphore(1),
						new Semaphore(1),
						new Semaphore(1),
						new Semaphore(1)};
	
	public class Philosopher extends Thread{
		int id;
		public Philosopher(int i){
			id = i;
		}
		
		public void run(){
			for(int i = 0; i < 10; i++){
				int left = id;
				int right = (id+1)%5;
				
				if(id == 0){
					left = (id+1)%5;
					right = id;
				}
				try {
					
					fork[left].acquire();
					fork[right].acquire();
					System.out.println("Philosopher"+id+" is eating "+ (i+1)+ "time" );
					sleep(1000);
					System.out.println("Philosopher"+id+" is done" + (i+1)+ "time");
					fork[right].release();
					fork[left].release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
	}
	public DiningPhilosopher(){
		for(int i =0; i < 5; i++){
			Thread t = new Philosopher(i);
			t.start();
		}
	}
	
	public static void main(String[] args) {
		   new DiningPhilosopher();
	    }

}
