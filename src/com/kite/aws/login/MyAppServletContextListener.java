package com.kite.aws.login;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.kite.aws.inflation.InflationTimer;
import com.kite.aws.risk.RiskTimer;
import com.kite.aws.util.GlobalConstants;

@WebListener
public class MyAppServletContextListener implements ServletContextListener {
	
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ServletContextListener destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("ServletContextListener started");
		
		RiskTimer riskTimer = new RiskTimer();
		Timer timer1 = new Timer();	
		timer1.schedule(riskTimer, 1000, GlobalConstants.RISK_TIMER_DELAY*1000);
		
		InflationTimer inflationTimer = new InflationTimer();
		Timer timer2 = new Timer();	
		timer2.schedule(inflationTimer, 1000, GlobalConstants.INFLATION_TIMER_DELAY*1000);
	}

}
