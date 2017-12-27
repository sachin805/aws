package com.kite.aws.login;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.kite.aws.inflation.InflationServlet;
import com.kite.aws.order.OrderExecutor;
import com.kite.aws.order.conditional.ConditionalServlet;
import com.kite.aws.risk.NewOrderBook;
import com.kite.aws.risk.RiskTimer;
import com.kite.aws.robo.RoboTimer;
import com.kite.aws.util.GlobalConstants;

public class GlobalLoggerHandler {
	
	public static FileHandler handler;
	public  static Logger LOGIN_LOGGER; 
	public  static Logger RISK_TIMER_LOGGER; 
	public  static Logger NEW_ORDERBOOK_LOGGER;
	public  static Logger ORDER_LOGGER;
	public  static Logger INFLATION_LOGGER;
	public  static Logger CONDITIONAL_LOGGER;
	public  static Logger ROBO_LOGGER;
	
	static {
		LOGIN_LOGGER = Logger.getLogger(LoginServlet.class.getName());
		RISK_TIMER_LOGGER = Logger.getLogger(RiskTimer.class.getName());
		NEW_ORDERBOOK_LOGGER = Logger.getLogger(NewOrderBook.class.getName());
		ORDER_LOGGER = Logger.getLogger(OrderExecutor.class.getName());
		INFLATION_LOGGER = Logger.getLogger(InflationServlet.class.getName());
		CONDITIONAL_LOGGER = Logger.getLogger(ConditionalServlet.class.getName());
		ROBO_LOGGER = Logger.getLogger(RoboTimer.class.getName());
	}
	
	public static void init() {
		
		try {
			handler = new FileHandler(GlobalConstants.LOG_FILE_PATH);
			handler.setFormatter(new MyFormatter());
			
			LOGIN_LOGGER.addHandler(handler);
			RISK_TIMER_LOGGER.addHandler(handler);
			NEW_ORDERBOOK_LOGGER.addHandler(handler);
			ORDER_LOGGER.addHandler(handler);
			INFLATION_LOGGER.addHandler(handler);
			CONDITIONAL_LOGGER.addHandler(handler);
			ROBO_LOGGER.addHandler(handler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
