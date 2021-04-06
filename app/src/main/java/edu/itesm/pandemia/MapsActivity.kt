package edu.itesm.pandemia

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


data class Pais(var nombre:String,
                var latitude: Double,
                var longitude: Double,
                var casos: Double,
                var recuperados: Double,
                var defunciones: Double,
                var tests: Double)

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val url ="https://disease.sh/v3/covid-19/countries"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        cargaDatos()
        //getCountries() //Agregado nueva clase
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        /*
        val requestQueue = Volley.newRequestQueue(this)
        val peticion = JsonArrayRequest(Request.Method.GET,url,null,Response.Listener{
            val jsonArray = it
            for (i in 0 until jsonArray.length()){
                val pais = jsonArray.getJSONObject(i)
                val nombre = pais.getString("name")
                val lat = pais.getDouble("lat")
                val lng = pais.getDouble("lng")
                val latLng = LatLng(lat,lng)

                mMap.addMarker(MarkerOptions().position(latLng).title(nombre))
            }
        }, Response.ErrorListener {

        })
        requestQueue.add(peticion)

        // Add a marker in Sydney and move the camera
        /*val mexico = LatLng(19.432608, -99.133209)
        mMap.addMarker(MarkerOptions().position(mexico).title("México City"))
        mMap.addMarker(MarkerOptions().position(LatLng(18.432608, -99.133209)).title("Desconocido"))

        mMap.moveCamera(CameraUpdateFactory.newLatLng(mexico))*/
        */
    }

    fun viewData(view: View){
        mMap.clear()
        for (pais in data){
            mMap.addMarker(MarkerOptions().position(
                    LatLng(pais.latitude,pais.longitude)).title(pais.nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.covid)))
        }
        /*
        mMap.clear()
        for (pais in paisesGson){
            mMap.addMarker(MarkerOptions().position(
                    LatLng(pais?.countryInfo.lat?:0.0,pais?.countryInfo.long?:0.0)).title(pais?.nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.covid)))
        }
        */
    }

    fun viewDefuns(view: View){
        mMap.clear()
        data.sortByDescending {
            it.defunciones
        }
        for (i in 0..9){
            mMap.addMarker(MarkerOptions().position(LatLng(data[i].latitude,data[i].longitude)).title(data[i].nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.defunciones)))
        }
    }

    fun viewReports(view: View){
        mMap.clear()
        data.sortByDescending {
            it.casos
        }
        for (i in 0..9){
            mMap.addMarker(MarkerOptions().position(LatLng(data[i].latitude,data[i].longitude)).title(data[i].nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.reportes)))
        }
    }

    fun viewTests(view: View){
        mMap.clear()
        data.sortByDescending {
            it.tests
        }
        for (i in 0..9){
            mMap.addMarker(MarkerOptions().position(LatLng(data[i].latitude,data[i].longitude)).title(data[i].nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.tests)))
        }
    }

    private val data = mutableListOf<Pais>() //PaisGson

    fun cargaDatos(){
        val requestQueue = Volley.newRequestQueue(this)
        val peticion = JsonArrayRequest(Request.Method.GET,url,null,Response.Listener{
            val jsonArray = it
            for (i in 0 until jsonArray.length()){
                val pais = jsonArray.getJSONObject(i)
                val nombre = pais.getString("country")
                val countryInfoData = pais.getJSONObject("countryInfo")

                val latitude =  countryInfoData.getDouble("lat")
                val longitude =  countryInfoData.getDouble("long")
                val casos = pais.getDouble("cases")
                val recuperados = pais.getDouble("recovered")
                val defunciones = pais.getDouble("deaths")
                val tests = pais.getDouble("tests")

                val paisObject = Pais(nombre,latitude, longitude, casos, recuperados, defunciones, tests) //PaisGson
                data.add(paisObject)
            }
        }, Response.ErrorListener {

        })
        requestQueue.add(peticion)
    }
    /*
    private fun getRetrofit():Retrofit{
        return Retrofit.Builder().baseUrl("https://disease.sh/v3/covid-19/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private lateinit var paisesGson: ArrayList<PaisGson>
    private fun getCountries(){
        val callToService = getRetrofit().create(APIService::class.java)

        CoroutineScope(Dispatchers.IO).launch{
            val responseFromService = callToService.getCountries()
            paisesGson = responseFromService.body() as ArrayList<PaisGson>
            runOnUiThread {
                if (responseFromService.isSuccessful) {
                    Toast.makeText(applicationContext, "Datos obtenidos", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Error!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    */

}