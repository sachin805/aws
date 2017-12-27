package com.kite.aws.robo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static com.kite.aws.login.GlobalLoggerHandler.ROBO_LOGGER;

/**
 * Servlet implementation class RoboServlet
 */
@WebServlet("/robo")
public class RoboServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RoboServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		myService(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		myService(request, response);
	}
	
	
	private void myService(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String action = request.getParameter("action");
		
		if (action != null) {
			/*if (action.equals("stop")) {
				Robo.stop();
				response.getWriter().println("Stopped robo........");
			}
			
			if (action.equals("start")) {
				Robo.start();
				response.getWriter().println("Started robo........");
			}*/
			
			ROBO_LOGGER.info("Control in RoboServlet.myService() method");
			ROBO_LOGGER.info("action = " +action);
			
			if (action.equals("set")) {
				String value = request.getParameter("value");
				ROBO_LOGGER.info("value = " +value);
				if (value.contains("-")) {
					String[] tokens = value.split("-");
					if ((tokens != null) && (tokens.length == 4)) {
						String quantity = tokens[0];
						String direction = tokens[1];
						String position = tokens[2];
						String expiry = tokens[3];
						
						ROBO_LOGGER.info("quantity = " +quantity);
						ROBO_LOGGER.info("direction = " +direction);
						ROBO_LOGGER.info("position = " +position);
						ROBO_LOGGER.info("expiry = " +expiry);
						
						RoboStore.quantity = quantity;
						RoboStore.direction = direction;
						RoboStore.position = position;
						RoboStore.instrument = expiry;
						response.getWriter().println("Updated robo arguments........");
						
					} else {
						response.getWriter().println("Insufficient arguments........");
					}
				} else {
					response.getWriter().println("Insufficient arguments........");
				}
			}
			
			if (action.equals("fetch")) {
				ROBO_LOGGER.info("Control in fetch block...");
				String reply = "";
				reply = RoboStore.quantity + "-"+ RoboStore.direction + "-" +RoboStore.position + "-" +RoboStore.instrument;
				ROBO_LOGGER.info("reply = " +reply);
				response.getWriter().println(reply);
			}
			
		}
		
	}

}
