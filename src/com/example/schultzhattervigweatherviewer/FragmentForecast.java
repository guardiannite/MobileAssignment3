package com.example.schultzhattervigweatherviewer;

import java.util.concurrent.TimeUnit;

import com.example.schultzhattervigweatherviewer.Forecast.LoadForecast;

import android.app.Fragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


/**************************************************************************
 * <h2>Description:</h2>
 * FragmentForecast is the fragment that displays all of the information
 *     on screen after the API call has been made and returns values. If
 *     API call fails this fragment sends a toast message indicating that
 *     there was a connection problem.
 * <p>
 * <b>Date:</b><br>
 * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
 * <p>
 * <b>Extends:</b><br>
 * &nbsp &nbsp &nbsp &nbsp Fragment
 * <p>
 * <b>Implements:</b><br>
 * &nbsp &nbsp &nbsp &nbsp IListeners
 * 
 * @author Josh Schultz
 * @author Erik Hattervig                
 *************************************************************************/
public class FragmentForecast extends Fragment implements IListeners
{
        public static final String LOCATION_KEY = "key_location";
        public static final String FORECAST_KEY = "key_forecast";
        
        public final String SAVED_LOCATION = "saved_location_key";
        public final String SAVED_FORECAST ="saved_forecast_key";
        public final String TAG = "Forecast Fragment";
        
        // The forecast that was returned by the API call
        private Forecast _currentForecast;
        private ForecastLocation _currentLocation;
        
        // Strings containing the zipcode for the API call
        private String _forecastZipCode;
        private String _locationZipCode;
        
        // TextViews, ImageView, and ProgressBar for the fragment
        private TextView _textViewLocation;
        private TextView _textViewChanceOfPrecip;
        private TextView _textViewFeelsLike;
        private TextView _textViewHumidity;
        private TextView _textViewTemperature;
        private TextView _textViewTime;
        private ImageView _imageView;
        private ProgressBar _progressBar;
        private TextView _textViewProgress;
        

        /**************************************************************************
         * <h2>Description:</h2>
         * OnCreate call, restores the state of the fragment from the
         *     argumentsBundle, or calls the API call if the bundle is null
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
         * 
         * @param argumentsBundle - bundle for restoring the fragment from an on
         *                          destroy.
         *************************************************************************/
        @Override
        public void onCreate(Bundle argumentsBundle)
        {
                super.onCreate(argumentsBundle);
                
                _currentForecast = null;
                _currentLocation = null;
                
                if(argumentsBundle != null)
                {
                	_currentForecast = (Forecast)argumentsBundle.getParcelable(SAVED_FORECAST);
                	_currentLocation = (ForecastLocation)argumentsBundle.getParcelable(SAVED_LOCATION);
                }
                
                Bundle bundle = this.getArguments();
                _forecastZipCode = bundle.getString(FORECAST_KEY);
                _locationZipCode = bundle.getString(LOCATION_KEY);
        }

        /**************************************************************************
         * <h2>Description:</h2>
         * Override of the onSaveInstanceState. Saves _currentForecast and 
         *     _currentLocation to the bundle.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
         * 
         * @param argumentsBundle[out] - bundle being saved
         *************************************************************************/
		@Override
        public void onSaveInstanceState(Bundle savedInstanceStateBundle)
        {
                super.onSaveInstanceState(savedInstanceStateBundle);

                savedInstanceStateBundle.putParcelable(SAVED_FORECAST, _currentForecast);
                savedInstanceStateBundle.putParcelable(SAVED_LOCATION, _currentLocation);        
        }

		/**************************************************************************
         * <h2>Description:</h2>
         * Override of the onCreateView. Creates the view and inflates it using the
         *     LayoutInflater passed to it.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
         * 
         * @param inflater - The LayoutInflater being used to create the view
         * @param container
         * @param savedInstaceState
         * 
         * 
         * @return View - The view being created
         * 
         *************************************************************************/
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
                View rootView = inflater.inflate(R.layout.fragment_forecast, null);
                
                _textViewLocation = (TextView)rootView.findViewById(R.id.textViewLocation);
                _textViewChanceOfPrecip = (TextView)rootView.findViewById(R.id.textViewChanceOfPrecip);
                _textViewFeelsLike = (TextView)rootView.findViewById(R.id.textViewFeelsLikeTemp);
                _textViewHumidity = (TextView)rootView.findViewById(R.id.textViewHumidity);
                _textViewTemperature = (TextView)rootView.findViewById(R.id.textViewTemp);
                _textViewTime = (TextView)rootView.findViewById(R.id.textViewAsOfTime);
                _imageView = (ImageView)rootView.findViewById(R.id.imageForecast);
                _progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
                _textViewProgress = (TextView)rootView.findViewById(R.id.textViewProgressBar);
                
