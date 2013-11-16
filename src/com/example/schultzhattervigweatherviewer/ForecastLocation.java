package com.example.schultzhattervigweatherviewer;

import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;


/**************************************************************************
 * Author:       Josh Schultz & Erik Hattervig
 * 
 * Date:         Novemeber 13, 2012
 * 
 * Extends:      (nothing)
 * 
 * Implements:   Parcelable
 *           
 * Description:  ForecastLocation makes an API call to get location information 
 *               for a zip code.  To use this class, users first must create
 *               the inner class object and run the ".execute(...)" method.
 *               Calling this method, the user must pass a 5 digit zip code
 *               in string format.  Doing this will return the forecast location
 *               to the listener.
 *               If the user wants, the class is a parcelable type and can
 *               be stored in bundles to prevent making the API call 
 *               repeatedly.  
 *               The information provided from the forecast is as follows:
 *                   zip code 
 *                   city
 *                   state
 *                   country
 *************************************************************************/
public class ForecastLocation implements Parcelable
{
		//Debugging tag
    	private static final String TAG = "Forecast Location";
    	
    	//number of milliseconds before socketTimeoutException is thrown
        private final int TIMEOUT = 5000;
        
        //URL to get the forecast location information
        //Notice the %s, which would be replaced by the zip code
        //Use String.format(_URL, zipCode);
        private String _URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" + 
                                     "&api_key=g5ag6gquyxh7czgfb4bj6b4b";
        
		//Stored forecast location information
	    public String ZipCode;
	    public String City;
	    public String State;
	    public String Country;

	    
	    
        /**************************************************************************
         * Date:         November 11, 2012
         * 
         * Description:  Constructor.  Public members are initialized to null.
         * 
         * Params:       (nothing)
         * 
         * Returns:      A Forecast Location object with all the private members 
         *               initialized to null.
         *************************************************************************/
        public ForecastLocation()
        {
                ZipCode = null;
                City = null;
                State = null;
                Country = null;
        }
        
        
        
        /**************************************************************************
         * Date:         November 15, 2012
         * 
         * Description:  Constructor.  Public members are initialized to saved
         *               parcel state.  The parcelable values are stored in a
         *               queue fashion.  writeToParcel() writes the values and this
         *               method reads the values.
         * 
         * Params:       parcel[in] - Saved values
         * 
         * Returns:      A Forecast Location object with all the private members 
         *               initialized to the saved state.
         *************************************************************************/
        public ForecastLocation(Parcel parcel)
        {
        	ZipCode = parcel.readString();
        	City = parcel.readString();
        	State = parcel.readString();
        	Country = parcel.readString();
        }
        
        
       
        /**************************************************************************
         * Date:         November 15, 2012
         * 
         * Description:  The documentation for this override is vague and hard to
         *               understand.  Honestly we have no idea what this does.
         * 
         * Params:       (nothing)
         * 
         * Returns:      0  - Not entirely sure what this value means
         *************************************************************************/
		@Override
		public int describeContents() 
		{
			return 0;
		}
		
		
		
        /**************************************************************************
         * Date:         November 15, 2012
         * 
         * Description:  Stores the private members into a parcel which is meant to
         *               be read later by the constructor.
         * 
         * Params:       dest[out] - Location to store the private forecast location
         *                           members
         *               flags[in] - Various flags to signal how the parcel should
         *                           be stored.
         * 
         * Returns:      (nothing)
         *************************************************************************/
		@Override
		public void writeToParcel(Parcel dest, int flags) 
		{
			dest.writeString(ZipCode);
			dest.writeString(City);
			dest.writeString(State);
			dest.writeString(Country);
		}
		
		
		
        /**************************************************************************
         * Date:         November 15, 2012
         * 
         * Description:  When this class is used as a parcelable, this method is
         *               used to call the parcel constructor.  
         * 
         * Params:       (nothing)
         * 
         * Returns:      ForecastLocation - A new ForecastLocation object 
         *                                  instantiated from existing parcels.  
         *************************************************************************/
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
        
        
        /**************************************************************************
         * Author:       Josh Schultz & Erik Hattervig
         * 
         * Date:         Novemeber 11, 2012
         * 
         * Extends:      AsyncTask<String, Void, ForecastLocation>
         *                         String   -> Parameter
         *                         Void     -> Progress
         *                         ForecastLocation -> Result
         * 
         * Implements:   (nothing)
         *           
         * Description:  Makes the API call to grab the forecast location
         *               information.  The class uses .execute(zipCode) to call the
         *               async task.  
         *************************************************************************/
        public class LoadLocation extends AsyncTask<String, Void, ForecastLocation>
        {
	            private IListeners _listener;  //Object that implements the IListener interface
	            private Context _context;      //Object that is associated with this class

                
                
                /**************************************************************************
                 * Date:         November 11, 2012
                 * 
                 * Description:  Constructor.  Sets up the listener which later will be used
                 *               to notify when the async task is finished.
                 * 
                 * Params:       context[in]  - The activity associated with this object
                 *               listener[in] - The object listening for the result.
                 * 
                 * Returns:      A LoadLocation object
                 *************************************************************************/
                public LoadLocation(Context context, IListeners listener)
                {
                        _context = context;
                        _listener = listener;
                }

                
                
                /**************************************************************************
                 * Date:         November 11, 2012
                 * 
                 * Description:  The async portion of LoadLocation.  This method makes the
                 *               API call for the forecast location, parses the call, and 
                 *               returns a ForecastLocation object.  If there is no network 
                 *               available, or the API call is unsuccessful, null is returned.
                 * 
                 * Params:       params[in] - The zip code for the API call.
                 * 
                 * Returns:      ForecastLocation - The forecast location information from 
                 *                                  the API call
                 *               null             - If the API call failed.
                 *************************************************************************/
                protected ForecastLocation doInBackground(String... params)
                {
                        ForecastLocation forecastLocation = new ForecastLocation();

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
                        	
                        	jsonReader.beginObject();
                        	
                        	//Get the first tag name, which should be "location"
                        	String name = jsonReader.nextName();
                        	
                        	//Parse each individual item from the forecast location and look for specific tags
                        	if(name.equals("location"))
                        	{
                        		jsonReader.beginObject();
                        		while(jsonReader.hasNext())
                        		{
                        			
                        			name = jsonReader.nextName();
                        			
                        			//Avoid reading in null values
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
                        catch(SocketTimeoutException e)
                        {
                        	//The TIMEOUT period exceeded in the API call
                        	Log.e(TAG, e.toString());
                        	Log.e(TAG, "More than " + String.valueOf(TIMEOUT) + " milliseconds passed in getting the location." );
                        	return null;
                        }
                        catch (UnknownHostException e)
                        {
                        	//The API call failed by not reaching the server
                        	Log.e(TAG, e.toString());
                        	Log.e(TAG, "Failed to reach api web server in getting the location.");
                        	return null;
                        }
                        catch (Exception e)
                        {
                                Log.e(TAG, e.toString());
                        }

                        return forecastLocation;
                }

                
                
                /**************************************************************************
                 * Date:         November 11, 2012
                 * 
                 * Description:  After the forecast location is received, this method is
                 *               automatically called to notify the listener.
                 * 
                 * Params:       forecastLocation[in] - The result of the API call.
                 * 
                 * Returns:      (nothing)
                 *************************************************************************/
                protected void onPostExecute(ForecastLocation forecastLocation)
                {
                        _listener.onLocationLoaded(forecastLocation);
                }

        }
}
