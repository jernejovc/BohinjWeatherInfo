package com.jernejovc.bohinjweatherinfo.dataengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataEngine {
	private DataEngineInterface selected;
	
	public List<HashMap<String,String>> getData(DataEngines engine) throws Exception
	{
		if(engine == DataEngines.JEZERO)
			selected = new JezeroDataEngine();
		if(engine == DataEngines.CESNJICA)
			selected = new CesnjicaDataEngine();
		if(engine == DataEngines.BISTRICA)
			selected = new BistricaDataEngine();
		if(engine == DataEngines.VOGEL)
			selected = new VogelEngine();
		
		List<HashMap<String,String>> out = new ArrayList<HashMap<String, String>>();
		String [] labels = selected.getLabels();
//		try
//		{
			String [] data = selected.getData();
			for(int i = 0; i<labels.length; i++)
			{	
				HashMap<String,String> map = new HashMap<String, String>();
				map.put("label", labels[i]);
				map.put("data", data[i]);
				out.add(map);
			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			return null;
//		}
		
		return out;
	}
}


