package com.jernejovc.bohinjweatherinfo;

import com.jernejovc.bohinjweatherinfo.webcamengine.WebcamEngine;

import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class WebcamFragment extends Fragment{
	WebcamEngine webcamEngine;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.webcam_layout, container, false);

		WebView web = (WebView) view.findViewById(R.id.webcamWebView) ; 
		Spinner webspin = (Spinner) view.findViewById(R.id.webcamSpinner);
		webcamEngine = new WebcamEngine();
		int num_cams = webcamEngine.getWebcamUrls().size();
		String[]webcam_names = new String[num_cams];
		int i = 0;
		for(String[] s : webcamEngine.getWebcamUrls())
			webcam_names[i++] = s[0];

		ArrayAdapter webcamadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, webcam_names);

		webspin.setAdapter(webcamadapter);
		//		  if(webspin.getSelectedItemPosition() > -1)
		//		  {
		//		  	updateWebcam((String)webspin.getSelectedItem());
		//		  }

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

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		updateWebcamButton(null);
	}

	public void updateWebcam(String cam_name){
		WebView web = (WebView) getView().findViewById(R.id.webcamWebView);
		String url = "about:blank";
		for(String[] cam : webcamEngine.getWebcamUrls())
		{
			if(cam[0].equalsIgnoreCase(cam_name))
			{
				url = cam[1];
				break;
			}
		}
		web.clearCache(true);
		String html = String.format("<html><body><img src=\"%s\" style=\"width:100%c\"/></body></html>", url, '%');
		web.loadDataWithBaseURL("fake://blah",html, "text/html", "utf-8", "");
		web.getSettings().setLoadWithOverviewMode(true);
		web.getSettings().setUseWideViewPort(true);
		web.getSettings().setBuiltInZoomControls(true);
		web.getSettings().setDefaultZoom(ZoomDensity.FAR);
		web.setWebViewClient(new WebViewClient() {

			   public void onPageFinished(WebView view, String url) {
				   view.setBackgroundColor(0x00000000);
			    }
			});
		
	}

	public void updateWebcamButton(View v){
		Spinner webspin = (Spinner) getView().findViewById(R.id.webcamSpinner);
		String selected_cam = (String) webspin.getSelectedItem();
		updateWebcam(selected_cam);
	}

}
