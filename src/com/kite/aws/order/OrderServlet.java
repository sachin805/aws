package com.kite.aws.order;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@WebServlet("/order")
public class OrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String order = request.getParameter("order");
		response.getWriter().print(placeOrder(order));
	}
	
	private String placeOrder(String order){
		NormalOrderResponse response = null;
		if(order != null) {
			String[] array = order.split("-");
			if(array!=null) {
				if(array.length != 5) {
					response = new NormalOrderResponse("300","insufficient arguments to place order");
				} else {
					String[] reply = OrderExecutor.placeKiteOrder(array[0],array[1],array[2],Boolean.valueOf(array[3]),array[4]);
					response = new NormalOrderResponse(reply[0],reply[1]);
				}
			} else {
				response = new NormalOrderResponse("300","insufficient arguments to place order");
			  }
		} else {
			response = new NormalOrderResponse("300","insufficient arguments to place order");
		  }
		
		Gson gson = new GsonBuilder().create();
		return gson.toJson(response);
	}

}
