package p1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kite.aws.inflation.InflationResponse;
import com.kite.aws.inflation.InflationStatusReply;
import com.kite.aws.inflation.InflationStore;
import com.kite.aws.risk.RiskResponse;
import com.kite.aws.risk.RiskStatus;
import com.kite.aws.util.Util;
import com.kite.aws.inflation.EnterButtonReponse;
public class JSONOut {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*UpdateProfitTargetResponse one = new UpdateProfitTargetResponse("2000","-3000","success","success");
		
		
		UpdateProfitTargetResponse two = new UpdateProfitTargetResponse("2000","-3000","error","some error message");*/
		
		/*Response res1 = new Response("success","some relevant message");
		
		
		Response res2 = new Response("error","some relevant message");
		
		Gson gson = new GsonBuilder().create();
		System.out.println(gson.toJson(res1));
		System.out.println(gson.toJson(res2));
		
		System.out.println();
		
		RiskResponse one = new RiskResponse("exithalf","success","some relevant message");
		RiskResponse two = new RiskResponse("exithalf","error","some relevant message");
		System.out.println(gson.toJson(one));
		System.out.println(gson.toJson(two));
		
		System.out.println();
		
		RiskStatus status1 = new RiskStatus("error","kiteConnect is null in RiskTimer.run()","2040.4","1200","-600",Util.getTime());
		RiskStatus status2 = new RiskStatus("NO_TRIGGER","NA","0.0","NA","NA",Util.getTime());
		RiskStatus status3 = new RiskStatus("complete","Squared off all open positions","3400.0","NA","NA",Util.getTime());
		System.out.println(gson.toJson(status1));
		System.out.println(gson.toJson(status2));
		System.out.println(gson.toJson(status3));
		
		System.out.println();
		
		InflationResponse a =   new InflationResponse("success", "Placed order");
		InflationResponse b = new InflationResponse("error", "Insufficient arguments successfully");
		System.out.println(gson.toJson(a));
		System.out.println(gson.toJson(b));*/
		
		Gson gson = new GsonBuilder().create();
		
		System.out.println();
		InflationResponse reply1 = new InflationResponse("error", "some error message");
		System.out.println(gson.toJson(reply1));
		
		InflationResponse reply2 = new InflationResponse("success",  "some  message");
		System.out.println(gson.toJson(reply2));
		
		System.out.println();
		InflationStatusReply reply = new InflationStatusReply();
		reply.setOrderStatus("NO_TRIGGER");
		reply.setQuantity("750");
		reply.setCallSymbol("NIFTY17JUN9800CE");
		reply.setPutSymbol("NIFTY17JUN9600PE");
		reply.setSell("Sell");
		reply.setProductType("MIS");
		reply.setTriggerSum("87.6");
		reply.setRealTimeSum("82.3");
		reply.setTime(Util.getTime());
		reply.setOrderStatusMessage("SOME MESSAGE");
		System.out.println(gson.toJson(reply));
		
		InflationResponse reply22 = new InflationResponse("success", "Cancelled order successfully");
		System.out.println(gson.toJson(reply22));
		
		EnterButtonReponse res = new EnterButtonReponse("success","some message");
		System.out.println(gson.toJson(res));
		
		res = new EnterButtonReponse("error","some message");
		System.out.println(gson.toJson(res));
	}
	
	

}
