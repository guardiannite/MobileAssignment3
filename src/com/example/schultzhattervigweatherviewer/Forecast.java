package com.example.schultzhattervigweatherviewer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;

/**************************************************************************
 * <h2>Description:</h2>
 * Forecast makes an API call to get weather information for
 * a zip code.  To use this class, users first must create
 * the inner class object and run the ".execute(...)" method.
 * Calling this method, the user must pass a 5 digit zip code
 * in string format.  Doing this will return the forecast
 * to the listener. <br>
 * If the user wants, the class is a parcelable type and can
 * be stored in bundles to prevent making the API call 
 * repeatedly. <p>
 * The information provided from the forecast is as follows:
 * <ul>
 * <li>temperature</li>
 * <li>feels like temperature</li>
 * <li>humidity</li>
 * <li>Chance of precipitation</li>
 * <li>icon representing the forecasted weather</li>
 * <li>time of the forecast</li>
 * </ul>
 * 
 * <p>
 * <b>Date:</b><br>
 * &nbsp &nbsp &nbsp &nbsp Novemeber 13, 2012
 * <p>
 * <b>Extends:</b><br>
 * &nbsp &nbsp &nbsp &nbsp (nothing)
 * <p>
 * <b>Implements:</b><br>
 * &nbsp &nbsp &nbsp &nbsp Parcelable
 *
 * @author Josh Schultz
 * @author Erik Hattervig
 *************************************************************************/
public class Forecast implements Parcelable
{
		//Debugging tag for LogCat
        private final static String TAG = "Forecast";
        
        //JSON tag names
        private final String CHANCE_PRECIP = "chancePrecip";
        private final String ICON = "icon";
        private final String FORECAST_HOURLY_LIST = "forecastHourlyList";
        private final String FEELS_LIKE = "feelsLike";
        private final String HUMIDITY = "humidity";
        private final String TEMPERATURE = "temperature";
        private final String DATE_TIME = "dateTime";
        
        //Number of milliseconds to try connecting to WeatherBug API
        //before throwing a socketTimeoutException
        private final int TIMEOUT = 5000;
        
        //Stores the forecast information
        private String _chancePrecip;
        private String _feelsLike;
        private String _humidity;
        private String _temperature;
        private String _time;
        
        //API call to grab the forecast.  "%s" is replaced with a 5 digit zip code
        //Example usage: String.format(_URL, zipCode);
        //The flags "ht=" means hourly time
        //"ht=t"  --> hourly temperature
        //"ht=i"  --> hourly icon
        //"ht=cp" --> hourly chance of precipitation
        //"ht=fl" --> hourly feels like temperature
        //"ht=h"  --> hourly humidity
        //The API key can handle up to 2 calls a second with a max of 5000 calls a day
        private String _URL = "http://i.wxbug.net/REST/Direct/GetForecastHourly.ashx?zip=" + "%s" + 
        					  "&ht=t&ht=i&ht=cp&ht=fl&ht=h" + 
        					  "&api_key=g5ag6gquyxh7czgfb4bj6b4b";
        
        //Location to get image from once the _icon private member is set
        //Notice the "%s" at the end of the string (use String.format(_imageURL, _icon);)
        private String _imageURL = "http://img.weather.weatherbug.com/forecast/icons/localized/500x420/en/trans/%s.png";
        
        //Bitmap of the icon grabbed from _imageURL API call
        public Bitmap Image;
        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * &nbsp &nbsp &nbsp &nbsp Constructor.  Private members are initialized
         * to null.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012
         * 
         * @return A Forecast object with all the private members initialized to
         * 			null.
         *************************************************************************/
        public Forecast()
        {
                Image = null;
                _chancePrecip = null;
                _feelsLike = null;
                _humidity = null;
                _temperature = null;
                _time = null;
        }
        
        

        /**************************************************************************
         * <h2>Description:</h2>
         * Constructor.  Private members are set to the stored
         * parcelable values. The parcelable values are stored in a
         * queue fashion. writeToParcel() writes the values and this
         * method reads the values.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 13, 2012
         * 
         * @param parcel[in] - Contains the stored icon bitmap, and forecast
         *                     information
         * 
         * @return A Forecast object with all the private members initialized 
         *         to a saved state.
         *************************************************************************/
        private Forecast(Parcel parcel)
        {
                Image = parcel.readParcelable(Bitmap.class.getClassLoader());
                _chancePrecip = parcel.readString();
                _feelsLike = parcel.readString();
                _humidity = parcel.readString();
                _temperature = parcel.readString();
                _time = parcel.readString();
        }
        
        

