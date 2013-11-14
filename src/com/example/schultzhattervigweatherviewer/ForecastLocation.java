package com.example.schultzhattervigweatherviewer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;

public class ForecastLocation implements Parcelable
{

        private static final String TAG = "";
        
        // http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
        // NOTE:  See example JSON in doc folder.
        private String _URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" + 
                                     "&api_key=g5ag6gquyxh7czgfb4bj6b4b";
        

        public ForecastLocation()
        {
                ZipCode = null;
                City = null;
                State = null;
                Country = null;
        }
        
        public ForecastLocation(Parcel parcel)
        {
        	ZipCode = parcel.readString();
        	City = parcel.readString();
        	State = parcel.readString();
        	Country = parcel.readString();
        }

        
        //Parameters String -> Zip Code
        //Progress Void
        //Result ForecastLocation
        public class LoadLocation extends AsyncTask<String, Void, ForecastLocation>
        {
                private IListeners _listener;
                private Context _context;

                public LoadLocation(Context context, IListeners listener)
                {
                        _context = context;
                        _listener = listener;
                }

                protected ForecastLocation doInBackground(String... params)
                {
                        ForecastLocation forecastLocation = new ForecastLocation();

                        try
                        {
                        	URL url = new URL(String.format(_URL, params[0]));
                        	InputStreamReader reader = new InputStreamReader( url.openStream());
                        	JsonReader jsonReader = new JsonReader(reader);
                        	jsonReader.beginObject();
                        	
                        	String name = jsonReader.nextName();
                        	if(name.equals("location"))
                        	{

                        		jsonReader.beginObject();
                        		while(jsonReader.hasNext())
                        		{
                        			
                        			name = jsonReader.nextName();
                        			if(jsonReader.peek() == null)
                        			{
                        				jsonReader.skipValue();
                        			}
                        			else if(name.equals("city"))
                        			{
                        				forecastLocation.City = jsonReader.nextString();
                        				Log.d(TAG, "Location: " + forecastLocation.City);
                        			}
                        			else if(name.equals("zipCode"))
                        			{
                        				forecastLocation.ZipCode = jsonReader.nextString();
                        				Log.d(TAG, "ZipCode: " + forecastLocation.ZipCode);
                        			}
                        			else if(name.equals("state"))
                        			{
                        				forecastLocation.State = jsonReader.nextString();
                        				Log.d(TAG, "State: " + forecastLocation.State);
                        			}
                        			else if(name.equals("country"))
                        			{
                        				forecastLocation.Country = jsonReader.nextString();
                        				Log.d(TAG, "Country: " + forecastLocation.Country);
                        			}
                        			else
                        			{
                        				jsonReader.skipValue();
                        			}

                        		}
                        		jsonReader.close();
                        	}
                        	
                        }
                        catch (IllegalStateException e)
                        {
                                Log.e(TAG, e.toString() + params[0]);
                        }
                        catch (Exception e)
                        {
                                Log.e(TAG, e.toString());
                        }

                        return forecastLocation;
                }

                protected void onPostExecute(ForecastLocation forecastLocation)
                {
                        _listener.onLocationLoaded(forecastLocation);
                }

        }
        
        public String ZipCode;
        public String City;
        public String State;
        public String Country;


		@Override
		public int describeContents() 
		{
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) 
		{
			dest.writeString(ZipCode);
			dest.writeString(City);
			dest.writeString(State);
			dest.writeString(Country);
		}
		
        public static final Parcelable.Creator<ForecastLocation> Creator = new Parcelable.Creator<ForecastLocation>()
        {
                @Override
                public ForecastLocation createFromParcel(Parcel pc)
                {
                        return new ForecastLocation(pc);
                }
                
                @Override
                public ForecastLocation[] newArray(int size)
                {
                        return new ForecastLocation[size];
                }
        };
}
