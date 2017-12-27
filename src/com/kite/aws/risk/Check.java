package com.kite.aws.risk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;

import com.kite.aws.login.Login;
import com.kite.aws.util.GlobalConstants;
import com.rainmatter.kitehttp.exceptions.KiteException;

public class Check {

	public static void main(String[] args) throws Exception, KiteException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter Token : ");
		String line = br.readLine().trim();
		
		Object[] response = Login.login(line);
		
		if (response[0].equals("success")) {
			System.out.println("Logged in.....");
			
			RiskTimer riskTimer = new RiskTimer();
			Timer timer1 = new Timer();	
			timer1.schedule(riskTimer, 1000, GlobalConstants.RISK_TIMER_DELAY*1000);
			
		/*	KiteConnect kiteconnect = (KiteConnect)response[2];
			Order orders = kiteconnect.getOrders();
			System.out.println(orders);*/
			
			
		}
		
		System.out.println("done.....");

	}

}
