package com.jernejovc.bohinjweatherinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.jernejovc.bohinjweatherinfo.webcamengine.WebcamEngine;

import com.jernejovc.bohinjweatherinfo.dataengine.DataEngine;
import com.jernejovc.bohinjweatherinfo.dataengine.DataEngines;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class DataFragment extends Fragment{
	WebcamEngine webcamEngine;
	UpdateDataTask updateDataTask;
	boolean creating;
	String locale;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		creating = true;
		locale = Locale.getDefault().getLanguage();
		View view = inflater.inflate(R.layout.data_layout, container, false);

		String [] data_names = {"Jezero", "Češnjica", "Bohinjska Bistrica", "Vogel"};
		Spinner dataSpin = (Spinner) view.findViewById(R.id.dataSpinner);
		ArrayAdapter dataadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, data_names);
		dataSpin.setAdapter(dataadapter);
		dataSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int pos, long id) {
				if(!creating)
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

			TextView cancelText = (TextView) view.findViewById(R.id.dataCancelText);
			if(locale.equals("sl"))
				cancelText.setText("<b>Nalagam...</b> <i>(pritisnite za preklic)</i>");
			else
				cancelText.setText("<b>Loading...</b> <i>(tap to cancel)</i>");
			
			cancelText.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					updateDataTask.cancel(true);
				}
			});
		}

		Button refreshButton = (Button) view.findViewById(R.id.dataRefreshButton);
		refreshButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				updateData(v);				
			}
		});

		view.setOnKeyListener( new OnKeyListener()
		{
			@Override
			public boolean onKey( View v, int keyCode, KeyEvent event )
			{
				if( keyCode == KeyEvent.KEYCODE_BACK )
				{
					while(getFragmentManager().getBackStackEntryCount()>0){
						getFragmentManager().popBackStack();
					}
				}
				return false;
			}
		} );

		creating = false;
		return view;
	}

	public void updateData(View v){
		DataEngine engine = new DataEngine(); 
		Spinner dataspin = (Spinner) getView().findViewById(R.id.dataSpinner); 
		LinearLayout toplay = (LinearLayout) getView().findViewById(R.id.dataselectorlayout);
		LinearLayout progresslay = (LinearLayout) getView().findViewById(R.id.dataprogresslayout);
		progresslay.setVisibility(View.VISIBLE);
		toplay.setVisibility(View.GONE);
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

	private class UpdateDataTask extends AsyncTask<UpdateDataTaskHelper, Integer, UpdateDataTaskReturnHelper> {
		Activity activity;
		protected UpdateDataTaskReturnHelper doInBackground(UpdateDataTaskHelper... params) {
			DataEngine engine = ((UpdateDataTaskHelper)params[0]).engine;
			DataEngines selected = ((UpdateDataTaskHelper)params[0]).selected;
			Activity activity = ((UpdateDataTaskHelper)params[0]).activity;
			this.activity = activity;
			List<HashMap<String,String>> result = engine.getData(selected);
			UpdateDataTaskReturnHelper out = new UpdateDataTaskReturnHelper(result, activity);
			return out;
		}


		protected void onPostExecute(UpdateDataTaskReturnHelper result) {
			ListView view = (ListView) getView().findViewById(R.id.dataList);
			LinearLayout toplay = (LinearLayout) getView().findViewById(R.id.dataselectorlayout);
			LinearLayout progresslay = (LinearLayout) getView().findViewById(R.id.dataprogresslayout);
			progresslay.setVisibility(View.GONE);
			toplay.setVisibility(View.VISIBLE);
			List<HashMap<String, String>> data = result.result;
			Activity activity = result.activity;
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
			SimpleAdapter adapter = new SimpleAdapter(activity, listdata,
					android.R.layout.simple_list_item_2,
					new String[] {"data", "label"},
					new int[] {android.R.id.text1,
					android.R.id.text2});
			view.setAdapter(adapter);
			view.setDividerHeight(0);
		}
		protected void onCancelled()
		{
			LinearLayout toplay = (LinearLayout) getView().findViewById(R.id.dataselectorlayout);
			LinearLayout progresslay = (LinearLayout) getView().findViewById(R.id.dataprogresslayout);
			progresslay.setVisibility(View.GONE);
			toplay.setVisibility(View.VISIBLE);

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
		public Activity activity;
		public UpdateDataTaskHelper(DataEngine engine, DataEngines selected, Activity activity)
		{
			this.engine = engine;
			this.selected = selected;
			this.activity = activity;
		}
	}

	private class UpdateDataTaskReturnHelper
	{
		public List<HashMap<String,String>>  result;
		public Activity activity;

		public UpdateDataTaskReturnHelper(List<HashMap<String,String>> result , Activity activity)
		{
			this.result = result;
			this.activity = activity;
		}
	}
}
