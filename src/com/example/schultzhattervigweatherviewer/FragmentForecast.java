package com.example.schultzhattervigweatherviewer;

import com.example.schultzhattervigweatherviewer.Forecast.LoadForecast;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FragmentForecast extends Fragment implements IListeners
{
        public static final String LOCATION_KEY = "key_location";
        public static final String FORECAST_KEY = "key_forecast";
        
        public final String SAVED_FORECAST ="saved_forecast_key";
        public final String TAG = "Forecast Fragment";
        
        private Forecast _currentForecast;
       
        
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
                
                if(argumentsBundle == null)
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
                	_currentForecast = (Forecast)argumentsBundle.getParcelable(SAVED_FORECAST);
                	//_textViewTemperature.setText( _currentForecast.getTemperature() );
                }
        }

        @Override
        public void onSaveInstanceState(Bundle savedInstanceStateBundle)
        {
                super.onSaveInstanceState(savedInstanceStateBundle);
                Log.d(TAG, "Hit 100");
                savedInstanceStateBundle.putParcelable(SAVED_FORECAST, _currentForecast);
                Log.d(TAG, "Hit 101");
                
                
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
                
                updateDisplay();
                
                return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceStateBundle)
        {
                super.onActivityCreated(savedInstanceStateBundle);
                Log.d(TAG, "HIT 200");
                if(savedInstanceStateBundle != null)
                {
                	_currentForecast = (Forecast)savedInstanceStateBundle.getParcelable(SAVED_FORECAST);
                }
                Log.d(TAG, "HIT 201");
        }
        
        @Override
        public void onDestroy()
        {             
                super.onDestroy();
        }
        

		@Override
		public void onLocationLoaded(ForecastLocation forecastLocation) 
		{
			_textViewLocation.setText(forecastLocation.City + " "  + forecastLocation.State);
		}

		@Override
		public void onForecastLoaded(Forecast forecast) 
		{
			_currentForecast = forecast;
			updateDisplay();
			/*
			_progressBar.setVisibility(View.INVISIBLE);
			_textViewProgress.setVisibility(View.INVISIBLE);
			
        	_textViewChanceOfPrecip.setText( forecast.getChancePrecip() );
        	_textViewFeelsLike.setText( forecast.getFeelsLike() );
        	_textViewHumidity.setText( forecast.getHumidity() );
        	_textViewTemperature.setText( forecast.getTemperature() );
        	_textViewTime.setText(forecast.getTime());
        	
        	_imageView.setImageBitmap(forecast.Image);
        	*/
			
		}
		
		private void updateDisplay()
		{
			if(_currentForecast == null)
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
		}

}