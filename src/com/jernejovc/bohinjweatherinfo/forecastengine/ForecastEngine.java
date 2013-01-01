package com.jernejovc.bohinjweatherinfo.forecastengine;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;

import com.jernejovc.bohinjweatherinfo.R;
import com.jernejovc.bohinjweatherinfo.dataengine.DataEngineDownloader;

public class ForecastEngine {

	public ForecastReturnHelper getData(Activity activity) throws Exception
	{
		ArrayList<ForecastDay> out = new ArrayList<ForecastDay>();
		DataEngineDownloader down = new DataEngineDownloader();
		String page = down.getPage("http://m.vreme.net/slovenija/gorenjska/bohinj/");
		Document doc = Jsoup.parse(page);

		String [] timerow = doc.getElementsByClass("qData").get(0).getElementsByTag("td").get(0).text().split(" ");
		String time = timerow[timerow.length-1];
		Element forecastTable = doc.getElementsByClass("tData").get(3);
		for(ForecastDay day : parsetDataTable(activity,forecastTable,true))
			out.add(day);
		forecastTable = doc.getElementsByClass("tData").get(4);
		for(ForecastDay day : parsetDataTable(activity,forecastTable, false))
			out.add(day);

		return new ForecastReturnHelper(out, time);
	}

	ArrayList<ForecastDay> parsetDataTable(Activity activity, Element table, boolean isTodaysForecast)
	{
		ArrayList<ForecastDay> out = new ArrayList<ForecastDay>();

		ForecastDay forecastDay;
		int high=Integer.MAX_VALUE,low=Integer.MIN_VALUE,condition=-1;
		String day="",wind="";
		boolean daytime = true;

		for(Element row : table.getElementsByTag("tr"))
		{
			if(isTodaysForecast)
			{
				if(row.hasClass("qData"))
					continue;
				if(row.hasClass("tFirst") || (row.hasClass("tSec") && !row.hasClass("gray")))
				{
					day = row.getElementsByClass("bName").get(0).text();	
					if(day.equalsIgnoreCase("dopoldan"))
						day = activity.getResources().getString(R.string.forecast_day_dopoldan);
					if(day.equalsIgnoreCase("popoldan"))
						day = activity.getResources().getString(R.string.forecast_day_popoldan);
					if(day.equalsIgnoreCase("zvečer"))
						day = activity.getResources().getString(R.string.forecast_day_zvecer);
					if(day.equalsIgnoreCase("ponoči"))
						day = activity.getResources().getString(R.string.forecast_day_noc);
					
					String lowhigh = row.getElementsByClass("tDeg").get(0).text();
					lowhigh = lowhigh.replace("°", " ");
					low = Integer.valueOf(lowhigh.split(" ")[0].trim());
					high = Integer.MAX_VALUE;
					
					String src = row.getElementsByTag("img").get(0).attr("src");
					String [] srcs = src.split("/");
					daytime = srcs[srcs.length-2].equals("dan");
					condition = Integer.valueOf(srcs[srcs.length-1].split("_")[0].trim());
				}
				if((row.hasClass("tSec")&& row.hasClass("gray") && !row.hasClass("tFirst")) || !(row.hasClass("tFirst") || row.hasClass("tSec")))
				{
					String[]wnd = row.getElementsByClass("tDeg").get(0).text().split(",");
					wind = String.format("%s (%s)", wnd[1],wnd[0]);
					
					forecastDay = new ForecastDay(day, low, high, condition, wind, daytime);
					out.add(forecastDay);
				}
				
			}
			else
			{
				if(row.hasClass("tFirst"))
				{
					day = row.getElementsByClass("bName").get(0).text();
					if(day.equalsIgnoreCase("ponedeljek"))
						day = activity.getResources().getString(R.string.forecast_day_ponedeljek);
					if(day.equalsIgnoreCase("torek"))
						day = activity.getResources().getString(R.string.forecast_day_torek);
					if(day.equalsIgnoreCase("sreda"))
						day = activity.getResources().getString(R.string.forecast_day_sreda);
					if(day.equalsIgnoreCase("četrtek"))
						day = activity.getResources().getString(R.string.forecast_day_cetrtek);
					if(day.equalsIgnoreCase("petek"))
						day = activity.getResources().getString(R.string.forecast_day_petek);
					if(day.equalsIgnoreCase("sobota"))
						day = activity.getResources().getString(R.string.forecast_day_sobota);
					if(day.equalsIgnoreCase("nedelja"))
						day = activity.getResources().getString(R.string.forecast_day_nedelja);
					
					String lowhigh = row.getElementsByClass("tDeg").get(0).text();
					lowhigh = lowhigh.replace("°C", "");
					if(lowhigh.contains("/"))
					{
						low = Integer.valueOf(lowhigh.split("/")[0].trim());
						high = Integer.valueOf(lowhigh.split("/")[1].trim());
					}
					else
					{
						lowhigh = lowhigh.replace("°", " ");
						low = Integer.valueOf(lowhigh.split(" ")[0].trim());
						high = Integer.MAX_VALUE;
					}
					String src = row.getElementsByTag("img").get(0).attr("src");
					String [] srcs = src.split("/");
					daytime = srcs[srcs.length-2].equals("dan");
					condition = Integer.valueOf(srcs[srcs.length-1].split("_")[0].trim());
				}
				else if(row.hasClass("tSec"))
				{
					wind = row.getElementsByTag("img").get(0).attr("alt");
				}
				else
				{
					try
					{
						if(wind.contains(","))
						{
							String[]wnd = row.getElementsByClass("tDeg").get(0).text().split(",");
							wind = String.format("%s (%s)", wnd[1],wnd[0]);
						}
						else
							wind = String.format("%s (%s)", row.getElementsByClass("tDeg").get(0).text(), wind);
					}
					catch(Exception e)
					{
						wind = "n/a";
					}
					forecastDay = new ForecastDay(day, low, high, condition, wind,daytime);
					out.add(forecastDay);
				}
			}
		}
		return out;
	}

}
