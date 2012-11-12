package dataengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class VogelEngine implements DataEngineInterface {
	
	
	public String[] getData()
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
		
		temperature = data[0].substring(data[0].indexOf('"'), data[0].length()).replaceAll("\"","") + "°C";
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
		String [] out = {"Čas podatkov", "Temperatura", "Veter", "Višina snega"};
		
		return out;
	}
	

}
