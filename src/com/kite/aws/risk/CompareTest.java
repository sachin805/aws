package com.kite.aws.risk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.kite.aws.util.GlobalConstants;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Order;
import com.rainmatter.models.UserModel;

public class CompareTest {

	public static void main(String[] args)  throws Exception, KiteException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("G");
		list.add("A");
		list.add("C");
		list.add("X");
		//sort(list);
		
		System.out.println("Enter token");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String token = br.readLine().trim();
		
		KiteConnect connect = new KiteConnect(GlobalConstants.API_KEY);
		UserModel userModel = null;
		userModel = connect.requestAccessToken(token, GlobalConstants.API_SECRET);
		connect.setAccessToken(userModel.accessToken);
		connect.setPublicToken(userModel.publicToken);
		
		Order orders = connect.getOrders();
		List<Order> orderList = orders.orders;
		System.out.println("Before sort .........");
	//	int size = list.size();
		for (Order item : orderList) {
			System.out.println(item.orderId + " " + item.tradingSymbol + " " +item.quantity + " " + item.orderTimestamp + " " + item.product);
		}
		
		System.out.println();
		System.out.println();
		System.out.println();
		
		System.out.println("After sort .........");
		
		 orderList = sortOrderList(orderList);
		 for (Order item : orderList) {
			 System.out.println(item.orderId + " " + item.tradingSymbol + " " +item.quantity + " " + item.orderTimestamp + " " + item.product);
			}
	}
	
	public static void sort(ArrayList<String> list) {
		int size = list.size();
		
		for (int i=0;i<size;i++) {
			for (int j=i+1;j<size;j++) {
				String one = list.get(i);
				String two = list.get(j);
				if(one.compareTo(two) < 0) {
					String temp = one;
					one = two;
					two = temp;
					list.set(i, one);
					list.set(j, two);
				}
				
			}
		}
		for (int i=0;i<size;i++)
			System.out.println(list.get(i));
		
	}
	
	private static List<Order> sortOrderList(List<Order> orderList) {
		int size = orderList.size();
		
		for (int i=0;i<size;i++) {
			for (int j=i+1;j<size;j++) {
				Order one = orderList.get(i);
				Order two = orderList.get(j);
				String oneOrderId = one.orderId;
				String twoOrderId = two.orderId;
				if (oneOrderId.compareTo(twoOrderId) < 0) {
					Order temp = one;
					one = two;
					two = temp;
					orderList.set(i, one);
					orderList.set(j, two);
				}
			}
		}
		return orderList;
	}

}