        /**************************************************************************
         * <h2>Description:</h2>
         * The documentation for this override is vague and hard to
         *    understand.  Honestly we have no idea what this does.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012 
         * 
         * @return 0  - Not entirely sure what this value means     
         *************************************************************************/
        @Override
        public int describeContents()
        {
                return 0;
        }

       
        
        /**************************************************************************
         * <h2>Description:</h2>
         * Stores the private members into a parcel which is meant to
         *    be read later by the constructor.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 13, 2012
         * 
         * @param dest[out] - Location to store the private forecast members
         * @param flags[in] - Various flags to signal how the parcel should
         *                           be stored.
         *************************************************************************/
        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
                dest.writeParcelable(Image, 0);
                dest.writeString(_chancePrecip);
                dest.writeString(_feelsLike);
                dest.writeString(_humidity);
                dest.writeString(_temperature);
                dest.writeString(_time);
        }
        
        

        /**************************************************************************
         * <h2>Description:</h2>
         * When this class is used as a parcelable, this method is
         * used to call the parcel constructor.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012
         * <p>
         * <b>Params:</b><br>
         * &nbsp &nbsp &nbsp &nbsp (nothing)
         * <p>
         * <b>Returns:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Forecast - A new Forecast object instantiated
         *   from existing parcels.
         * 
         * @author Josh Schultz
         * @author Erik Hattervig
         *************************************************************************/
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
        
        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * A getter for the private member _chancePrecip.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012
         * 
         * @return _chancePrecip - Chance of precipitation string    
         *************************************************************************/
        public String getChancePrecip()
        {
        	return _chancePrecip;
        }
        
        
        
        /**************************************************************************
         * Date:         November 11, 2012
         * 
         * Description:  A getter for the private member _feelsLike. 
         * 
         * Returns:      _feelsLike - Feels like temperature string
         *************************************************************************/
        public String getFeelsLike()
        {
        	return _feelsLike;
        }
        
        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * A getter for the private member _humidity.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012
         * 
         * @return _humidity - Humidity string     
         *************************************************************************/
        public String getHumidity()
        {
        	return _humidity;
        }
        
        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * A getter for the private member _temperature.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012
         * 
         * @return _temperature - Temperature string    
         *************************************************************************/
        public String getTemperature()
        {
        	return _temperature;
        }
        
        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * A getter for the private member _time.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012
         * 
         * @return _time - Time of forecast string    
         *************************************************************************/
        public String getTime()
        {
        	return _time;
        }
        
        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * A setter for the private member _chancePrecip. 
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012
         * 
         * @param chancePrecip - value used to set the private member
         *************************************************************************/
        public void setChancePrecip(String chancePrecip)
        {
        	_chancePrecip = chancePrecip;
        }

        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * A setter for the private member _feelsLike.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012
         *          
         * @param feelsLike - value used to set the private member
         *************************************************************************/
        public void setFeelsLike(String feelsLike)
        {
        	_feelsLike = feelsLike;
        }
        
        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * A setter for the private member _humidity.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012
         * 
         * @param humidity - value used to set the private member
         *************************************************************************/
        public void setHumidity(String humidity)
        {
        	_humidity = humidity;
        }
        
        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * A setter for the private member _temperature.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012
         * 
         * @param temperature - value used to set the private member
         *************************************************************************/
        public void setTemperature(String temperature)
        {
        	_temperature = temperature;
        }
        
        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * A setter for the private member _time.
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp November 11, 2012
         * 
         * @param time - value used to set the private member    
         *************************************************************************/
        public void setTime(String time)
        {
        	_time = time;
        }
        
        
        
        
        
        
        /**************************************************************************
         * <h2>Description:</h2>
         * Makes the API call to grab the forecast information and
         *    the icon.  The class uses .execute(zipCode) to call the
         *    async task. 
         * <p>
         * <b>Date:</b><br>
         * &nbsp &nbsp &nbsp &nbsp Novemeber 11, 2012
         * <p>
         * <b>Extends:</b><br>
         * &nbsp &nbsp &nbsp &nbsp AsyncTask &#60;String, Void, Forecast&#62; <br>
         *                         String   -> Parameter <br>
         *                         Void     -> Progress <br>
         *                         Forecast -> Result
         * <p>
         * <b>Implements:</b><br>
         * &nbsp &nbsp &nbsp &nbsp (nothing)
         * 
         * @author Josh Schultz
         * @author Erik Hattervig
         *************************************************************************/
        public class LoadForecast extends AsyncTask<String, Void, Forecast>
        {
        		
                private IListeners _listener;  //Object that implements the IListener interface
                private Context _context;      //Object that is associated with this class

                //Used in creating the bitmap for the icon
                //The value is used to scale the bitmap image (use less memory)
                //Any value <= 1 will be the normal scale
                //Any value > 1 is rounded up to the nearest power of 2.  This value then
                //represents how much the image will be scaled in height and width.
                //Ex: bitmapSampleSize = 4 means the image will be 1/4 the normal height/width
                private int bitmapSampleSize = -1;

                
                /**************************************************************************
                 * <h2>Description:</h2>
                 * Constructor.  Sets up the listener which later will be used
                 *               to notify when the async task is finished.
                 * <p>
                 * <b>Date:</b><br>
                 * &nbsp &nbsp &nbsp &nbsp Novemeber 11, 2012
                 * 
                 * @param context[in]  - The activity associated with this object
                 * @param listener[in] - The object listening for the result.
                 * 
                 * @return A LoadForecast object   
                 *************************************************************************/
                public LoadForecast(Context context, IListeners listener)
                {
                        _context = context;
                        _listener = listener;
                }

                
                
                /**************************************************************************
                 * <h2>Description:</h2>
                 * The async portion of LoadForecast.  This method makes the
                 *    API call for the forecast, parses the call, and returns
                 *    a Forecast object.  If there is no network available, or
                 *    the API call is unsuccessful, null is returned.
                 * <p>
                 * <b>Date:</b><br>
                 * &nbsp &nbsp &nbsp &nbsp Novemeber 11, 2012
                 * 
                 * @param params[in] - The zip code for the API call.
                 * 
                 * @return Forecast - The forecast information from the API call
                 * @return null     - If the API call failed.          
                 *************************************************************************/
                protected Forecast doInBackground(String... params)
                {
                        Forecast forecast = new Forecast();
                        
                        try
                        {
                            //Create the url based off the zipCode passed in	
                        	URL url = new URL(String.format(_URL, params[0]));
                        	
                        	//Set timeouts on the url connection
                        	//If the connection fails to connect or read from
                        	//the connection in TIMEOUT milliseconds, an exception is thrown
                        	URLConnection connection = url.openConnection();
                        	connection.setConnectTimeout(TIMEOUT);
                        	connection.setReadTimeout(TIMEOUT);
                        	
                        	//Create the jsonReader based on the API call result
                        	InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                        	JsonReader jsonReader = new JsonReader(reader);
                        	
                        	//Begin reading the JSON
                        	jsonReader.beginObject();
                        	
                        	//Get the first tag name, which should be FORECAST_HOURLY_LIST
                        	String name = jsonReader.nextName();
                        	
                        	//Parse each individual item from the forecast hour and look for specific tags
                        	if(name.equals(FORECAST_HOURLY_LIST))
                        	{
                        		jsonReader.beginArray();

                        		jsonReader.beginObject();
                        		while(jsonReader.hasNext())
                        		{
                        			name = jsonReader.nextName();
                        			
                        			//Avoid grabbing nulls from the JSON
                        			if(jsonReader.peek() == null)
                        			{
                        				jsonReader.skipValue();
                        			}
                        			else if(name.equals(CHANCE_PRECIP))
                        			{
                        				forecast.setChancePrecip( jsonReader.nextString() + "%");
                        				Log.d(TAG, "Chance of precipitation: " + forecast.getChancePrecip() );
                        			}
                        			else if(name.equals(FEELS_LIKE))
                        			{
                        				forecast.setFeelsLike( jsonReader.nextString() + (char) 0x00B0 + "F");
                        				Log.d(TAG, "Feels like " + forecast.getFeelsLike() );
                        			}
                        			else if(name.equals(HUMIDITY))
                        			{
                        				forecast.setHumidity( jsonReader.nextString() + "%");
                        				Log.d(TAG, "Humidity " + forecast.getHumidity() );
                        			}
                        			else if(name.equals(TEMPERATURE))
                        			{
                        				forecast.setTemperature( jsonReader.nextString() + (char) 0x00B0 + "F");
                        				Log.d(TAG, "Temperature " + forecast.getTemperature() );
                        			}
                        			else if(name.equals(DATE_TIME))
                        			{
                        				//Andrew Thompson's solution for the setting the time
                        				long localZipCodeTime = jsonReader.nextLong();
                        				Date date = new Date(localZipCodeTime);  
                        				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, h:mm a", Locale.US);
                        				dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
                        				forecast.setTime(dateFormat.format(date));

                        				Log.d(TAG, "Time: " + forecast.getTime());
                        			}
                        			else if(name.equals(ICON))
                        			{
                        				String icon = jsonReader.nextString();
                        				Log.d(TAG, "Icon name: " + icon);
                        				
                        				//Get the icon bitmap based on the icon string
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
                        catch(SocketTimeoutException e)
                        {
                        	//This means the TIMEOUT time exceeded, so this exception was thrown
                        	Log.e(TAG, e.toString());
                        	Log.e(TAG, "More than " + String.valueOf(TIMEOUT) + " milliseconds passed in getting the forecast.");
                        	return null;
                        }
                        catch (UnknownHostException e)
                        {
                        	//This means the API call was made, but the server never repsonded
                        	Log.e(TAG, e.toString());
                        	Log.e(TAG, "Failed to reach api web server in getting the forecast.");
                        	return null;
                        }
                        catch (Exception e)
                        {
                                Log.e(TAG, e.toString());
                        }

                        return forecast;
                }

                
                
                /**************************************************************************
                 * <h2Description:</h2>
                 * After the forecast is received, this method is automatically
                 *    called to notify the listener.
                 * <p>
                 * <b>Date:</b><br>
                 * &nbsp &nbsp &nbsp &nbsp Novemeber 11, 2012
                 * 
                 * @param forecast[in] - The result of the API call.
                 *************************************************************************/
                @Override
                protected void onPostExecute(Forecast forecast)
                {
                        _listener.onForecastLoaded(forecast);
                }

                
                
                /**************************************************************************
                 * <h2Description:</h2>
                 * Written by Brian Butterfield, this method takes in an icon
                 *     name and retrieves the bitmap for that name.  The method
                 *     was modified to add a timeout period for the API call.
                 * <p>
                 * <b>Date:</b><br>
                 * &nbsp &nbsp &nbsp &nbsp Novemeber 11, 2012
                 * 
                 * @param conditionString  - Name of the icon
                 * @param bitmapSampleSize - Value to scale the bitmap by
                 * 
                 * @return iconBitmp - The bitmap of the icon 
                 *************************************************************************/
                private Bitmap readIconBitmap(String conditionString, int bitmapSampleSize)
                {
                        Bitmap iconBitmap = null;
                        try
                        {
                        		//Makes a call to the server for the bitmap of the icon.
                        		//If the call takes over TIMEOUT milliseconds, a
                        		//SocketTimeOutException is thrown
                                URL weatherURL = new URL(String.format(_imageURL, conditionString));
                            	URLConnection connection = weatherURL.openConnection();
                            	connection.setConnectTimeout(TIMEOUT);
                            	connection.setReadTimeout(TIMEOUT);

                                BitmapFactory.Options options = new BitmapFactory.Options();
                                if (bitmapSampleSize != -1)
                                {
                                		//Determines if the bitmap should be scaled
                                		//<= 1 means the original width and height
                                		//2 means 1/2 the original width and height
                                		//4 means 1/4 the original width and height, etc.
                                        options.inSampleSize = bitmapSampleSize;
                                }

                                iconBitmap = BitmapFactory.decodeStream(connection.getInputStream(), null, options);
                        }
                        catch (MalformedURLException e)
                        {
                                Log.e(TAG, e.toString());
                        }
                        catch (SocketTimeoutException e)
                        {
                        		//The TIMEOUT limit was reached
                            	Log.e(TAG, e.toString());
                            	Log.e(TAG, "More than " + String.valueOf(TIMEOUT) + " milliseconds passed in getting the forecast image.");
                        }
                        catch (UnknownHostException e)
                        {
                        		//The server failed to communicate back
                        		Log.e(TAG, e.toString());
                        		Log.e(TAG, "Failed to reach api web server in getting the forecast image.");
                        }
                        catch (IOException e)
                        {
                                Log.e(TAG, e.toString());
                        }
                        catch (Exception e)
                        {
                                Log.e(TAG, e.toString());
                        }

                        return iconBitmap;  //bitmap of the icon
                }
                
        }
}