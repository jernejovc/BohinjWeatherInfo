package com.jernejovc.bohinjweatherinfo.dataengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class DataEngineDownloader {
	public String getPage(String url) throws Exception
	{
//		try 
//		{
		    URL Url = new URL(url);
		    // Read all the text returned by the server
		    BufferedReader in = new BufferedReader(new InputStreamReader(Url.openStream()));
		    String str;
		    StringBuilder stringBuilder = new StringBuilder();
		    while ((str = in.readLine()) != null) 
		    {
			    stringBuilder.append(str+"\n");
		    }
		    in.close();
		    return stringBuilder.toString();
//		} catch (MalformedURLException e) {
//		} catch (IOException e) {
//		}
//		return null;
    }
}
