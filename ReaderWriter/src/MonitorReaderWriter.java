
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorReaderWriter<E> {
	
	E data = null;
	int num_waiting_writers = 0; 
	int num_writers = 0;
	int num_waiting_readers = 0;
	int num_readers = 0;
	Condition okToRead = null;
	Condition okToWrite = null;
	Lock mutex = null;
	
	public MonitorReaderWriter(E def_value){
		mutex = new ReentrantLock();
		okToRead = mutex.newCondition();
		okToWrite = mutex.newCondition();
		data = def_value;
	}
	
	/*
	 * synchronized method 
	 */
	public E request_read() throws InterruptedException{
		try{
			mutex.lock();
			while(num_writers == 1 || num_waiting_writers > 0){
				num_waiting_readers++;
				okToRead.await();
				num_waiting_readers--;
			}
			num_readers++;
			okToRead.signal();
			return data;
		}finally{
			mutex.unlock();
		}
	}
	
	/*
	 * synchronized method 
	 */
		
	public void release_read() throws InterruptedException{
		try{
			mutex.lock();
			num_readers--;
			if(num_readers == 0){ //is the last writer
				okToWrite.signal();
			}
		}finally{
			mutex.unlock();
		}	
	}
	
	
	/*
	 * synchronized method 
	 */
	public void request_write(E e)throws InterruptedException{
		try{
			mutex.lock();
			while(num_writers == 1 || num_readers > 0){
				num_waiting_writers++;
				okToWrite.await();
				num_waiting_writers--;
			}
			num_writers = 1;
			data = e;
		}finally{
			mutex.unlock();
		}
	}

	/*
	 * synchronized method 
	 */
	public void release_write() throws InterruptedException{
		try{
			mutex.lock();
			num_writers = 0;
			if(num_waiting_readers > 0){
				okToRead.signal();
			}else{
				okToWrite.signal();
			}
			
		}finally{
			mutex.unlock();
		}	
	}
	
}
