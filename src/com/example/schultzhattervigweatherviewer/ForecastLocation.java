package com.example.schultzhattervigweatherviewer;

public class ForecastLocation
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

        public String ZipCode;
        public String City;
        public String State;
        public String Country;
}
