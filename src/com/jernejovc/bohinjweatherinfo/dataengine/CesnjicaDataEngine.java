package com.jernejovc.bohinjweatherinfo.dataengine;

import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jernejovc.bohinjweatherinfo.dataengine.DataEngineDownloader;
import com.jernejovc.bohinjweatherinfo.dataengine.DataEngineInterface;



public class CesnjicaDataEngine implements DataEngineInterface {

	@Override
	public String[] getData() throws Exception {
		DataEngineDownloader down = new DataEngineDownloader();
		String page = down.getPage("http://www2.arnes.si/~smisma1/bohinj.html");
		Document doc = Jsoup.parse(page);
		String temperatura="N/A", 
				vlaga="N/A", 
				tlak="N/A", 
				veter="N/A",
				cas = "N/A",
				rosisce = "N/A",
				mraz = "N/A",
				sneg = "N/A";
		Elements rows = doc.getElementsByTag("tr");
		for(Element row :  rows)
		{ 
			Elements tds = row.getElementsByTag("td");
			try
			{
				String text = tds.get(0).getElementsByTag("font").get(0).getElementsByTag("b").get(0).getElementsByTag("nobr").get(0).ownText();
				if(text.equalsIgnoreCase("temperatura"))
					temperatura = tds.get(2).getElementsByTag("font").get(1).getElementsByTag("font").get(0).getElementsByTag("nobr").text().split(" ")[0].replace(',', '.') + "°C";
				if(text.equalsIgnoreCase("zunanja vlaga"))
					vlaga = tds.get(2).getElementsByTag("font").get(1).getElementsByTag("font").get(0).getElementsByTag("nobr").text().replace(',', '.');
				if(text.equalsIgnoreCase("tlak"))
					tlak = tds.get(3).getElementsByTag("font").get(1).getElementsByTag("font").get(0).getElementsByTag("nobr").text().replace(',', '.');
				if(text.equalsIgnoreCase("veter"))
					veter = tds.get(2).getElementsByTag("font").get(1).getElementsByTag("font").get(0).getElementsByTag("nobr").text().replace(',', '.');
				if(text.equalsIgnoreCase("datum"))
					cas = tds.get(2).getElementsByTag("font").text();
				if(text.equalsIgnoreCase("�AS"))
					cas += " " + tds.get(2).getElementsByTag("b").get(0).getElementsByTag("font").text();
				if(text.equalsIgnoreCase("ROSI��E"))
				{
					rosisce = tds.get(3).getElementsByTag("font").get(1).getElementsByTag("font").get(0).getElementsByTag("nobr").text().replace(',', '.');
					rosisce = rosisce.substring(0, rosisce.length()-3) + "°C";
				}
				if(text.equalsIgnoreCase("ob�utek mraza"))
				{
					mraz = tds.get(2).getElementsByTag("font").get(1).getElementsByTag("font").get(0).getElementsByTag("nobr").text().replace(',', '.');
					mraz = mraz.substring(0, mraz.length()-3) + "°C";
				}
				
				if(text.equalsIgnoreCase("padavine"))
				{
					Element td = tds.get(2);
					Element table = td.getElementsByTag("table").get(0);
					for(Element trow : table.getElementsByTag("tr"))
					{
						if(trow.getElementsByTag("td").get(0).text().equalsIgnoreCase("sne�ne razmere"))
							sneg = trow.getElementsByTag("td").get(2).getElementsByTag("b").get(0).getElementsByTag("font").get(0).text().replace(',', '.');
					}
				}
				
					
			}
			catch (Exception e)
			{}
		}
		if (veter.contains("km/h"))
		{
			int windidx = veter.indexOf("km/h") + 4;
			veter = veter.substring(0, windidx);
		}
		String [] out = {cas, temperatura, vlaga, tlak, veter, rosisce, mraz, sneg};
		return out;
	}

	@Override
	public String[] getLabels() {
		String locale = Locale.getDefault().getDisplayLanguage();
		if(locale.equalsIgnoreCase("slovenščina"))
			return new String [] {"Čas podatkov","Temperatura", "Vlažnost", "Tlak", "Veter", "Rosišče", "Občutek mraza", "Snežne razmere"};
		else 
			return new String [] {"Data time","Temperature", "Humidity", "Pressure", "Wind", "Dew", "Feels like", "Snow height"};
		
	}
}