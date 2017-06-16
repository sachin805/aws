package com.kite.aws.login;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rainmatter.kitehttp.exceptions.KiteException;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if ((action != null) && (action.equals("logout"))) {
			response.getWriter().print(logoutUser());
		} 
		
		if ((action != null) && (action.equals("login"))) {
			String token = request.getParameter("token");
			response.getWriter().print(loginUser(token));
		}
	}
	
	
	private String loginUser(String token) {
		
		if (token != null) {
			Object[] reply = Login.login(token);
			LoginResponse response = new LoginResponse((String) reply[0], (String) reply[1]);
			Gson gson = new GsonBuilder().create();
			return gson.toJson(response);
		} else {
			LoginResponse response = new LoginResponse("error", "Insufficient arguments");
			Gson gson = new GsonBuilder().create();
			return gson.toJson(response);
		}
		
	}
	
	private String logoutUser() {
		String message = "";
		if (KiteStore.kiteconnect != null) {
			try {
				KiteStore.kiteconnect.logout();
				KiteStore.kiteconnect = null;
				KiteStore.accessToken = null;
				KiteStore.publicToken = null;
				KiteStore.userModel = null;
			} catch (KiteException e) {
				message = "KiteException in LoginServlet.logoutUser()";
				return message;
			}
		} else {
			return "KiteStore.kiteconnect is null in LoginServlet.logoutUser()";
		}
		
		return "Logged out of application successfully";
	}

}
