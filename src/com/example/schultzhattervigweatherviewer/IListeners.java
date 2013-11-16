package com.example.schultzhattervigweatherviewer;

/**************************************************************************
 * Author:       Brian Butterfield
 * 
 * Date:         November 11, 2012
 * 
 * Description:  Interface used to handle getting the results from the
 *               forecast and forecastLocation call.
 *************************************************************************/
public interface IListeners
{
        public void onLocationLoaded(ForecastLocation forecastLocation);
        public void onForecastLoaded(Forecast forecast);
}
