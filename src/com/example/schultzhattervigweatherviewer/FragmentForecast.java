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

public class FragmentForecast extends Fragment implements IListeners
{
        public static final String LOCATION_KEY = "key_location";
        public static final String FORECAST_KEY = "key_forecast";
        
        public final String SAVED_LOCATION = "saved_location_key";
        public final String SAVED_FORECAST ="saved_forecast_key";
        public final String TAG = "Forecast Fragment";
        
        private Forecast _currentForecast;
        private ForecastLocation _currentLocation;
       
        
        private TextView _textViewLocation;
        private TextView _textViewChanceOfPrecip;
        private TextView _textViewFeelsLike;
        private TextView _textViewHumidity;
        private TextView _textViewTemperature;
        private TextView _textViewTime;
        private ImageView _imageView;
        private ProgressBar _progressBar;
        private TextView _textViewProgress;
        

        @Override
        public void onCreate(Bundle argumentsBundle)
        {
                super.onCreate(argumentsBundle);
                
                Bundle bundle = this.getArguments();
                String forecastZipCode = bundle.getString(FORECAST_KEY);
                String locationZipCode = bundle.getString(LOCATION_KEY);
                
                _currentForecast = null;
                _currentLocation = null;
                
                if(argumentsBundle != null)
                {
                	_currentForecast = (Forecast)argumentsBundle.getParcelable(SAVED_FORECAST);
                	_currentLocation = (ForecastLocation)argumentsBundle.getParcelable(SAVED_LOCATION);
                }
                if(_currentForecast == null || _currentLocation == null)
                {
                	if(isNetworkAvailable())
                	{
	                //Make async call to forecast
	                Forecast forecast = new Forecast();
	                Forecast.LoadForecast loadedForecast = forecast.new LoadForecast(getActivity(), this);
					loadedForecast.execute(forecastZipCode);
	                
	                
	                //Make async call to location
	                ForecastLocation location = new ForecastLocation();
	                ForecastLocation.LoadLocation loadedLocation = location.new LoadLocation(getActivity(), this);
	                loadedLocation.execute(locationZipCode);
                	}
                	else
                	{
                		//Display Toast
                		Context context = getActivity();
                		Toast.makeText(context, "Network Unavailable", Toast.LENGTH_LONG).show();
                		
                		Log.d(TAG, "NETWORK UNAVAILABLE!!!");
                	}
                }
        }

		@Override
        public void onSaveInstanceState(Bundle savedInstanceStateBundle)
        {
                super.onSaveInstanceState(savedInstanceStateBundle);

                savedInstanceStateBundle.putParcelable(SAVED_FORECAST, _currentForecast);
                savedInstanceStateBundle.putParcelable(SAVED_LOCATION, _currentLocation);
                
                
        }

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
                
                if(_currentForecast != null && _currentLocation != null)
                {
                	updateDisplay(true);
                }
                
                return rootView;
        }

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
        
        @Override
        public void onDestroy()
        {             
                super.onDestroy();
        }
        

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
		
        private boolean isNetworkAvailable() 
        {
            ConnectivityManager connectivityManager 
                  = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

}