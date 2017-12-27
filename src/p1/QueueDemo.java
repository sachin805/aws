package p1;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

public class QueueDemo {

	public static void main(String[] args) {
		ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(3,true);
		
		queue.add("a");
		queue.add("b");
		queue.add("c");
		
		System.out.println("before : ");
		System.out.println("queue.size() : " +queue.size());
		print(queue);
		
		String item = queue.poll();
		System.out.println("item : " +item);
		
		System.out.println("after : ");
		print(queue);
		System.out.println("queue.size() : " +queue.size());
	}
	
	public static void print(ArrayBlockingQueue<String> queue) {
		Iterator<String> iterator = queue.iterator();
		while(iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}

}
