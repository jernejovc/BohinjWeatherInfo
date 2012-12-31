package com.jernejovc.bohinjweatherinfo.dataengine;

import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BistricaDataEngine implements DataEngineInterface{

	@Override
	public String[] getData() throws Exception {
		DataEngineDownloader down = new DataEngineDownloader();
		String page = down.getPage("http://bohinjska-bistrica.zevs.si/");
		
		Document doc = Jsoup.parse(page);
		Element main = doc.getElementById("main");
		main = main.getElementById("left");
		
		String temperatura = "N/A",
				vlaga = "N/A",
				tlak = "N/A",
				veter = "N/A",
				padavine = "N/A",
				rosisce = "N/A",
				mraz = "N/A",
				datum = "N/A";
		
		Element date = main.getElementsByTag("p").get(0);
		Elements dates = date.getElementsByTag("font");
		
		datum = dates.get(0).text() + " " + dates.get(1).text();
		
		
		Elements tables = main.getElementsByTag("table");
		Element temphum = tables.get(0);
		Elements fonts = temphum.getElementsByTag("font");
		temperatura = fonts.get(0).text();
		temperatura = temperatura.substring(0,temperatura.length()-3).replace(',', '.') + "°C";
		vlaga = fonts.get(1).text();
		vlaga = vlaga.replace(',', '.');
		
		Element presswind = tables.get(1);
		fonts = presswind.getElementsByTag("font");		
		tlak = fonts.get(1).text();
		String winddir = presswind.getElementsByTag("b").get(1).text();
		veter = windDirection(Integer.valueOf(winddir.split(" ")[0]));
		veter += " " + fonts.get(2).text();
		veter = veter.replace(',', '.');
		
		Element rain = tables.get(2);
		fonts = rain.getElementsByTag("span");
		padavine = fonts.get(0).text();
		padavine = padavine.replace(',', '.');
		
		Element dewcold = tables.get(3);
		fonts = dewcold.getElementsByTag("font");
		rosisce = fonts.get(0).text();
		rosisce = rosisce.substring(0,rosisce.length()-3).replace(',', '.') + "°C";
		mraz = fonts.get(2).text();
		mraz = mraz.substring(0,mraz.length()-3).replace(',', '.') + "°C";
		
		String [] out = {datum, temperatura, tlak, vlaga, veter, padavine, rosisce, mraz};
		return out;
	}

	@Override
	public String[] getLabels() {
		String locale = Locale.getDefault().getDisplayLanguage();
		if(locale.equalsIgnoreCase("slovenščina"))
			return new String [] {"Čas meritve", "Temperatura", "Zračni tlak", "Vlaga", "Veter", "Padavine", "Rosišče", "Občutek mraza"};
		else
			return new String [] {"Data time", "Temperature", "Pressure", "Humidity", "Wind", "Precipitation", "Dew", "Feels like"};
	}
	
	private String windDirection(int angle)
	{
		try
		{
			String [] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"}; 
			int idx = ((angle+(360/8)/2)%360)/(360/8);
			return dirs[idx];
		}
		catch (Exception e) {}
		return "N/A";
	}

}
