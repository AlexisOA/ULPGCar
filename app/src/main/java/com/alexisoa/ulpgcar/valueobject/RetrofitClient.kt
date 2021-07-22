package com.alexisoa.ulpgcar.valueobject

import com.alexisoa.ulpgcar.data.repository.location.StreetMapWS
import com.alexisoa.ulpgcar.data.repository.routes.OpenRoutesWS
import com.alexisoa.ulpgcar.data.repository.vehicles.VehiclesWS
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val webserviceStreet by lazy{
        Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build().create(StreetMapWS::class.java)
    }

    val webserviceVehicles by lazy{
        Retrofit.Builder()
                .baseUrl("https://databases.one/")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build().create(VehiclesWS::class.java)
    }

    val webserviceOpenRoute by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/v2/directions/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(OpenRoutesWS::class.java)
    }
}