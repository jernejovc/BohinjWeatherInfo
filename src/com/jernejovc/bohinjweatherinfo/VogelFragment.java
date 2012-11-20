package com.jernejovc.bohinjweatherinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jernejovc.bohinjweatherinfo.dataengine.VogelEngine;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;


public class VogelFragment extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.vogel_layout, container, false);
		Button refreshButton = (Button) view.findViewById(R.id.vogelRefreshButton);
		refreshButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Button refreshButton = (Button) getView().findViewById(R.id.vogelRefreshButton);
				refreshButton.setVisibility(View.GONE);

				ProgressBar progress = (ProgressBar) getView().findViewById(R.id.vogelProgressBar);
				progress.setVisibility(View.VISIBLE);
				new VogelDataDownloadThread().execute();

			}
		});

		return view;
	}

	private class VogelDataDownloadThread extends AsyncTask<Void, Void, ArrayList<HashMap<String,String>>>{

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Void... params) {
			VogelEngine engine = new VogelEngine();
			ArrayList<HashMap<String,String>> result;
			result = engine.getWorking();
			return result;
		}

		protected void onPostExecute(ArrayList<HashMap<String,String>> result)
		{

			List<Map<String, String>> napravelistdata = new ArrayList<Map<String, String>>();
			List<Map<String, String>> progelistdata = new ArrayList<Map<String, String>>();

			if(result == null)
			{
				Map<String,String> map = new HashMap<String,String>();
				map.put("data", "Ni podatkov");
				map.put("label", "Spletna stran se ne odziva / napaka podatkov");
				napravelistdata.add(map);
				progelistdata.add(map);
			}
			else
			{
				for(HashMap<String,String> map : result)
				{
					if(map.get("kind").equals("lift"))
					{
						Map<String, String> temp = new HashMap<String, String>(2);
						temp.put("data", map.get("opened"));
						temp.put("label", map.get("name"));
						napravelistdata.add(temp);
					}
					else
					{
						Map<String, String> temp = new HashMap<String, String>(2);
						temp.put("data", map.get("opened"));
						temp.put("label", map.get("name"));
						progelistdata.add(temp);
					}
				}
			}
			SimpleAdapter napraveadapter = new SimpleAdapter(getActivity(), napravelistdata,
					android.R.layout.simple_list_item_2,
					new String[] {"data", "label"},
					new int[] {android.R.id.text1,
				android.R.id.text2});
			SimpleAdapter progeadapter = new SimpleAdapter(getActivity(), progelistdata,
					android.R.layout.simple_list_item_2,
					new String[] {"data", "label"},
					new int[] {android.R.id.text1,
				android.R.id.text2});
			
			ListView napraveView = (ListView) getView().findViewById(R.id.vogelDataList);
			ListView progeView = (ListView) getView().findViewById(R.id.vogelOpenedList);
			
			napraveView.setAdapter(napraveadapter);
			progeView.setAdapter(progeadapter);
			
			napraveView.setDividerHeight(0);
			progeView.setDividerHeight(0);

			Button refreshButton = (Button) getView().findViewById(R.id.vogelRefreshButton);
			refreshButton.setVisibility(View.VISIBLE);

			ProgressBar progress = (ProgressBar) getView().findViewById(R.id.vogelProgressBar);
			progress.setVisibility(View.GONE);
		}

	}
}
