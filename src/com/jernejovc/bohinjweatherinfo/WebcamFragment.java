package com.jernejovc.bohinjweatherinfo;

import com.jernejovc.bohinjweatherinfo.webcamengine.WebcamEngine;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class WebcamFragment extends Fragment{
	WebcamEngine webcamEngine;
	boolean firstRun;
	boolean helperTextsShown;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		firstRun = true;
		helperTextsShown = true;
		
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.webcam_layout, container, false);
		ProgressBar bar = (ProgressBar) view.findViewById(R.id.webcamProgress);
		Spinner webspin = (Spinner) view.findViewById(R.id.webcamSpinner);
		ImageView refresh = (ImageView) view.findViewById(R.id.webcamRefresh);
		webcamEngine = new WebcamEngine();
		int num_cams = webcamEngine.getWebcamUrls().size();
		String[]webcam_names = new String[num_cams];
		int i = 0;
		for(String[] s : webcamEngine.getWebcamUrls())
			webcam_names[i++] = s[0];

		ArrayAdapter webcamadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, webcam_names);

		webspin.setAdapter(webcamadapter);
		webspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View v,int pos, long id) {
				updateWebcamButton(v);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateWebcamButton(v);
			}
		});

		WebView webview = (WebView) view.findViewById(R.id.webcamWebView);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			//Disable hardware acceleration on Android > 3
			webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		webview.setBackgroundColor(0x000000ff);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		//updateWebcamButton(null);
	}

	public void updateWebcam(String cam_name){
		WebView web = (WebView) getView().findViewById(R.id.webcamWebView);
		ProgressBar bar = (ProgressBar) getView().findViewById(R.id.webcamProgress);
		
		String url = "about:blank";
		for(String[] cam : webcamEngine.getWebcamUrls())
		{
			if(cam[0].equalsIgnoreCase(cam_name))
			{
				url = cam[1];
				break;
			}
		}
		bar.setVisibility(View.VISIBLE);
		String html = String.format("<html><body style=\"background:rgba(0,0,0,255);\"><img src=\"%s\" style=\"width:100%c\"/></body></html>", url, '%');
		web.loadDataWithBaseURL("fake://blah",html, "text/html", "utf-8", "");
		web.getSettings().setLoadWithOverviewMode(true);
		web.getSettings().setUseWideViewPort(true);
		web.getSettings().setBuiltInZoomControls(true);
		web.getSettings().setDefaultZoom(ZoomDensity.FAR);
		web.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress){
				ProgressBar bar = (ProgressBar) getView().findViewById(R.id.webcamProgress);
				bar.setProgress(progress);
				
				if(progress == 100)
				{
					bar.setVisibility(View.INVISIBLE);
				}
				
			}
		});
	}

	public void updateWebcamButton(View v){
		if(firstRun)
		{
			// Don't let event triggered while loading adapter cause refreshing  
			// the webview thus maybe loading unneccesary data over mobile network.
			firstRun = !firstRun;
			
			ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
		    	return;
			}
		}
		if(helperTextsShown)
		{
			TextView dataNotLoadedView = (TextView) getView().findViewById(R.id.webcamNotLoadedText);
		    TextView dataNotLoadedHelperView = (TextView) getView().findViewById(R.id.webcamNotLoadedHelperText);
		    dataNotLoadedView.setVisibility(View.GONE);
		    dataNotLoadedHelperView.setVisibility(View.GONE);
		    helperTextsShown = false;
		}
	    
		Spinner webspin = (Spinner) getView().findViewById(R.id.webcamSpinner);
		String selected_cam = (String) webspin.getSelectedItem();
		updateWebcam(selected_cam);
	}
	
	public void cancelLoading()
	{
		WebView web = (WebView) getView().findViewById(R.id.webcamWebView);
		web.stopLoading();
	}
}
