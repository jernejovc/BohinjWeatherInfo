package com.jernejovc.bohinjweatherinfo.forecastengine;

public class ForecastDay {
	String day;
	String wind;
	int low;
	int high;
	int condition;
	boolean daytime;
	
	public boolean isDaytime() {
		return daytime;
	}

	public ForecastDay(String day, int low, int high, int condition, String wind, boolean daytime)
	{
		this.day = day;
		this.low = low;
		this.high = high;
		this.condition = condition;
		this.wind = wind;
		this.daytime = daytime;
	}

	public String getDay() {
		return day;
	}

	public String getWind() {
		return wind;
	}

	public int getCondition() {
		return condition;
	}

	public int getLow() {
		return low;
	}

	public int getHigh() {
		return high;
	}
}
