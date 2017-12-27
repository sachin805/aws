package p1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import com.kite.aws.login.Login;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Order;
import com.rainmatter.models.Position;

public class Trial {

	public static void main(String[] args) throws Exception, KiteException {
		System.out.println("Enter token :");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String token = br.readLine().trim();
		Object[] reply = Login.login(token);
		
		String code = (String) reply[0];
		if (code.equals("success")) {
			System.out.println("Login success !!");
			
			KiteConnect kiteConnect = (KiteConnect)reply[2];
			
			Position position = kiteConnect.getPositions();
			List<Position> netPositions = position.netPositions;
			
			Order orderBook = kiteConnect.getOrders();
			List<Order> orderList = orderBook.orders;
			int size = orderList.size();
			
			if (size > 0) {
				for (int i = size-1; i>0 ;i--) {
					Order item = orderList.get(i);
					String status = item.status;
					String tradingSymbol = item.tradingSymbol;
					String transactionType = item.transactionType;
					String product = item.product;
					
					/*if  ((status.equals("COMPLETE")) && (tradingSymbol.equals(symbol)) && (transactionType.equals(opeation)) && (product.equals("MIS"))) {
						matchingOrderList.add(item);
					}*/
					System.out.println(item.orderTimestamp + " " + tradingSymbol + " " + transactionType + " " +item.quantity + " " + item.averagePrice + " " + product + " " + status);
				}
			}
		}
		
		
	}

}
