package com.jernejovc.bohinjweatherinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.jernejovc.bohinjweatherinfo.dataengine.DataEngine;
import com.jernejovc.bohinjweatherinfo.dataengine.DataEngines;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		firstRun = true;
		helperTextsShown = true;
		locale = Locale.getDefault().getLanguage();
		View view = inflater.inflate(R.layout.data_layout, container, false);

		String [] data_names = {"Jezero", "Češnjica", "Bohinjska Bistrica", "Vogel"};
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

		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			ProgressBar bar = (ProgressBar) view.findViewById(R.id.dataprogress);
			bar.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					updateDataTask.cancel(true);				
				}
			});

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
			List<HashMap<String,String>> result = engine.getData(selected);
			return result;
		}


		protected void onPostExecute(List<HashMap<String,String>> result) {
			ListView view = (ListView) getView().findViewById(R.id.dataList);
			ImageView refresh = (ImageView) getView().findViewById(R.id.dataRefresh);
			ProgressBar bar = (ProgressBar) getView().findViewById(R.id.dataprogress);
			refresh.setVisibility(View.VISIBLE);
			bar.setVisibility(View.GONE);
			List<HashMap<String, String>> data = result;
			List<Map<String, String>> listdata = new ArrayList<Map<String, String>>();

			if(data == null)
			{
				Map<String,String> map = new HashMap<String,String>();
				map.put("data", "Ni podatkov");
				map.put("label", "Spletna stran se ne odziva / napaka podatkov");
				listdata.add(map);
			}
			else
			{
				for(HashMap<String, String> k : data)
				{
					Map<String, String> map = new HashMap<String, String>(2);
					map.put("data", k.get("data"));
					map.put("label", k.get("label"));
					listdata.add(map);
				}
			}
			SimpleAdapter adapter = new SimpleAdapter(getActivity(), listdata,
					android.R.layout.simple_list_item_2,
					new String[] {"data", "label"},
					new int[] {android.R.id.text1,
					android.R.id.text2});
			view.setAdapter(adapter);
			view.setDividerHeight(0);
		}
		protected void onCancelled()
		{
			ImageView refresh = (ImageView) getView().findViewById(R.id.dataRefresh);
			ProgressBar bar = (ProgressBar) getView().findViewById(R.id.dataprogress);
			refresh.setVisibility(View.VISIBLE);
			bar.setVisibility(View.GONE);

			List<Map<String, String>> listdata = new ArrayList<Map<String, String>>();
			ListView view = (ListView) getView().findViewById(R.id.dataList);

			Map<String,String> map = new HashMap<String,String>();
			map.put("data", "Ni podatkov");
			map.put("label", "Preklicali ste nalaganje");
			listdata.add(map);

			SimpleAdapter adapter = new SimpleAdapter(getActivity(), listdata,
					android.R.layout.simple_list_item_2,
					new String[] {"data", "label"},
					new int[] {android.R.id.text1,
				android.R.id.text2});
			view.setAdapter(adapter);
			view.setDividerHeight(0);
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