                return rootView;
        }

        /**************************************************************************
         * <h2>Description:</h2>
         * Override of the onResume function. Refreshes the forecast.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
         * 
         *************************************************************************/
        @Override
        public void onResume()
        {
        	super.onResume();
        	
        	refreshForecast();
        }
        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * Override of the onActivityCreated instance. If the bundle passed to it
         *     is null, it adds _currentForecast and _currentLocation to it.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
         * 
         * @param savedInstanceStateBundle - bundle for recreating the Fragment.
         * 
         *************************************************************************/
        @Override
        public void onActivityCreated(Bundle savedInstanceStateBundle)
        {
                super.onActivityCreated(savedInstanceStateBundle);

                if(savedInstanceStateBundle != null)
                {
                	_currentForecast = (Forecast)savedInstanceStateBundle.getParcelable(SAVED_FORECAST);
                	_currentLocation = (ForecastLocation)savedInstanceStateBundle.getParcelable(SAVED_LOCATION);
                }
        }
        

        
        /**************************************************************************
         * <h2>Description:</h2>
         * Handles updating the display. Shows toast if the forecastLocation is
         *     null, otherwise it updates _currentLocation and updates the display.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
         * 
         * @param forecastLocation - The ForecastLocation being updated
         * 
         *************************************************************************/
		@Override
		public void onLocationLoaded(ForecastLocation forecastLocation) 
		{
			if(forecastLocation == null)
			{
				//Do toast
        		Context context = getActivity();
        		Toast.makeText(context, "Timeout on webcall", Toast.LENGTH_LONG).show();
				Log.d(TAG, "Forecast location was returned as null");	  //aka the call timed out
				
				updateDisplay(false);
			}
			else
			{
				_currentLocation = forecastLocation;
				
				updateDisplay(true);
			}
		}

		
		/**************************************************************************
         * <h2>Description:</h2>
         * Handles updating the display. Shows toast if the forecastLocation is
         *     null, otherwise it updates _currentLocation and updates the display.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
         * 
         * @param forecast - The Forecast being updated
         * 
         *************************************************************************/
		@Override
		public void onForecastLoaded(Forecast forecast) 
		{
			
			if(forecast == null)
			{
				//Do toast
        		Context context = getActivity();
        		Toast.makeText(context, "Timeout on webcall", Toast.LENGTH_LONG).show();
				Log.d(TAG, "Forecast was returned as null");  //aka the call timed out
				
				updateDisplay(false);
			}
			else
			{
				_currentForecast = forecast;
				
				updateDisplay(true);
			}
		}
		
		
		/**************************************************************************
         * <h2>Description:</h2>
         * Refreshes the Forecast. Calls an async task to receive the current
         *     forecast from the web API call. If the network is not available,
         *     this function displays toast.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
         * 
         *************************************************************************/
		private void refreshForecast()
		{
            if(_currentForecast == null || _currentLocation == null)
            {
            	if(isNetworkAvailable())
            	{
	                //Make async call to forecast
	                Forecast forecast = new Forecast();
	                Forecast.LoadForecast loadedForecast = forecast.new LoadForecast(getActivity(), this);
					loadedForecast.execute(_forecastZipCode);
                
                
	                //Make async call to location
	                ForecastLocation location = new ForecastLocation();
	                ForecastLocation.LoadLocation loadedLocation = location.new LoadLocation(getActivity(), this);
	                loadedLocation.execute(_locationZipCode);
            	}
            	else
            	{
            		//Display Toast
            		Context context = getActivity();
            		Toast.makeText(context, "Network Unavailable", Toast.LENGTH_LONG).show();
            		updateDisplay(false);
            		Log.d(TAG, "NETWORK UNAVAILABLE!!!");
            	}
            }
            else
            {
            	updateDisplay(true);
            }
		}
		
		/**************************************************************************
         * <h2>Description:</h2>
         * This function updates the display if a forecast has been received. If a
         *     forcast has not been received then the display is set to display
         *     the unavailable_message string.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
         * 
         * @param receivedForecast - a boolean indicating whether a forecast has
         *                           been receved or not.
         * 
         *************************************************************************/
		private void updateDisplay(boolean receivedForecast)
		{		
			if(!receivedForecast)
			{
				_progressBar.setVisibility(View.INVISIBLE);
				_textViewProgress.setVisibility(View.INVISIBLE);
				
	        	_textViewChanceOfPrecip.setText( R.string.unavailable_message );
	        	_textViewFeelsLike.setText( R.string.unavailable_message );
	        	_textViewHumidity.setText( R.string.unavailable_message );
	        	_textViewTemperature.setText(R.string.unavailable_message );
	        	_textViewTime.setText( R.string.unavailable_message );
	        	
	        	_textViewLocation.setText( R.string.unavailable_message );
	        	
	        	_imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.unavailable));
	        	
	        	return;
			}
			
			if(_currentForecast == null || _currentLocation == null)
			{
				return;
			}
			
			_progressBar.setVisibility(View.INVISIBLE);
			_textViewProgress.setVisibility(View.INVISIBLE);
			
        	_textViewChanceOfPrecip.setText( _currentForecast.getChancePrecip() );
        	_textViewFeelsLike.setText( _currentForecast.getFeelsLike() );
        	_textViewHumidity.setText( _currentForecast.getHumidity() );
        	_textViewTemperature.setText( _currentForecast.getTemperature() );
        	_textViewTime.setText(_currentForecast.getTime());
        	
        	_imageView.setImageBitmap(_currentForecast.Image);
        	
        	_textViewLocation.setText(_currentLocation.City + " "  + _currentLocation.State);
		}
		
		
		/**************************************************************************
         * <h2>Description:</h2>
         * This function tests to see if there is a network available using a
         *     ConnectivityManager
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
         * 
         * @return true - the network is available <br>
         *         false - the network is not available 
         * 
         *************************************************************************/
        private boolean isNetworkAvailable() 
        {
            ConnectivityManager connectivityManager 
                  = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

}