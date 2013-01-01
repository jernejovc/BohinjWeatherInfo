package com.jernejovc.bohinjweatherinfo;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jernejovc.bohinjweatherinfo.forecastengine.ForecastDay;
import com.jernejovc.bohinjweatherinfo.forecastengine.ForecastEngine;
import com.jernejovc.bohinjweatherinfo.forecastengine.ForecastReturnHelper;


public class ForecastFragment extends Fragment{
	UpdateForecastTask updateForecastTask;
	boolean firstRun;
	boolean helperTextsShown;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		firstRun = true;
		helperTextsShown = true;
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.forecast_layout, container, false);
		
		
		ImageView refresh = (ImageView) view.findViewById(R.id.forecastRefresh);
		refresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateData();
			}
		});
		
		return view;
	}
	
	public void onViewCreated (View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		updateData();
	}
	
	private void updateData()
	{
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
			TextView dataNotLoadedView = (TextView) getView().findViewById(R.id.forecastNotLoadedText);
		    TextView dataNotLoadedHelperView = (TextView) getView().findViewById(R.id.forecastNotLoadedHelperText);
		    dataNotLoadedView.setVisibility(View.GONE);
		    dataNotLoadedHelperView.setVisibility(View.GONE);
		    helperTextsShown = false;
		}
		
		updateForecastTask = new UpdateForecastTask();
		updateForecastTask.execute(new Object());
		
		ImageView refresh = (ImageView) getView().findViewById(R.id.forecastRefresh);
		ProgressBar bar = (ProgressBar) getView().findViewById(R.id.forecastProgress);
		refresh.setVisibility(View.GONE);
		bar.setVisibility(View.VISIBLE);
	}
	
	private class UpdateForecastTask extends AsyncTask<Object, Integer, ForecastReturnHelper> {
		protected ForecastReturnHelper doInBackground(Object... params) {
			ForecastEngine engine = new ForecastEngine();
			ForecastDay day = new ForecastDay(getResources().getString(R.string.forecastNotAvailableText), -273, 1000, -1, "", true);
			ArrayList<ForecastDay> out = new ArrayList<ForecastDay>();
			out.add(day);
			ForecastReturnHelper data = new ForecastReturnHelper(out, "n/a");
			
			try
			{
				data = engine.getData(getActivity());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return data;
		}


		protected void onPostExecute(ForecastReturnHelper result) {
			ListView forecastview = (ListView) getView().findViewById(R.id.forecastList);
			TextView dataTimeText = (TextView) getView().findViewById(R.id.forecastDataTime);
			ImageView refresh = (ImageView) getView().findViewById(R.id.forecastRefresh);
			ProgressBar bar = (ProgressBar) getView().findViewById(R.id.forecastProgress);
			refresh.setVisibility(View.VISIBLE);
			bar.setVisibility(View.GONE);
			ForecastAdapter adapter = new ForecastAdapter(getActivity(), R.layout.forecast_row_layout, result.getDays());
			forecastview.setAdapter(adapter);
			forecastview.setDividerHeight(0);
			dataTimeText.setText(String.format(getResources().getString(R.string.forecastDataTimeString),result.getTime()));
		}
	}
	
	
	private class ForecastAdapter extends ArrayAdapter<ForecastDay>{
	    Context context;
	    int layoutResourceId;   
	    ArrayList<ForecastDay> data = null;
	   
	    public ForecastAdapter(Context context, int layoutResourceId, ArrayList<ForecastDay> data) {
	        super(context, layoutResourceId, data);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	        this.data = data;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        ForecastHolder holder = null;
	       
	        if(row == null)
	        {
	            LayoutInflater inflater = getActivity().getLayoutInflater();
	            row = inflater.inflate(layoutResourceId, parent, false);
	           
	            holder = new ForecastHolder();
	            holder.dayIcon = (ImageView)row.findViewById(R.id.forecastRowIcon);
	            holder.dayText = (TextView)row.findViewById(R.id.forecastRowDayText);
	            holder.temperatureText = (TextView)row.findViewById(R.id.forecastRowTemperatureText);
	            holder.windText = (TextView)row.findViewById(R.id.forecastRowWindText);
	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (ForecastHolder)row.getTag();
	        }
	       
	        ForecastDay weather = data.get(position);
	        Drawable icon;
			switch (weather.getCondition()) {
			case 0:
				icon = weather.isDaytime() ? getResources().getDrawable(R.drawable.weather_clear) : getResources().getDrawable(R.drawable.weather_clear_night) ;
				break;
			case 1:
			case 2:
				icon = getResources().getDrawable(R.drawable.weather_mist);
				break;
			case 3:
				icon = weather.isDaytime() ? getResources().getDrawable(R.drawable.weather_clouds) : getResources().getDrawable(R.drawable.weather_clouds_night);
				break;
			case 4:
				icon = weather.isDaytime() ? getResources().getDrawable(R.drawable.weather_many_clouds_day): getResources().getDrawable(R.drawable.weather_many_clouds_night);
				break;
			case 5:
				icon = weather.isDaytime() ? getResources().getDrawable(R.drawable.weather_showers_day) : getResources().getDrawable(R.drawable.weather_showers_night);
				break;
			case 6:
				icon = getResources().getDrawable(R.drawable.weather_showers);
				break;
			case 7:
				icon = weather.isDaytime() ? getResources().getDrawable(R.drawable.weather_storm) : getResources().getDrawable(R.drawable.weather_storm_night);
				break;
			case 8:
				icon = weather.isDaytime() ? getResources().getDrawable(R.drawable.weather_many_clouds) : getResources().getDrawable(R.drawable.weather_many_clouds_night);
				break;
			case 9:
				icon = getResources().getDrawable(R.drawable.weather_snow);
				break;
			case 10:
				icon = weather.isDaytime() ? getResources().getDrawable(R.drawable.weather_snow_scattered_day) : getResources().getDrawable(R.drawable.weather_snow_scattered_night);
				break;
			case 11:
			case 12:
				icon = weather.isDaytime() ? getResources().getDrawable(R.drawable.weather_snow_rain) : getResources().getDrawable(R.drawable.weather_snow_rain_night);
				break;
			default:
				icon = getResources().getDrawable(R.drawable.weather_not_available);
			}
	        holder.dayIcon.setImageDrawable(icon);
	        holder.dayText.setText(weather.getDay());
	        if(weather.getHigh() == Integer.MAX_VALUE)
	        	holder.temperatureText.setText(String.format("%d°C",weather.getLow()));
	        else
	        	holder.temperatureText.setText(String.format("%d°C / %d°C",weather.getLow(),weather.getHigh()));
	        holder.windText.setText(weather.getWind());
	       
	        return row;
	    }
	   
	    private class ForecastHolder
	    {
	    	ImageView dayIcon;
	        TextView dayText;
	        TextView temperatureText;
	        TextView windText;
	    }
	}

}
