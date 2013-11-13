package com.example.schultzhattervigweatherviewer;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;


public class MainActivity extends Activity{
	
	private final String TAG = "MAIN_ACTIVITY";
	private FragmentManager _fragmentManager;
	private FragmentForecast _fragmentForecast;
	private final String FRAGMENT_FORECAST_TAG = "ForecastTag";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            _fragmentManager = getFragmentManager();
            
            //Instantiate _fragmentForecast
            _fragmentForecast = (FragmentForecast) _fragmentManager.findFragmentByTag(FRAGMENT_FORECAST_TAG);
            if (_fragmentForecast == null)
            {
            	_fragmentForecast = new FragmentForecast();
            }
            
            //_fragmentManager.beginTransaction().replace(R.id.fragmentContainerFrame, _fragmentForecast, FRAGMENT_FORECAST_TAG ).commit();
            
            showForecast("57701");
            
    }

    private void showForecast(String zipCode)
    {    	
    	Bundle bundle = new Bundle();
    	bundle.putString(FragmentForecast.FORECAST_KEY, zipCode);
    	bundle.putString(_fragmentForecast.LOCATION_KEY, zipCode);
    	
        _fragmentForecast.setArguments(bundle);  //Must set arguments before fragmentManager.beginTransaction()
        
        _fragmentManager.beginTransaction().replace(R.id.fragmentContainerFrame, _fragmentForecast, FRAGMENT_FORECAST_TAG ).commit();
    }

}
