package p1;

import java.util.concurrent.ArrayBlockingQueue;

import com.kite.aws.robo.NiftyQuote;

public class MyQueueDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 ArrayBlockingQueue<Double> queue;
		 
		 queue = new ArrayBlockingQueue<Double>(2);
		 
		 queue.add(new Double(100210d));
		 queue.add(new Double(100212d));
		 
		 Double[] array =queue.toArray(new Double[2]);
		 
		 System.out.println(array);
		 
		 
		 System.out.println(queue.poll());
		 
		 System.out.println(queue);
		 

	}

}
