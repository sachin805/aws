package com.kite.aws.risk;

import java.io.IOException;
import java.io.PrintWriter;

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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");

		if (action != null) {
			RiskResponse riskResponse = null;
			Gson gson = new GsonBuilder().create();
			PrintWriter writer = response.getWriter();
			switch (action) {
			case "exitall":
			//	RiskStore.trailStopLoss = "NA";
				String[] reply = OrderExecutor.exitAllPositions();
				riskResponse = new RiskResponse("exitall", reply[0], reply[1]);
				writer.print(gson.toJson(riskResponse));
				break;
			case "exithalf":
				String[] reply1 = OrderExecutor.exitHalfPositions();
				riskResponse = new RiskResponse("exithalf", reply1[0], reply1[1]);
				writer.print(gson.toJson(riskResponse));
				break;
			case "getm2m":
				String m2mStr = RiskStore.m2m;
				String ts = Util.getTime();
				/*RiskStatus riskStatus = new RiskStatus(RiskStore.riskTimerStatus, RiskStore.riskTimerStatusMessage,
						m2mStr, RiskStore.profitTarget, RiskStore.stopLoss, ts);*/
				/*RiskStatus riskStatus = new RiskStatus(RiskStore.riskTimerStatus, RiskStore.riskTimerStatusMessage,
						m2mStr, RiskStore.profitTarget, RiskStore.trailStopLossTriggerUI, ts);*/
				RiskStatus riskStatus = new RiskStatus(RiskStore.riskTimerStatus, RiskStore.riskTimerStatusMessage,
						m2mStr, RiskStore.profitTarget, RiskStore.stopLoss, ts);
				System.out.println(gson.toJson(riskStatus));
				writer.print(gson.toJson(riskStatus));
				break;
			case "update":
				String profit = request.getParameter("profit");
				String loss = request.getParameter("loss");
				RiskStore.profitTarget = profit;

				String[] msg = RiskUtil.stopLossLimitCheck(loss);
				if ((msg[0].equals("success")) && (msg[1].equals("success"))) {
					RiskStore.stopLoss = loss;
					String displayMessage = "Updated profit target to " + RiskStore.profitTarget + " and stop loss to "
							+ RiskStore.stopLoss;
					UpdateRiskPOJO obj = new UpdateRiskPOJO("success", displayMessage);
					writer.print(gson.toJson(obj));
				}

				if ((msg[0].equals("error")) && (msg[2] != null)) {
					RiskStore.stopLoss = msg[2];
					String displayMessage = "Updated profit target to " + RiskStore.profitTarget + " and stop loss to "
							+ RiskStore.stopLoss;
					UpdateRiskPOJO obj = new UpdateRiskPOJO("error", displayMessage);
					writer.print(gson.toJson(obj));
				}

				if ((msg[0].equals("fatal"))) {
					RiskStore.stopLoss = "NA";
					RiskStore.profitTarget = "NA";
					String displayMessage = "Unable to update stop loss due to error :" + msg[2];
					UpdateRiskPOJO obj = new UpdateRiskPOJO("error", displayMessage);
					writer.print(gson.toJson(obj));
				}
				break;
			}
		}
	}

}
