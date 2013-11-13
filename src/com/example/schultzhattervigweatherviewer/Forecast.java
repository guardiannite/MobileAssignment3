package com.example.schultzhattervigweatherviewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.JsonReader;
import android.util.Log;


public class Forecast implements Parcelable
{

        private static final String TAG = "";
        private static final String CHANCE_PRECIP = "chancePrecip";
        private static final String ICON = "icon";
        private static final String FORECAST_HOURLY_LIST = "forecastHourlyList";
        private static final String FEELS_LIKE = "feelsLike";
        private static final String HUMIDITY = "humidity";
        private static final String TEMPERATURE = "temperature";
        private static final String DATE_TIME = "dateTime";
        
        // http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
        // NOTE:  See example JSON in doc folder.
        private String _URL = "http://i.wxbug.net/REST/Direct/GetForecastHourly.ashx?zip=" + "%s" + 
        					  "&ht=t&ht=i&ht=cp&ht=fl&ht=h" + 
        					  "&api_key=g5ag6gquyxh7czgfb4bj6b4b";
        
        // http://developer.weatherbug.com/docs/read/List_of_Icons
                
        private String _imageURL = "http://img.weather.weatherbug.com/forecast/icons/localized/500x420/en/trans/%s.png";
        
        public Bitmap Image;
        
        public Forecast()
        {
                Image = null;
        }

        private Forecast(Parcel parcel)
        {
                Image = parcel.readParcelable(Bitmap.class.getClassLoader());
        }

        @Override
        public int describeContents()
        {
                return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
                dest.writeParcelable(Image, 0);
        }

        public static final Parcelable.Creator<Forecast> Creator = new Parcelable.Creator<Forecast>()
        {
                @Override
                public Forecast createFromParcel(Parcel pc)
                {
                        return new Forecast(pc);
                }
                
                @Override
                public Forecast[] newArray(int size)
                {
                        return new Forecast[size];
                }
        };

        //Parameters -> strings  = Zip Code
        //Progress -> Void
        //Result -> Forecast
        public class LoadForecast extends AsyncTask<String, Void, Forecast>
        {
                private IListeners _listener;
                private Context _context;

                private int bitmapSampleSize = -1;

                public LoadForecast(Context context, IListeners listener)
                {
                        _context = context;
                        _listener = listener;
                }

                protected Forecast doInBackground(String... params)
                {
                        Forecast forecast = new Forecast();
                        
                        try
                        {
                        	URL url = new URL(String.format(_URL, params[0]));
                        	InputStreamReader reader = new InputStreamReader( url.openStream());
                        	JsonReader jsonReader = new JsonReader(reader);
                        	jsonReader.beginObject();
                        	
                        	String name = jsonReader.nextName();
                        	if(name.equals(FORECAST_HOURLY_LIST))
                        	{
                        		jsonReader.beginArray();

                        		jsonReader.beginObject();
                        		while(jsonReader.hasNext())
                        		{
                        			
                        			name = jsonReader.nextName();
                        			if(jsonReader.peek() == null)
                        			{
                        				jsonReader.skipValue();
                        			}
                        			else if(name.equals(CHANCE_PRECIP))
                        			{
                        				String chancePrecip = jsonReader.nextString();
                        				Log.d(TAG, "Chance of precipitation: " + chancePrecip + "%");
                        			}
                        			else if(name.equals(FEELS_LIKE))
                        			{
                        				String feelsLike = jsonReader.nextString();
                        				Log.d(TAG, "Feels like " + feelsLike + "F");
                        			}
                        			else if(name.equals(HUMIDITY))
                        			{
                        				String humidity = jsonReader.nextString();
                        				Log.d(TAG, "Humidity " + humidity + "%");
                        			}
                        			else if(name.equals(TEMPERATURE))
                        			{
                        				String temperature = jsonReader.nextString();
                        				Log.d(TAG, "Temperature " + temperature + "F");
                        			}
                        			else if(name.equals(DATE_TIME))
                        			{
                        				Date date = new Date();
                        				//date.setTime(jsonReader.nextLong());
                        				//Calendar.HOUR_OF_DAY
                        				long value = jsonReader.nextLong();
                        				date.setTime(value);
                        				Log.d(TAG, "Time: " + value);
                        				Log.d(TAG, "Time: " + date.getTime());
                        				Log.d(TAG, "Time: " + date.getHours());
                        			}
                        			else if(name.equals(ICON))
                        			{
                        				String icon = jsonReader.nextString();
                        				Log.d(TAG, "Icon name: " + icon);
                        				forecast.Image = readIconBitmap(icon, 1);
                        			}
                        			else
                        			{
                        				jsonReader.skipValue();
                        			}

                        		}
                        		jsonReader.endObject();
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

                        return forecast;
                }

                @Override
                protected void onPostExecute(Forecast forecast)
                {
                        _listener.onForecastLoaded(forecast);
                }

                private Bitmap readIconBitmap(String conditionString, int bitmapSampleSize)
                {
                        Bitmap iconBitmap = null;
                        try
                        {
                                URL weatherURL = new URL(String.format(_imageURL, conditionString));

                                BitmapFactory.Options options = new BitmapFactory.Options();
                                if (bitmapSampleSize != -1)
                                {
                                        options.inSampleSize = bitmapSampleSize;
                                }

                                iconBitmap = BitmapFactory.decodeStream(weatherURL.openStream(), null, options);
                        }
                        catch (MalformedURLException e)
                        {
                                Log.e(TAG, e.toString());
                        }
                        catch (IOException e)
                        {
                                Log.e(TAG, e.toString());
                        }
                        catch (Exception e)
                        {
                                Log.e(TAG, e.toString());
                        }

                        return iconBitmap;
                }
        }
}