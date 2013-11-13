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
        
        //TODO: Change to longs OR add labels (%, F, etc.)
        private String _chancePrecip;
        private String _feelsLike;
        private String _humidity;
        private String _temperature;
        private String _time;
        
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
                _chancePrecip = parcel.readString();
                _feelsLike = parcel.readString();
                _humidity = parcel.readString();
                _temperature = parcel.readString();
                //TODO: Add time
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
                dest.writeString(_chancePrecip);
                dest.writeString(_feelsLike);
                dest.writeString(_humidity);
                dest.writeString(_temperature);
                //TODO: dest.writeString(_time);
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
        
        public String getChancePrecip()
        {
        	return _chancePrecip;
        }
        
        public String getFeelsLike()
        {
        	return _feelsLike;
        }
        
        public String getHumidity()
        {
        	return _humidity;
        }
        
        public String getTemperature()
        {
        	return _temperature;
        }
        
        public String getTime()
        {
        	return _time;
        }
        
        public void setChancePrecip(String chancePrecip)
        {
        	_chancePrecip = chancePrecip;
        }

        public void setFeelsLike(String feelsLike)
        {
        	_feelsLike = feelsLike;
        }
        
        public void setHumidity(String humidity)
        {
        	_humidity = humidity;
        }
        
        public void setTemperature(String temperature)
        {
        	_temperature = temperature;
        }
        
        public void setTime(String time)
        {
        	_time = time;
        }
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
                        Log.d(TAG, "Starting url call...");
                        
                        try
                        {
                        	Log.d(TAG, String.format(_URL, params[0]));
                        	
                        	URL url = new URL(String.format(_URL, params[0]));
                        	InputStreamReader reader = new InputStreamReader( url.openStream());
                        	JsonReader jsonReader = new JsonReader(reader);
                        	jsonReader.beginObject();
                        	
                        	Log.d(TAG, "Started parsing JSON");
                        	
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
                        				forecast.setChancePrecip( jsonReader.nextString() );
                        				Log.d(TAG, "Chance of precipitation: " + forecast.getChancePrecip() + "%");
                        			}
                        			else if(name.equals(FEELS_LIKE))
                        			{
                        				forecast.setFeelsLike( jsonReader.nextString() );
                        				Log.d(TAG, "Feels like " + forecast.getFeelsLike() + "F");
                        			}
                        			else if(name.equals(HUMIDITY))
                        			{
                        				forecast.setHumidity( jsonReader.nextString() );
                        				Log.d(TAG, "Humidity " + forecast.getHumidity() + "%");
                        			}
                        			else if(name.equals(TEMPERATURE))
                        			{
                        				forecast.setTemperature( jsonReader.nextString() );
                        				Log.d(TAG, "Temperature " + forecast.getTemperature() + "F");
                        			}
                        			else if(name.equals(DATE_TIME))
                        			{
                        				Date date = new Date();
                        				//date.setTime(jsonReader.nextLong());
                        				//Calendar.HOUR_OF_DAY
                        				long value = jsonReader.nextLong();
                        				date.setTime(value);
                        				//_time = date.getHours();
                        				Log.d(TAG, "Time: " + value);
                        				Log.d(TAG, "Time: " + date.getTime());
                        				Log.d(TAG, "Time: " + date.getHours());
                        			}
                        			else if(name.equals(ICON))
                        			{
                        				String icon = jsonReader.nextString();
                        				Log.d(TAG, "Icon name: " + icon);
                        				forecast.Image = readIconBitmap(icon, bitmapSampleSize);
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

                //Written by Brian, takes icon name, makes a call to get the bitmap associated with the name
                private Bitmap readIconBitmap(String conditionString, int bitmapSampleSize)
                {
                        Bitmap iconBitmap = null;
                        try
                        {
                                URL weatherURL = new URL(String.format(_imageURL, conditionString));

                                BitmapFactory.Options options = new BitmapFactory.Options();
                                if (bitmapSampleSize != -1)
                                {
                                		//Determines if the bitmap should be scaled
                                		//<= 1 means the original width and height
                                		//2 means 1/2 the original width and height
                                		//4 means 1/4 the original width and height, etc.
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