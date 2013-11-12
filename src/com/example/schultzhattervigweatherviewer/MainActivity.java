package com.example.schultzhattervigweatherviewer;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.text.TextUtils;
import android.view.Menu;

public class MainActivity extends Activity {
	
	private FragmentManager _fragmentManager;
	private FragmentForecast _fragmentForecast;
	private final String FRAGMENT_FORECAST_TAG = "ForecastTag";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            _fragmentManager = getFragmentManager();
            
            
            _fragmentForecast = (FragmentForecast) _fragmentManager.findFragmentByTag(FRAGMENT_FORECAST_TAG);
            if (_fragmentForecast == null)
            {
            	_fragmentForecast = new FragmentForecast();
            }
            
            _fragmentManager.beginTransaction().replace(R.id.fragmentContainerFrame, _fragmentForecast, FRAGMENT_FORECAST_TAG ).commit();
            
            // Get City array from resources.
            //_citiesArray = getResources().getStringArray(R.array.cityArray);

            // By default, first element is "favorite" city, go get location.
            // TextUtils.split() takes a regular expression and in the case
            // of a pipe delimiter, it needs to be escaped.
            //showForecast(TextUtils.split(_citiesArray[0], "\\|")[0]);
    }

    private void showForecast(String zipCode)
    {
            // HINT: Use bundle to pass arguments to fragment.
            //
            //                Bundle bundle = new Bundle();
            //                bundle.putString("key", "value");
            //                ForecastFragment.setArguments(bundle);
            
            // HINT: FragmentManager().beginTransaction()
    	
    		
            
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
