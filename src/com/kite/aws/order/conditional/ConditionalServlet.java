package com.kite.aws.order.conditional;

import static com.kite.aws.login.GlobalLoggerHandler.CONDITIONAL_LOGGER;

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
import com.kite.aws.risk.RiskStore;	

/**
 * Servlet implementation class ConditionalServlet
 */
@WebServlet("/conditional")
public class ConditionalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConditionalServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String action = request.getParameter("action").trim();
		Gson gson = new GsonBuilder().create();
		PrintWriter writer = response.getWriter();
		if (action.equals("place")) {
			String data = request.getParameter("data").trim();
			ConditionalOrder order = ConditionalUtil.createOrder(data);
			CONDITIONAL_LOGGER.info("RAW data : " +data);
			CONDITIONAL_LOGGER.info("got place action");
			CONDITIONAL_LOGGER.info(" order.getCall() : " + order.getCall());
			CONDITIONAL_LOGGER.info(" order.getCallEntry() : " + order.getCallEntry());
			CONDITIONAL_LOGGER.info(" order.getCallSL() : " + order.getCallSL());
			CONDITIONAL_LOGGER.info(" order.getPut() : " + order.getPut());
			CONDITIONAL_LOGGER.info(" order.getPutEntry() : " + order.getPutEntry());
			CONDITIONAL_LOGGER.info(" order.getPutSL() : " + order.getPutSL());
			CONDITIONAL_LOGGER.info(" order.getPosition() : " + order.getPosition());
			CONDITIONAL_LOGGER.info(" order.getProduct() : " + order.getProduct());
			CONDITIONAL_LOGGER.info(" order.getQuantity() : " + order.getQuantity());
			CONDITIONAL_LOGGER.info("  order.getTarget() : " + order.getTarget());
			PlaceOrderPOJO pojo = null;
			if (order != null) {
				//String[] reply = ConditionalOrderExecutor.placeConditionalOrder(order.getCall(), order.getPut(), order.getQuantity(), order.getOrder(), order.getPosition());
				String[] reply = ConditionalOrderExecutor.placeConditionalOrder(order);
				if (reply[0].equals("success")) {
					ConditionalOrderStore.order = order;
					RiskStore.profitTarget =  order.getTarget();
					pojo = new PlaceOrderPOJO("success", "Placed conditional order");
				} else {
					pojo = new PlaceOrderPOJO("error", reply[1]);
				}
			} else {
				pojo = new PlaceOrderPOJO("error", "Insufficient data to form order");
			}
			writer.print(gson.toJson(pojo));
			return;
		}
		
		if (action.equals("abort")) {
			String[] reply = OrderExecutor.exitAllPositions();
			AbortPOJO abort = new AbortPOJO(reply[0],reply[1]);
			writer.print(gson.toJson(abort));
			ConditionalOrderStore.order = null;
			return;
		}
		
		if (action.equals("ltp")) {
			String call = request.getParameter("call").trim();
			String put = request.getParameter("put").trim();
			String[] quotes = ConditionalUtil.getQuotes(call, put);
			
			QuotePOJO pojo = null;
			
			if (quotes[0].equals("error")) {
				pojo = new QuotePOJO("error", quotes[2], "NA", "NA");
			} else {
				pojo = new QuotePOJO("success", quotes[2], quotes[0], quotes[1]);
			}
			writer.print(gson.toJson(pojo));
			return;
		}
		
		if (action.equals("enternow")) {
			String[] reply = ConditionalUtil.firePendingOrder();
			AbortPOJO abort = new AbortPOJO(reply[0],reply[1]);
			writer.print(gson.toJson(abort));
			return;
		}
		
		if (action.equals("status")) {
			AbortPOJO abort = new AbortPOJO(ConditionalOrderStore.code,ConditionalOrderStore.message);
			writer.print(gson.toJson(abort));
			return;
		}
		
		
	}

}
