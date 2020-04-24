package com.example.pokemonandroid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        loadPokemon()
    }

    var ACCESSLOCATION=123
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)
                return
            }
        }
        GetUserLocation()
    }

    @SuppressLint("MissingPermission")  //Suprimindo o erro, pois já é feito uma verificação de permissão anteriormente
    fun GetUserLocation(){
        Toast.makeText(this, "User location acess on", Toast.LENGTH_SHORT).show()
        //TODO: Will implement later

        var myLocation = MyLocationListener()

        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, myLocation)
        var myThread=myThread()
        myThread.start()
    }

    //Automaticamente chamada quando chamada o requestPermissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){
            ACCESSLOCATION->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    GetUserLocation()
                }else{
                    Toast.makeText(this, "We can't acess to your location", Toast.LENGTH_SHORT).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
    }

    var location:Location?=null

    //Get user location
    inner class MyLocationListener:LocationListener{

        constructor(){
            location=Location("Start")
            location!!.longitude=0.0
            location!!.latitude=0.0
        }
        override fun onLocationChanged(p0: Location?) {
            location=p0
        }

        override fun onStatusChanged(p0: String?, status: Int, extras: Bundle?) {
            //TODO("Not yet implemented")
        }

        override fun onProviderEnabled(p0: String?) {
            //TODO("Not yet implemented")
        }

        override fun onProviderDisabled(p0: String?) {
            //TODO("Not yet implemented")
        }

    }

    var oldLocation:Location?=null
    inner class myThread:Thread{
        constructor():super(){
            oldLocation = Location("Start")
            oldLocation!!.longitude=0.0
            oldLocation!!.latitude=0.0
        }

        override fun run() {
            while (true){
                try {
                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }

                    oldLocation=location
                    runOnUiThread{
                        mMap!!.clear()
                        //show me
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap!!.addMarker(MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet( "he is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                        //show pokemon
                        for(i in 0 until listPokemon.size){
                            var newPokemon = listPokemon[i]
                            if(newPokemon.isCatch==false){
                                val pokemonLoc = LatLng(newPokemon.location!!.latitude, newPokemon.location!!.longitude)
                                mMap!!.addMarker(MarkerOptions()
                                        .position(pokemonLoc)
                                        .title(newPokemon.name!!)
                                        .snippet( newPokemon.des!! + "power:${newPokemon.power}")
                                        .icon(BitmapDescriptorFactory.fromResource(newPokemon.image!!)))

                                if(location!!.distanceTo(newPokemon.location)<2){
                                    newPokemon.isCatch=true
                                    listPokemon[i]=newPokemon
                                    playerPower+=newPokemon.power!!
                                    Toast.makeText(applicationContext, "You catch new pokemon your power is:$playerPower", Toast.LENGTH_LONG).show()

                                }
                            }
                        }
                    }
                    Thread.sleep(1000)
                }catch (ex:Exception){

                }
            }
        }
    }

    var playerPower=0.0
    var listPokemon=ArrayList<Pokemon>()

    fun loadPokemon(){
        listPokemon.add(Pokemon(R.drawable.bulbasaur, "Bulbasaur", "here is from japan", 55.0, 37.33, -122.22))
        listPokemon.add(Pokemon(R.drawable.charmander, "Charmander", "here is from japan", 55.0, 37.33, -122.22))
        listPokemon.add(Pokemon(R.drawable.squirtle, "Squirtle", "here is from japan", 55.0, 37.33, -122.22))
    }
}
