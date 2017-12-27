<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.kite.aws.util.GlobalConstants" %>
<%@ page import="com.kite.aws.risk.RiskStore" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Application information page</title>
</head>
<body>

<%
   String user = "";
   String apiKey = GlobalConstants.API_KEY;
   
   if (apiKey.equals("oc1g68oja0hpnjcp")) {
	   user = "Ashish";
   } else {
	   user = "Sachin";
   }
   
   String absLossPoints = RiskStore.absPointsStopLoss + "";
   String absProfitPoints = RiskStore.absPointsTarget + "";
   
%>

<center>

<h3> User : <%= user%> </h3>
<br>
<h3> Absolute loss points : <%= absLossPoints%></h3>
<br>
<h3> Absolute profit points : <%= absProfitPoints%></h3>

</center>

</body>
</html>