package p1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.kite.aws.util.GlobalConstants;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Order;
import com.rainmatter.models.Position;
import com.rainmatter.models.UserModel;

public class Demo {

	/*public static void main(String[] args) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
		cal.add(Calendar.DATE, 0);

		System.out.println(dateFormat.format( cal.getTime()));
		
		String[] availableIDs = TimeZone.getAvailableIDs();
		
		for (String s : availableIDs)
			System.out.println(s);
	}*/
	
	
	public static void main(String[] args) throws Exception {
	/*	test(1500,1200);
		test(1500,-1200);
		test(1500,-2000);
		test(1500,1000);
		test(1500,1500);
		test(1500,-1500);
		test(1500,2000);*/
		//test("qyjsbdjmhl0egnusrmgufflakjrjpgsg");
		//System.out.println(Double.isFinite(0.67));
		/*BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter token :");
		String token = br.readLine().trim();
		test(token);*/
		
		String s = "2017-10-13T09:15:00+0530";
		String[] tokens = s.split("T");
		//String ans = tokens[0] + " " +tokens[1].split("+")[0];
		//System.out.println(ans);
		System.out.println(tokens[0]);
		System.out.println(tokens[1].substring(0, tokens[1].lastIndexOf(":")+3));
		
	}
	
	static void test(int trigger,int pnl) {
		int sum = trigger + pnl;
		
		if (sum <= 0)
			System.out.println("Execute");
		else
			System.out.println("Do not Execute");
		
	}
	
	static void test(String requestToken) {
		KiteConnect connect = new KiteConnect(GlobalConstants.API_KEY);
		Position position = null;
		UserModel userModel = null;
		
		try {
			userModel = connect.requestAccessToken(requestToken, GlobalConstants.API_SECRET);
			connect.setAccessToken(userModel.accessToken);
			connect.setPublicToken(userModel.publicToken);
		} catch (JSONException | KiteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//trigger_price
		 Map<String, Object> callParam = new HashMap<String, Object>();
		 callParam.put("tradingsymbol", "NIFTY17SEP10200CE");
		 callParam.put("exchange", "NFO");
		 callParam.put("transaction_type", "SELL");
		 callParam.put("order_type","SL-M");
		 callParam.put("quantity", "75");
		 callParam.put("product", "MIS");
		 callParam.put("validity","DAY");
		 callParam.put("trigger_price","15");
		 
		/* try {
			connect.placeOrder(callParam, "regular");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		 
		 Order book;
		try {
			book = connect.getOrders();
			List<Order> orders = book.orders;
			
			for (Order item : orders) {
				System.out.println(item.orderId + " : " + item.orderTimestamp + " : " + item.orderType + " : " + item.status);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
		/*try {
			position = connect.getPositions();
		} catch (JSONException | KiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double pnl = 0.0;
		if (position != null) {
			System.out.println(position);
			List<Position> positions = position.netPositions;
			if (positions != null)  {
				for (Position item : positions) {
					if ((item.netQuantity != 0) && (item.product.equals("MIS"))){
						pnl = pnl + item.unrealised;
					}
				}
			} else {
				System.out.println("There are no open positions !!");
			}
		} else {
			System.out.println("position is null !!");
		}
		
		System.out.println(pnl);*/
	}

}
