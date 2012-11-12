package dataengine;

import dataengine.DataEngineDownloader;


public class JezeroDataEngine implements DataEngineInterface {	
	public String[] getData()
	{
		DataEngineDownloader down = new DataEngineDownloader();
		//weatherData = urllib.urlopen("http://www.bohinj.si/cam/weather.js").read().replace('\t','').replace('\r','').replace("var",'').replace(';','').split("\n")
		String weatherData1 = down.getPage("http://www3.bohinj.si/cam/weather.js");
		String [] weatherData = weatherData1.replaceAll("\t","").replaceAll("\r","").replaceAll("var","").replaceAll(";","").split("\n");
		String date = weatherData[17].substring(weatherData[17].indexOf('"'),weatherData[17].length()).replaceAll("\"","");
		weatherData = weatherData1.replaceAll("\t","").replaceAll("\r","").replaceAll("var","").replaceAll(";","").replaceAll(" ","").split("\n");
		String waterData1 = down.getPage("http://www3.bohinj.si/cam/thst2.js");
		String [] waterData = waterData1.replaceAll("\t","").replaceAll("\r","").replaceAll("var","").replaceAll(";","").replaceAll(" ","").split("\n");
		
		String outTemp = weatherData[4].substring(weatherData[4].indexOf('"'),weatherData[4].length()).replaceAll("\"","") + "°C";
		String outTempAvg = weatherData[5].substring(weatherData[5].indexOf('"'),weatherData[5].length()).replaceAll("\"","") + "°C";
		String outHum = weatherData[6].substring(weatherData[6].indexOf('"'),weatherData[6].length()).replaceAll("\"","") + "%";
		String pressure = weatherData[12].substring(weatherData[12].indexOf('"'),weatherData[12].length()).replaceAll("\"","") + " mbar"; 
		String lakeTemp = waterData[0].substring(waterData[0].indexOf('"'),waterData[0].length()).replaceAll("\"","") + "°C";
		String dew = weatherData[8].substring(weatherData[8].indexOf('"'),weatherData[8].length()).replaceAll("\"","") + " °C";
		String windSpeed = weatherData[0].substring(weatherData[0].indexOf('"'),weatherData[0].length()).replaceAll("\"","");
		String windDir = weatherData[3].substring(weatherData[3].indexOf('"'),weatherData[3].length()).replaceAll("\"","");
		String [] out = {date, outTemp, outTempAvg, lakeTemp, outHum, pressure, dew, String.format("%s (%s)", windSpeed, windDir)}; 
		return out;
	}
	public String[] getLabels() {
		String [] out= {"Čas podatkov", "Zunanja temperatura", "Povprečna temperatura", "Temperatura jezera", "Vlažnost", "Zračni tlak", "Rosišče", "Veter"};
		return out;
	}
}