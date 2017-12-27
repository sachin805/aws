package p1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kite.aws.login.Login;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.HistoricalData;

public class HistoricalDataDemo {
	
	
	public static void myMain() throws Exception {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter token :");
		String token = br.readLine().trim();
		Object[] reply = Login.login(token);
		
		System.out.println("Menu :: ");
		System.out.println(" Symbol :: 1. Nifty 2. Banknifty ");
		
		
	}

	public static void main(String[] args) throws Exception, KiteException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter token :");
		String token = br.readLine().trim();
		Object[] reply = Login.login(token);
		String code = (String) reply[0];
		if (code.equals("success")) {
			System.out.println("Login success");
			KiteConnect kiteconnect = (KiteConnect) reply[2];
			ArrayList<OHLC> list = start(kiteconnect);
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\amazon_key\\data\\nifty-17-1-min.csv"));
			
			for (OHLC item : list) {
				String line = item.getT() + "," + item.getO() + "," + item.getH() + "," +item.getL() + "," +item.getC();
				bw.write(line);
				bw.newLine();
			}
			
			bw.close();
			System.out.println("done writing all records to file");
			
			/*Map<String, Object> params = new HashMap<String, Object> ();
			params.put("from", "2017-10-13");
			params.put("to", "2017-10-13");
			String instrument = "256265";
			String interval = "minute";*/
			/*HistoricalData data = kiteconnect.getHistoricalData(params, instrument, interval);
			List<HistoricalData> dataArrayList = data.dataArrayList;
			
			for (HistoricalData item : dataArrayList) {
				System.out.println(item.timeStamp + " " + item.open + " " + item.high + " " + item.low + " " + item.close);
			}*/
			
			
		} else {
			System.out.println("Login failed.....");
		}
		
	}
	
	
	public static ArrayList<OHLC> start(KiteConnect kiteconnect) {
		ArrayList<FromTo> fromTo = null;
		try {
			fromTo = getTimeFrames();
		} catch (Exception e) {
			System.out.println("Exception : " +e);
		}
		
		//List<HistoricalData> fullList = new ArrayList<HistoricalData>();
		
		ArrayList<OHLC> fullList = new ArrayList<OHLC>();
		
		for (FromTo item : fromTo) {
			ArrayList<OHLC> frameData = getData(item.getFrom(), item.getTo(), kiteconnect);
			try {
			Thread.sleep(3000); 
			} catch (Exception e) {
				System.out.println("Thread exception.....");
			}
			fullList.addAll(frameData);
		}
		return fullList;
	}
	
	
	public static ArrayList<OHLC> getData(String from,String to,KiteConnect kiteconnect) {
		Map<String, Object> params = new HashMap<String, Object> ();
		params.put("from", from);
		params.put("to", to);
		String instrument = "256265";
		String interval = "minute";
		HistoricalData data = null;
		try {
			data = kiteconnect.getHistoricalData(params, instrument, interval);
		} catch (KiteException e) {
			e.printStackTrace();
		}
		List<HistoricalData> dataArrayList = data.dataArrayList;
		ArrayList<OHLC> list = new ArrayList<OHLC>();
		
		for (HistoricalData item : dataArrayList) {
			list.add(convert(item));
		}
		
		return list;
	}
	
	
	public static OHLC convert(HistoricalData item) {
		OHLC ohlc = null;
		
		String ts = item.timeStamp;
		String[] tokens = ts.split("T");
		String t = tokens[0] + tokens[1].substring(0, tokens[1].lastIndexOf(":")+3);
		
		ohlc = new OHLC(t, item.open+"", item.high+"", item.low+"", item.close+"");
		
		
		return ohlc;
	}
	
	
	public static ArrayList<FromTo> getTimeFrames() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("C:\\amazon_key\\data\\2017.txt"));
		ArrayList<FromTo> dates = new ArrayList<FromTo>();
		
		while(true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			line = line.trim();
			if (line.length() >0) {
			String [] tokens = line.split(",");
			FromTo obj = new FromTo(tokens[0],tokens[1]);
			dates.add(obj);
			}
		}
		return dates;
	}
	
	
	public static void writeToFile() {
		
	}
}

class FromTo {
	private String from;
	private String to;
	
	public FromTo(String from, String to) {
		super();
		this.from = from;
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
}

class OHLC {
	String t;
	String o;
	String h;
	String l;
	String c;
	
	public OHLC(String t, String o, String h, String l, String c) {
		super();
		this.t = t;
		this.o = o;
		this.h = h;
		this.l = l;
		this.c = c;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getO() {
		return o;
	}

	public void setO(String o) {
		this.o = o;
	}

	public String getH() {
		return h;
	}

	public void setH(String h) {
		this.h = h;
	}

	public String getL() {
		return l;
	}

	public void setL(String l) {
		this.l = l;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}
	
	
	
	
}
