package com.example.schultzhattervigweatherviewer;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;


public class MainActivity extends Activity implements IListeners{
	
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
            
            _fragmentManager.beginTransaction().replace(R.id.fragmentContainerFrame, _fragmentForecast, FRAGMENT_FORECAST_TAG ).commit();
            
            showForecast("57701");
            
    }

    private void showForecast(String zipCode)
    {    	
        Forecast forecast = new Forecast();
        Forecast.LoadForecast loadedForecast = forecast.new LoadForecast(this, this);
        loadedForecast.execute("57701");
        
        Log.d(TAG, "Hello There!");
        
        //ForecastLocation forecastLocation = new ForecastLocation();
        //ForecastLocation.LoadLocation loadedLocation = forecastLocation.new LoadLocation(this, this);
        //loadedLocation.execute("57701");
        
        //All of these will be null since forecast executes async
        //Log.d(TAG, forecast.getChancePrecip());
        //Log.d(TAG, forecast.getFeelsLike());
        //Log.d(TAG, forecast.getHumidity());
        //Log.d(TAG, forecast.getTemperature());
        Log.d(TAG, "Hello There! (again)");
        
    	Bundle bundle = new Bundle();
    	bundle.putString("KEY", "Hello");
        //Parcelable parcelable = forecast;
        //bundle.putParcelable(FragmentForecast.FORECAST_KEY, parcelable);
        
        _fragmentForecast.setArguments(bundle);  //Must set arguments before fragmentManager.beginTransaction()
        
        _fragmentManager.beginTransaction().replace(R.id.fragmentContainerFrame, _fragmentForecast, FRAGMENT_FORECAST_TAG ).commit();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onLocationLoaded(ForecastLocation forecastLocation) 
	{
		_fragmentForecast.updateLocation(forecastLocation);
	}

	@Override
	public void onForecastLoaded(Forecast forecast) 
	{
        Log.d(TAG, forecast.getChancePrecip());
        Log.d(TAG, forecast.getFeelsLike());
        Log.d(TAG, forecast.getHumidity());
        Log.d(TAG, forecast.getTemperature());
		_fragmentForecast.updateForecast(forecast);
	}

}
