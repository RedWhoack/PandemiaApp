package edu.itesm.pandemia

import com.google.gson.annotations.SerializedName

data class PaisGson(
    @SerializedName("continent")
    var nombre:String?,
    var continentInfo: ContinentInfo,
    var cases: Double?,
    var recovered: Double?
)

data class ContinentInfo(
    var lat: Double?,
    var long: Double?
)