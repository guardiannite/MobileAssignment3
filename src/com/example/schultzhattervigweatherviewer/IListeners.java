package com.example.schultzhattervigweatherviewer;

/**************************************************************************
 * <h2>Description:</h2>
 * Interface used to handle getting the results from the
 *     forecast and forecastLocation call.
 * <p>
 * <b>Date:</b><br>
 * &nbsp &nbsp &nbsp &nbsp Novemeber 11, 2012
 * 
 * @author Brian Butterfield  
 *************************************************************************/
public interface IListeners
{
        public void onLocationLoaded(ForecastLocation forecastLocation);
        public void onForecastLoaded(Forecast forecast);
}
