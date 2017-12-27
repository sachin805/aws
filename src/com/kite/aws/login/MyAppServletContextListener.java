package com.kite.aws.login;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.kite.aws.inflation.InflationTimer;
import com.kite.aws.risk.RiskTimer;
import com.kite.aws.robo.Robo;
import com.kite.aws.robo.RoboRiskTimer;
import com.kite.aws.util.GlobalConstants;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import static com.kite.aws.login.GlobalLoggerHandler.LOGIN_LOGGER;

@WebListener
public class MyAppServletContextListener implements ServletContextListener {
	
	// LOGIN_LOGGER.info("");
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ServletContextListener destroyed");
		LOGIN_LOGGER.info("Control in MyAppServletContextListener.contextDestroyed()");
		LOGIN_LOGGER.info("");
		KiteConnect connect = KiteStore.kiteconnect;
		 
		 if (connect == null) {
			 LOGIN_LOGGER.info("KiteConnect is null in MyAppServletContextListener.contextDestroyed()"); 
		 } else {
			 try {
				connect.logout();
				connect = null;
				LOGIN_LOGGER.info("Logged out of KiteConnect session ");
			} catch (KiteException e) {
				e.printStackTrace();
				LOGIN_LOGGER.info("KiteException in MyAppServletContextListener.contextDestroyed()");
			}
		 }
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("ServletContextListener started");
		
		
		/*RiskTimer riskTimer = new RiskTimer();
		Timer timer1 = new Timer();	
		timer1.schedule(riskTimer, 1000, GlobalConstants.RISK_TIMER_DELAY*1000);*/
		
		/*InflationTimer inflationTimer = new InflationTimer();
		Timer timer2 = new Timer();	
		timer2.schedule(inflationTimer, 1000, GlobalConstants.INFLATION_TIMER_DELAY*1000);
		*/
			/*RoboTimer robotTimer = new RoboTimer();
			Timer timer3 = new Timer();	
			timer2.schedule(robotTimer, 1000, GlobalConstants.ROBO_TIMER_DELAY*1000);*/
		
		
		/*OneMinRoboNew oneMinTimer = new OneMinRoboNew();
		Timer timer4 = new Timer();
		timer4.schedule(oneMinTimer, 1000, GlobalConstants.ROBO_TIMER_DELAY*1000);*/
		
		/*ConditionalTimer conditionalTimer = new ConditionalTimer();
		Timer timer3 = new Timer();	
		timer3.schedule(conditionalTimer, 1000, GlobalConstants.CONDITIONAL_TIMER_DELAY*1000);*/
		
		
		RiskTimer riskTimer = new RiskTimer();
		Timer timer3 = new Timer();	
		timer3.schedule(riskTimer, 1000, GlobalConstants.RISK_TIMER_DELAY*1000);
		
		Robo roboTimer = new Robo();
		Timer timer1 = new Timer();	
		timer1.schedule(roboTimer, 1000, GlobalConstants.ROBO_TIMER_DELAY*1000);
		
		RoboRiskTimer roboRiskTimer = new RoboRiskTimer();
		Timer timer2 = new Timer();	
		timer2.schedule(roboRiskTimer, 1000, GlobalConstants.ROBO_RISK_TIMER_DELAY*1000);
		
		InflationTimer inflationTimer = new InflationTimer();
		Timer timer4 = new Timer();	
		timer4.schedule(inflationTimer, 1000, GlobalConstants.INFLATION_TIMER_DELAY*1000);
		
		GlobalLoggerHandler.init();
		LOGIN_LOGGER.info("Control in MyAppServletContextListener.contextInitialized()");
		LOGIN_LOGGER.info("Started all timers.....");
	}

}
