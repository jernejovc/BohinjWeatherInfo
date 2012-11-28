package com.jernejovc.bohinjweatherinfo.forecastengine;

import java.util.ArrayList;

public class ForecastReturnHelper {
	ArrayList<ForecastDay> days;
	String time;
	
	public ForecastReturnHelper(ArrayList<ForecastDay> days,String time)
	{
		this.days = days;
		this.time = time;
	}
	
	public ArrayList<ForecastDay> getDays() {
		return days;
	}

	public String getTime() {
		return time;
	}
}
