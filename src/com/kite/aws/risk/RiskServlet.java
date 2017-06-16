package com.kite.aws.risk;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kite.aws.order.OrderExecutor;
import com.kite.aws.util.Util;

@WebServlet("/risk")
public class RiskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		
		if (action != null) {
			RiskResponse riskResponse = null;
			Gson gson = new GsonBuilder().create();
			PrintWriter writer = response.getWriter();
			switch (action) {
			case "exitall":
						String[]reply = OrderExecutor.exitAllPositions();
						riskResponse = new RiskResponse("exitall", reply[0], reply[1]);
						writer.print(gson.toJson(riskResponse));
							break;
			case "exithalf":
						String[]reply1 = OrderExecutor.exitHalfPositions();
						riskResponse = new RiskResponse("exithalf", reply1[0], reply1[1]);
						writer.print(gson.toJson(riskResponse));
							break;
			case "getm2m":
						String m2mStr = RiskStore.m2m + " @ " + Util.getTime();
						RiskStatus riskStatus = new RiskStatus(RiskStore.riskTimerStatus,RiskStore.riskTimerStatusMessage,m2mStr,RiskStore.profitTarget,RiskStore.stopLoss);
						System.out.println(gson.toJson(riskStatus));
						writer.print(gson.toJson(riskStatus));
							break;
			case "update":	
						String profit = request.getParameter("profit");
						//String loss = request.getParameter("loss");
						RiskStore.profitTarget = profit;
						UpdateProfitTargetResponse uptr = new UpdateProfitTargetResponse(RiskStore.profitTarget);
						writer.print(gson.toJson(uptr));
						//RiskStore.stopLoss = loss;
						break;
			case "set":	
						String[]reply2 = RiskUtil.setAutoTargets();
						riskResponse = new RiskResponse("set", reply2[0], reply2[1]);
						break;
			case  "getm2m1":
				//RiskStatus riskStatus = new RiskStatus(RiskStore.riskTimerStatus,RiskStore.riskTimerStatusMessage,RiskStore.m2m,RiskStore.profitTarget,RiskStore.stopLoss);
				M2M m2m = new M2M(Util.getTime());
				writer.print(gson.toJson(m2m));
					break;
			}
		} 
	}
	
	
	
	

}
