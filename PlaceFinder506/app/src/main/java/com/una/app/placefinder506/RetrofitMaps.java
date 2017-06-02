package com.una.app.placefinder506;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by
 * Angélica
 * Bryhan
 * Marco
 * Massiel Mora Rodríguez cedula: 604190071
 */
public interface RetrofitMaps {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/directions/json?key=AIzaSyC-eiIykTIrym1SdmrRYOonW2XRrIxO7Pk")
    Call<Example> getDistanceDuration(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);

}
