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
        public final String TAG = "Forecast Fragment";
        
        private TextView _textViewLocation;
        private TextView _textViewChanceOfPrecip;
        private TextView _textViewFeelsLike;
        private TextView _textViewHumidity;
        private TextView _textViewTemperature;
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
                
                //Make async call to forecast
                Forecast forecast = new Forecast();
                Forecast.LoadForecast loadedForecast = forecast.new LoadForecast(getActivity(), this);
                loadedForecast.execute(forecastZipCode);
                
                //Make async call to location
                ForecastLocation location = new ForecastLocation();
                ForecastLocation.LoadLocation loadedLocation = location.new LoadLocation(getActivity(), this);
                loadedLocation.execute(locationZipCode);
        }

        @Override
        public void onSaveInstanceState(Bundle savedInstanceStateBundle)
        {
                super.onSaveInstanceState(savedInstanceStateBundle);
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
                _imageView = (ImageView)rootView.findViewById(R.id.imageForecast);
                _progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
                _textViewProgress = (TextView)rootView.findViewById(R.id.textViewProgressBar);
                
                return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceStateBundle)
        {
                super.onActivityCreated(savedInstanceStateBundle);
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
			_progressBar.setVisibility(View.INVISIBLE);
			_textViewProgress.setVisibility(View.INVISIBLE);
			
        	_textViewChanceOfPrecip.setText( forecast.getChancePrecip() );
        	_textViewFeelsLike.setText( forecast.getFeelsLike() );
        	_textViewHumidity.setText( forecast.getHumidity() );
        	_textViewTemperature.setText( forecast.getTemperature() );
        	_imageView.setImageBitmap(forecast.Image);
			
		}

}