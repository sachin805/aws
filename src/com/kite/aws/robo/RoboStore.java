package com.kite.aws.robo;

import java.time.LocalTime;

public class RoboStore {
	
	public static RoboOrder order;
	
	public static double netPoints;
	
	public static LocalTime executionTime;
	
	public static LocalTime twoMins;
	
	public static boolean oneTime2MinCheck = false;
	
	public static String trailSL = "NA";
	
	public static LocalTime delayTime;
	
	public static LocalTime exitTime;
	
	
	// robo specific attributes
	public static String quantity = "";
	
	public static String direction = "";
	
	public static String position = "";
	
	public static String instrument = "";
	
	public static void clearRoboStore() {
		order = null;
		netPoints = 0.0;
	//	executionTime = null;
		twoMins = null;
		oneTime2MinCheck = false;
		trailSL = "NA";
	}
}
