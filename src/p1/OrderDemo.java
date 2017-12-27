package p1;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.kite.aws.login.Login;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Order;

public class OrderDemo {

	public static void main(String[] args) throws Exception, KiteException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
		System.out.println("Enter token : ");
		String t = br.readLine().trim();
		Object[] reply = Login.login(t);
		
		String code = (String) reply[0];
		if (code.equals("error")) {
			System.out.println(reply[1]);
			return;
		}
		
		KiteConnect kiteconnect = (KiteConnect) reply[2];
		
		Order order = kiteconnect.getOrder("170927000077115");
		System.out.println(" : " + order.orderType);
		System.out.println(" order.averagePrice : " +order.averagePrice);
		System.out.println(" order.price : " +order.price);
	}

}
