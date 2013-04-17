package com.jernejovc.bohinjweatherinfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.jernejovc.bohinjweatherinfo.dataengine.DataEngine;
import com.jernejovc.bohinjweatherinfo.dataengine.DataEngines;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class DataFragment extends Fragment{
	UpdateDataTask updateDataTask;
	boolean firstRun;
	boolean helperTextsShown;
	String locale;
	static String preferencesName = "BohinjWeatherInfoPreferences";
	static String preferencesKey = "DataFragmentSelectedValue";
	SharedPreferences preferences;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		firstRun = true;
		helperTextsShown = true;
		locale = Locale.getDefault().getLanguage();
		preferences = getActivity().getSharedPreferences(preferencesName, 0);
		View view = inflater.inflate(R.layout.data_layout, container, false);

		String [] data_names = {"Bohinjska Bistrica", "Češnjica", "Jezero", "Vogel"};
		Spinner dataSpin = (Spinner) view.findViewById(R.id.dataSpinner);
		ArrayAdapter dataadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, data_names);
		dataSpin.setAdapter(dataadapter);
		dataSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int pos, long id) {
				updateData(null);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		String prefs_selected = preferences.getString(preferencesKey, "");
		if(!prefs_selected.equals("")) {
			int pos;
			for(pos = 0; pos < data_names.length;  ++pos) {
				if(data_names[pos].equals(prefs_selected)) {
					dataSpin.setSelection(pos);
					break;
				}
			}
		}
		
		ImageView refresh = (ImageView) view.findViewById(R.id.dataRefresh);
		refresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				updateData(v);				
			}
		});
		
		return view;
	}

	public void updateData(View v){
		if(firstRun)
		{
			// Don't let event triggered while loading adapter cause refreshing  
			// weather data thus maybe loading unneccesary data.
			firstRun = !firstRun;
			
			ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
		    	return;
			}
		}
		if(helperTextsShown)
		{
			//Remove helper texts, as user is manually refreshing data or we are on WiFi
			TextView dataNotLoadedView = (TextView) getView().findViewById(R.id.dataNotLoadedText);
		    TextView dataNotLoadedHelperView = (TextView) getView().findViewById(R.id.dataNotLoadedHelperText);
		    dataNotLoadedView.setVisibility(View.GONE);
		    dataNotLoadedHelperView.setVisibility(View.GONE);
		    helperTextsShown = false;
		}
		
		DataEngine engine = new DataEngine(); 
		Spinner dataspin = (Spinner) getView().findViewById(R.id.dataSpinner); 
		ImageView refresh = (ImageView) getView().findViewById(R.id.dataRefresh);
		ProgressBar bar = (ProgressBar) getView().findViewById(R.id.dataprogress);
		
		refresh.setVisibility(View.GONE);
		bar.setVisibility(View.VISIBLE);

		String selected = (String) dataspin.getSelectedItem();
		if(preferences == null) {
			preferences = getActivity().getSharedPreferences(preferencesName, 0);
		}
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(preferencesKey, selected);
		editor.commit();
		
		DataEngines sel = DataEngines.JEZERO;
		if (selected.equalsIgnoreCase("češnjica"))
			sel = DataEngines.CESNJICA;
		if (selected.equalsIgnoreCase("bohinjska bistrica"))
			sel = DataEngines.BISTRICA;
		if (selected.equalsIgnoreCase("vogel"))
			sel = DataEngines.VOGEL;

		UpdateDataTaskHelper helper = new UpdateDataTaskHelper(engine, sel, getActivity());
		updateDataTask = new UpdateDataTask();
		updateDataTask.execute(helper);
	}

	private class UpdateDataTask extends AsyncTask<UpdateDataTaskHelper, Integer, List<HashMap<String,String>>> {
		protected List<HashMap<String,String>> doInBackground(UpdateDataTaskHelper... params) {
			DataEngine engine = ((UpdateDataTaskHelper)params[0]).engine;
			DataEngines selected = ((UpdateDataTaskHelper)params[0]).selected;
			List<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
			try {
				result = engine.getData(selected);
			}
			catch (UnknownHostException e)
			{
				result.add(createRow(getResources().getString(R.string.network_error), getResources().getString(R.string.network_error_description)));
			}
			catch (ConnectException e)
			{
				result.add(createRow(getResources().getString(R.string.network_error), getResources().getString(R.string.network_error_description)));
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				result.add(createRow(getResources().getString(R.string.data_error), getResources().getString(R.string.data_error_description)));
				result.add(createRow(getResources().getString(R.string.data_error_additional_developer_data), stackTraceToString(e)));
			}
			return result;
		}


		protected void onPostExecute(List<HashMap<String,String>> result) {
			ListView view = (ListView) getView().findViewById(R.id.dataList);
			ImageView refresh = (ImageView) getView().findViewById(R.id.dataRefresh);
			ProgressBar bar = (ProgressBar) getView().findViewById(R.id.dataprogress);
			refresh.setVisibility(View.VISIBLE);
			bar.setVisibility(View.GONE);
			List<HashMap<String, String>> data = result;
//			List<Map<String, String>> listdata = new ArrayList<Map<String, String>>();
//
//			if(data == null)
//			{
//				Map<String,String> map = new HashMap<String,String>();
//				map.put("data", "Ni podatkov");
//				map.put("label", "Spletna stran se ne odziva / napaka podatkov");
//				listdata.add(map);
//			}
//			else
//			{
//				for(HashMap<String, String> k : data)
//				{
//					Map<String, String> map = new HashMap<String, String>(2);
//					map.put("data", k.get("data"));
//					map.put("label", k.get("label"));
//					listdata.add(map);
//				}
//			}
			SimpleAdapter adapter = new SimpleAdapter(getActivity(), data,
					android.R.layout.simple_list_item_2,
					new String[] {"data", "label"},
					new int[] {android.R.id.text1,
					android.R.id.text2});
			view.setAdapter(adapter);
			view.setDividerHeight(0);
		}
		
		private HashMap<String,String> createRow(String data, String label)
		{
			HashMap<String, String> map = new HashMap<String, String>(2);
			map.put("data", data);
			map.put("label", label);
			return map;
		}
		public String stackTraceToString(Throwable e) {
			String retValue = null;
			StringWriter sw = null;
			PrintWriter pw = null;
			try {
			 sw = new StringWriter();
			 pw = new PrintWriter(sw);
			 e.printStackTrace(pw);
			 retValue = sw.toString();
			} finally {
			 try {
			   if(pw != null)  pw.close();
			   if(sw != null)  sw.close();
			 } catch (IOException ignore) {}
			}
			return retValue;
			}
	}
	private class UpdateDataTaskHelper
	{
		public DataEngine engine;
		public DataEngines selected;
		
		public UpdateDataTaskHelper(DataEngine engine, DataEngines selected, Activity activity)
		{
			this.engine = engine;
			this.selected = selected;
		}
	}
}
