package com.example.schultzhattervigweatherviewer;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentForecast extends Fragment
{
        public static final String LOCATION_KEY = "key_location";
        public static final String FORECAST_KEY = "key_forecast";
        public final String TAG = "Forecast Fragment";
        
        private TextView _textViewChanceOfPrecip;
        private TextView _textViewFeelsLike;
        private TextView _textViewHumidity;
        private TextView _textViewTemperature;
        private ImageView _imageView;

        @Override
        public void onCreate(Bundle argumentsBundle)
        {
                super.onCreate(argumentsBundle);
                
                Bundle bundle = this.getArguments();
                //Forecast forecast = (Forecast)bundle.getParcelable(LOCATION_KEY);
                
                //Log.d(TAG, forecast.getChancePrecip());
                //Log.d(TAG, forecast.getFeelsLike());
                //Log.d(TAG, forecast.getHumidity());
                //Log.d(TAG, forecast.getTemperature());
                
                //bundle.getParcelable(FORECAST_KEY);
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
                
                _textViewChanceOfPrecip = (TextView)rootView.findViewById(R.id.textViewChanceOfPrecip);
                _textViewFeelsLike = (TextView)rootView.findViewById(R.id.textViewFeelsLikeTemp);
                _textViewHumidity = (TextView)rootView.findViewById(R.id.textViewHumidity);
                _textViewTemperature = (TextView)rootView.findViewById(R.id.textViewTemp);
                _imageView = (ImageView)rootView.findViewById(R.id.imageForecast);
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
        
        public void updateForecast(Forecast forecast)
        {
        	_textViewChanceOfPrecip.setText( forecast.getChancePrecip() );
        	_textViewFeelsLike.setText( forecast.getFeelsLike() );
        	_textViewHumidity.setText( forecast.getHumidity() );
        	_textViewTemperature.setText( forecast.getTemperature() );
        	_imageView.setImageBitmap(forecast.Image);
        	
        }
        
        public void updateLocation(ForecastLocation location)
        {
        	
        }

}