package com.example.schultzhattervigweatherviewer;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;

/***************************************************************************
 * <h2>Description:</h2>
 * MainActivity of the app. Attaches the FragmentForecast to the activity 
 *     with the layout inflater.
 * <p>
 * <b>Date:</b><br>
 * &nbsp &nbsp &nbsp &nbsp Novemeber 10, 2012
 * <p>
 * 
 * @author Josh Schultz
 * @author Erik Hattervig
 *
 **************************************************************************/
public class MainActivity extends Activity{
	
	private final String TAG = "MAIN_ACTIVITY";
	private FragmentManager _fragmentManager;
	private FragmentForecast _fragmentForecast;
	private final String FRAGMENT_FORECAST_TAG = "ForecastTag";
	private final String ZIP_CODE = "57701";

	
	/**************************************************************************
     * <h2>Description:</h2>
     * Override of the onCreate function. Attaches the fragmentForecast to the
     *     activity with _fragmentManager.
     * <p>
     * <b>Date:</b><br>
     * &nbsp &nbsp &nbsp &nbsp Novemeber 11, 2012
     * 
     * @param savedInstanceState - a saved bundle for recreation of the
     *                             activity
     *************************************************************************/
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
            	showForecast(ZIP_CODE);
            }
            
            //_fragmentManager.beginTransaction().replace(R.id.fragmentContainerFrame, _fragmentForecast, FRAGMENT_FORECAST_TAG ).commit();
            
            
            
    }

    /**************************************************************************
     * <h2>Description:</h2>
     * Puts the zip code passed to it into the fragment. 
     * <p>
     * <b>Date:</b><br>
     * &nbsp &nbsp &nbsp &nbsp Novemeber 11, 2012
     * 
     * @param zipCode - a String containing the zip code that is being sent to
     *                  the fragment
     *************************************************************************/
    private void showForecast(String zipCode)
    {    	
    	Bundle bundle = new Bundle();
    	bundle.putString(FragmentForecast.FORECAST_KEY, zipCode);
    	bundle.putString(FragmentForecast.LOCATION_KEY, zipCode);
    	
        _fragmentForecast.setArguments(bundle);  //Must set arguments before fragmentManager.beginTransaction()
        
        _fragmentManager.beginTransaction().replace(R.id.fragmentContainerFrame, _fragmentForecast, FRAGMENT_FORECAST_TAG ).commit();
    }

}
