package com.jernejovc.bohinjweatherinfo.dataengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class VogelEngine implements DataEngineInterface {
	
	
	public String[] getData() throws Exception
	{
		DataEngineDownloader down = new DataEngineDownloader();
		String [] out = new String[4];
		String wind = "N/A",
				temperature = "N/A",
				snow = "N/A",
				time = "N/A";
		
		String data1 = down.getPage("http://www.snezni-telefon.si/images/vogel/weather.js");
		String [] data = data1.replaceAll("\t","").replaceAll("\r","").replaceAll("var","").replaceAll(";","").split("\n");
		time = data[17].substring(data[17].indexOf('"'), data[17].length()).replaceAll("\"","");
		data = data1.replaceAll("\t","").replaceAll("\r","").replaceAll("var","").replaceAll(";","").replaceAll(" ","").split("\n");
		String page = down.getPage("http://www.vogel.si/zima");
		
		wind = data[3].substring(data[3].indexOf('"'), data[3].length()).replaceAll("\"","");
		wind = wind + " " + data[0].substring(data[0].indexOf('"'), data[0].length()).replaceAll("\"","") + " m/s";
		
		temperature = data[4].substring(data[4].indexOf('"'), data[4].length()).replaceAll("\"","") + "°C";
		Document doc = Jsoup.parse(page);
		Element elt = doc.getElementById("blockStyle8382Sneznaodeja207");
		snow = elt.getElementsByTag("p").get(0).text();

		elt = doc.getElementById("blockStyle7932Obratovanje179");
		
//		for(Element tr : elt.getElementsByTag("tr"))
//		{
//			HashMap<String,String> map = new HashMap<String, String>();
//			String name = tr.getElementsByClass("podatki3").text();
//			String obratuje = tr.getElementsByTag("img").get(0).attr("alt");
//			
//			if(obratuje.contains("ne-obratuje"))
//				obratuje = "ne-obratuje";
//			else
//				obratuje = "obratuje";
//			
//			map.put("data",obratuje);
//			map.put("label", name);
//		}		
		out[0] = time;
		out[1] = temperature;
		out[2] = wind;
		out[3] = snow;
		return out;
	}

	@Override
	public String[] getLabels() {
		String locale = Locale.getDefault().getDisplayLanguage();
		if(locale.equalsIgnoreCase("slovenščina"))
			return new String [] {"Čas podatkov", "Temperatura", "Veter", "Višina snega"};
		else
			return new String []  {"Data time", "Temperature", "Wind", "Snow height"};
		
	}
	
	public ArrayList<HashMap<String,String>> getWorking() throws Exception
	{
		ArrayList<HashMap<String,String>> out = new ArrayList<HashMap<String,String>>();
		DataEngineDownloader down = new DataEngineDownloader();
		String page = down.getPage("http://www.vogel.si/zima/ski-center/zicniske-naprave");
		int begin = page.indexOf("<div id=\"blockStyle8310Vsebinalevo2202\"");
		int end = page.indexOf("<div id=\"blockStyle9059Vsebinalevo2260\"", begin);
		end = page.indexOf("</div>", end);
		page = page.substring(begin, end);
		
		Document doc = Jsoup.parse(page);
		
		Element napraveTable = doc.getElementById("blockStyle8310Vsebinalevo2202");
		for(Element tr : napraveTable.getElementsByTag("tr"))
		{
			if(tr.getElementsByTag("img").size() == 0)
				continue;
			HashMap<String,String> tmp = new HashMap<String, String>();
			tmp.put("kind","lift");
			String opened = tr.getElementsByTag("img").get(0).attr("alt").equals("ne-obratuje.png") ? "opened" : "closed";
			String name = tr.getElementsByClass("podatki3").get(0).text();
			tmp.put("name", name);
			tmp.put("opened", opened);
			
			out.add(tmp);
		}
		
		Element progeTable = doc.getElementById("content-half-desno").getElementsByTag("table").get(0);
		for(Element tr : progeTable.getElementsByTag("tr"))
		{
			if(tr.getElementsByTag("img").size() == 0)
				continue;
			HashMap<String,String> tmp = new HashMap<String, String>();
			tmp.put("kind","run");
			String opened = tr.getElementsByTag("img").get(0).attr("alt").equals("ne-obratuje.png") ? "opened" : "closed";
			String name = tr.getElementsByClass("podatki5").get(0).text();
			tmp.put("name", name);
			tmp.put("opened", opened);
			out.add(tmp);
		}
		return out;
		
	}
}
