package edu.itesm.pandemia

import retrofit2.Response
import retrofit2.http.GET
import java.net.ResponseCache

interface APIService {
    @GET("continents")
    suspend fun getCountries(): Response<List<PaisGson>>
}